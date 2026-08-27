package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.WeightUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ResponseStockDTO {

    // --------------------------------------------------
    // Stock Identification
    // --------------------------------------------------

    private UUID publicId;


    // --------------------------------------------------
    // Stock Details
    // --------------------------------------------------

    private String rawMaterial;

    private WeightUnit unit;


    // --------------------------------------------------
    // Quantity
    // --------------------------------------------------

    private BigDecimal currentQuantity;

    private BigDecimal minimumStockLevel;


    // --------------------------------------------------
    // Stock Status
    // --------------------------------------------------

    private Boolean isLowStock;

    private Boolean isActive;


    // --------------------------------------------------
    // Audit
    // --------------------------------------------------

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}