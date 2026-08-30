package FinanceManangementSystem.demo.Payloads.RequestDTO;

import FinanceManangementSystem.demo.Enums.WeightUnit;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSaleDTO {

    // =========================================================
    // CUSTOMER
    // =========================================================

    @NotNull(message = "Customer is required")
    private UUID customerPublicId;


    // =========================================================
    // RAW MATERIAL
    // =========================================================

    @NotBlank(message = "Raw material is required")
    @Size(
            min = 2,
            max = 100,
            message = "Raw material must be between 2 and 100 characters"
    )
    private String rawMaterial;


    // =========================================================
    // QUANTITY
    // =========================================================

    @NotNull(message = "Weight is required")
    @DecimalMin(
            value = "0.001",
            message = "Weight must be greater than zero"
    )
    private BigDecimal weight;


    @NotNull(message = "Weight unit is required")
    private WeightUnit unit;


    // =========================================================
    // RATE
    // =========================================================

    @NotNull(message = "Rate per unit is required")
    @DecimalMin(
            value = "0.01",
            message = "Rate per unit must be greater than zero"
    )
    private BigDecimal ratePerUnit;


    // =========================================================
    // GST
    // =========================================================

    @NotNull(message = "GST percentage is required")
    @DecimalMin(
            value = "0",
            message = "GST percentage cannot be negative"
    )
    @DecimalMax(
            value = "100",
            message = "GST percentage cannot exceed 100"
    )
    private BigDecimal gstPercentage;


    // =========================================================
    // CUSTOMER INVOICE
    // =========================================================

    @Size(
            max = 50,
            message = "Customer invoice number cannot exceed 50 characters"
    )
    private String customerInvoiceNumber;


    // =========================================================
    // SALE DATE
    // =========================================================

    @NotNull(message = "Sale date is required")
    private LocalDate saleDate;
}
