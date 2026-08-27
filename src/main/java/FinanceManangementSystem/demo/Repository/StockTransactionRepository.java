package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Enums.StockTransactionType;
import FinanceManangementSystem.demo.Model.Stock;
import FinanceManangementSystem.demo.Model.StockTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockTransactionRepository
        extends JpaRepository<StockTransaction, Long> {

    // =========================================================
    // FIND BY PUBLIC ID
    // =========================================================

    Optional<StockTransaction> findByPublicId(
            UUID publicId
    );


    // =========================================================
    // FIND TRANSACTIONS BY STOCK
    // =========================================================

    List<StockTransaction>
    findByStockOrderByTransactionDateDesc(
            Stock stock
    );


    // =========================================================
    // FIND TRANSACTIONS BY STOCK PUBLIC ID
    // =========================================================

    List<StockTransaction>
    findByStockPublicIdOrderByTransactionDateDesc(
            UUID stockPublicId
    );


    // =========================================================
    // FIND BY STOCK AND TRANSACTION TYPE
    // =========================================================

    List<StockTransaction>
    findByStockAndTransactionType(
            Stock stock,
            StockTransactionType transactionType
    );


    // =========================================================
    // FIND BY STOCK, TYPE AND DATE RANGE
    // =========================================================

    List<StockTransaction>
    findByStockAndTransactionTypeAndTransactionDateBetween(
            Stock stock,
            StockTransactionType transactionType,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );


    // =========================================================
    // FIND BY DATE RANGE
    // =========================================================

    List<StockTransaction>
    findByTransactionDateBetween(
            LocalDateTime fromDate,
            LocalDateTime toDate
    );


    // =========================================================
    // FIND BY TRANSACTION TYPE
    // =========================================================

    List<StockTransaction>
    findByTransactionType(
            StockTransactionType transactionType
    );


    // =========================================================
    // FIND BY REFERENCE NUMBER
    // =========================================================

    List<StockTransaction>
    findByReferenceNumber(
            String referenceNumber
    );


    // =========================================================
    // CHECK DUPLICATE TRANSACTION
    // =========================================================
    /*
     * The combination of referenceNumber + transactionType
     * identifies one stock transaction operation.
     *
     * Example:
     *
     * PURCHASE_IN + PUR-2026-000001
     * PURCHASE_CANCEL_OUT + PUR-2026-000001
     *
     * These are both allowed because their transaction
     * types are different.
     *
     * But:
     *
     * PURCHASE_IN + PUR-2026-000001
     * PURCHASE_IN + PUR-2026-000001
     *
     * is not allowed.
     */

    boolean existsByReferenceNumberAndTransactionType(
            String referenceNumber,
            StockTransactionType transactionType
    );


    // =========================================================
    // LATEST TRANSACTIONS
    // =========================================================

    List<StockTransaction>
    findByOrderByTransactionDateDesc(
            Pageable pageable
    );
}