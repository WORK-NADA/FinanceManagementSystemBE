package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import java.math.BigDecimal;
import java.util.UUID;

public class ResponseSupplierOutstandingReportDTO {

    private UUID supplierPublicId;
    private String supplierName;
    private BigDecimal outstandingAmount;

    public ResponseSupplierOutstandingReportDTO() {}

    public UUID getSupplierPublicId() { return supplierPublicId; }
    public void setSupplierPublicId(UUID supplierPublicId) { this.supplierPublicId = supplierPublicId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
}
