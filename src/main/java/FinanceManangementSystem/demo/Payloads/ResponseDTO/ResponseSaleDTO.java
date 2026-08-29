package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.SalePaymentStatus;
import FinanceManangementSystem.demo.Enums.SaleStatus;
import FinanceManangementSystem.demo.Enums.WeightUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
    // PRODUCT
    // =========================================================

    private String product;

    private BigDecimal weight;

    private WeightUnit unit;


    // =========================================================
    // PRICE
    // =========================================================

    private BigDecimal ratePerUnit;

    private BigDecimal gstPercentage;

    private BigDecimal amount;

    private BigDecimal gstAmount;

    private BigDecimal totalAmount;


    // =========================================================
    // CUSTOMER INVOICE
    // =========================================================

    private String customerInvoiceNumber;


    // =========================================================
    // DATE
    // =========================================================

    private LocalDate saleDate;


    // =========================================================
    // STATUS
    // =========================================================

    private SaleStatus saleStatus;

    private SalePaymentStatus paymentStatus;


    // =========================================================
    // PAYMENT SUMMARY
    // =========================================================

    /*
     * Total amount already received
     * against this sale.
     */
    private BigDecimal paidAmount;


    /*
     * Remaining amount that the customer
     * still has to pay.
     */
    private BigDecimal remainingAmount;


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
    public static class CustomerDetails {

        private UUID publicId;

        private String customerName;

        private String mobileNumber;

        private String email;

        private String gstNumber;
    }
}