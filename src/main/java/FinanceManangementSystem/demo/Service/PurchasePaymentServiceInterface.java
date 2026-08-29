package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchasePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchasePaymentDTO;

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
}