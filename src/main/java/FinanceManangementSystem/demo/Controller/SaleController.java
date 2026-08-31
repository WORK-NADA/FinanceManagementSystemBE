package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSaleDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSaleDTO;
import FinanceManangementSystem.demo.Service.SaleServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("sale")
@RequiredArgsConstructor
@CrossOrigin
public class SaleController {

    private final SaleServiceInterface saleService;


    // =========================================================
    // ADD SALE
    // =========================================================

    @PostMapping
    public ResponseEntity<APIResponse<ResponseSaleDTO>>
    addSale(
            @Valid @RequestBody RequestSaleDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addSale..."
        );


        log.info(
                "CONTROLLER - calling sale service..."
        );

        ResponseSaleDTO response =
                saleService.addSale(
                        dto
                );


        log.info(
                "CONTROLLER - sale created successfully..."
        );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Sale created successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET SALE BY PUBLIC ID
    // =========================================================

    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSaleDTO>>
    getSaleByPublicId(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in getSaleByPublicId..."
        );


        log.info(
                "CONTROLLER - calling sale service..."
        );

        ResponseSaleDTO response =
                saleService.getSaleByPublicId(
                        publicId
                );


        log.info(
                "CONTROLLER - sale fetched successfully..."
        );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET ALL SALES
    // =========================================================

    @GetMapping
        public ResponseEntity<APIResponse<Page<ResponseSaleDTO>>>
        getAllSales(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {

        log.info(
                "CONTROLLER - request came in getAllSales..."
        );


        log.info(
                "CONTROLLER - calling sale service..."
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate"));

        Page<ResponseSaleDTO> response = saleService.getAllSales(pageable);


        log.info(
                "CONTROLLER - all sales fetched successfully..."
        );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "All sales fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // UPDATE SALE
    // =========================================================

    @PutMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSaleDTO>>
    updateSale(
            @PathVariable UUID publicId,
            @Valid @RequestBody RequestSaleDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in updateSale..."
        );


        log.info(
                "CONTROLLER - calling sale service..."
        );

        ResponseSaleDTO response =
                saleService.updateSale(
                        publicId,
                        dto
                );


        log.info(
                "CONTROLLER - sale updated successfully..."
        );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale updated successfully...",
                                response
                        )
                );
    }
}
