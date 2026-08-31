package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.ProfitDistribution;
import FinanceManangementSystem.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfitDistributionRepository
        extends JpaRepository<ProfitDistribution, Long> {

    // =========================================================
    // FIND BY PUBLIC ID
    // =========================================================

    Optional<ProfitDistribution> findByPublicId(
            UUID publicId
    );

    Optional<ProfitDistribution> findByUserAndPublicId(
            User user,
            UUID publicId
    );


    // =========================================================
    // FIND ALL ORDERED BY DATE
    // =========================================================

    List<ProfitDistribution> findAllByOrderByToDateDesc();

    List<ProfitDistribution> findByUserOrderByToDateDesc(
            User user
    );


    // =========================================================
    // CHECK DUPLICATE PERIOD
    // =========================================================

    boolean existsByFromDateAndToDate(
            LocalDate fromDate,
            LocalDate toDate
    );

    boolean existsByUserAndFromDateAndToDate(
            User user,
            LocalDate fromDate,
            LocalDate toDate
    );


    // =========================================================
    // LATEST DISTRIBUTION
    // =========================================================

    Optional<ProfitDistribution> findFirstByOrderByToDateDesc();

    Optional<ProfitDistribution> findFirstByUserOrderByToDateDesc(
            User user
    );
}
