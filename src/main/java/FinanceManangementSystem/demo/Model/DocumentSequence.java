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
                        name = "uk_document_type_year",
                        columnNames = {"document_type", "year"}
                )
        }
)
public class DocumentSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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