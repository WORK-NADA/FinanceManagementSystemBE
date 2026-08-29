package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Enums.SalePaymentStatus;
import FinanceManangementSystem.demo.Model.Sale;
import FinanceManangementSystem.demo.Model.SalePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalePaymentRepository
        extends JpaRepository<SalePayment, Long> {

    // =========================================================
    // FIND BY PUBLIC ID
    // =========================================================

    Optional<SalePayment> findByPublicId(
            UUID publicId
    );


    // =========================================================
    // FIND BY SALE
    // =========================================================

    List<SalePayment> findBySaleOrderByPaymentDateDesc(
            Sale sale
    );


    // =========================================================
    // FIND BY SALE AND STATUS
    // =========================================================

    List<SalePayment> findBySaleAndPaymentStatusOrderByPaymentDateDesc(
            Sale sale,
            SalePaymentStatus paymentStatus
    );


    // =========================================================
    // CHECK PAYMENT NUMBER
    // =========================================================

    boolean existsByPaymentNumber(
            String paymentNumber
    );


    // =========================================================
    // TOTAL PAID AMOUNT FOR SALE
    // =========================================================

    /*
     * Only PAID payments are included.
     *
     * In this project, every successfully created payment
     * represents money received from the customer and is
     * therefore stored with PAID status.
     *
     * CANCELLED payments must never contribute to the
     * received amount.
     */

    @Query(
            """
            SELECT COALESCE(SUM(p.amount), 0)
            FROM SalePayment p
            WHERE p.sale = :sale
              AND p.paymentStatus = :paymentStatus
            """
    )
    BigDecimal getTotalPaidAmount(
            @Param("sale")
            Sale sale,

            @Param("paymentStatus")
            SalePaymentStatus paymentStatus
    );


    // =========================================================
    // CHECK PAYMENT REFERENCE
    // =========================================================

    boolean existsByReferenceNumber(
            String referenceNumber
    );
}