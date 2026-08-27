package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.ResponseDTO.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportServiceInterface {

    Object getSalesReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    Object getPurchaseReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    Object getExpenseReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    Object getProfitLossReport(
            LocalDate fromDate,
            LocalDate toDate
    );

    Object getStockReport();

    Object getCustomerOutstandingReport();

    Object getSupplierOutstandingReport();
}