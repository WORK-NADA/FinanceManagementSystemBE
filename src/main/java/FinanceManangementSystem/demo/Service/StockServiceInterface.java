package FinanceManangementSystem.demo.Service;

import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestMinimumStockLevelDTO;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestStockDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseStockDTO;

import java.util.List;
import java.util.UUID;

public interface StockServiceInterface {

    // --------------------------------------------------
    // Create Stock
    // --------------------------------------------------

    ResponseStockDTO addStock(
            RequestStockDTO dto
    );


    // --------------------------------------------------
    // Get Stock By Public ID
    // --------------------------------------------------

    ResponseStockDTO getStockByPublicId(
            UUID publicId
    );


    // --------------------------------------------------
    // Get All Active Stocks
    // --------------------------------------------------

    List<ResponseStockDTO> getAllActiveStocks();


    // --------------------------------------------------
    // Get All Inactive Stocks
    // --------------------------------------------------

    List<ResponseStockDTO> getAllInactiveStocks();


    // --------------------------------------------------
    // Get All Stocks
    // --------------------------------------------------

    List<ResponseStockDTO> getAllStocks();


    // --------------------------------------------------
    // Search Stock
    // --------------------------------------------------

    List<ResponseStockDTO> searchStock(
            String rawMaterial
    );

    List<ResponseStockDTO> getLowStockList();


    // --------------------------------------------------
    // Update Stock Master Details
    // --------------------------------------------------

    ResponseStockDTO updateStock(
            UUID publicId,
            RequestStockDTO dto
    );


    // --------------------------------------------------
    // Update Minimum Stock Level
    // --------------------------------------------------

    ResponseStockDTO updateMinimumStockLevel(
            UUID publicId,
            RequestMinimumStockLevelDTO dto
    );


    // --------------------------------------------------
    // Activate Stock
    // --------------------------------------------------

    void activateStock(
            UUID publicId
    );


    // --------------------------------------------------
    // Deactivate Stock
    // --------------------------------------------------

    void deactivateStock(
            UUID publicId
    );
}