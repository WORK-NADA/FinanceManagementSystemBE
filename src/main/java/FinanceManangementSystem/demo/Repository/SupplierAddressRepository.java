package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.SupplierAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierAddressRepository extends JpaRepository<SupplierAddress,Long> {
}
