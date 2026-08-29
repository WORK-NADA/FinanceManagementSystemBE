package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSaleDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSaleDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SaleServiceInterface {

    // =========================================================
    // ADD SALE
    // =========================================================

    ResponseSaleDTO addSale(
            RequestSaleDTO dto
    );


    // =========================================================
    // GET SALE BY PUBLIC ID
    // =========================================================

    ResponseSaleDTO getSaleByPublicId(
            UUID publicId
    );


    // =========================================================
    // GET ALL SALES
    // =========================================================

    List<ResponseSaleDTO> getAllSales();


    // =========================================================
    // GET SALES BY CUSTOMER
    // =========================================================

    List<ResponseSaleDTO> getSalesByCustomer(
            UUID customerPublicId
    );


    // =========================================================
    // GET SALES BY DATE RANGE
    // =========================================================

    List<ResponseSaleDTO> getSalesByDateRange(
            LocalDate fromDate,
            LocalDate toDate
    );


    // =========================================================
    // GET SALES BY STATUS
    // =========================================================

    List<ResponseSaleDTO> getSalesByStatus(
            String status
    );


    // =========================================================
    // UPDATE SALE
    // =========================================================

    ResponseSaleDTO updateSale(
            UUID publicId,
            RequestSaleDTO dto
    );


    // =========================================================
    // CANCEL SALE
    // =========================================================

    void cancelSale(
            UUID publicId
    );
}