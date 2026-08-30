package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Purchase;
import FinanceManangementSystem.demo.Model.PurchasePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchasePaymentRepository
        extends JpaRepository<PurchasePayment, Long> {

    Optional<PurchasePayment> findByPublicId(
            UUID publicId
    );

    List<PurchasePayment> findByPurchaseOrderByPaymentDateDesc(
            Purchase purchase
    );

    List<PurchasePayment> findByPurchase_Supplier_PublicIdOrderByPaymentDateDesc(
            UUID supplierPublicId
    );

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM PurchasePayment p WHERE p.purchase = :purchase")
    BigDecimal sumPaidAmountByPurchase(
            @Param("purchase") Purchase purchase
    );

    boolean existsByReferenceNumber(
            String referenceNumber
    );
}
