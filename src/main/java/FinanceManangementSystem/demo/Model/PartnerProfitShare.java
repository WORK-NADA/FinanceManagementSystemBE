package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "partner_profit_shares",
        indexes = {
                @Index(
                        name = "idx_share_distribution",
                        columnList = "distribution_id"
                ),
                @Index(
                        name = "idx_share_partner",
                        columnList = "partner_id"
                )
        }
)
public class PartnerProfitShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID publicId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "distribution_id",
            nullable = false
    )
    private ProfitDistribution distribution;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "partner_id",
            nullable = false
    )
    private Partner partner;

    @Column(
            name = "share_percentage_at_distribution",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal sharePercentageAtDistribution;

    @Column(
            name = "share_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal shareAmount;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;
}
