package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    Optional<User> findByEmail(String email);

    // ?1 = email, ?2 = contact — matches method signature (email, contact)
    @Query(value = "SELECT username FROM users WHERE email = ?1 OR mobile_number = ?2", nativeQuery = true)
    Optional<String> findByEmailOrContact(String email, String contact);

    @Query(value = "SELECT name FROM users WHERE email=?1 And password=?2", nativeQuery = true)
    String findByEmailAndPassword(String email, String password);

    java.util.List<User> findAllByOrderByCreatedAtDesc();
}
