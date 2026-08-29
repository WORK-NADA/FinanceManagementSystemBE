package FinanceManangementSystem.demo.Payloads.RequestDTO;

import FinanceManangementSystem.demo.Enums.WeightUnit;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class RequestSaleDTO {

    // =========================================================
    // CUSTOMER
    // =========================================================

    @NotNull(message = "Customer is required")
    private UUID customerPublicId;


    // =========================================================
    // PRODUCT
    // =========================================================

    @NotBlank(message = "Product is required")
    @Size(
            min = 2,
            max = 150,
            message = "Product must be between 2 and 150 characters"
    )
    private String product;


    // =========================================================
    // WEIGHT
    // =========================================================

    @NotNull(message = "Weight is required")
    @DecimalMin(
            value = "0.001",
            message = "Weight must be greater than zero"
    )
    @Digits(
            integer = 12,
            fraction = 3,
            message = "Weight can have maximum 12 integer digits and 3 decimal places"
    )
    private BigDecimal weight;


    // =========================================================
    // UNIT
    // =========================================================

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
    @Digits(
            integer = 13,
            fraction = 2,
            message = "Rate per unit can have maximum 13 integer digits and 2 decimal places"
    )
    private BigDecimal ratePerUnit;


    // =========================================================
    // GST
    // =========================================================

    @NotNull(message = "GST percentage is required")
    @DecimalMin(
            value = "0.00",
            message = "GST percentage cannot be negative"
    )
    @DecimalMax(
            value = "100.00",
            message = "GST percentage cannot exceed 100"
    )
    @Digits(
            integer = 3,
            fraction = 2,
            message = "GST percentage can have maximum 3 integer digits and 2 decimal places"
    )
    private BigDecimal gstPercentage;


    // =========================================================
    // CUSTOMER INVOICE NUMBER
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