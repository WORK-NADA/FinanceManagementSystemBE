package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Enums.UserRole;
import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u.username FROM User u WHERE u.email = :email OR u.mobileNumber = :contact")
    Optional<String> findByEmailOrContact(@Param("email") String email, @Param("contact") String contact);

    @Query("SELECT u.username FROM User u WHERE (u.email = :email OR u.mobileNumber = :contact) AND u.publicId != :publicId")
    Optional<String> findByEmailOrContactAndNotPublicId(@Param("email") String email, @Param("contact") String contact, @Param("publicId") UUID publicId);

    @Query("SELECT u.ownerName FROM User u WHERE u.email = :email AND u.password = :password")
    String findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    java.util.List<User> findAllByOrderByCreatedAtDesc();

    java.util.List<User> findByRoleOrderByCreatedAtDesc(UserRole role);

    Optional<User> findByPublicId(UUID publicId);
}
