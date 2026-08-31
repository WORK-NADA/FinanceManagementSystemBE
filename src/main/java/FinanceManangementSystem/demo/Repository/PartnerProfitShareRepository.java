package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Model.PartnerProfitShare;
import FinanceManangementSystem.demo.Model.ProfitDistribution;
import FinanceManangementSystem.demo.Model.User;
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


    // =========================================================
    // SHARE HISTORY BY PARTNER (global — ADMIN only)
    // =========================================================

    List<PartnerProfitShare> findByPartner_PublicIdOrderByCreatedAtDesc(
            UUID partnerPublicId
    );


    // =========================================================
    // SHARE HISTORY BY PARTNER (scoped to user's distributions)
    // =========================================================

    List<PartnerProfitShare>
    findByPartner_PublicIdAndDistribution_UserOrderByCreatedAtDesc(
            UUID partnerPublicId,
            User user
    );


    // =========================================================
    // LIFETIME EARNINGS BY PARTNER (global — ADMIN only)
    // =========================================================

    @Query("SELECT COALESCE(SUM(s.shareAmount), 0) FROM PartnerProfitShare s WHERE s.partner.publicId = :partnerPublicId")
    BigDecimal sumLifetimeEarningsByPartner(
            @Param("partnerPublicId") UUID partnerPublicId
    );


    // =========================================================
    // LIFETIME EARNINGS BY PARTNER (scoped to user's distributions)
    // =========================================================

    @Query("SELECT COALESCE(SUM(s.shareAmount), 0) FROM PartnerProfitShare s WHERE s.partner.publicId = :partnerPublicId AND s.distribution.user = :user")
    BigDecimal sumLifetimeEarningsByPartnerAndUser(
            @Param("partnerPublicId") UUID partnerPublicId,
            @Param("user") User user
    );
}

