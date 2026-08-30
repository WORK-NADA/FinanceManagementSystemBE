package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSaleDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSaleDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SaleServiceInterface {

    ResponseSaleDTO addSale(
            RequestSaleDTO dto
    );

    ResponseSaleDTO getSaleByPublicId(
            UUID publicId
    );

        Page<ResponseSaleDTO> getAllSales(Pageable pageable);

    ResponseSaleDTO updateSale(
            UUID publicId,
            RequestSaleDTO dto
    );
}