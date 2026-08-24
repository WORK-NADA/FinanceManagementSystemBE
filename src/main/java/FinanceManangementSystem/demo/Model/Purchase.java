package FinanceManangementSystem.demo.Model;

import FinanceManangementSystem.demo.Enums.PaymentStatus;
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
                @Index(name = "idx_purchase_supplier", columnList = "supplier_id"),
                @Index(name = "idx_purchase_date", columnList = "purchase_date"),
                @Index(name = "idx_purchase_invoice", columnList = "invoice_number")
        }
)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    @UuidGenerator
    private UUID publicId;

    // --------------------------------
    // Supplier
    // --------------------------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "supplier_id",
            nullable = false
    )
    private Supplier supplier;

    // --------------------------------
    // Product / Raw Material
    // --------------------------------


    private String rawMaterial = "Raw material";

    // --------------------------------
    // Quantity
    // --------------------------------

    @Column(
            nullable = false,
            precision = 15,
            scale = 3
    )
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private WeightUnit unit = WeightUnit.KG;

    // --------------------------------
    // Rate
    // --------------------------------

    @Column(
            name = "rate_per_kg",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal ratePerUnit;

    // --------------------------------
    // GST
    // --------------------------------

    @Column(
            name = "gst_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal gstPercentage = new BigDecimal(18);

    @Column(
            name = "amount",
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

    // --------------------------------
    // Invoice
    // --------------------------------

    @Column(
            name = "purchase_number",
            nullable = false,
            unique = true,
            length = 30,
            updatable = false
    )
    private String purchaseNumber;      //purchase number should be auto generated.

    @Column(
            name = "supplier_invoice_number",
            length = 50
    )
    private String supplierInvoiceNumber;

    @Column(
            name = "purchase_date",
            nullable = false
    )
    private LocalDate purchaseDate = LocalDate.now();

    // --------------------------------
    // Payment
    // --------------------------------

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false,
            length = 20
    )
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // --------------------------------
    // Audit
    // --------------------------------

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}