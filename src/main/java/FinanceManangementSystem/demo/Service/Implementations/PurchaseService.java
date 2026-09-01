package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Exceptions.InvalidRequestException;
import FinanceManangementSystem.demo.Exceptions.ResourceNotFoundException;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Enums.PurchaseStatus;
import FinanceManangementSystem.demo.Enums.UserRole;
import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Model.User;
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

    private final CurrentUserService currentUserService;

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

                            return new InvalidRequestException(
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

                throw new InvalidRequestException(
                        "Purchase with this supplier invoice number already exists"
                );
            }
        }


        // -----------------------------------------------------
        // CREATE PURCHASE
        // -----------------------------------------------------

        User currentUser = currentUserService.getCurrentUser();

        Purchase purchase =
                new Purchase();

        purchase.setUser(currentUser);

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
                PaymentStatus.PENDING
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


        User currentUser = currentUserService.getCurrentUser();

        Purchase purchase;

        if (currentUser.getRole() == UserRole.ADMIN) {
            purchase = purchaseRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - purchase not found...");
                        return new ResourceNotFoundException("Purchase not found");
                    });
        } else {
            purchase = purchaseRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - purchase not found for current user...");
                        return new ResourceNotFoundException("Purchase not found");
                    });
        }

        return mapToResponse(purchase);
    }


    // =========================================================
    // GET ALL PURCHASES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ResponsePurchaseDTO> getAllPurchases(org.springframework.data.domain.Pageable pageable) {

        log.info(
                "SERVICE - request came in getAllPurchases..."
        );

        User currentUser = currentUserService.getCurrentUser();

        return purchaseRepo.findByUser(currentUser, pageable).map(this::mapToResponse);
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


        User currentUser = currentUserService.getCurrentUser();

        Supplier supplier;

        if (currentUser.getRole() == UserRole.ADMIN) {
            supplier = supplierRepo.findByPublicId(supplierPublicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - supplier not found...");
                        return new ResourceNotFoundException("Supplier not found");
                    });
        } else {
            supplier = supplierRepo.findByUserAndPublicId(currentUser, supplierPublicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - supplier not found for current user...");
                        return new ResourceNotFoundException("Supplier not found");
                    });
        }

        return purchaseRepo.findByUser(currentUser).stream()
                .filter(p -> p.getSupplier() != null && p.getSupplier().getPublicId().equals(supplier.getPublicId()))
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

            throw new InvalidRequestException(
                    "From date and to date are required"
            );
        }


        if (fromDate.isAfter(toDate)) {

            throw new InvalidRequestException(
                    "From date cannot be after to date"
            );
        }


        User currentUser = currentUserService.getCurrentUser();

        return purchaseRepo
                .findByUserAndPurchaseDateBetween(
                        currentUser,
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

            throw new InvalidRequestException(
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

            throw new InvalidRequestException(
                    "Invalid purchase status"
            );
        }


        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return purchaseRepo.findByPurchaseStatus(purchaseStatus).stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return purchaseRepo
                .findByUserAndPurchaseStatus(
                        currentUser,
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

        User currentUser = currentUserService.getCurrentUser();

        Purchase purchase;

        if (currentUser.getRole() == UserRole.ADMIN) {
            purchase = purchaseRepo.findByPublicId(publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - purchase not found...");
                        return new ResourceNotFoundException("Purchase not found");
                    });
        } else {
            purchase = purchaseRepo.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> {
                        log.info("SERVICE - purchase not found for current user...");
                        return new ResourceNotFoundException("Purchase not found");
                    });
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

            throw new InvalidRequestException(
                    "Raw material cannot be changed after purchase creation"
            );
        }


        if (purchase.getWeight()
                .compareTo(
                        dto.getWeight()
                ) != 0) {

            throw new InvalidRequestException(
                    "Purchase quantity cannot be changed after purchase creation"
            );
        }


        if (purchase.getUnit()
                != dto.getUnit()) {

            throw new InvalidRequestException(
                    "Purchase unit cannot be changed after purchase creation"
            );
        }


        // -----------------------------------------------------
        // FIND ACTIVE SUPPLIER
        // -----------------------------------------------------

        Supplier supplier;

        if (currentUser.getRole() == UserRole.ADMIN) {
            supplier = supplierRepo.findByPublicIdAndIsActiveTrue(dto.getSupplierPublicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Active supplier not found"));
        } else {
            supplier = supplierRepo.findByUserAndPublicIdAndIsActiveTrue(
                            currentUser,
                            dto.getSupplierPublicId()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException("Active supplier not found"));
        }


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

            throw new InvalidRequestException(
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
