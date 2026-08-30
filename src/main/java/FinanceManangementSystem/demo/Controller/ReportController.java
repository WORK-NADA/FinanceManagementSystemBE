package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.*;
import FinanceManangementSystem.demo.Service.ReportServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportServiceInterface reportService;

    @GetMapping("/sales")
    public ResponseEntity<APIResponse<List<ResponseSaleReportDTO>>> getSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("CONTROLLER - request came in getSalesReport...");
        var res = reportService.getSalesReport(from, to);
        return ResponseEntity.ok(new APIResponse<>("Sales report", res));
    }

    @GetMapping("/purchases")
    public ResponseEntity<APIResponse<List<ResponsePurchaseReportDTO>>> getPurchases(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("CONTROLLER - request came in getPurchaseReport...");
        var res = reportService.getPurchaseReport(from, to);
        return ResponseEntity.ok(new APIResponse<>("Purchase report", res));
    }

    @GetMapping("/expenses")
    public ResponseEntity<APIResponse<List<ResponseExpenseReportDTO>>> getExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("CONTROLLER - request came in getExpenseReport...");
        var res = reportService.getExpenseReport(from, to);
        return ResponseEntity.ok(new APIResponse<>("Expense report", res));
    }

    @GetMapping("/profit-loss")
    public ResponseEntity<APIResponse<ResponseProfitLossReportDTO>> getProfitLoss(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("CONTROLLER - request came in getProfitLossReport...");
        var res = reportService.getProfitLossReport(from, to);
        return ResponseEntity.ok(new APIResponse<>("Profit & Loss report", res));
    }

    @GetMapping("/stock")
    public ResponseEntity<APIResponse<List<ResponseStockReportDTO>>> getStock() {
        log.info("CONTROLLER - request came in getStockReport...");
        var res = reportService.getStockReport();
        return ResponseEntity.ok(new APIResponse<>("Stock report", res));
    }

    @GetMapping("/customers/outstanding")
    public ResponseEntity<APIResponse<List<ResponseCustomerOutstandingReportDTO>>> getCustomerOutstanding() {
        log.info("CONTROLLER - request came in getCustomerOutstandingReport...");
        var res = reportService.getCustomerOutstandingReport();
        return ResponseEntity.ok(new APIResponse<>("Customer outstanding report", res));
    }

    @GetMapping("/suppliers/outstanding")
    public ResponseEntity<APIResponse<List<ResponseSupplierOutstandingReportDTO>>> getSupplierOutstanding() {
        log.info("CONTROLLER - request came in getSupplierOutstandingReport...");
        var res = reportService.getSupplierOutstandingReport();
        return ResponseEntity.ok(new APIResponse<>("Supplier outstanding report", res));
    }
}
