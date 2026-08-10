package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

//    @Query(value = "SELECT * FROM users WHERE email=?1",nativeQuery = true)
//    Optional<User> findByEmail(String email);

    @Procedure(procedureName = "findByEmail")
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT username FROM users WHERE mobile_number=?1 OR email=?2",nativeQuery = true)
     Optional<String> findByEmailOrContact(String email,String contact);

//    @Procedure(procedureName = "findByEmailOrContact")
//    Optional<String> findByEmailOrContact(String email, String contact);

    @Query(value = "SELECT name FROM users WHERE email=?1 And password=?2",nativeQuery = true)
    String findByEmailAndPassword(String email,String password);
}
