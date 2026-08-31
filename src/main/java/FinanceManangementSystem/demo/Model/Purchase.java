package FinanceManangementSystem.demo.Model;

import FinanceManangementSystem.demo.Enums.PaymentStatus;
import FinanceManangementSystem.demo.Enums.PurchaseStatus;
import FinanceManangementSystem.demo.Enums.WeightUnit;
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
        name = "purchases",
        indexes = {

                // Supplier lookup
                @Index(
                        name = "idx_purchase_supplier",
                        columnList = "supplier_id"
                ),

                // Purchase date filtering
                @Index(
                        name = "idx_purchase_date",
                        columnList = "purchase_date"
                ),

                // Supplier invoice search
                @Index(
                        name = "idx_purchase_supplier_invoice",
                        columnList = "supplier_invoice_number"
                ),

                // Purchase status filtering
                @Index(
                        name = "idx_purchase_status",
                        columnList = "purchase_status"
                ),

                // Payment status filtering
                @Index(
                        name = "idx_purchase_payment_status",
                        columnList = "payment_status"
                ),

                // Raw material lookup
                @Index(
                        name = "idx_purchase_raw_material",
                        columnList = "raw_material"
                )
        }
)
public class Purchase {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // PUBLIC ID
    // =========================================================

    @UuidGenerator
    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID publicId;


    // =========================================================
    // OWNER USER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;


    // =========================================================
    // SUPPLIER
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "supplier_id",
            nullable = false
    )
    private Supplier supplier;


    // =========================================================
    // RAW MATERIAL
    // =========================================================

    @Column(
            name = "raw_material",
            nullable = false,
            length = 150
    )
    private String rawMaterial;


    // =========================================================
    // QUANTITY
    // =========================================================

    @Column(
            nullable = false,
            precision = 15,
            scale = 3
    )
    private BigDecimal weight;


    // =========================================================
    // UNIT
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private WeightUnit unit = WeightUnit.KG;


    // =========================================================
    // RATE
    // =========================================================

    @Column(
            name = "rate_per_unit",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal ratePerUnit;


    // =========================================================
    // GST
    // =========================================================

    @Column(
            name = "gst_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal gstPercentage = new BigDecimal("18.00");


    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal amount;


    @Column(
            name = "gst_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal gstAmount;


    @Column(
            name = "total_amount",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal totalAmount;


    // =========================================================
    // PURCHASE NUMBER
    // =========================================================

    /*
     * Internal database ID:
     * Used only internally by JPA.
     *
     * Public ID:
     * Used by APIs.
     *
     * Purchase Number:
     * Business/document identifier.
     *
     * Purchase number should be generated by
     * DocumentSequenceService and should NOT be
     * accepted from the client request DTO.
     */

    @Column(
            name = "purchase_number",
            nullable = false,
            unique = true,
            length = 30,
            updatable = false
    )
    private String purchaseNumber;


    // =========================================================
    // SUPPLIER INVOICE NUMBER
    // =========================================================

    @Column(
            name = "supplier_invoice_number",
            length = 50
    )
    private String supplierInvoiceNumber;


    // =========================================================
    // PURCHASE DATE
    // =========================================================

    @Column(
            name = "purchase_date",
            nullable = false
    )
    private LocalDate purchaseDate = LocalDate.now();


    // =========================================================
    // PURCHASE STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "purchase_status",
            nullable = false,
            length = 20
    )
    private PurchaseStatus purchaseStatus =
            PurchaseStatus.ACTIVE;


    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false,
            length = 20
    )
    private PaymentStatus paymentStatus =
            PaymentStatus.PENDING;


    // =========================================================
    // AUDIT
    // =========================================================

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(
            nullable = false
    )
    private LocalDateTime updatedAt;
}