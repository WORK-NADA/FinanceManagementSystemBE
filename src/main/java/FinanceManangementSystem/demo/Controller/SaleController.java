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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("sale")
@RequiredArgsConstructor
public class SaleController {

    private final SaleServiceInterface saleService;


    // ==================================================
    // ADD SALE
    // ==================================================

    @PostMapping("add")
    public ResponseEntity<APIResponse<ResponseSaleDTO>>
    addSale(
            @Valid @RequestBody RequestSaleDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addSale..."
        );


        ResponseSaleDTO response =
                saleService.addSale(
                        dto
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Sale added successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // GET SALE BY PUBLIC ID
    // ==================================================

    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSaleDTO>>
    getSaleByPublicId(
            @PathVariable UUID publicId
    ) {

        ResponseSaleDTO response =
                saleService.getSaleByPublicId(
                        publicId
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


    // ==================================================
    // GET ALL SALES
    // ==================================================

    @GetMapping("all")
    public ResponseEntity<APIResponse<List<ResponseSaleDTO>>>
    getAllSales() {

        List<ResponseSaleDTO> response =
                saleService.getAllSales();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sales fetched successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // GET SALES BY CUSTOMER
    // ==================================================

    @GetMapping("customer/{customerPublicId}")
    public ResponseEntity<APIResponse<List<ResponseSaleDTO>>>
    getSalesByCustomer(
            @PathVariable UUID customerPublicId
    ) {

        List<ResponseSaleDTO> response =
                saleService.getSalesByCustomer(
                        customerPublicId
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Customer sales fetched successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // GET SALES BY DATE RANGE
    // ==================================================

    @GetMapping("date-range")
    public ResponseEntity<APIResponse<List<ResponseSaleDTO>>>
    getSalesByDateRange(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {

        List<ResponseSaleDTO> response =
                saleService.getSalesByDateRange(
                        fromDate,
                        toDate
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sales fetched successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // GET SALES BY STATUS
    // ==================================================

    @GetMapping("status/{status}")
    public ResponseEntity<APIResponse<List<ResponseSaleDTO>>>
    getSalesByStatus(
            @PathVariable String status
    ) {

        List<ResponseSaleDTO> response =
                saleService.getSalesByStatus(
                        status
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sales fetched successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // UPDATE SALE
    // ==================================================

    @PutMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSaleDTO>>
    updateSale(
            @PathVariable UUID publicId,
            @Valid @RequestBody RequestSaleDTO dto
    ) {

        ResponseSaleDTO response =
                saleService.updateSale(
                        publicId,
                        dto
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


    // ==================================================
    // CANCEL SALE
    // ==================================================

    @PatchMapping("/{publicId}/cancel")
    public ResponseEntity<APIResponse<Void>>
    cancelSale(
            @PathVariable UUID publicId
    ) {

        saleService.cancelSale(
                publicId
        );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale cancelled successfully...",
                                null
                        )
                );
    }
}