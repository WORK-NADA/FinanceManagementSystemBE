package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.Repository.PurchaseRepository;
import FinanceManangementSystem.demo.Repository.SupplierRepository;
import FinanceManangementSystem.demo.Service.PurchaseServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService implements PurchaseServiceInterface {

    private final PurchaseRepository purchaseRepo;

    private final SupplierRepository supplierRepo;

    private final ModelMapper modelMapper;

    private final DocumentSequenceService documentSequenceService;


    @Override
    @Transactional
    public ResponsePurchaseDTO addPurchase(
            RequestPurchaseDTO dto
    ) {

        log.info("SERVICE - request came in addPurchase...");


        // -----------------------------------------
        // Find Supplier
        // -----------------------------------------

        log.info("SERVICE - searching supplier by publicId...");

        Supplier supplier =
                supplierRepo
                        .findByPublicId(
                                dto.getSupplierPublicId()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "SERVICE - supplier not found for publicId: {}",
                                    dto.getSupplierPublicId()
                            );

                            return new RuntimeException(
                                    "Supplier not found"
                            );
                        });

        log.info("SERVICE - supplier found successfully...");


        // -----------------------------------------
        // Check Supplier Invoice Number
        // -----------------------------------------

        if (
                dto.getSupplierInvoiceNumber() != null
                        &&
                        !dto.getSupplierInvoiceNumber().isBlank()
        ) {

            log.info(
                    "SERVICE - checking supplier invoice number..."
            );

            boolean invoiceExists =
                    purchaseRepo
                            .existsBySupplierInvoiceNumberAndSupplier_PublicId(
                                    dto.getSupplierInvoiceNumber(),
                                    dto.getSupplierPublicId()
                            );

            if (invoiceExists) {

                log.warn(
                        "SERVICE - supplier invoice number already exists..."
                );

                throw new RuntimeException(
                        "Supplier invoice number already exists for this supplier"
                );
            }
        }


        // -----------------------------------------
        // Purchase Date
        // -----------------------------------------

        LocalDate purchaseDate =
                dto.getPurchaseDate();

        log.info(
                "SERVICE - purchase date received: {}",
                purchaseDate
        );


        // -----------------------------------------
        // Generate Purchase Number
        // -----------------------------------------

        log.info(
                "SERVICE - generating purchase number..."
        );

        String purchaseNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.PURCHASE,
                                purchaseDate.getYear()
                        );

        log.info(
                "SERVICE - purchase number generated: {}",
                purchaseNumber
        );


        // -----------------------------------------
        // Calculate Amount
        // -----------------------------------------

        log.info(
                "SERVICE - calculating purchase amount..."
        );

        BigDecimal amount =
                dto.getWeight()
                        .multiply(dto.getRatePerUnit())
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        log.info(
                "SERVICE - purchase amount calculated: {}",
                amount
        );


        // -----------------------------------------
        // Calculate GST
        // -----------------------------------------

        log.info(
                "SERVICE - calculating GST amount..."
        );

        BigDecimal gstAmount =
                amount
                        .multiply(dto.getGstPercentage())
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        log.info(
                "SERVICE - GST amount calculated: {}",
                gstAmount
        );


        // -----------------------------------------
        // Calculate Total Amount
        // -----------------------------------------

        log.info(
                "SERVICE - calculating total purchase amount..."
        );

        BigDecimal totalAmount =
                amount
                        .add(gstAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        log.info(
                "SERVICE - total purchase amount calculated: {}",
                totalAmount
        );


        // -----------------------------------------
        // Map DTO → Entity
        // -----------------------------------------

        log.info(
                "SERVICE - mapping request DTO to purchase entity..."
        );

        Purchase purchase =
                modelMapper.map(
                        dto,
                        Purchase.class
                );


        // -----------------------------------------
        // Set Business Fields
        // -----------------------------------------

        purchase.setSupplier(supplier);

        purchase.setPurchaseNumber(
                purchaseNumber
        );

        purchase.setAmount(
                amount
        );

        purchase.setGstAmount(
                gstAmount
        );

        purchase.setTotalAmount(
                totalAmount
        );

        purchase.setPaymentStatus(
                PaymentStatus.PENDING
        );


        // -----------------------------------------
        // Save Purchase
        // -----------------------------------------

        log.info(
                "SERVICE - saving purchase..."
        );

        purchase =
                purchaseRepo.save(
                        purchase
                );

        log.info(
                "SERVICE - purchase saved successfully..."
        );


        // -----------------------------------------
        // Prepare Response
        // -----------------------------------------

        log.info(
                "SERVICE - preparing purchase response..."
        );

        ResponsePurchaseDTO response =
                modelMapper.map(
                        purchase,
                        ResponsePurchaseDTO.class
                );

        response.setSupplierPublicId(
                supplier.getPublicId()
        );

        response.setSupplierName(
                supplier.getSupplierName()
        );


        log.info(
                "SERVICE - addPurchase completed successfully...");

        return response;
    }


    // =====================================================
    // GET PURCHASE BY PUBLIC ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ResponsePurchaseDTO getPurchaseByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getPurchaseByPublicId..."
        );


        // -----------------------------------------
        // Find Purchase
        // -----------------------------------------

        log.info(
                "SERVICE - searching purchase by publicId: {}",
                publicId
        );

        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "SERVICE - purchase not found for publicId: {}",
                                    publicId
                            );

                            return new RuntimeException(
                                    "Purchase not found"
                            );
                        });


        log.info(
                "SERVICE - purchase found successfully..."
        );


        // -----------------------------------------
        // Map Entity → Response DTO
        // -----------------------------------------

        ResponsePurchaseDTO response =
                modelMapper.map(
                        purchase,
                        ResponsePurchaseDTO.class
                );


        // -----------------------------------------
        // Supplier Details
        // -----------------------------------------

        if (purchase.getSupplier() != null) {

            response.setSupplierPublicId(
                    purchase.getSupplier()
                            .getPublicId()
            );

            response.setSupplierName(
                    purchase.getSupplier()
                            .getSupplierName()
            );
        }


        log.info(
                "SERVICE - getPurchaseByPublicId completed successfully..."
        );

        return response;
    }
}