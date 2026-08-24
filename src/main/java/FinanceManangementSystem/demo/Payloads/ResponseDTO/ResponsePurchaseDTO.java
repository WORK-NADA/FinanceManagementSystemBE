package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Enums.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class ResponsePurchaseDTO {

    private UUID publicId;

    private String purchaseNumber;

    private UUID supplierPublicId;

    private String supplierName;

    private String rawMaterial;

    private BigDecimal weight;

    private WeightUnit unit;

    private BigDecimal ratePerUnit;

    private BigDecimal gstPercentage;

    private BigDecimal amount;

    private BigDecimal gstAmount;

    private BigDecimal totalAmount;

    private String supplierInvoiceNumber;

    private LocalDate purchaseDate;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}