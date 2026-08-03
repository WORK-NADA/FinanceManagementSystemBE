package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.RefreshToken;
import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    @Query(value = "SELECT * from refresh_tokens WHERE token=?1",nativeQuery = true)
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_tokens WHERE user_id=?1",nativeQuery = true)
    void deleteByUser(Long userId);
}
