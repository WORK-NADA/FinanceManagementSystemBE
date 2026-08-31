package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Customer;
import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    // =========================================================
    // FIND BY PUBLIC ID
    // =========================================================

    Optional<Customer> findByPublicId(
            UUID publicId
    );

    Optional<Customer> findByUserAndPublicId(User user, UUID publicId);

    Optional<Customer> findByUserAndPublicIdAndIsActiveTrue(User user, UUID publicId);

    List<Customer> findByUser(User user);

    List<Customer> findByUserAndIsActiveTrue(User user);


    // =========================================================
    // FIND ACTIVE CUSTOMER BY PUBLIC ID
    // =========================================================

    Optional<Customer> findByPublicIdAndIsActiveTrue(
            UUID publicId
    );


    // =========================================================
    // FIND ALL ACTIVE CUSTOMERS
    // =========================================================

    List<Customer> findByIsActiveTrue();


    // =========================================================
    // CHECK MOBILE NUMBER
    // =========================================================

    boolean existsByMobileNumber(
            String mobileNumber
    );


    // =========================================================
    // CHECK MOBILE NUMBER DURING UPDATE
    // =========================================================

    boolean existsByMobileNumberAndPublicIdNot(
            String mobileNumber,
            UUID publicId
    );


    // =========================================================
    // CHECK EMAIL
    // =========================================================

    boolean existsByEmail(
            String email
    );


    // =========================================================
    // CHECK EMAIL DURING UPDATE
    // =========================================================

    boolean existsByEmailAndPublicIdNot(
            String email,
            UUID publicId
    );


    // =========================================================
    // CHECK GST NUMBER
    // =========================================================

    boolean existsByGstNumber(
            String gstNumber
    );


    // =========================================================
    // CHECK GST NUMBER DURING UPDATE
    // =========================================================

    boolean existsByGstNumberAndPublicIdNot(
            String gstNumber,
            UUID publicId
    );
}