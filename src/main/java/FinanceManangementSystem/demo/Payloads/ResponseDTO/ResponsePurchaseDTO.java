package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Enums.PurchaseStatus;
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
public class ResponsePurchaseDTO {

    // =========================================================
    // PURCHASE
    // =========================================================

    private UUID publicId;

    private String purchaseNumber;


    // =========================================================
    // SUPPLIER
    // =========================================================

    private SupplierDetails supplier;


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
    // SUPPLIER INVOICE
    // =========================================================

    /*
     * This field is optional.
     *
     * If supplier does not provide an invoice number,
     * this value will be null.
     */

    private String supplierInvoiceNumber;


    // =========================================================
    // PURCHASE DATE
    // =========================================================

    private LocalDate purchaseDate;


    // =========================================================
    // STATUS
    // =========================================================

    private PurchaseStatus purchaseStatus;

    private PaymentStatus paymentStatus;


    // =========================================================
    // AUDIT
    // =========================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =========================================================
    // SUPPLIER DETAILS
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierDetails {

        private UUID publicId;

        private String supplierName;

        private String mobileNumber;

        private String email;

        private String gstNumber;
    }
}