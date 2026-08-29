package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSalePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSalePaymentDTO;

import java.util.List;
import java.util.UUID;

public interface SalePaymentServiceInterface {

    // =========================================================
    // ADD PAYMENT
    // =========================================================

    ResponseSalePaymentDTO addPayment(
            RequestSalePaymentDTO dto
    );


    // =========================================================
    // GET PAYMENT BY PUBLIC ID
    // =========================================================

    ResponseSalePaymentDTO getPaymentByPublicId(
            UUID publicId
    );


    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    List<ResponseSalePaymentDTO> getAllPayments();


    // =========================================================
    // GET PAYMENTS BY SALE
    // =========================================================

    List<ResponseSalePaymentDTO> getPaymentsBySale(
            UUID salePublicId
    );
}