package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Enums.WeightUnit;
import FinanceManangementSystem.demo.Model.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository
        extends JpaRepository<Stock, Long> {

    // =========================================================
    // FIND BY PUBLIC ID
    // =========================================================

    Optional<Stock> findByPublicId(
            UUID publicId
    );


    // =========================================================
    // FIND ACTIVE STOCK BY PUBLIC ID
    // =========================================================

    Optional<Stock> findByPublicIdAndIsActiveTrue(
            UUID publicId
    );


    // =========================================================
    // FIND BY RAW MATERIAL AND UNIT
    // =========================================================

    Optional<Stock> findByRawMaterialIgnoreCaseAndUnit(
            String rawMaterial,
            WeightUnit unit
    );


    // =========================================================
    // CHECK EXISTING STOCK
    // =========================================================

    boolean existsByRawMaterialIgnoreCaseAndUnit(
            String rawMaterial,
            WeightUnit unit
    );


    // =========================================================
    // ACTIVE STOCKS
    // =========================================================

    List<Stock> findByIsActiveTrue();


    // =========================================================
    // INACTIVE STOCKS
    // =========================================================

    List<Stock> findByIsActiveFalse();


    // =========================================================
    // ALL STOCKS SEARCH
    // =========================================================

    List<Stock> findByRawMaterialContainingIgnoreCase(
            String rawMaterial
    );


    // =========================================================
    // ACTIVE STOCK SEARCH
    // =========================================================

    List<Stock>
    findByRawMaterialContainingIgnoreCaseAndIsActiveTrue(
            String rawMaterial
    );


    // =========================================================
    // FIND STOCK WITH PESSIMISTIC WRITE LOCK
    // =========================================================
    /*
     * Used when stock quantity is going to be changed.
     *
     * Example:
     * PURCHASE_IN
     * SALE_OUT
     * PURCHASE_RETURN_OUT
     * SALE_RETURN_IN
     * PURCHASE_CANCEL_OUT
     * ADJUSTMENT_IN
     * ADJUSTMENT_OUT
     *
     * PESSIMISTIC_WRITE prevents concurrent transactions
     * from modifying the same stock record simultaneously.
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Stock s
            WHERE LOWER(s.rawMaterial) = LOWER(:rawMaterial)
            AND s.unit = :unit
            """)
    Optional<Stock> findStockForUpdate(
            @Param("rawMaterial") String rawMaterial,
            @Param("unit") WeightUnit unit
    );


    // =========================================================
    // FIND STOCK BY PUBLIC ID WITH PESSIMISTIC WRITE LOCK
    // =========================================================
    /*
     * Used for manual stock adjustments where the stock
     * is identified using publicId.
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Stock s
            WHERE s.publicId = :publicId
            """)
    Optional<Stock> findStockForUpdateByPublicId(
            @Param("publicId") UUID publicId
    );
}