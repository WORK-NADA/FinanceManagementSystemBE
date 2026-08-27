package FinanceManangementSystem.demo.Model;

import FinanceManangementSystem.demo.Enums.StockTransactionType;
import FinanceManangementSystem.demo.Enums.WeightUnit;
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
        name = "stock_transactions",

        indexes = {

                // --------------------------------------------------
                // Stock History
                // --------------------------------------------------

                @Index(
                        name = "idx_stock_transaction_stock",
                        columnList = "stock_id"
                ),

                @Index(
                        name = "idx_stock_transaction_stock_date",
                        columnList = "stock_id, transaction_date"
                ),

                // --------------------------------------------------
                // Transaction Type
                // --------------------------------------------------

                @Index(
                        name = "idx_stock_transaction_type",
                        columnList = "transaction_type"
                ),

                // --------------------------------------------------
                // Reference Number
                // --------------------------------------------------

                @Index(
                        name = "idx_stock_transaction_reference",
                        columnList = "reference_number"
                ),

                // --------------------------------------------------
                // Transaction Date
                // --------------------------------------------------

                @Index(
                        name = "idx_stock_transaction_date",
                        columnList = "transaction_date"
                )
        },

        uniqueConstraints = {

                // --------------------------------------------------
                // Prevent Duplicate Business Transaction
                // --------------------------------------------------

                @UniqueConstraint(
                        name = "uk_stock_reference_type",
                        columnNames = {
                                "reference_number",
                                "transaction_type"
                        }
                )
        }
)
public class StockTransaction {

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
    // STOCK
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "stock_id",
            nullable = false,
            updatable = false
    )
    private Stock stock;


    // =========================================================
    // TRANSACTION TYPE
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false,
            length = 30,
            updatable = false
    )
    private StockTransactionType transactionType;


    // =========================================================
    // QUANTITY
    // =========================================================

    @Column(
            name = "quantity",
            nullable = false,
            precision = 15,
            scale = 3,
            updatable = false
    )
    private BigDecimal quantity;


    // =========================================================
    // UNIT
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "unit",
            nullable = false,
            length = 20,
            updatable = false
    )
    private WeightUnit unit;


    // =========================================================
    // REFERENCE NUMBER
    // =========================================================

    @Column(
            name = "reference_number",
            nullable = false,
            length = 50,
            updatable = false
    )
    private String referenceNumber;


    // =========================================================
    // TRANSACTION DATE
    // =========================================================

    @Column(
            name = "transaction_date",
            nullable = false,
            updatable = false
    )
    private LocalDateTime transactionDate;


    // =========================================================
    // REMARKS
    // =========================================================

    @Column(
            name = "remarks",
            length = 500,
            updatable = false
    )
    private String remarks;


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
}