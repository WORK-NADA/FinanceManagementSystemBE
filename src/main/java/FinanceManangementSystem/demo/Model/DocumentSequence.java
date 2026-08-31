package FinanceManangementSystem.demo.Model;

import FinanceManangementSystem.demo.Enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "document_sequences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seq_user_type_year",
                        columnNames = {"user_id", "document_type", "year"}
                )
        }
)
public class DocumentSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(
            name = "document_type",
            nullable = false,
            length = 30
    )
    private DocumentType documentType;

    @Column(
            nullable = false
    )
    private Integer year;

    @Column(
            name = "current_number",
            nullable = false
    )
    private Long currentNumber;
}