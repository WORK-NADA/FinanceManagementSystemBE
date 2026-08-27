package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RequestMinimumStockLevelDTO {

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