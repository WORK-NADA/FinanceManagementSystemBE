package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.Expense;
import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.Sale;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSaleReportDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseReportDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseExpenseReportDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseProfitLossReportDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseStockReportDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseCustomerOutstandingReportDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierOutstandingReportDTO;
import FinanceManangementSystem.demo.Repository.ExpenseRepository;
import FinanceManangementSystem.demo.Repository.PurchaseRepository;
import FinanceManangementSystem.demo.Repository.SaleRepository;
import FinanceManangementSystem.demo.Repository.StockRepository;
import FinanceManangementSystem.demo.Service.ReportServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService implements ReportServiceInterface {

    private final CurrentUserService currentUserService;

    private final SaleRepository saleRepo;

    private final PurchaseRepository purchaseRepo;

    private final ExpenseRepository expenseRepo;

    private final StockRepository stockRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSaleReportDTO> getSalesReport(LocalDate fromDate, LocalDate toDate) {
        log.info("SERVICE - request came in getSalesReport...");
        User currentUser = currentUserService.getCurrentUser();
        List<Sale> sales = saleRepo.findByUserAndSaleDateBetween(currentUser, fromDate, toDate);

        return sales.stream().map(s -> {
            ResponseSaleReportDTO dto = new ResponseSaleReportDTO();
            dto.setPublicId(s.getPublicId());
            dto.setSaleDate(s.getSaleDate());
            dto.setTotalAmount(s.getTotalAmount());
            dto.setPaymentStatus(s.getPaymentStatus());
            dto.setCustomerName(s.getCustomer() != null ? s.getCustomer().getCustomerName() : null);
            return dto;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ResponsePurchaseReportDTO> getPurchaseReport(LocalDate fromDate, LocalDate toDate) {
        log.info("SERVICE - request came in getPurchaseReport...");
        User currentUser = currentUserService.getCurrentUser();
        List<Purchase> purchases = purchaseRepo.findByUserAndPurchaseDateBetween(currentUser, fromDate, toDate);

        return purchases.stream().map(p -> {
            ResponsePurchaseReportDTO dto = new ResponsePurchaseReportDTO();
            dto.setPublicId(p.getPublicId());
            dto.setPurchaseDate(p.getPurchaseDate());
            dto.setTotalAmount(p.getTotalAmount());
            dto.setPaymentStatus(p.getPaymentStatus());
            dto.setSupplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : null);
            return dto;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ResponseExpenseReportDTO> getExpenseReport(LocalDate fromDate, LocalDate toDate) {
        log.info("SERVICE - request came in getExpenseReport...");
        User currentUser = currentUserService.getCurrentUser();
        return expenseRepo.findByUserAndExpenseDateBetweenAndIsActiveTrueOrderByExpenseDateDesc(currentUser, fromDate, toDate)
                .stream()
                .map(e -> {
                    ResponseExpenseReportDTO dto = new ResponseExpenseReportDTO();
                    dto.setPublicId(e.getPublicId());
                    dto.setExpenseDate(e.getExpenseDate());
                    dto.setAmount(e.getAmount());
                    dto.setDescription(e.getDescription());
                    return dto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseProfitLossReportDTO getProfitLossReport(LocalDate fromDate, LocalDate toDate) {
        log.info("SERVICE - request came in getProfitLossReport...");

        User currentUser = currentUserService.getCurrentUser();

        BigDecimal totalRevenue = saleRepo.findByUserAndSaleDateBetween(currentUser, fromDate, toDate)
                .stream()
                .map(s -> s.getTotalAmount() == null ? BigDecimal.ZERO : s.getTotalAmount())
                .reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value == null ? BigDecimal.ZERO : value));

        BigDecimal totalPurchase = purchaseRepo.findByUserAndPurchaseDateBetween(currentUser, fromDate, toDate)
                .stream()
                .map(p -> p.getTotalAmount() == null ? BigDecimal.ZERO : p.getTotalAmount())
                .reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value == null ? BigDecimal.ZERO : value));

        BigDecimal totalExpenses = expenseRepo.findByUserAndExpenseDateBetweenAndIsActiveTrueOrderByExpenseDateDesc(currentUser, fromDate, toDate)
                .stream()
                .map(e -> e.getAmount() == null ? BigDecimal.ZERO : e.getAmount())
                .reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value == null ? BigDecimal.ZERO : value));
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal netProfit = totalRevenue.subtract(totalPurchase).subtract(totalExpenses);

        ResponseProfitLossReportDTO resp = new ResponseProfitLossReportDTO();
        resp.setTotalRevenue(totalRevenue);
        resp.setTotalPurchaseCost(totalPurchase);
        resp.setTotalExpenses(totalExpenses);
        resp.setNetProfit(netProfit);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ResponseStockReportDTO> getStockReport() {
        log.info("SERVICE - request came in getStockReport...");
        User currentUser = currentUserService.getCurrentUser();
        return stockRepo.findByUser(currentUser)
                .stream()
                .map(s -> {
                    ResponseStockReportDTO dto = new ResponseStockReportDTO();
                    dto.setPublicId(s.getPublicId());
                    dto.setRawMaterial(s.getRawMaterial());
                    dto.setUnit(s.getUnit());
                    dto.setCurrentQuantity(s.getCurrentQuantity());
                    dto.setMinimumStockLevel(s.getMinimumStockLevel());
                    return dto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ResponseCustomerOutstandingReportDTO> getCustomerOutstandingReport() {
        log.info("SERVICE - request came in getCustomerOutstandingReport...");
        User currentUser = currentUserService.getCurrentUser();
        // Outstanding sales: PENDING or PARTIALLY_PAID
        return saleRepo.findByUserAndPaymentStatusIn(currentUser, List.of(
                FinanceManangementSystem.demo.Enums.PaymentStatus.PENDING,
                FinanceManangementSystem.demo.Enums.PaymentStatus.PARTIALLY_PAID
        )).stream().map(s -> {
            ResponseCustomerOutstandingReportDTO dto = new ResponseCustomerOutstandingReportDTO();
            dto.setCustomerPublicId(s.getCustomer() != null ? s.getCustomer().getPublicId() : null);
            dto.setCustomerName(s.getCustomer() != null ? s.getCustomer().getCustomerName() : null);
            dto.setOutstandingAmount(s.getTotalAmount());
            return dto;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ResponseSupplierOutstandingReportDTO> getSupplierOutstandingReport() {
        log.info("SERVICE - request came in getSupplierOutstandingReport...");
        User currentUser = currentUserService.getCurrentUser();
        return purchaseRepo.findByUserAndPaymentStatusIn(currentUser, List.of(
                FinanceManangementSystem.demo.Enums.PaymentStatus.PENDING,
                FinanceManangementSystem.demo.Enums.PaymentStatus.PARTIALLY_PAID
        )).stream().map(p -> {
            ResponseSupplierOutstandingReportDTO dto = new ResponseSupplierOutstandingReportDTO();
            dto.setSupplierPublicId(p.getSupplier() != null ? p.getSupplier().getPublicId() : null);
            dto.setSupplierName(p.getSupplier() != null ? p.getSupplier().getSupplierName() : null);
            dto.setOutstandingAmount(p.getTotalAmount());
            return dto;
        }).toList();
    }
}
