package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchasePaymentDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchasePaymentDTO;
import FinanceManangementSystem.demo.Service.PurchasePaymentServiceInterface;
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
@RequestMapping("purchase-payment")
@RequiredArgsConstructor
@CrossOrigin
public class PurchasePaymentController {

    private final PurchasePaymentServiceInterface purchasePaymentService;


    // =========================================================
    // ADD PAYMENT
    // =========================================================

    @PostMapping("/add")
    public ResponseEntity<APIResponse<ResponsePurchasePaymentDTO>>
    addPayment(
            @Valid @RequestBody RequestPurchasePaymentDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addPayment..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        ResponsePurchasePaymentDTO response =
                purchasePaymentService.addPayment(
                        dto
                );

        log.info(
                "CONTROLLER - purchase payment created successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Purchase payment recorded successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PAYMENT BY PUBLIC ID
    // =========================================================

    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponsePurchasePaymentDTO>>
    getPaymentByPublicId(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentByPublicId..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        ResponsePurchasePaymentDTO response =
                purchasePaymentService.getPaymentByPublicId(
                        publicId
                );

        log.info(
                "CONTROLLER - purchase payment fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Purchase payment fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PAYMENTS BY PURCHASE
    // =========================================================

    @GetMapping("/purchase/{purchasePublicId}")
    public ResponseEntity<APIResponse<List<ResponsePurchasePaymentDTO>>>
    getPaymentsByPurchase(
            @PathVariable UUID purchasePublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentsByPurchase..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        List<ResponsePurchasePaymentDTO> response =
                purchasePaymentService.getPaymentsByPurchase(
                        purchasePublicId
                );

        log.info(
                "CONTROLLER - payments fetched by purchase successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Purchase payments fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PAYMENTS BY SUPPLIER
    // =========================================================

    @GetMapping("/supplier/{supplierPublicId}")
    public ResponseEntity<APIResponse<List<ResponsePurchasePaymentDTO>>>
    getPaymentsBySupplier(
            @PathVariable UUID supplierPublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPaymentsBySupplier..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        List<ResponsePurchasePaymentDTO> response =
                purchasePaymentService.getPaymentsBySupplier(
                        supplierPublicId
                );

        log.info(
                "CONTROLLER - payments fetched by supplier successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Supplier purchase payments fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET PURCHASE PAYMENT SUMMARY
    // =========================================================

    @GetMapping("/purchase/{purchasePublicId}/summary")
    public ResponseEntity<APIResponse<ResponsePurchasePaymentDTO.PurchaseDetails>>
    getPurchasePaymentSummary(
            @PathVariable UUID purchasePublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPurchasePaymentSummary..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        ResponsePurchasePaymentDTO.PurchaseDetails response =
                purchasePaymentService.getPurchasePaymentSummary(
                        purchasePublicId
                );

        log.info(
                "CONTROLLER - purchase payment summary fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Purchase payment summary fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET ALL PENDING PAYMENTS (DASHBOARD)
    // =========================================================

    @GetMapping("/pending")
    public ResponseEntity<APIResponse<List<ResponsePurchasePaymentDTO.PurchaseDetails>>>
    getAllPendingPayments() {

        log.info(
                "CONTROLLER - request came in getAllPendingPayments..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        List<ResponsePurchasePaymentDTO.PurchaseDetails> response =
                purchasePaymentService.getAllPendingPayments();

        log.info(
                "CONTROLLER - all pending payments fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "All pending purchase payments fetched successfully...",
                                response
                        )
                );
    }

        // =========================================================
        // GET ALL PAYMENTS (PAGINATED)
        // =========================================================

        @GetMapping("/all")
        public ResponseEntity<APIResponse<org.springframework.data.domain.Page<ResponsePurchasePaymentDTO>>> getAllPayments(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size
        ) {

                log.info("CONTROLLER - request came in getAllPayments...");

                var pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "paymentDate"));

                var resp = purchasePaymentService.getAllPayments(pageable);

                return ResponseEntity.ok(new APIResponse<>("Purchase payments", resp));
        }


    // =========================================================
    // GET PENDING PAYMENTS BY SUPPLIER (DASHBOARD)
    // =========================================================

    @GetMapping("/pending/supplier/{supplierPublicId}")
    public ResponseEntity<APIResponse<List<ResponsePurchasePaymentDTO.PurchaseDetails>>>
    getPendingPaymentsBySupplier(
            @PathVariable UUID supplierPublicId
    ) {

        log.info(
                "CONTROLLER - request came in getPendingPaymentsBySupplier..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        List<ResponsePurchasePaymentDTO.PurchaseDetails> response =
                purchasePaymentService.getPendingPaymentsBySupplier(
                        supplierPublicId
                );

        log.info(
                "CONTROLLER - pending payments fetched by supplier successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Pending purchase payments by supplier fetched successfully...",
                                response
                        )
                );
    }


    // =========================================================
    // GET TOTAL OUTSTANDING AMOUNT (DASHBOARD SUMMARY)
    // =========================================================

    @GetMapping("/outstanding-total")
    public ResponseEntity<APIResponse<BigDecimal>>
    getTotalOutstandingAmount() {

        log.info(
                "CONTROLLER - request came in getTotalOutstandingAmount..."
        );

        log.info(
                "CONTROLLER - calling purchase payment service..."
        );

        BigDecimal response =
                purchasePaymentService.getTotalOutstandingAmount();

        log.info(
                "CONTROLLER - total outstanding amount fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Total outstanding purchase amount fetched successfully...",
                                response
                        )
                );
    }
}
