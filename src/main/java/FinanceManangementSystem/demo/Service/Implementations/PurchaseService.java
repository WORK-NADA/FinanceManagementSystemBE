package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.PurchasePaymentStatus;
import FinanceManangementSystem.demo.Enums.PurchaseStatus;
import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.Repository.PurchaseRepository;
import FinanceManangementSystem.demo.Repository.SupplierRepository;
import FinanceManangementSystem.demo.Service.PurchaseServiceInterface;
import FinanceManangementSystem.demo.Service.StockTransactionServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService
        implements PurchaseServiceInterface {

    private final PurchaseRepository purchaseRepo;

    private final SupplierRepository supplierRepo;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;

    private final StockTransactionServiceInterface
            stockTransactionService;


    // =========================================================
    // ADD PURCHASE
    // =========================================================

    @Override
    @Transactional
    public ResponsePurchaseDTO addPurchase(
            RequestPurchaseDTO dto
    ) {

        log.info(
                "SERVICE - request came in addPurchase..."
        );


        // -----------------------------------------------------
        // FIND ACTIVE SUPPLIER
        // -----------------------------------------------------

        log.info(
                "SERVICE - finding active supplier..."
        );

        Supplier supplier =
                supplierRepo
                        .findByPublicIdAndIsActiveTrue(
                                dto.getSupplierPublicId()
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - active supplier not found..."
                            );

                            return new RuntimeException(
                                    "Active supplier not found"
                            );
                        });


        // -----------------------------------------------------
        // CHECK SUPPLIER INVOICE NUMBER
        // -----------------------------------------------------

        /*
         * Supplier invoice number is optional.
         *
         * Duplicate checking is performed only when
         * an invoice number is actually provided.
         */

        String supplierInvoiceNumber =
                dto.getSupplierInvoiceNumber();

        if (supplierInvoiceNumber != null) {

            supplierInvoiceNumber =
                    supplierInvoiceNumber.trim();

            if (!supplierInvoiceNumber.isBlank()
                    && purchaseRepo
                    .existsBySupplierInvoiceNumberAndSupplier(
                            supplierInvoiceNumber,
                            supplier
                    )) {

                log.info(
                        "SERVICE - supplier invoice number already exists..."
                );

                throw new RuntimeException(
                        "Purchase with this supplier invoice number already exists"
                );
            }
        }


        // -----------------------------------------------------
        // CREATE PURCHASE
        // -----------------------------------------------------

        Purchase purchase =
                new Purchase();


        purchase.setSupplier(
                supplier
        );


        purchase.setRawMaterial(
                dto.getRawMaterial().trim()
        );


        purchase.setWeight(
                dto.getWeight()
        );


        purchase.setUnit(
                dto.getUnit()
        );


        purchase.setRatePerUnit(
                dto.getRatePerUnit()
        );


        purchase.setGstPercentage(
                dto.getGstPercentage()
        );


        purchase.setSupplierInvoiceNumber(
                supplierInvoiceNumber
        );


        purchase.setPurchaseDate(
                dto.getPurchaseDate()
        );


        // -----------------------------------------------------
        // INITIAL STATUS
        // -----------------------------------------------------

        /*
         * Status is controlled by the service.
         *
         * Frontend cannot decide whether a new purchase
         * should be ACTIVE/CANCELLED or PAID/PENDING.
         */

        purchase.setPurchaseStatus(
                PurchaseStatus.ACTIVE
        );


        purchase.setPaymentStatus(
                PurchasePaymentStatus.PENDING
        );


        // -----------------------------------------------------
        // CALCULATE AMOUNT
        // -----------------------------------------------------

        BigDecimal amount =
                dto.getWeight()
                        .multiply(
                                dto.getRatePerUnit()
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        purchase.setAmount(
                amount
        );


        // -----------------------------------------------------
        // CALCULATE GST
        // -----------------------------------------------------

        BigDecimal gstAmount =
                amount
                        .multiply(
                                dto.getGstPercentage()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        purchase.setGstAmount(
                gstAmount
        );


        // -----------------------------------------------------
        // CALCULATE TOTAL
        // -----------------------------------------------------

        BigDecimal totalAmount =
                amount
                        .add(gstAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        purchase.setTotalAmount(
                totalAmount
        );


        // -----------------------------------------------------
        // GENERATE PURCHASE NUMBER
        // -----------------------------------------------------

        log.info(
                "SERVICE - generating purchase number..."
        );

        int year =
                dto.getPurchaseDate()
                        .getYear();

        String purchaseNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.PURCHASE,
                                year
                        );

        purchase.setPurchaseNumber(
                purchaseNumber
        );


        // -----------------------------------------------------
        // SAVE PURCHASE
        // -----------------------------------------------------

        log.info(
                "SERVICE - saving purchase..."
        );

        purchase =
                purchaseRepo.save(
                        purchase
                );


        // -----------------------------------------------------
        // ADD STOCK
        // -----------------------------------------------------

        /*
         * Every successful purchase automatically creates
         * a PURCHASE_IN stock transaction.
         *
         * Purchase number is used as the reference number.
         */

        log.info(
                "SERVICE - adding purchased quantity to stock..."
        );

        stockTransactionService.purchaseStockIn(
                purchase.getRawMaterial(),
                purchase.getUnit(),
                purchase.getWeight(),
                purchase.getPurchaseNumber()
        );


        log.info(
                "SERVICE - purchase and stock added successfully..."
        );


        return mapToResponse(
                purchase
        );
    }


    // =========================================================
    // GET PURCHASE BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponsePurchaseDTO getPurchaseByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getPurchaseByPublicId..."
        );


        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - purchase not found..."
                            );

                            return new RuntimeException(
                                    "Purchase not found"
                            );
                        });


        return mapToResponse(
                purchase
        );
    }


    // =========================================================
    // GET ALL PURCHASES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchaseDTO> getAllPurchases() {

        log.info(
                "SERVICE - request came in getAllPurchases..."
        );


        return purchaseRepo
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET PURCHASES BY SUPPLIER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchaseDTO> getPurchasesBySupplier(
            UUID supplierPublicId
    ) {

        log.info(
                "SERVICE - request came in getPurchasesBySupplier..."
        );


        Supplier supplier =
                supplierRepo
                        .findByPublicId(
                                supplierPublicId
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - supplier not found..."
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });


        return purchaseRepo
                .findBySupplier(
                        supplier
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET PURCHASES BY DATE RANGE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchaseDTO> getPurchasesByDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        log.info(
                "SERVICE - request came in getPurchasesByDateRange..."
        );


        if (fromDate == null ||
                toDate == null) {

            throw new RuntimeException(
                    "From date and to date are required"
            );
        }


        if (fromDate.isAfter(toDate)) {

            throw new RuntimeException(
                    "From date cannot be after to date"
            );
        }


        return purchaseRepo
                .findByPurchaseDateBetween(
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET PURCHASES BY STATUS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchaseDTO> getPurchasesByStatus(
            String status
    ) {

        log.info(
                "SERVICE - request came in getPurchasesByStatus..."
        );


        if (status == null ||
                status.isBlank()) {

            throw new RuntimeException(
                    "Purchase status is required"
            );
        }


        PurchaseStatus purchaseStatus;

        try {

            purchaseStatus =
                    PurchaseStatus.valueOf(
                            status.trim().toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new RuntimeException(
                    "Invalid purchase status"
            );
        }


        return purchaseRepo
                .findByPurchaseStatus(
                        purchaseStatus
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // UPDATE PURCHASE
    // =========================================================

    @Override
    @Transactional
    public ResponsePurchaseDTO updatePurchase(
            UUID publicId,
            RequestPurchaseDTO dto
    ) {

        log.info(
                "SERVICE - request came in updatePurchase..."
        );


        // -----------------------------------------------------
        // FIND PURCHASE
        // -----------------------------------------------------

        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - purchase not found..."
                            );

                            return new RuntimeException(
                                    "Purchase not found"
                            );
                        });


        // -----------------------------------------------------
        // CHECK PURCHASE STATUS
        // -----------------------------------------------------

        if (purchase.getPurchaseStatus()
                == PurchaseStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled purchase cannot be updated"
            );
        }


        // -----------------------------------------------------
        // PREVENT STOCK-AFFECTING CHANGES
        // -----------------------------------------------------

        /*
         * The original purchase has already generated:
         *
         * PURCHASE_IN
         *
         * Therefore raw material, quantity and unit cannot
         * be changed directly.
         *
         * This keeps the stock ledger consistent.
         */

        if (!purchase.getRawMaterial()
                .equalsIgnoreCase(
                        dto.getRawMaterial().trim()
                )) {

            throw new RuntimeException(
                    "Raw material cannot be changed after purchase creation"
            );
        }


        if (purchase.getWeight()
                .compareTo(
                        dto.getWeight()
                ) != 0) {

            throw new RuntimeException(
                    "Purchase quantity cannot be changed after purchase creation"
            );
        }


        if (purchase.getUnit()
                != dto.getUnit()) {

            throw new RuntimeException(
                    "Purchase unit cannot be changed after purchase creation"
            );
        }


        // -----------------------------------------------------
        // FIND ACTIVE SUPPLIER
        // -----------------------------------------------------

        Supplier supplier =
                supplierRepo
                        .findByPublicIdAndIsActiveTrue(
                                dto.getSupplierPublicId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Active supplier not found"
                                )
                        );


        // -----------------------------------------------------
        // SUPPLIER INVOICE NUMBER
        // -----------------------------------------------------

        String newInvoiceNumber =
                dto.getSupplierInvoiceNumber();

        if (newInvoiceNumber != null) {

            newInvoiceNumber =
                    newInvoiceNumber.trim();

            if (newInvoiceNumber.isBlank()) {

                newInvoiceNumber = null;
            }
        }


        String oldInvoiceNumber =
                purchase.getSupplierInvoiceNumber();


        boolean invoiceChanged =
                !Objects.equals(
                        oldInvoiceNumber,
                        newInvoiceNumber
                );


        boolean supplierChanged =
                !purchase.getSupplier()
                        .getPublicId()
                        .equals(
                                supplier.getPublicId()
                        );


        /*
         * Supplier invoice number is optional.
         *
         * Duplicate validation is only performed when
         * an actual invoice number exists.
         */

        if ((invoiceChanged || supplierChanged)
                && newInvoiceNumber != null
                && purchaseRepo
                .existsBySupplierInvoiceNumberAndSupplier(
                        newInvoiceNumber,
                        supplier
                )) {

            throw new RuntimeException(
                    "Purchase with this supplier invoice number already exists"
            );
        }


        // -----------------------------------------------------
        // UPDATE SUPPLIER
        // -----------------------------------------------------

        purchase.setSupplier(
                supplier
        );


        // -----------------------------------------------------
        // UPDATE NON-STOCK DETAILS
        // -----------------------------------------------------

        /*
         * Raw material, weight and unit intentionally remain
         * unchanged.
         */

        purchase.setRatePerUnit(
                dto.getRatePerUnit()
        );


        purchase.setGstPercentage(
                dto.getGstPercentage()
        );


        purchase.setSupplierInvoiceNumber(
                newInvoiceNumber
        );


        purchase.setPurchaseDate(
                dto.getPurchaseDate()
        );


        // -----------------------------------------------------
        // RECALCULATE AMOUNT
        // -----------------------------------------------------

        BigDecimal amount =
                purchase.getWeight()
                        .multiply(
                                dto.getRatePerUnit()
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        purchase.setAmount(
                amount
        );


        // -----------------------------------------------------
        // RECALCULATE GST
        // -----------------------------------------------------

        BigDecimal gstAmount =
                amount
                        .multiply(
                                dto.getGstPercentage()
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        purchase.setGstAmount(
                gstAmount
        );


        // -----------------------------------------------------
        // RECALCULATE TOTAL
        // -----------------------------------------------------

        BigDecimal totalAmount =
                amount
                        .add(gstAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        purchase.setTotalAmount(
                totalAmount
        );


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        purchase =
                purchaseRepo.save(
                        purchase
                );


        log.info(
                "SERVICE - purchase updated successfully..."
        );


        return mapToResponse(
                purchase
        );
    }


    // =========================================================
    // CANCEL PURCHASE
    // =========================================================

    @Override
    @Transactional
    public void cancelPurchase(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in cancelPurchase..."
        );


        // -----------------------------------------------------
        // FIND PURCHASE
        // -----------------------------------------------------

        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> {

                            log.info(
                                    "SERVICE - purchase not found..."
                            );

                            return new RuntimeException(
                                    "Purchase not found"
                            );
                        });


        // -----------------------------------------------------
        // CHECK STATUS
        // -----------------------------------------------------

        if (purchase.getPurchaseStatus()
                == PurchaseStatus.CANCELLED) {

            throw new RuntimeException(
                    "Purchase is already cancelled"
            );
        }


        // -----------------------------------------------------
        // REVERSE STOCK
        // -----------------------------------------------------

        /*
         * The purchase originally created:
         *
         * PURCHASE_IN
         *
         * Cancellation creates:
         *
         * PURCHASE_CANCEL_OUT
         *
         * The purchase number is used as the reference.
         */

        log.info(
                "SERVICE - reversing purchase stock..."
        );


        stockTransactionService.purchaseCancelStockOut(
                purchase.getRawMaterial(),
                purchase.getUnit(),
                purchase.getWeight(),
                purchase.getPurchaseNumber()
        );


        // -----------------------------------------------------
        // MARK PURCHASE AS CANCELLED
        // -----------------------------------------------------

        purchase.setPurchaseStatus(
                PurchaseStatus.CANCELLED
        );


        purchaseRepo.save(
                purchase
        );


        log.info(
                "SERVICE - purchase cancelled and stock reversed successfully..."
        );
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ResponsePurchaseDTO mapToResponse(
            Purchase purchase
    ) {

        log.info(
                "SERVICE - mapping purchase to response DTO..."
        );


        // -----------------------------------------------------
        // MAP PURCHASE FIELDS
        // -----------------------------------------------------

        ResponsePurchaseDTO response =
                modelMapper.map(
                        purchase,
                        ResponsePurchaseDTO.class
                );


        // -----------------------------------------------------
        // MAP SUPPLIER DETAILS
        // -----------------------------------------------------

        Supplier supplier =
                purchase.getSupplier();


        if (supplier != null) {

            ResponsePurchaseDTO.SupplierDetails
                    supplierDetails =
                    new ResponsePurchaseDTO.SupplierDetails();


            supplierDetails.setPublicId(
                    supplier.getPublicId()
            );


            supplierDetails.setSupplierName(
                    supplier.getSupplierName()
            );


            supplierDetails.setMobileNumber(
                    supplier.getMobileNumber()
            );


            supplierDetails.setEmail(
                    supplier.getEmail()
            );


            supplierDetails.setGstNumber(
                    supplier.getGstNumber()
            );


            response.setSupplier(
                    supplierDetails
            );
        }


        return response;
    }
}