package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "SELECT * FROM user WHERE email=?1",nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT name FROM user WHERE contact=?1 OR email=?2",nativeQuery = true)
     Optional<String> findByEmailOrContact(long contact,String email);

    @Query(value = "SELECT name FROM user WHERE email=?1 And password=?2",nativeQuery = true)
    String findByEmailAndPassword(String email,String password);
}
