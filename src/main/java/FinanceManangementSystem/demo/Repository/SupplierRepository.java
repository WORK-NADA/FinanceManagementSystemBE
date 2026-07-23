package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.Supplier;
import FinanceManangementSystem.demo.ResponseDTO.ResponseSupplierDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Integer> {
    @Query(value = "SELECT name FROM supplier WHERE contact=?1 OR email=?2 OR pan_no=?3",nativeQuery = true)
    Optional<String> findByEmailorContactorPANno(long contact,String email,String panNo);
}
