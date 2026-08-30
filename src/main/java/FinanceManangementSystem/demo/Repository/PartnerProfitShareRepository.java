package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.PartnerProfitShare;
import FinanceManangementSystem.demo.Model.ProfitDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerProfitShareRepository
        extends JpaRepository<PartnerProfitShare, Long> {

    List<PartnerProfitShare> findByDistribution(
            ProfitDistribution distribution
    );

    List<PartnerProfitShare> findByPartner_PublicIdOrderByCreatedAtDesc(
            UUID partnerPublicId
    );

    @Query("SELECT COALESCE(SUM(s.shareAmount), 0) FROM PartnerProfitShare s WHERE s.partner.publicId = :partnerPublicId")
    BigDecimal sumLifetimeEarningsByPartner(
            @Param("partnerPublicId") UUID partnerPublicId
    );
}
