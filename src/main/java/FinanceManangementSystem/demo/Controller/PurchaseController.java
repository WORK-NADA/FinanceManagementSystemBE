package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.Service.PurchaseServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseServiceInterface purchaseService;


    @PostMapping("add")
    public ResponseEntity<APIResponse<ResponsePurchaseDTO>> addPurchase(
            @Valid @RequestBody RequestPurchaseDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addPurchase..."
        );


        log.info(
                "CONTROLLER - calling purchase service..."
        );

        ResponsePurchaseDTO response =
                purchaseService.addPurchase(dto);


        log.info(
                "CONTROLLER - purchase added successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Purchase added successfully...",
                                response
                        )
                );
    }


    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponsePurchaseDTO>>
    getPurchaseByPublicId(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in getPurchaseByPublicId..."
        );


        log.info(
                "CONTROLLER - calling purchase service..."
        );

        ResponsePurchaseDTO response =
                purchaseService.getPurchaseByPublicId(
                        publicId
                );


        log.info(
                "CONTROLLER - purchase fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Purchase fetched successfully...",
                                response
                        )
                );
    }
}