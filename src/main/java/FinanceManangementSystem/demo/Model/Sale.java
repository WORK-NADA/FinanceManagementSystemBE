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
        name = "sales",
        indexes = {
                @Index(
                        name = "idx_sale_customer",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_sale_date",
                        columnList = "sale_date"
                ),
                @Index(
                        name = "idx_sale_invoice",
                        columnList = "customer_invoice_number"
                )
        }
)
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // --------------------------------
    // Public ID
    // --------------------------------

    @UuidGenerator
    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID publicId;


    // --------------------------------
    // OWNER USER
    // --------------------------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    // --------------------------------
    // Customer
    // --------------------------------

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;


    // --------------------------------
    // Raw Material
    // --------------------------------

    @Column(
            name = "raw_material",
            nullable = false,
            length = 100
    )
    private String rawMaterial;


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
            name = "rate_per_unit",
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
    private BigDecimal gstPercentage =
            new BigDecimal("18.00");


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
    // Sale Number
    // --------------------------------

    @Column(
            name = "sale_number",
            nullable = false,
            unique = true,
            length = 30,
            updatable = false
    )
    private String saleNumber;


    // --------------------------------
    // Customer Invoice Number
    // --------------------------------

    @Column(
            name = "customer_invoice_number",
            length = 50
    )
    private String customerInvoiceNumber;


    // --------------------------------
    // Sale Date
    // --------------------------------

    @Column(
            name = "sale_date",
            nullable = false
    )
    private LocalDate saleDate = LocalDate.now();


    // --------------------------------
    // Payment Status
    // --------------------------------

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false,
            length = 20
    )
    private PaymentStatus paymentStatus =
            PaymentStatus.PENDING;


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