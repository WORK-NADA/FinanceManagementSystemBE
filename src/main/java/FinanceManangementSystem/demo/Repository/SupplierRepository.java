package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.Model.User;
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

    Optional<Supplier> findByUserAndPublicId(User user, UUID publicId);

    List<Supplier> findByUser(User user);

    List<Supplier> findByUserAndIsActiveTrue(User user);

    Optional<Supplier> findByUserAndPublicIdAndIsActiveTrue(User user, UUID publicId);


    // --------------------------------------------------
    // Check Duplicate Mobile Number (global — kept for potential admin use)
    // --------------------------------------------------

    boolean existsByMobileNumber(String mobileNumber);

    // Per-user duplicate check (used by addSupplier / updateSupplier)
    boolean existsByUserAndMobileNumber(User user, String mobileNumber);

    boolean existsByUserAndMobileNumberAndPublicIdNot(User user, String mobileNumber, UUID publicId);


    // --------------------------------------------------
    // Check Duplicate GST Number
    // --------------------------------------------------

    boolean existsByGstNumber(String gstNumber);

    // Per-user GST duplicate check
    boolean existsByUserAndGstNumber(User user, String gstNumber);

    boolean existsByUserAndGstNumberAndPublicIdNot(User user, String gstNumber, UUID publicId);


    // --------------------------------------------------
    // Check Duplicate Email
    // --------------------------------------------------

    boolean existsByEmail(String email);

    // Per-user email duplicate check
    boolean existsByUserAndEmail(User user, String email);

    boolean existsByUserAndEmailAndPublicIdNot(User user, String email, UUID publicId);



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