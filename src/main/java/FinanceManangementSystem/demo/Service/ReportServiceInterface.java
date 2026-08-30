package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.ResponseDTO.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportServiceInterface {

    List<ResponseSaleReportDTO> getSalesReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    List<ResponsePurchaseReportDTO> getPurchaseReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    List<ResponseExpenseReportDTO> getExpenseReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    ResponseProfitLossReportDTO getProfitLossReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    List<ResponseStockReportDTO> getStockReport();

    List<ResponseCustomerOutstandingReportDTO> getCustomerOutstandingReport();

    List<ResponseSupplierOutstandingReportDTO> getSupplierOutstandingReport();
}