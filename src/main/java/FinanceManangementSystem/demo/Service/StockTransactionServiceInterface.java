package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Enums.StockTransactionType;
import FinanceManangementSystem.demo.Enums.WeightUnit;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestStockTransactionDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseStockTransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockTransactionServiceInterface {

    // =========================================================
    // CREATE MANUAL ADJUSTMENT
    // =========================================================

    ResponseStockTransactionDTO createAdjustment(
            RequestStockTransactionDTO dto
    );


    // =========================================================
    // GET TRANSACTION BY PUBLIC ID
    // =========================================================

    ResponseStockTransactionDTO getTransactionByPublicId(
            UUID publicId
    );


    // =========================================================
    // GET ALL TRANSACTIONS
    // =========================================================

    List<ResponseStockTransactionDTO> getAllTransactions();


    // =========================================================
    // GET TRANSACTIONS BY STOCK
    // =========================================================

    List<ResponseStockTransactionDTO> getTransactionsByStockPublicId(
            UUID stockPublicId
    );


    // =========================================================
    // GET TRANSACTIONS BY STOCK AND TYPE
    // =========================================================

    List<ResponseStockTransactionDTO> getTransactionsByType(
            UUID stockPublicId,
            StockTransactionType transactionType
    );


    // =========================================================
    // GET TRANSACTIONS BY DATE RANGE
    // =========================================================

    List<ResponseStockTransactionDTO> getTransactionsBetweenDates(
            LocalDateTime fromDate,
            LocalDateTime toDate
    );


    // =========================================================
    // GET TRANSACTIONS BY TRANSACTION TYPE
    // =========================================================

    List<ResponseStockTransactionDTO> getTransactionsByTransactionType(
            StockTransactionType transactionType
    );


    // =========================================================
    // GET TRANSACTIONS BY REFERENCE NUMBER
    // =========================================================

    List<ResponseStockTransactionDTO> getTransactionsByReferenceNumber(
            String referenceNumber
    );


    // =========================================================
    // PURCHASE STOCK OPERATIONS
    // =========================================================

    void purchaseStockIn(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String purchaseNumber
    );


    void purchaseCancelStockOut(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String purchaseNumber
    );


    void purchaseReturnStockOut(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String returnNumber
    );


    // =========================================================
    // SALE STOCK OPERATIONS
    // =========================================================

    void saleStockOut(
            String product,
            WeightUnit unit,
            BigDecimal quantity,
            String saleNumber
    );


    void saleCancelStockIn(
            String product,
            WeightUnit unit,
            BigDecimal quantity,
            String saleNumber
    );


    void saleReturnStockIn(
            String product,
            WeightUnit unit,
            BigDecimal quantity,
            String returnNumber
    );


    // =========================================================
    // MANUAL STOCK ADJUSTMENTS
    // =========================================================

    void adjustmentStockIn(
            UUID stockPublicId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks
    );


    void adjustmentStockOut(
            UUID stockPublicId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks
    );
}