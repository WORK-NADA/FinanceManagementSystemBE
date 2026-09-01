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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("sale-payment")
@RequiredArgsConstructor
public class SalePaymentController {

    private final SalePaymentServiceInterface salePaymentService;


    // =========================================================
    // ADD PAYMENT
    // =========================================================

    @PostMapping("/add")
    public ResponseEntity<APIResponse<ResponseSalePaymentDTO>>
    addPayment(
            @Valid @RequestBody RequestSalePaymentDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addPayment for sale..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        ResponseSalePaymentDTO response =
                salePaymentService.addPayment(
                        dto
                );

        log.info(
                "CONTROLLER - sale payment created successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Sale payment recorded successfully...",
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
                "CONTROLLER - request came in getPaymentByPublicId for sale..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        ResponseSalePaymentDTO response =
                salePaymentService.getPaymentByPublicId(
                        publicId
                );

        log.info(
                "CONTROLLER - sale payment fetched successfully..."
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
    // GET PAYMENTS BY SALE
    // =========================================================

    @GetMapping("/sale/{salePublicId}")
    public ResponseEntity<APIResponse<List<ResponseSalePaymentDTO>>>
    getPaymentsBySale(
            @PathVariable UUID salePublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentsBySale..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        List<ResponseSalePaymentDTO> response =
                salePaymentService.getPaymentsBySale(
                        salePublicId
                );

        log.info(
                "CONTROLLER - payments fetched by sale successfully..."
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
    // GET PAYMENTS BY CUSTOMER
    // =========================================================

    @GetMapping("/customer/{customerPublicId}")
    public ResponseEntity<APIResponse<List<ResponseSalePaymentDTO>>>
    getPaymentsByCustomer(
            @PathVariable UUID customerPublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentsByCustomer..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        List<ResponseSalePaymentDTO> response =
                salePaymentService.getPaymentsByCustomer(
                        customerPublicId
                );

        log.info(
                "CONTROLLER - payments fetched by customer successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Customer sale payments fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET SALE PAYMENT SUMMARY
    // =========================================================

    @GetMapping("/sale/{salePublicId}/summary")
    public ResponseEntity<APIResponse<ResponseSalePaymentDTO.SaleDetails>>
    getSalePaymentSummary(
            @PathVariable UUID salePublicId
    ) {

        log.info(
                "CONTROLLER - request came in getSalePaymentSummary..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        ResponseSalePaymentDTO.SaleDetails response =
                salePaymentService.getSalePaymentSummary(
                        salePublicId
                );

        log.info(
                "CONTROLLER - sale payment summary fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Sale payment summary fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET ALL PENDING PAYMENTS (DASHBOARD)
    // =========================================================

    @GetMapping("/pending")
    public ResponseEntity<APIResponse<List<ResponseSalePaymentDTO.SaleDetails>>>
    getAllPendingPayments() {

        log.info(
                "CONTROLLER - request came in getAllPendingPayments for sales..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        List<ResponseSalePaymentDTO.SaleDetails> response =
                salePaymentService.getAllPendingPayments();

        log.info(
                "CONTROLLER - all pending sale payments fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "All pending sale payments fetched successfully...",
                                response
                        )
                );
    }

        // =========================================================
        // GET ALL PAYMENTS (PAGINATED)
        // =========================================================

        @GetMapping("/all")
        public ResponseEntity<APIResponse<org.springframework.data.domain.Page<ResponseSalePaymentDTO>>> getAllPayments(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size
        ) {

                log.info("CONTROLLER - request came in getAllPayments for sales...");

                var pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "paymentDate"));

                var resp = salePaymentService.getAllPayments(pageable);

                return ResponseEntity.ok(new APIResponse<>("Sale payments", resp));
        }


    // =========================================================
    // GET PENDING PAYMENTS BY CUSTOMER (DASHBOARD)
    // =========================================================

    @GetMapping("/pending/customer/{customerPublicId}")
    public ResponseEntity<APIResponse<List<ResponseSalePaymentDTO.SaleDetails>>>
    getPendingPaymentsByCustomer(
            @PathVariable UUID customerPublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPendingPaymentsByCustomer..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        List<ResponseSalePaymentDTO.SaleDetails> response =
                salePaymentService.getPendingPaymentsByCustomer(
                        customerPublicId
                );

        log.info(
                "CONTROLLER - pending payments fetched by customer successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Pending sale payments by customer fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET TOTAL RECEIVABLE AMOUNT (DASHBOARD SUMMARY)
    // =========================================================

    @GetMapping("/receivable-total")
    public ResponseEntity<APIResponse<BigDecimal>>
    getTotalReceivableAmount() {

        log.info(
                "CONTROLLER - request came in getTotalReceivableAmount..."
        );

        log.info(
                "CONTROLLER - calling sale payment service..."
        );

        BigDecimal response =
                salePaymentService.getTotalReceivableAmount();

        log.info(
                "CONTROLLER - total receivable amount fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Total receivable sale amount fetched successfully...",
                                response
                        )
                );
    }
}
