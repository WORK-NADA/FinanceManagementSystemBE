package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Payloads.ResponseDTO.DashboardSummaryDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseProfitDistributionDTO;
import FinanceManangementSystem.demo.Service.DashboardServiceInterface;
import FinanceManangementSystem.demo.Service.ExpenseServiceInterface;
import FinanceManangementSystem.demo.Service.PurchasePaymentServiceInterface;
import FinanceManangementSystem.demo.Service.SalePaymentServiceInterface;
import FinanceManangementSystem.demo.Service.StockServiceInterface;
import FinanceManangementSystem.demo.Service.ProfitDistributionServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardServiceInterface {

    private final PurchasePaymentServiceInterface purchasePaymentService;
    private final SalePaymentServiceInterface salePaymentService;
    private final ExpenseServiceInterface expenseService;
    private final StockServiceInterface stockService;
    private final ProfitDistributionServiceInterface profitDistributionService;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDTO getDashboardSummary() {
        log.info("SERVICE - request came in getDashboardSummary...");

        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        dto.setTotalOutstanding(purchasePaymentService.getTotalOutstandingAmount());
        dto.setTotalReceivable(salePaymentService.getTotalReceivableAmount());

        YearMonth ym = YearMonth.now();
        LocalDate from = ym.atDay(1);
        LocalDate to = LocalDate.now();

        dto.setTotalExpensesThisMonth(expenseService.getTotalExpenses(from, to));

        dto.setLowStockCount(stockService.getLowStockList().size());

        ResponseProfitDistributionDTO latest = profitDistributionService.getLatestDistribution();
        dto.setLatestProfitDistribution(latest);

        return dto;
    }
}
