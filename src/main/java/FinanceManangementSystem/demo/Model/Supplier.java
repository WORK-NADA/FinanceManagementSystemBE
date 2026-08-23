package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "suppliers",
        indexes = {
                @Index(
                        name = "idx_supplier_name",
                        columnList = "supplier_name"
                ),
                @Index(
                        name = "idx_supplier_active",
                        columnList = "is_active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            updatable = false
    )
    @UuidGenerator
    private UUID publicId;

    @Column(nullable = false, length = 150)
    private String supplierName;

    @Column(nullable = false,unique = true, length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 15)
    private String alternateMobileNumber;

    @Column(length = 150)
    private String email;

    @Column(unique = true,length = 15)
    private String gstNumber;

    @OneToOne(
            mappedBy = "supplier",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private SupplierAddress address;

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal openingBalance = BigDecimal.ZERO;

    private Integer paymentTerms = 30;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}