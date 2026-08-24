package FinanceManangementSystem.demo.Repository;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Model.DocumentSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface DocumentSequenceRepository
        extends JpaRepository<DocumentSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DocumentSequence> findByDocumentTypeAndYear(
            DocumentType documentType,
            Integer year
    );
}