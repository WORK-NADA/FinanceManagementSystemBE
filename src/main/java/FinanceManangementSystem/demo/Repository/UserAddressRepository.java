package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress,Integer> {
}
