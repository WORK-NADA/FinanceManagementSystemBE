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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "customers",
        indexes = {

                @Index(
                        name = "idx_customer_public_id",
                        columnList = "public_id"
                ),

                @Index(
                        name = "idx_customer_name",
                        columnList = "customer_name"
                ),

                @Index(
                        name = "idx_customer_mobile",
                        columnList = "mobile_number"
                ),

                @Index(
                        name = "idx_customer_gst",
                        columnList = "gst_number"
                ),

                @Index(
                        name = "idx_customer_active",
                        columnList = "is_active"
                )
        }
)
public class Customer {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // PUBLIC ID
    // =========================================================

    /*
     * Public ID is exposed through APIs.
     *
     * Internal database ID:
     *     Long id
     *
     * External/API ID:
     *     UUID publicId
     */

    @UuidGenerator
    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID publicId;


    // =========================================================
    // CUSTOMER DETAILS
    // =========================================================

    @Column(
            name = "customer_name",
            nullable = false,
            length = 150
    )
    private String customerName;


    @Column(
            name = "mobile_number",
            nullable = false,
            unique = true,
            length = 15
    )
    private String mobileNumber;


    @Column(
            name = "contact_person",
            length = 100
    )
    private String contactPerson;


    @Column(
            name = "alternate_mobile_number",
            length = 15
    )
    private String alternateMobileNumber;


    @Column(
            name = "email",
            unique = true,
            length = 150
    )
    private String email;


    // =========================================================
    // GST
    // =========================================================

    @Column(
            name = "gst_number",
            unique = true,
            length = 15
    )
    private String gstNumber;


    // =========================================================
    // ADDRESS
    // =========================================================

    @OneToOne(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private CustomerAddress address;


    // =========================================================
    // OPENING BALANCE
    // =========================================================

    /*
     * Opening balance represents the amount already
     * outstanding when the customer is created.
     *
     * Positive value can represent an amount receivable,
     * depending on your accounting convention.
     */

    @Column(
            name = "opening_balance",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal openingBalance = BigDecimal.ZERO;


    // =========================================================
    // PAYMENT TERMS
    // =========================================================

    /*
     * Number of credit days allowed to the customer.
     *
     * Example:
     * 30 = payment due within 30 days.
     */

    @Column(
            name = "payment_terms",
            nullable = false
    )
    private Integer paymentTerms = 30;


    // =========================================================
    // STATUS
    // =========================================================

    /*
     * Soft status.
     *
     * true  = active customer
     * false = inactive customer
     *
     * Customer is not physically deleted.
     */

    @Column(
            name = "is_active",
            nullable = false
    )
    private Boolean isActive = true;


    // =========================================================
    // AUDIT
    // =========================================================

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