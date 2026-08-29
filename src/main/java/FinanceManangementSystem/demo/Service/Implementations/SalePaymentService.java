package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.SalePaymentStatus;
import FinanceManangementSystem.demo.Enums.SaleStatus;
import FinanceManangementSystem.demo.Model.Sale;
import FinanceManangementSystem.demo.Model.SalePayment;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSalePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSalePaymentDTO;
import FinanceManangementSystem.demo.Repository.SalePaymentRepository;
import FinanceManangementSystem.demo.Repository.SaleRepository;
import FinanceManangementSystem.demo.Service.SalePaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalePaymentService
        implements SalePaymentServiceInterface {

    private final SalePaymentRepository salePaymentRepo;

    private final SaleRepository saleRepo;

    private final DocumentSequenceService documentSequenceService;

    private final ModelMapper modelMapper;


    // =========================================================
    // ADD PAYMENT
    // =========================================================

    @Override
    @Transactional
    public ResponseSalePaymentDTO addPayment(
            RequestSalePaymentDTO dto
    ) {

        log.info(
                "SERVICE - request came in addPayment..."
        );


        // -----------------------------------------------------
        // FIND SALE
        // -----------------------------------------------------

        Sale sale =
                saleRepo
                        .findByPublicId(
                                dto.getSalePublicId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sale not found"
                                )
                        );


        // -----------------------------------------------------
        // CHECK SALE STATUS
        // -----------------------------------------------------

        if (sale.getSaleStatus()
                == SaleStatus.CANCELLED) {

            throw new RuntimeException(
                    "Payment cannot be added to cancelled sale"
            );
        }


        // -----------------------------------------------------
        // VALIDATE PAYMENT DATE
        // -----------------------------------------------------

        if (dto.getPaymentDate() == null) {

            throw new RuntimeException(
                    "Payment date is required"
            );
        }


        // -----------------------------------------------------
        // VALIDATE PAYMENT AMOUNT
        // -----------------------------------------------------

        if (dto.getAmount() == null) {

            throw new RuntimeException(
                    "Payment amount is required"
            );
        }


        BigDecimal paymentAmount =
                dto.getAmount()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        if (paymentAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new RuntimeException(
                    "Payment amount must be greater than zero"
            );
        }


        // -----------------------------------------------------
        // GET PREVIOUS PAID AMOUNT
        // -----------------------------------------------------

        BigDecimal paidAmount =
                salePaymentRepo
                        .getTotalPaidAmount(
                                sale
                        );


        if (paidAmount == null) {

            paidAmount =
                    BigDecimal.ZERO;
        }


        paidAmount =
                paidAmount.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // -----------------------------------------------------
        // GET SALE TOTAL
        // -----------------------------------------------------

        BigDecimal totalAmount =
                sale.getTotalAmount();


        if (totalAmount == null) {

            throw new RuntimeException(
                    "Sale total amount is not available"
            );
        }


        totalAmount =
                totalAmount.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // -----------------------------------------------------
        // CALCULATE REMAINING AMOUNT
        // -----------------------------------------------------

        BigDecimal remainingAmount =
                totalAmount
                        .subtract(
                                paidAmount
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // -----------------------------------------------------
        // CHECK ALREADY FULLY PAID
        // -----------------------------------------------------

        if (remainingAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new RuntimeException(
                    "Sale is already fully paid"
            );
        }


        // -----------------------------------------------------
        // PREVENT OVER PAYMENT
        // -----------------------------------------------------

        if (paymentAmount.compareTo(
                remainingAmount
        ) > 0) {

            throw new RuntimeException(
                    "Payment amount cannot exceed remaining amount. "
                            + "Remaining amount: "
                            + remainingAmount
            );
        }


        // -----------------------------------------------------
        // CREATE SALE PAYMENT
        // -----------------------------------------------------

        SalePayment payment =
                new SalePayment();


        payment.setSale(
                sale
        );


        payment.setAmount(
                paymentAmount
        );


        payment.setPaymentMethod(
                dto.getPaymentMethod()
        );


        payment.setReferenceNumber(
                normalizeString(
                        dto.getReferenceNumber()
                )
        );


        payment.setPaymentDate(
                dto.getPaymentDate()
        );


        payment.setNotes(
                normalizeString(
                        dto.getNotes()
                )
        );


        // -----------------------------------------------------
        // PAYMENT STATUS
        // -----------------------------------------------------

        /*
         * Every newly created payment represents an
         * actual received payment.
         *
         * Therefore the payment itself is marked PAID.
         *
         * Sale.paymentStatus represents the overall
         * payment status of the sale.
         */

        payment.setPaymentStatus(
                SalePaymentStatus.PAID
        );


        // -----------------------------------------------------
        // GENERATE PAYMENT NUMBER
        // -----------------------------------------------------

        int year =
                dto.getPaymentDate()
                        .getYear();


        String paymentNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.SALE_PAYMENT,
                                year
                        );


        payment.setPaymentNumber(
                paymentNumber
        );


        // -----------------------------------------------------
        // SAVE PAYMENT
        // -----------------------------------------------------

        payment =
                salePaymentRepo.save(
                        payment
                );


        // -----------------------------------------------------
        // CALCULATE NEW PAID AMOUNT
        // -----------------------------------------------------

        BigDecimal newPaidAmount =
                paidAmount
                        .add(
                                paymentAmount
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // -----------------------------------------------------
        // CALCULATE NEW REMAINING AMOUNT
        // -----------------------------------------------------

        BigDecimal newRemainingAmount =
                totalAmount
                        .subtract(
                                newPaidAmount
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // -----------------------------------------------------
        // UPDATE SALE PAYMENT STATUS
        // -----------------------------------------------------

        SalePaymentStatus salePaymentStatus;


        if (newPaidAmount.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            salePaymentStatus =
                    SalePaymentStatus.PENDING;

        } else if (newRemainingAmount.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            salePaymentStatus =
                    SalePaymentStatus.PAID;

        } else {

            salePaymentStatus =
                    SalePaymentStatus.PARTIAL;
        }


        sale.setPaymentStatus(
                salePaymentStatus
        );


        saleRepo.save(
                sale
        );


        log.info(
                "SERVICE - sale payment added successfully. Payment Number: {}",
                payment.getPaymentNumber()
        );


        return mapToResponse(
                payment
        );
    }


    // =========================================================
    // GET PAYMENT BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseSalePaymentDTO getPaymentByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentByPublicId..."
        );


        if (publicId == null) {

            throw new RuntimeException(
                    "Payment public ID is required"
            );
        }


        SalePayment payment =
                salePaymentRepo
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sale payment not found"
                                )
                        );


        return mapToResponse(
                payment
        );
    }


    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO> getAllPayments() {

        log.info(
                "SERVICE - request came in getAllPayments..."
        );


        return salePaymentRepo
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET PAYMENTS BY SALE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO> getPaymentsBySale(
            UUID salePublicId
    ) {

        log.info(
                "SERVICE - request came in getPaymentsBySale..."
        );


        if (salePublicId == null) {

            throw new RuntimeException(
                    "Sale public ID is required"
            );
        }


        Sale sale =
                saleRepo
                        .findByPublicId(
                                salePublicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sale not found"
                                )
                        );


        return salePaymentRepo
                .findBySale(
                        sale
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET PAYMENTS BY DATE RANGE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSalePaymentDTO> getPaymentsByDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        log.info(
                "SERVICE - request came in getPaymentsByDateRange..."
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


        return salePaymentRepo
                .findByPaymentDateBetween(
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ResponseSalePaymentDTO mapToResponse(
            SalePayment payment
    ) {

        ResponseSalePaymentDTO response =
                modelMapper.map(
                        payment,
                        ResponseSalePaymentDTO.class
                );


        Sale sale =
                payment.getSale();


        if (sale != null) {

            ResponseSalePaymentDTO.SaleDetails
                    saleDetails =
                    new ResponseSalePaymentDTO.SaleDetails();


            // -------------------------------------------------
            // SALE IDENTIFICATION
            // -------------------------------------------------

            saleDetails.setPublicId(
                    sale.getPublicId()
            );


            saleDetails.setSaleNumber(
                    sale.getSaleNumber()
            );


            // -------------------------------------------------
            // SALE AMOUNT
            // -------------------------------------------------

            BigDecimal totalAmount =
                    sale.getTotalAmount();


            if (totalAmount == null) {

                totalAmount =
                        BigDecimal.ZERO;
            }


            totalAmount =
                    totalAmount.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );


            saleDetails.setTotalAmount(
                    totalAmount
            );


            // -------------------------------------------------
            // TOTAL PAID AMOUNT
            // -------------------------------------------------

            BigDecimal totalPaidAmount =
                    salePaymentRepo
                            .getTotalPaidAmount(
                                    sale
                            );


            if (totalPaidAmount == null) {

                totalPaidAmount =
                        BigDecimal.ZERO;
            }


            totalPaidAmount =
                    totalPaidAmount.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );


            saleDetails.setTotalPaidAmount(
                    totalPaidAmount
            );


            // -------------------------------------------------
            // REMAINING AMOUNT
            // -------------------------------------------------

            BigDecimal remainingAmount =
                    totalAmount
                            .subtract(
                                    totalPaidAmount
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            /*
             * Remaining amount should never be negative
             * in the response.
             */

            if (remainingAmount.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                remainingAmount =
                        BigDecimal.ZERO;
            }


            saleDetails.setRemainingAmount(
                    remainingAmount
            );


            // -------------------------------------------------
            // SALE PAYMENT STATUS
            // -------------------------------------------------

            saleDetails.setPaymentStatus(
                    sale.getPaymentStatus()
            );


            response.setSale(
                    saleDetails
            );
        }


        return response;
    }


    // =========================================================
    // NORMALIZE STRING
    // =========================================================

    private String normalizeString(
            String value
    ) {

        if (value == null) {

            return null;
        }


        String trimmed =
                value.trim();


        return trimmed.isBlank()
                ? null
                : trimmed;
    }
}