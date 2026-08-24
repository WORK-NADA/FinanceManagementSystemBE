package FinanceManangementSystem.demo.Payloads.RequestDTO;

import FinanceManangementSystem.demo.Enums.WeightUnit;
import jakarta.validation.constraints.*;
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
public class RequestPurchaseDTO {

    @NotNull(message = "Supplier is required")
    private UUID supplierPublicId;


    @NotBlank(message = "Raw material is required")
    @Size(
            min = 2,
            max = 150,
            message = "Raw material must be between 2 and 150 characters"
    )
    private String rawMaterial;


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


    @NotNull(message = "Weight unit is required")
    private WeightUnit unit;


    @NotNull(message = "Rate per Kg is required")
    @DecimalMin(
            value = "0.01",
            message = "Rate per Kg must be greater than zero"
    )
    @Digits(
            integer = 13,
            fraction = 2,
            message = "Rate per Kg can have maximum 13 integer digits and 2 decimal places"
    )
    private BigDecimal ratePerUnit;


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
            message = "Invalid GST percentage"
    )
    private BigDecimal gstPercentage =
            new BigDecimal("18.00");


    @Size(
            max = 50,
            message = "Supplier invoice number cannot exceed 50 characters"
    )
    private String supplierInvoiceNumber;


    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;
}