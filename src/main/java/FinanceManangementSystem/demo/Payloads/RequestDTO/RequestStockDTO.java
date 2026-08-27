package FinanceManangementSystem.demo.Payloads.RequestDTO;

import FinanceManangementSystem.demo.Enums.WeightUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RequestStockDTO {

    // --------------------------------------------------
    // Raw Material
    // --------------------------------------------------

    @NotBlank(
            message = "Raw material is required"
    )
    @Size(
            min = 2,
            max = 150,
            message = "Raw material must be between 2 and 150 characters"
    )
    private String rawMaterial;


    // --------------------------------------------------
    // Unit
    // --------------------------------------------------

    @NotNull(
            message = "Unit is required"
    )
    private WeightUnit unit;


    // --------------------------------------------------
    // Minimum Stock Level
    // --------------------------------------------------

    @NotNull(
            message = "Minimum stock level is required"
    )
    @DecimalMin(
            value = "0.000",
            inclusive = true,
            message = "Minimum stock level cannot be negative"
    )
    private BigDecimal minimumStockLevel;
}