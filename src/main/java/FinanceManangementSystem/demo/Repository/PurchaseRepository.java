package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByPublicId(UUID publicId);

    Optional<Purchase> findByPurchaseNumber(String purchaseNumber);

    boolean existsBySupplierInvoiceNumberAndSupplier_PublicId(String supplierInvoiceNumber, UUID supplierPublicId);
}