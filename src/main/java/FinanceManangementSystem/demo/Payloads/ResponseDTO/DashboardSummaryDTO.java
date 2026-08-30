package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import java.math.BigDecimal;

public class DashboardSummaryDTO {

    private BigDecimal totalOutstanding;
    private BigDecimal totalReceivable;
    private BigDecimal totalExpensesThisMonth;
    private int lowStockCount;
    private ResponseProfitDistributionDTO latestProfitDistribution;

    public DashboardSummaryDTO() {}

    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }
    public BigDecimal getTotalReceivable() { return totalReceivable; }
    public void setTotalReceivable(BigDecimal totalReceivable) { this.totalReceivable = totalReceivable; }
    public BigDecimal getTotalExpensesThisMonth() { return totalExpensesThisMonth; }
    public void setTotalExpensesThisMonth(BigDecimal totalExpensesThisMonth) { this.totalExpensesThisMonth = totalExpensesThisMonth; }
    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }
    public ResponseProfitDistributionDTO getLatestProfitDistribution() { return latestProfitDistribution; }
    public void setLatestProfitDistribution(ResponseProfitDistributionDTO latestProfitDistribution) { this.latestProfitDistribution = latestProfitDistribution; }
}
