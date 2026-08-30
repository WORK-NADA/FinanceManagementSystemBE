package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import java.math.BigDecimal;

public class ResponseProfitLossReportDTO {

    private BigDecimal totalRevenue;
    private BigDecimal totalPurchaseCost;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;

    public ResponseProfitLossReportDTO() {}

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalPurchaseCost() { return totalPurchaseCost; }
    public void setTotalPurchaseCost(BigDecimal totalPurchaseCost) { this.totalPurchaseCost = totalPurchaseCost; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
}
