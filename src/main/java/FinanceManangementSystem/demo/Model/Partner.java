package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "partners",
        indexes = {
                @Index(
                        name = "idx_partner_active",
                        columnList = "is_active"
                ),
                @Index(
                        name = "idx_partner_user",
                        columnList = "user_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_partner_user_mobile",
                        columnNames = {"user_id", "mobile_number"}
                )
        }
)
public class Partner {

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

    @Column(
            name = "partner_name",
            nullable = false,
            length = 150
    )
    private String partnerName;

    @Column(
            name = "mobile_number",
            nullable = false,
            length = 15
    )
    private String mobileNumber;

    @Column(
            name = "email",
            length = 150
    )
    private String email;

    @Column(
            name = "share_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal sharePercentage;

    @Column(
            name = "joining_date",
            nullable = false
    )
    private LocalDate joiningDate;

    @Column(
            name = "is_active",
            nullable = false
    )
    private Boolean isActive = true;

    // =========================================================
    // OWNER USER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private User user;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
}
