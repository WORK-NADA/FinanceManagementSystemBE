package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Enums.DocumentType;
import FinanceManangementSystem.demo.Enums.StockTransactionType;
import FinanceManangementSystem.demo.Enums.WeightUnit;
import FinanceManangementSystem.demo.Model.Stock;
import FinanceManangementSystem.demo.Model.StockTransaction;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestStockTransactionDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseStockTransactionDTO;
import FinanceManangementSystem.demo.Repository.StockRepository;
import FinanceManangementSystem.demo.Repository.StockTransactionRepository;
import FinanceManangementSystem.demo.Service.StockTransactionServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTransactionService
        implements StockTransactionServiceInterface {

    private final StockRepository stockRepository;

    private final StockTransactionRepository stockTransactionRepository;

    private final DocumentSequenceService documentSequenceService;


    // =========================================================
    // CREATE MANUAL ADJUSTMENT
    // =========================================================

    @Override
    @Transactional
    public ResponseStockTransactionDTO createAdjustment(
            RequestStockTransactionDTO dto
    ) {

        log.info(
                "SERVICE - request came in createAdjustment..."
        );


        if (dto.getTransactionType()
                != StockTransactionType.ADJUSTMENT_IN
                &&
                dto.getTransactionType()
                        != StockTransactionType.ADJUSTMENT_OUT) {

            throw new RuntimeException(
                    "Only adjustment transactions are allowed"
            );
        }


        validateQuantity(
                dto.getQuantity()
        );


        Stock stock =
                stockRepository
                        .findStockForUpdateByPublicId(
                                dto.getStockPublicId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock not found"
                                )
                        );


        validateActiveStock(
                stock
        );


        if (dto.getTransactionType()
                == StockTransactionType.ADJUSTMENT_IN) {

            increaseStock(
                    stock,
                    dto.getQuantity()
            );

        } else {

            decreaseStock(
                    stock,
                    dto.getQuantity()
            );
        }


        String referenceNumber =
                documentSequenceService
                        .generateDocumentNumber(
                                DocumentType.STOCK_ADJUSTMENT,
                                LocalDateTime.now().getYear()
                        );


        StockTransaction transaction =
                createTransaction(
                        stock,
                        dto.getTransactionType(),
                        dto.getQuantity(),
                        referenceNumber,
                        dto.getRemarks()
                );


        log.info(
                "SERVICE - stock adjustment created successfully..."
        );


        return mapToResponse(
                transaction
        );
    }


    // =========================================================
    // PURCHASE IN
    // =========================================================

    @Override
    @Transactional
    public void purchaseStockIn(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String purchaseNumber
    ) {

        log.info(
                "SERVICE - request came in purchaseStockIn..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                findStockForUpdate(
                        rawMaterial,
                        unit
                );


        validateActiveStock(
                stock
        );


        increaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.PURCHASE_IN,
                quantity,
                purchaseNumber,
                "Stock added through purchase"
        );


        log.info(
                "SERVICE - purchase stock added successfully..."
        );
    }


    // =========================================================
    // SALE OUT
    // =========================================================

    @Override
    @Transactional
    public void saleStockOut(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String saleNumber
    ) {

        log.info(
                "SERVICE - request came in saleStockOut..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                findStockForUpdate(
                        rawMaterial,
                        unit
                );


        validateActiveStock(
                stock
        );


        /*
         * decreaseStock() automatically checks
         * whether sufficient stock is available.
         */

        decreaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.SALE_OUT,
                quantity,
                saleNumber,
                "Stock removed through sale"
        );


        log.info(
                "SERVICE - sale stock removed successfully..."
        );
    }


    // =========================================================
    // SALE CANCEL IN
    // =========================================================

    @Override
    @Transactional
    public void saleCancelStockIn(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String saleNumber
    ) {

        log.info(
                "SERVICE - request came in saleCancelStockIn..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                findStockForUpdate(
                        rawMaterial,
                        unit
                );


        validateActiveStock(
                stock
        );


        /*
         * Original sale removed stock.
         *
         * Cancellation reverses that operation.
         *
         * SALE_OUT
         *     ↓
         * SALE_CANCEL_IN
         *
         * Therefore stock is increased.
         */

        increaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.SALE_CANCEL_IN,
                quantity,
                saleNumber,
                "Stock added through sale cancellation"
        );


        log.info(
                "SERVICE - sale cancellation stock added successfully..."
        );
    }


    // =========================================================
    // PURCHASE RETURN OUT
    // =========================================================

    @Override
    @Transactional
    public void purchaseReturnStockOut(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String returnNumber
    ) {

        log.info(
                "SERVICE - request came in purchaseReturnStockOut..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                findStockForUpdate(
                        rawMaterial,
                        unit
                );


        validateActiveStock(
                stock
        );


        decreaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.PURCHASE_RETURN_OUT,
                quantity,
                returnNumber,
                "Stock removed through purchase return"
        );


        log.info(
                "SERVICE - purchase return stock removed successfully..."
        );
    }


    // =========================================================
    // SALE RETURN IN
    // =========================================================

    @Override
    @Transactional
    public void saleReturnStockIn(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String returnNumber
    ) {

        log.info(
                "SERVICE - request came in saleReturnStockIn..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                findStockForUpdate(
                        rawMaterial,
                        unit
                );


        validateActiveStock(
                stock
        );


        increaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.SALE_RETURN_IN,
                quantity,
                returnNumber,
                "Stock added through sales return"
        );


        log.info(
                "SERVICE - sale return stock added successfully..."
        );
    }


    // =========================================================
    // PURCHASE CANCEL OUT
    // =========================================================

    @Override
    @Transactional
    public void purchaseCancelStockOut(
            String rawMaterial,
            WeightUnit unit,
            BigDecimal quantity,
            String purchaseNumber
    ) {

        log.info(
                "SERVICE - request came in purchaseCancelStockOut..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                findStockForUpdate(
                        rawMaterial,
                        unit
                );


        validateActiveStock(
                stock
        );


        decreaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.PURCHASE_CANCEL_OUT,
                quantity,
                purchaseNumber,
                "Stock removed through purchase cancellation"
        );


        log.info(
                "SERVICE - purchase cancellation stock removed successfully..."
        );
    }


    // =========================================================
    // ADJUSTMENT IN
    // =========================================================

    @Override
    @Transactional
    public void adjustmentStockIn(
            UUID stockPublicId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks
    ) {

        log.info(
                "SERVICE - request came in adjustmentStockIn..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                stockRepository
                        .findStockForUpdateByPublicId(
                                stockPublicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock not found"
                                )
                        );


        validateActiveStock(
                stock
        );


        increaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.ADJUSTMENT_IN,
                quantity,
                referenceNumber,
                remarks
        );


        log.info(
                "SERVICE - stock adjustment IN completed successfully..."
        );
    }


    // =========================================================
    // ADJUSTMENT OUT
    // =========================================================

    @Override
    @Transactional
    public void adjustmentStockOut(
            UUID stockPublicId,
            BigDecimal quantity,
            String referenceNumber,
            String remarks
    ) {

        log.info(
                "SERVICE - request came in adjustmentStockOut..."
        );


        validateQuantity(
                quantity
        );


        Stock stock =
                stockRepository
                        .findStockForUpdateByPublicId(
                                stockPublicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock not found"
                                )
                        );


        validateActiveStock(
                stock
        );


        decreaseStock(
                stock,
                quantity
        );


        createTransaction(
                stock,
                StockTransactionType.ADJUSTMENT_OUT,
                quantity,
                referenceNumber,
                remarks
        );


        log.info(
                "SERVICE - stock adjustment OUT completed successfully..."
        );
    }


    // =========================================================
    // GET TRANSACTION BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseStockTransactionDTO getTransactionByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getTransactionByPublicId..."
        );


        StockTransaction transaction =
                stockTransactionRepository
                        .findByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock transaction not found"
                                )
                        );


        return mapToResponse(
                transaction
        );
    }


    // =========================================================
    // GET ALL TRANSACTIONS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockTransactionDTO> getAllTransactions() {

        log.info(
                "SERVICE - request came in getAllTransactions..."
        );


        return stockTransactionRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "transactionDate"
                        )
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET STOCK HISTORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockTransactionDTO>
    getTransactionsByStockPublicId(
            UUID stockPublicId
    ) {

        log.info(
                "SERVICE - request came in getTransactionsByStockPublicId..."
        );


        stockRepository
                .findByPublicId(
                        stockPublicId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock not found"
                        )
                );


        return stockTransactionRepository
                .findByStockPublicIdOrderByTransactionDateDesc(
                        stockPublicId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET BY STOCK AND TYPE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockTransactionDTO> getTransactionsByType(
            UUID stockPublicId,
            StockTransactionType transactionType
    ) {

        log.info(
                "SERVICE - request came in getTransactionsByType..."
        );


        Stock stock =
                stockRepository
                        .findByPublicId(
                                stockPublicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock not found"
                                )
                        );


        if (transactionType == null) {

            throw new RuntimeException(
                    "Transaction type is required"
            );
        }


        return stockTransactionRepository
                .findByStockAndTransactionType(
                        stock,
                        transactionType
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET BETWEEN DATES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockTransactionDTO>
    getTransactionsBetweenDates(
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {

        log.info(
                "SERVICE - request came in getTransactionsBetweenDates..."
        );


        if (fromDate == null ||
                toDate == null) {

            throw new RuntimeException(
                    "From date and to date are required"
            );
        }


        if (fromDate.isAfter(toDate)) {

            throw new RuntimeException(
                    "From date cannot be after to date"
            );
        }


        return stockTransactionRepository
                .findByTransactionDateBetween(
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET BY TRANSACTION TYPE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockTransactionDTO>
    getTransactionsByTransactionType(
            StockTransactionType transactionType
    ) {

        log.info(
                "SERVICE - request came in getTransactionsByTransactionType..."
        );


        if (transactionType == null) {

            throw new RuntimeException(
                    "Transaction type is required"
            );
        }


        return stockTransactionRepository
                .findByTransactionType(
                        transactionType
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET BY REFERENCE NUMBER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockTransactionDTO>
    getTransactionsByReferenceNumber(
            String referenceNumber
    ) {

        log.info(
                "SERVICE - request came in getTransactionsByReferenceNumber..."
        );


        if (referenceNumber == null ||
                referenceNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Reference number is required"
            );
        }


        return stockTransactionRepository
                .findByReferenceNumber(
                        referenceNumber.trim()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // FIND STOCK WITH LOCK
    // =========================================================

    private Stock findStockForUpdate(
            String rawMaterial,
            WeightUnit unit
    ) {

        if (rawMaterial == null ||
                rawMaterial.trim().isEmpty()) {

            throw new RuntimeException(
                    "Raw material is required"
            );
        }


        if (unit == null) {

            throw new RuntimeException(
                    "Unit is required"
            );
        }


        return stockRepository
                .findStockForUpdate(
                        rawMaterial.trim(),
                        unit
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock not found"
                        )
                );
    }


    // =========================================================
    // INCREASE STOCK
    // =========================================================

    private void increaseStock(
            Stock stock,
            BigDecimal quantity
    ) {

        stock.setCurrentQuantity(
                stock.getCurrentQuantity()
                        .add(quantity)
        );


        stockRepository.save(
                stock
        );
    }


    // =========================================================
    // DECREASE STOCK
    // =========================================================

    private void decreaseStock(
            Stock stock,
            BigDecimal quantity
    ) {

        if (stock.getCurrentQuantity()
                .compareTo(quantity) < 0) {

            throw new RuntimeException(
                    "Insufficient stock. Available: "
                            + stock.getCurrentQuantity()
                            + " "
                            + stock.getUnit()
                            + ", Requested: "
                            + quantity
                            + " "
                            + stock.getUnit()
            );
        }


        stock.setCurrentQuantity(
                stock.getCurrentQuantity()
                        .subtract(quantity)
        );


        stockRepository.save(
                stock
        );
    }


    // =========================================================
    // VALIDATE ACTIVE STOCK
    // =========================================================

    private void validateActiveStock(
            Stock stock
    ) {

        if (!Boolean.TRUE.equals(
                stock.getIsActive()
        )) {

            throw new RuntimeException(
                    "Stock is inactive"
            );
        }
    }


    // =========================================================
    // VALIDATE QUANTITY
    // =========================================================

    private void validateQuantity(
            BigDecimal quantity
    ) {

        if (quantity == null ||
                quantity.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }
    }


    // =========================================================
    // CREATE TRANSACTION
    // =========================================================

    private StockTransaction createTransaction(
            Stock stock,
            StockTransactionType transactionType,
            BigDecimal quantity,
            String referenceNumber,
            String remarks
    ) {

        if (referenceNumber == null ||
                referenceNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Reference number is required"
            );
        }


        String trimmedReference =
                referenceNumber.trim();


        if (stockTransactionRepository
                .existsByReferenceNumberAndTransactionType(
                        trimmedReference,
                        transactionType
                )) {

            throw new RuntimeException(
                    "Stock transaction already exists for this reference"
            );
        }


        StockTransaction transaction =
                new StockTransaction();


        transaction.setStock(
                stock
        );


        transaction.setTransactionType(
                transactionType
        );


        transaction.setQuantity(
                quantity
        );


        /*
         * Unit is always taken from Stock.
         *
         * This prevents a transaction from
         * having a different unit from Stock.
         */

        transaction.setUnit(
                stock.getUnit()
        );


        transaction.setReferenceNumber(
                trimmedReference
        );


        transaction.setTransactionDate(
                LocalDateTime.now()
        );


        transaction.setRemarks(
                remarks
        );


        return stockTransactionRepository.save(
                transaction
        );
    }


    // =========================================================
    // MAP TO RESPONSE
    // =========================================================

    private ResponseStockTransactionDTO mapToResponse(
            StockTransaction transaction
    ) {

        ResponseStockTransactionDTO response =
                new ResponseStockTransactionDTO();


        response.setPublicId(
                transaction.getPublicId()
        );


        response.setStockPublicId(
                transaction.getStock()
                        .getPublicId()
        );


        response.setRawMaterial(
                transaction.getStock()
                        .getRawMaterial()
        );


        response.setTransactionType(
                transaction.getTransactionType()
        );


        response.setQuantity(
                transaction.getQuantity()
        );


        response.setUnit(
                transaction.getUnit()
        );


        response.setReferenceNumber(
                transaction.getReferenceNumber()
        );


        response.setTransactionDate(
                transaction.getTransactionDate()
        );


        response.setRemarks(
                transaction.getRemarks()
        );


        response.setCreatedAt(
                transaction.getCreatedAt()
        );


        return response;
    }
}