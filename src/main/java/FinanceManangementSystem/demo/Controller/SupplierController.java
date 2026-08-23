package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierDTO;
import FinanceManangementSystem.demo.Service.SupplierServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierServiceInterface supplierService;


    @PostMapping("add")
    public ResponseEntity<APIResponse<ResponseSupplierDTO>> addSupplier(
            @Valid @RequestBody RequestSupplierDTO dto
    ) {

        log.info("CONTROLLER - request came in addSupplier...");


        log.info("CONTROLLER - calling supplier service...");

        ResponseSupplierDTO response =
                supplierService.addSupplier(dto);


        log.info("CONTROLLER - supplier added successfully...");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Supplier added successfully...",
                                response
                        )
                );
    }


    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSupplierDTO>> getSupplierByPublicId(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in getSupplierByPublicId..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        ResponseSupplierDTO response =
                supplierService.getSupplierByPublicId(publicId);


        log.info(
                "CONTROLLER - supplier fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Supplier fetched successfully...",
                                response
                        )
                );
    }
}