package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Enums.SaleStatus;
import FinanceManangementSystem.demo.Model.Customer;
import FinanceManangementSystem.demo.Model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleRepository
        extends JpaRepository<Sale, Long> {

    // =========================================================
    // FIND BY PUBLIC ID
    // =========================================================

    Optional<Sale> findByPublicId(
            UUID publicId
    );


    // =========================================================
    // FIND ACTIVE SALE BY PUBLIC ID
    // =========================================================

    Optional<Sale> findByPublicIdAndSaleStatus(
            UUID publicId,
            SaleStatus saleStatus
    );


    // =========================================================
    // FIND SALES BY CUSTOMER
    // =========================================================

    List<Sale> findByCustomer(
            Customer customer
    );


    // =========================================================
    // FIND SALES BY CUSTOMER AND STATUS
    // =========================================================

    List<Sale> findByCustomerAndSaleStatus(
            Customer customer,
            SaleStatus saleStatus
    );


    // =========================================================
    // FIND SALES BY DATE RANGE
    // =========================================================

    List<Sale> findBySaleDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );


    // =========================================================
    // FIND SALES BY STATUS
    // =========================================================

    List<Sale> findBySaleStatus(
            SaleStatus saleStatus
    );


    // =========================================================
    // CHECK CUSTOMER INVOICE NUMBER
    // =========================================================

    boolean existsByCustomerInvoiceNumberAndCustomer(
            String customerInvoiceNumber,
            Customer customer
    );


    // =========================================================
    // CHECK CUSTOMER INVOICE NUMBER DURING UPDATE
    // =========================================================

    boolean existsByCustomerInvoiceNumberAndCustomerAndPublicIdNot(
            String customerInvoiceNumber,
            Customer customer,
            UUID publicId
    );


    // =========================================================
    // CHECK SALE NUMBER
    // =========================================================

    boolean existsBySaleNumber(
            String saleNumber
    );
}