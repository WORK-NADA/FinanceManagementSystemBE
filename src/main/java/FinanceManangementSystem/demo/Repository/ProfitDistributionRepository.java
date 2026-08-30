package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.ProfitDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfitDistributionRepository
        extends JpaRepository<ProfitDistribution, Long> {

    Optional<ProfitDistribution> findByPublicId(
            UUID publicId
    );

    List<ProfitDistribution> findAllByOrderByToDateDesc();

    boolean existsByFromDateAndToDate(
            LocalDate fromDate,
            LocalDate toDate
    );

    Optional<ProfitDistribution> findFirstByOrderByToDateDesc();
}
