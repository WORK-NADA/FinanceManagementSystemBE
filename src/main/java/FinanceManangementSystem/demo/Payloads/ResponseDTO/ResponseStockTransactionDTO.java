package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.StockTransactionType;
import FinanceManangementSystem.demo.Enums.WeightUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ResponseStockTransactionDTO {

    // --------------------------------------------------
    // Transaction Identification
    // --------------------------------------------------

    private UUID publicId;


    // --------------------------------------------------
    // Stock Information
    // --------------------------------------------------

    private UUID stockPublicId;

    private String rawMaterial;


    // --------------------------------------------------
    // Transaction Details
    // --------------------------------------------------

    private StockTransactionType transactionType;

    private BigDecimal quantity;

    private WeightUnit unit;


    // --------------------------------------------------
    // Reference
    // --------------------------------------------------

    private String referenceNumber;


    // --------------------------------------------------
    // Date
    // --------------------------------------------------

    private LocalDateTime transactionDate;


    // --------------------------------------------------
    // Remarks
    // --------------------------------------------------

    private String remarks;


    // --------------------------------------------------
    // Audit
    // --------------------------------------------------

    private LocalDateTime createdAt;
}