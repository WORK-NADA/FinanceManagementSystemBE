package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSalePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSalePaymentDTO;
import FinanceManangementSystem.demo.Service.SalePaymentServiceInterface;
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
@RequestMapping("/sale-payment")
@RequiredArgsConstructor
public class SalePaymentController {

    private final SalePaymentServiceInterface salePaymentService;


    // =========================================================
    // ADD SALE PAYMENT
    // =========================================================

    @PostMapping("/add")
    public ResponseEntity<APIResponse<ResponseSalePaymentDTO>>
    addPayment(
            @Valid @RequestBody RequestSalePaymentDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addPayment..."
        );


        ResponseSalePaymentDTO response =
                salePaymentService.addPayment(
                        dto
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Sale payment added successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PAYMENT BY PUBLIC ID
    // =========================================================

    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSalePaymentDTO>>
    getPaymentByPublicId(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentByPublicId..."
        );


        ResponseSalePaymentDTO response =
                salePaymentService.getPaymentByPublicId(
                        publicId
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale payment fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<
            APIResponse<List<ResponseSalePaymentDTO>>>
    getAllPayments() {

        log.info(
                "CONTROLLER - request came in getAllPayments..."
        );


        List<ResponseSalePaymentDTO> response =
                salePaymentService.getAllPayments();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale payments fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PAYMENTS BY SALE
    // =========================================================

    @GetMapping("/sale/{salePublicId}")
    public ResponseEntity<
            APIResponse<List<ResponseSalePaymentDTO>>>
    getPaymentsBySale(
            @PathVariable UUID salePublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentsBySale..."
        );


        List<ResponseSalePaymentDTO> response =
                salePaymentService.getPaymentsBySale(
                        salePublicId
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale payments fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PAYMENTS BY DATE RANGE
    // =========================================================

    @GetMapping("/date-range")
    public ResponseEntity<
            APIResponse<List<ResponseSalePaymentDTO>>>
    getPaymentsByDateRange(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentsByDateRange..."
        );


        List<ResponseSalePaymentDTO> response =
                salePaymentService.getPaymentsByDateRange(
                        fromDate,
                        toDate
                );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale payments fetched successfully...",
                                response
                        )
                );
    }
}