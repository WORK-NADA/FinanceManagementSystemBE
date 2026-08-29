package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {

    // --------------------------------------------------
    // Find Supplier
    // --------------------------------------------------

    Optional<Supplier> findByPublicId(UUID publicId);


    // --------------------------------------------------
    // Check Duplicate Mobile Number
    // --------------------------------------------------

    boolean existsByMobileNumber(String mobileNumber);


    // --------------------------------------------------
    // Check Duplicate GST Number
    // --------------------------------------------------

    boolean existsByGstNumber(String gstNumber);


    // --------------------------------------------------
    // Check Duplicate Email
    // --------------------------------------------------

    boolean existsByEmail(String email);


    // --------------------------------------------------
    // Active Suppliers
    // --------------------------------------------------

    List<Supplier> findByIsActiveTrue();


    // --------------------------------------------------
    // Inactive Suppliers
    // --------------------------------------------------

    List<Supplier> findByIsActiveFalse();


    // --------------------------------------------------
    // Find Active Supplier By Public ID
    // --------------------------------------------------

    Optional<Supplier> findByPublicIdAndIsActiveTrue(
            UUID publicId
    );


    // --------------------------------------------------
    // Search Suppliers By Name
    // --------------------------------------------------

    List<Supplier> findBySupplierNameContainingIgnoreCase(
            String supplierName
    );


    // --------------------------------------------------
    // Search Active Suppliers By Name
    // --------------------------------------------------

    List<Supplier> findBySupplierNameContainingIgnoreCaseAndIsActiveTrue(
            String supplierName
    );
}