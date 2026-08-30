package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ResponseExpenseReportDTO {

    private UUID publicId;
    private LocalDate expenseDate;
    private BigDecimal amount;
    private String description;

    public ResponseExpenseReportDTO() {}

    public UUID getPublicId() { return publicId; }
    public void setPublicId(UUID publicId) { this.publicId = publicId; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
