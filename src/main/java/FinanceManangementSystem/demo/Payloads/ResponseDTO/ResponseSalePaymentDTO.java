package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.PaymentMethod;
import FinanceManangementSystem.demo.Enums.SalePaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ResponseSalePaymentDTO {

    // =========================================================
    // PAYMENT
    // =========================================================

    private UUID publicId;

    private String paymentNumber;


    // =========================================================
    // SALE
    // =========================================================

    private SaleDetails sale;


    // =========================================================
    // PAYMENT DETAILS
    // =========================================================

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private String referenceNumber;

    private LocalDate paymentDate;

    private String notes;


    // =========================================================
    // AUDIT
    // =========================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =========================================================
    // SALE DETAILS
    // =========================================================

    @Getter
    @Setter
    public static class SaleDetails {

        // -----------------------------------------------------
        // SALE IDENTIFICATION
        // -----------------------------------------------------

        private UUID publicId;

        private String saleNumber;


        // -----------------------------------------------------
        // SALE AMOUNT
        // -----------------------------------------------------

        private BigDecimal totalAmount;

        private BigDecimal totalPaidAmount;

        private BigDecimal remainingAmount;


        // -----------------------------------------------------
        // PAYMENT STATUS
        // -----------------------------------------------------

        private SalePaymentStatus paymentStatus;
    }
}