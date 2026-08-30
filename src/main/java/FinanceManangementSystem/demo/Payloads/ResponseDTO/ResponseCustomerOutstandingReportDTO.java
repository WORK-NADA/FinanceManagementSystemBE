package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import java.math.BigDecimal;
import java.util.UUID;

public class ResponseCustomerOutstandingReportDTO {

    private UUID customerPublicId;
    private String customerName;
    private BigDecimal outstandingAmount;

    public ResponseCustomerOutstandingReportDTO() {}

    public UUID getCustomerPublicId() { return customerPublicId; }
    public void setCustomerPublicId(UUID customerPublicId) { this.customerPublicId = customerPublicId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
}
