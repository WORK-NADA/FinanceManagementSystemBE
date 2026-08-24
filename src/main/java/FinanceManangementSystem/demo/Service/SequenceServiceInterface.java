package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Model.DocumentSequence;

public interface SequenceServiceInterface {
    String generateDocumentNumber(DocumentType documentType, int year);

    DocumentSequence createSequence(DocumentType documentType,int year);

    String getPrefix(DocumentType documentType);
}
