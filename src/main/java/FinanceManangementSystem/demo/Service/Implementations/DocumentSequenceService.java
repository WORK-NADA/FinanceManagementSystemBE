package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Model.DocumentSequence;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.DocumentSequenceRepository;
import FinanceManangementSystem.demo.Service.SequenceServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentSequenceService implements SequenceServiceInterface {
    private final DocumentSequenceRepository sequenceRepository;

    private final CurrentUserService currentUserService;

    public String generateDocumentNumber(DocumentType documentType, int year) {
        User currentUser = currentUserService.getCurrentUser();

        DocumentSequence sequence =
                sequenceRepository
                        .findByUserAndDocumentTypeAndYear(
                                currentUser,
                                documentType,
                                year
                        )
                        .orElseGet(() ->
                                createSequence(
                                        currentUser,
                                        documentType,
                                        year
                                )
                        );

        Long nextNumber =
                sequence.getCurrentNumber() + 1;

        sequence.setCurrentNumber(nextNumber);

        sequenceRepository.save(sequence);

        return String.format(
                "%s-%d-%06d",
                getPrefix(documentType),
                year,
                nextNumber
        );
    }

    public DocumentSequence createSequence(User user, DocumentType documentType, int year) {
        try {

            DocumentSequence sequence =
                    new DocumentSequence();

            sequence.setUser(user);
            sequence.setDocumentType(documentType);
            sequence.setYear(year);
            sequence.setCurrentNumber(0L);

            return sequenceRepository.saveAndFlush(sequence);

        } catch (DataIntegrityViolationException exception) {
            return sequenceRepository
                    .findByUserAndDocumentTypeAndYear(
                            user,
                            documentType,
                            year
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Unable to initialize document sequence"
                            )
                    );
        }
    }

    public String getPrefix(
            DocumentType documentType
    ) {

        return switch (documentType) {

            case PURCHASE -> "PUR";

            case SALE -> "SAL";

            case PURCHASE_PAYMENT -> "PPY";

            case CUSTOMER_RECEIPT -> "REC";

            case EXPENSE -> "EXP";

            case STOCK_ADJUSTMENT -> "STA";
        };
    }
}