package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSalePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSalePaymentDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SalePaymentServiceInterface {

    ResponseSalePaymentDTO addPayment(
            RequestSalePaymentDTO dto
    );

    ResponseSalePaymentDTO getPaymentByPublicId(
            UUID publicId
    );

    List<ResponseSalePaymentDTO> getPaymentsBySale(
            UUID salePublicId
    );

    List<ResponseSalePaymentDTO> getPaymentsByCustomer(
            UUID customerPublicId
    );

    ResponseSalePaymentDTO.SaleDetails getSalePaymentSummary(
            UUID salePublicId
    );

    List<ResponseSalePaymentDTO.SaleDetails> getAllPendingPayments();

    List<ResponseSalePaymentDTO.SaleDetails> getPendingPaymentsByCustomer(
            UUID customerPublicId
    );

    org.springframework.data.domain.Page<ResponseSalePaymentDTO> getAllPayments(org.springframework.data.domain.Pageable pageable);

    BigDecimal getTotalReceivableAmount();
}
