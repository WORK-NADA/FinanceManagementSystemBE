package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Exceptions.DuplicateResourceException;
import FinanceManangementSystem.demo.Exceptions.InvalidRequestException;
import FinanceManangementSystem.demo.Exceptions.InvalidStateException;
import FinanceManangementSystem.demo.Exceptions.ResourceNotFoundException;
import FinanceManangementSystem.demo.Model.Stock;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestMinimumStockLevelDTO;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestStockDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseStockDTO;
import FinanceManangementSystem.demo.Repository.StockRepository;
import FinanceManangementSystem.demo.Service.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService
        implements StockServiceInterface {

    private final StockRepository stockRepository;

    private final CurrentUserService currentUserService;


    // =========================================================
    // ADD STOCK
    // =========================================================

    @Override
    @Transactional
    public ResponseStockDTO addStock(
            RequestStockDTO dto
    ) {

        log.info(
                "SERVICE - request came in addStock..."
        );


        User currentUser = currentUserService.getCurrentUser();

        String rawMaterial =
                dto.getRawMaterial().trim();


        if (stockRepository
                .existsByUserAndRawMaterialIgnoreCaseAndUnit(
                        currentUser,
                        rawMaterial,
                        dto.getUnit()
                )) {

            log.info(
                    "SERVICE - stock already exists..."
            );

            throw new DuplicateResourceException(
                    "Stock already exists for this raw material and unit"
            );
        }


        Stock stock =
                new Stock();

        stock.setUser(currentUser);

        stock.setRawMaterial(
                rawMaterial
        );

        stock.setUnit(
                dto.getUnit()
        );

        /*
         * Current quantity must always start
         * from zero.
         *
         * Stock quantity will be changed only
         * through StockTransactionService.
         */
        stock.setCurrentQuantity(
                BigDecimal.ZERO
        );

        stock.setMinimumStockLevel(
                dto.getMinimumStockLevel()
        );

        stock.setIsActive(true);


        stock =
                stockRepository.save(
                        stock
                );


        log.info(
                "SERVICE - stock added successfully..."
        );


        return mapToResponse(
                stock
        );
    }


    // =========================================================
    // GET STOCK BY PUBLIC ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ResponseStockDTO getStockByPublicId(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in getStockByPublicId..."
        );


        Stock stock =
                findStock(
                        publicId
                );


        log.info(
                "SERVICE - stock fetched successfully..."
        );


        return mapToResponse(
                stock
        );
    }


    // =========================================================
    // GET ALL ACTIVE STOCKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockDTO> getAllActiveStocks() {

        log.info(
                "SERVICE - request came in getAllActiveStocks..."
        );


        User currentUser = currentUserService.getCurrentUser();

        return stockRepository
                .findByUserAndIsActiveTrue(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET ALL INACTIVE STOCKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockDTO> getAllInactiveStocks() {

        log.info(
                "SERVICE - request came in getAllInactiveStocks..."
        );


        User currentUser = currentUserService.getCurrentUser();

        return stockRepository
                .findByUserAndIsActiveFalse(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET ALL STOCKS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockDTO> getAllStocks() {

        log.info(
                "SERVICE - request came in getAllStocks..."
        );


        User currentUser = currentUserService.getCurrentUser();

        return stockRepository
                .findByUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET LOW STOCK LIST
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockDTO> getLowStockList() {

        log.info(
                "SERVICE - request came in getLowStockList..."
        );

        User currentUser = currentUserService.getCurrentUser();

        return stockRepository
                .findByUser(currentUser)
                .stream()
                .filter(stock -> Boolean.TRUE.equals(stock.getIsActive()))
                .filter(stock -> stock.getCurrentQuantity().compareTo(stock.getMinimumStockLevel()) <= 0)
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // SEARCH STOCK
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ResponseStockDTO> searchStock(
            String rawMaterial
    ) {

        log.info(
                "SERVICE - request came in searchStock..."
        );


        if (rawMaterial == null ||
                rawMaterial.trim().isEmpty()) {

            throw new InvalidRequestException(
                    "Raw material is required"
            );
        }


        User currentUser = currentUserService.getCurrentUser();

        return stockRepository
                .findByUserAndRawMaterialContainingIgnoreCaseAndIsActiveTrue(
                        currentUser,
                        rawMaterial.trim()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // UPDATE STOCK MASTER DETAILS
    // =========================================================

    @Override
    @Transactional
    public ResponseStockDTO updateStock(
            UUID publicId,
            RequestStockDTO dto
    ) {

        log.info(
                "SERVICE - request came in updateStock..."
        );


        Stock stock =
                stockRepository
                        .findStockForUpdateByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock not found"
                                )
                        );


        if (!Boolean.TRUE.equals(
                stock.getIsActive()
        )) {

            throw new InvalidStateException(
                    "Cannot update inactive stock"
            );
        }


        String rawMaterial =
                dto.getRawMaterial().trim();


        /*
         * Check whether another stock already
         * exists with the same raw material
         * and unit.
         *
         * The current stock itself must be excluded.
         */
        if (
                (
                        !stock.getRawMaterial()
                                .equalsIgnoreCase(rawMaterial)
                                ||
                                stock.getUnit()
                                        != dto.getUnit()
                )
                        &&
                        stockRepository
                                .existsByRawMaterialIgnoreCaseAndUnit(
                                        rawMaterial,
                                        dto.getUnit()
                                )
        ) {

            throw new DuplicateResourceException(
                    "Stock already exists for this raw material and unit"
            );
        }


        /*
         * Do not modify currentQuantity here.
         *
         * Quantity changes must happen through
         * StockTransactionService.
         */
        stock.setRawMaterial(
                rawMaterial
        );


        /*
         * Unit should preferably remain immutable
         * once transactions exist.
         *
         * If your Stock entity has transaction
         * history validation, this can be restricted
         * further.
         */
        stock.setUnit(
                dto.getUnit()
        );


        stock.setMinimumStockLevel(
                dto.getMinimumStockLevel()
        );


        stock =
                stockRepository.save(
                        stock
                );


        log.info(
                "SERVICE - stock updated successfully..."
        );


        return mapToResponse(
                stock
        );
    }


    // =========================================================
    // UPDATE MINIMUM STOCK LEVEL
    // =========================================================

    @Override
    @Transactional
    public ResponseStockDTO updateMinimumStockLevel(
            UUID publicId,
            RequestMinimumStockLevelDTO dto
    ) {

        log.info(
                "SERVICE - request came in updateMinimumStockLevel..."
        );


        User currentUser = currentUserService.getCurrentUser();

        Stock stock;

        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            stock = stockRepository.findByPublicId(publicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
        } else {
            stock = stockRepository.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
        }

        if (!Boolean.TRUE.equals(
                stock.getIsActive()
        )) {

            throw new InvalidStateException(
                    "Cannot update minimum stock level for inactive stock"
            );
        }


        stock.setMinimumStockLevel(
                dto.getMinimumStockLevel()
        );


        stock =
                stockRepository.save(
                        stock
                );


        log.info(
                "SERVICE - minimum stock level updated successfully..."
        );


        return mapToResponse(
                stock
        );
    }


    // =========================================================
    // ACTIVATE STOCK
    // =========================================================

    @Override
    @Transactional
    public void activateStock(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in activateStock..."
        );


        User currentUser = currentUserService.getCurrentUser();

        Stock stock;

        if (currentUser.getRole() == FinanceManangementSystem.demo.Enums.UserRole.ADMIN) {
            stock = stockRepository.findByPublicId(publicId)
                    .orElseThrow(() -> new RuntimeException("Stock not found"));
        } else {
            stock = stockRepository.findByUserAndPublicId(currentUser, publicId)
                    .orElseThrow(() -> new RuntimeException("Stock not found"));
        }

        if (Boolean.TRUE.equals(
                stock.getIsActive()
        )) {

            throw new InvalidStateException(
                    "Stock is already active"
            );
        }


        stock.setIsActive(true);


        stockRepository.save(
                stock
        );


        log.info(
                "SERVICE - stock activated successfully..."
        );
    }


    // =========================================================
    // DEACTIVATE STOCK
    // =========================================================

    @Override
    @Transactional
    public void deactivateStock(
            UUID publicId
    ) {

        log.info(
                "SERVICE - request came in deactivateStock..."
        );


        Stock stock =
                stockRepository
                        .findStockForUpdateByPublicId(
                                publicId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock not found"
                                )
                        );


        if (!Boolean.TRUE.equals(
                stock.getIsActive()
        )) {

            throw new InvalidStateException(
                    "Stock is already inactive"
            );
        }


        stock.setIsActive(false);


        stockRepository.save(
                stock
        );


        log.info(
                "SERVICE - stock deactivated successfully..."
        );
    }


    // =========================================================
    // FIND STOCK
    // =========================================================

    private Stock findStock(
            UUID publicId
    ) {

        User currentUser = currentUserService.getCurrentUser();

        return stockRepository
                .findByUserAndPublicId(
                        currentUser,
                        publicId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock not found"
                        )
                );
    }


    // =========================================================
    // MAP TO RESPONSE
    // =========================================================

    private ResponseStockDTO mapToResponse(
            Stock stock
    ) {

        ResponseStockDTO response =
                new ResponseStockDTO();


        response.setPublicId(
                stock.getPublicId()
        );

        response.setRawMaterial(
                stock.getRawMaterial()
        );

        response.setUnit(
                stock.getUnit()
        );

        response.setCurrentQuantity(
                stock.getCurrentQuantity()
        );

        response.setMinimumStockLevel(
                stock.getMinimumStockLevel()
        );

        response.setIsLowStock(
                stock.getCurrentQuantity()
                        .compareTo(
                                stock.getMinimumStockLevel()
                        ) <= 0
        );

        response.setIsActive(
                stock.getIsActive()
        );

        response.setCreatedAt(
                stock.getCreatedAt()
        );

        response.setUpdatedAt(
                stock.getUpdatedAt()
        );


        return response;
    }
}