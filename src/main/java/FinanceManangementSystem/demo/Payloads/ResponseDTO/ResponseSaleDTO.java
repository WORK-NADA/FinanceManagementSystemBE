package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Enums.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSaleDTO {

    // =========================================================
    // SALE
    // =========================================================

    private UUID publicId;

    private String saleNumber;


    // =========================================================
    // CUSTOMER
    // =========================================================

    private CustomerDetails customer;


    // =========================================================
    // RAW MATERIAL
    // =========================================================

    private String rawMaterial;


    // =========================================================
    // QUANTITY
    // =========================================================

    private BigDecimal weight;

    private WeightUnit unit;


    // =========================================================
    // RATE
    // =========================================================

    private BigDecimal ratePerUnit;


    // =========================================================
    // GST
    // =========================================================

    private BigDecimal gstPercentage;

    private BigDecimal amount;

    private BigDecimal gstAmount;

    private BigDecimal totalAmount;


    // =========================================================
    // CUSTOMER INVOICE
    // =========================================================

    private String customerInvoiceNumber;


    // =========================================================
    // SALE DATE
    // =========================================================

    private LocalDate saleDate;


    // =========================================================
    // STATUS
    // =========================================================

    private PaymentStatus paymentStatus;


    // =========================================================
    // AUDIT
    // =========================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =========================================================
    // CUSTOMER DETAILS
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetails {

        private UUID publicId;

        private String customerName;

        private String mobileNumber;

        private String email;

        private String gstNumber;
    }
}
