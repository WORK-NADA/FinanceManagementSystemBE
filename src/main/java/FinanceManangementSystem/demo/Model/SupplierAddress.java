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
@Table(name = "supplier_address")
public class SupplierAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(
            name = "supplier_id",
            nullable = false,
            unique = true
    )
    private Supplier supplier;


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
            nullable = false,
            length = 100
    )
    private String city;


    @Column(
            nullable = false,
            length = 100
    )
    private String state;


    @Column(
            nullable = false,
            length = 100
    )
    private String country = "India";


    @Column(
            nullable = false,
            length = 6
    )
    private String pincode;


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