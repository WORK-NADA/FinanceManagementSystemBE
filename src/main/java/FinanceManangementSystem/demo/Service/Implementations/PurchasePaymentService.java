package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.PurchasePayment;
import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchasePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchasePaymentDTO;
import FinanceManangementSystem.demo.Repository.PurchasePaymentRepository;
import FinanceManangementSystem.demo.Repository.PurchaseRepository;
import FinanceManangementSystem.demo.Repository.SupplierRepository;
import FinanceManangementSystem.demo.Service.PurchasePaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchasePaymentService
        implements PurchasePaymentServiceInterface {

    private final PurchasePaymentRepository purchasePaymentRepo;

    private final PurchaseRepository purchaseRepo;

    private final SupplierRepository supplierRepo;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;


    // =========================================================
    // ADD PAYMENT
    // =========================================================

    @Override
    @Transactional
    public ResponsePurchasePaymentDTO addPayment(
            RequestPurchasePaymentDTO dto
    ) {

        log.info(
                "SERVICE - request came in addPayment..."
        );


        // -----------------------------------------------------
        // FIND PURCHASE
        // -----------------------------------------------------

        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                dto.getPurchasePublicId()
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
        // VALIDATE AMOUNT
        // -----------------------------------------------------

        BigDecimal currentPaidAmount =
                purchasePaymentRepo
                        .sumPaidAmountByPurchase(
                                purchase
                        );

        BigDecimal newTotalPaid =
                currentPaidAmount
                        .add(
                                dto.getAmountPaid()
                        );

        if (newTotalPaid.compareTo(purchase.getTotalAmount()) > 0) {

            log.info(
                    "SERVICE - payment amount exceeds pending balance..."
            );

            BigDecimal currentPending =
                    purchase
                            .getTotalAmount()
                            .subtract(
                                    currentPaidAmount
                            )
                            .max(
                                    BigDecimal.ZERO
                            );

            throw new RuntimeException(
                    "Payment amount exceeds remaining pending amount of " + currentPending
            );
        }


        // -----------------------------------------------------
        // GENERATE REFERENCE NUMBER
        // -----------------------------------------------------

        int year =
                dto.getPaymentDate()
                        .getYear();

        String referenceNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.PURCHASE_PAYMENT,
                                year
                        );


        // -----------------------------------------------------
        // CREATE PAYMENT ENTITY
        // -----------------------------------------------------

        PurchasePayment payment =
                new PurchasePayment();

        payment.setPurchase(
                purchase
        );

        payment.setAmountPaid(
                dto.getAmountPaid()
        );

        payment.setPaymentDate(
                dto.getPaymentDate()
        );

        payment.setPaymentMode(
                dto.getPaymentMode()
        );

        payment.setReferenceNumber(
                referenceNumber
        );

        payment.setRemarks(
                dto.getRemarks() != null ? dto.getRemarks().trim() : null
        );

        payment =
                purchasePaymentRepo.save(
                        payment
                );


        // -----------------------------------------------------
        // UPDATE PURCHASE PAYMENT STATUS
        // -----------------------------------------------------

        if (newTotalPaid.compareTo(purchase.getTotalAmount()) >= 0) {

            purchase.setPaymentStatus(
                    PaymentStatus.COMPLETED
            );

        } else if (newTotalPaid.compareTo(BigDecimal.ZERO) > 0) {

            purchase.setPaymentStatus(
                    PaymentStatus.PARTIALLY_PAID
            );

        } else {

            purchase.setPaymentStatus(
                    PaymentStatus.PENDING
            );
        }

        purchaseRepo.save(
                purchase
        );


        log.info(
                "SERVICE - purchase payment added successfully..."
        );


        return mapToResponse(
                payment,
                purchase,
                newTotalPaid
        );
    }


    // =========================================================
    // GET PAYMENT BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponsePurchasePaymentDTO getPaymentByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentByPublicId..."
        );

        PurchasePayment payment =
                purchasePaymentRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Purchase payment not found"
                        ));

        Purchase purchase =
                payment.getPurchase();

        BigDecimal paidAmount =
                purchasePaymentRepo
                        .sumPaidAmountByPurchase(
                                purchase
                        );

        return mapToResponse(
                payment,
                purchase,
                paidAmount
        );
    }


    // =========================================================
    // GET PAYMENTS BY PURCHASE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchasePaymentDTO> getPaymentsByPurchase(
            UUID purchasePublicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentsByPurchase..."
        );

        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                purchasePublicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Purchase not found"
                        ));

        BigDecimal paidAmount =
                purchasePaymentRepo
                        .sumPaidAmountByPurchase(
                                purchase
                        );

        return purchasePaymentRepo
                .findByPurchaseOrderByPaymentDateDesc(
                        purchase
                )
                .stream()
                .map(payment -> mapToResponse(payment, purchase, paidAmount))
                .toList();
    }


    // =========================================================
    // GET PAYMENTS BY SUPPLIER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchasePaymentDTO> getPaymentsBySupplier(
            UUID supplierPublicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentsBySupplier..."
        );

        supplierRepo
                .findByPublicIdAndIsActiveTrue(
                        supplierPublicId
                )
                .orElseThrow(() -> new RuntimeException(
                        "Active supplier not found"
                ));

        return purchasePaymentRepo
                .findByPurchase_Supplier_PublicIdOrderByPaymentDateDesc(
                        supplierPublicId
                )
                .stream()
                .map(payment -> {
                    Purchase purchase = payment.getPurchase();
                    BigDecimal paidAmount = purchasePaymentRepo.sumPaidAmountByPurchase(purchase);
                    return mapToResponse(payment, purchase, paidAmount);
                })
                .toList();
    }


    // =========================================================
    // GET PURCHASE PAYMENT SUMMARY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponsePurchasePaymentDTO.PurchaseDetails getPurchasePaymentSummary(
            UUID purchasePublicId
    ) {

        log.info(
                "SERVICE - request came in getPurchasePaymentSummary..."
        );

        Purchase purchase =
                purchaseRepo
                        .findByPublicId(
                                purchasePublicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Purchase not found"
                        ));

        return buildPurchaseDetails(
                purchase
        );
    }


    // =========================================================
    // GET ALL PENDING PAYMENTS (DASHBOARD)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchasePaymentDTO.PurchaseDetails> getAllPendingPayments() {

        log.info(
                "SERVICE - request came in getAllPendingPayments..."
        );

        List<Purchase> pendingPurchases =
                purchaseRepo
                        .findByPaymentStatusIn(
                                List.of(
                                        PaymentStatus.PENDING,
                                        PaymentStatus.PARTIALLY_PAID
                                )
                        );

        return pendingPurchases
                .stream()
                .map(this::buildPurchaseDetails)
                .toList();
    }

        @Override
        @Transactional(readOnly = true)
        public org.springframework.data.domain.Page<ResponsePurchasePaymentDTO> getAllPayments(org.springframework.data.domain.Pageable pageable) {

                log.info("SERVICE - request came in getAllPayments...");

                return purchasePaymentRepo.findAll(pageable)
                                .map(payment -> {
                                        Purchase purchase = payment.getPurchase();
                                        java.math.BigDecimal paidAmount = purchasePaymentRepo.sumPaidAmountByPurchase(purchase);
                                        return mapToResponse(payment, purchase, paidAmount);
                                });
        }


    // =========================================================
    // GET PENDING PAYMENTS BY SUPPLIER (DASHBOARD)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponsePurchasePaymentDTO.PurchaseDetails> getPendingPaymentsBySupplier(
            UUID supplierPublicId
    ) {

        log.info(
                "SERVICE - request came in getPendingPaymentsBySupplier..."
        );

        Supplier supplier =
                supplierRepo
                        .findByPublicIdAndIsActiveTrue(
                                supplierPublicId
                        )
                        .orElseThrow(() -> new RuntimeException(
                                "Active supplier not found"
                        ));

        List<Purchase> pendingPurchases =
                purchaseRepo
                        .findBySupplierAndPaymentStatusIn(
                                supplier,
                                List.of(
                                        PaymentStatus.PENDING,
                                        PaymentStatus.PARTIALLY_PAID
                                )
                        );

        return pendingPurchases
                .stream()
                .map(this::buildPurchaseDetails)
                .toList();
    }


    // =========================================================
    // GET TOTAL OUTSTANDING AMOUNT (DASHBOARD SUMMARY)
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingAmount() {

        log.info(
                "SERVICE - request came in getTotalOutstandingAmount..."
        );

        List<Purchase> pendingPurchases =
                purchaseRepo
                        .findByPaymentStatusIn(
                                List.of(
                                        PaymentStatus.PENDING,
                                        PaymentStatus.PARTIALLY_PAID
                                )
                        );

        return pendingPurchases
                .stream()
                .map(purchase -> {
                    BigDecimal paid = purchasePaymentRepo.sumPaidAmountByPurchase(purchase);
                    return purchase.getTotalAmount().subtract(paid).max(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // =========================================================
    // HELPER: BUILD PURCHASE DETAILS
    // =========================================================

    private ResponsePurchasePaymentDTO.PurchaseDetails buildPurchaseDetails(
            Purchase purchase
    ) {

        BigDecimal paidAmount =
                purchasePaymentRepo
                        .sumPaidAmountByPurchase(
                                purchase
                        );

        BigDecimal pendingAmount =
                purchase
                        .getTotalAmount()
                        .subtract(
                                paidAmount
                        )
                        .max(
                                BigDecimal.ZERO
                        );

        ResponsePurchasePaymentDTO.PurchaseDetails details =
                new ResponsePurchasePaymentDTO.PurchaseDetails();

        details.setPublicId(
                purchase.getPublicId()
        );

        details.setPurchaseNumber(
                purchase.getPurchaseNumber()
        );

        details.setTotalAmount(
                purchase.getTotalAmount()
        );

        details.setPaidAmount(
                paidAmount
        );

        details.setPendingAmount(
                pendingAmount
        );

        details.setPaymentStatus(
                purchase.getPaymentStatus()
        );

        return details;
    }


    // =========================================================
    // HELPER: MAP TO RESPONSE DTO
    // =========================================================

    private ResponsePurchasePaymentDTO mapToResponse(
            PurchasePayment payment,
            Purchase purchase,
            BigDecimal paidAmount
    ) {

        ResponsePurchasePaymentDTO response =
                modelMapper.map(
                        payment,
                        ResponsePurchasePaymentDTO.class
                );

        response.setPurchase(
                buildPurchaseDetails(
                        purchase
                )
        );

        return response;
    }
}
