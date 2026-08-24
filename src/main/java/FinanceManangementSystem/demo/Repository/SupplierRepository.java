package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByGstNumber(String gstNumber);

    Optional<Supplier> findByPublicId(UUID publicId);
}