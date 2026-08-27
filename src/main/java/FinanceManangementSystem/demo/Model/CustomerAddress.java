package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "customer_address",
        indexes = {
                @Index(
                        name = "idx_customer_address_customer",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_customer_address_city",
                        columnList = "city"
                ),
                @Index(
                        name = "idx_customer_address_pincode",
                        columnList = "pincode"
                )
        }
)
public class CustomerAddress {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // CUSTOMER
    // =========================================================

    /*
     * Each customer can have only one address.
     *
     * Customer:
     *     CustomerAddress address
     *
     * CustomerAddress:
     *     Customer customer
     */

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true
    )
    private Customer customer;


    // =========================================================
    // ADDRESS
    // =========================================================

    @Column(
            name = "address_line_1",
            nullable = false,
            length = 150
    )
    private String addressLine1;


    @Column(
            name = "address_line_2",
            length = 150
    )
    private String addressLine2;


    @Column(
            name = "city",
            nullable = false,
            length = 100
    )
    private String city;


    @Column(
            name = "state",
            nullable = false,
            length = 100
    )
    private String state;


    @Column(
            name = "country",
            nullable = false,
            length = 100
    )
    private String country = "India";


    @Column(
            name = "pincode",
            nullable = false,
            length = 6
    )
    private String pincode;


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