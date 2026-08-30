package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import FinanceManangementSystem.demo.Enums.WeightUnit;

import java.math.BigDecimal;
import java.util.UUID;

public class ResponseStockReportDTO {

    private UUID publicId;
    private String rawMaterial;
    private WeightUnit unit;
    private BigDecimal currentQuantity;
    private BigDecimal minimumStockLevel;

    public ResponseStockReportDTO() {}

    public UUID getPublicId() { return publicId; }
    public void setPublicId(UUID publicId) { this.publicId = publicId; }
    public String getRawMaterial() { return rawMaterial; }
    public void setRawMaterial(String rawMaterial) { this.rawMaterial = rawMaterial; }
    public WeightUnit getUnit() { return unit; }
    public void setUnit(WeightUnit unit) { this.unit = unit; }
    public BigDecimal getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(BigDecimal currentQuantity) { this.currentQuantity = currentQuantity; }
    public BigDecimal getMinimumStockLevel() { return minimumStockLevel; }
    public void setMinimumStockLevel(BigDecimal minimumStockLevel) { this.minimumStockLevel = minimumStockLevel; }
}
