package FinanceManangementSystem.demo.Payloads.RequestDTO;

import FinanceManangementSystem.demo.Enums.StockTransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RequestStockTransactionDTO {

    // --------------------------------------------------
    // Stock
    // --------------------------------------------------

    @NotNull(
            message = "Stock public ID is required"
    )
    private UUID stockPublicId;


    // --------------------------------------------------
    // Transaction Type
    // --------------------------------------------------

    @NotNull(
            message = "Transaction type is required"
    )
    private StockTransactionType transactionType;


    // --------------------------------------------------
    // Quantity
    // --------------------------------------------------

    @NotNull(
            message = "Quantity is required"
    )
    @DecimalMin(
            value = "0.001",
            inclusive = true,
            message = "Quantity must be greater than zero"
    )
    private BigDecimal quantity;


    // --------------------------------------------------
    // Remarks
    // --------------------------------------------------

    @Size(
            max = 500,
            message = "Remarks cannot exceed 500 characters"
    )
    private String remarks;
}