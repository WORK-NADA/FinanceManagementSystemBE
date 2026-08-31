package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Model.DocumentSequence;
import FinanceManangementSystem.demo.Model.User;

public interface SequenceServiceInterface {
    String generateDocumentNumber(DocumentType documentType, int year);

    DocumentSequence createSequence(User user, DocumentType documentType,int year);

    String getPrefix(DocumentType documentType);
}
