package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchasePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchasePaymentDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PurchasePaymentServiceInterface {

    ResponsePurchasePaymentDTO addPayment(
            RequestPurchasePaymentDTO dto
    );

    ResponsePurchasePaymentDTO getPaymentByPublicId(
            UUID publicId
    );

    List<ResponsePurchasePaymentDTO> getPaymentsByPurchase(
            UUID purchasePublicId
    );

    List<ResponsePurchasePaymentDTO> getPaymentsBySupplier(
            UUID supplierPublicId
    );

    ResponsePurchasePaymentDTO.PurchaseDetails getPurchasePaymentSummary(
            UUID purchasePublicId
    );

    List<ResponsePurchasePaymentDTO.PurchaseDetails> getAllPendingPayments();

    List<ResponsePurchasePaymentDTO.PurchaseDetails> getPendingPaymentsBySupplier(
            UUID supplierPublicId
    );
    BigDecimal getTotalOutstandingAmount();

    org.springframework.data.domain.Page<ResponsePurchasePaymentDTO> getAllPayments(org.springframework.data.domain.Pageable pageable);
}