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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierServiceInterface supplierService;


    // ==================================================
    // ADD SUPPLIER
    // ==================================================

    @PostMapping("add")
    public ResponseEntity<APIResponse<ResponseSupplierDTO>> addSupplier(
            @Valid @RequestBody RequestSupplierDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in addSupplier..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        ResponseSupplierDTO response =
                supplierService.addSupplier(dto);


        log.info(
                "CONTROLLER - supplier added successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse<>(
                                "Supplier added successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // GET SUPPLIER
    // ==================================================

    @GetMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSupplierDTO>>
    getSupplierByPublicId(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in getSupplierByPublicId..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        ResponseSupplierDTO response =
                supplierService.getSupplierByPublicId(
                        publicId
                );


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


    // ==================================================
    // GET ALL SUPPLIERS
    // ==================================================

    @GetMapping("all")
    public ResponseEntity<APIResponse<List<ResponseSupplierDTO>>>
    getAllSuppliers() {

        log.info(
                "CONTROLLER - request came in getAllSuppliers..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        List<ResponseSupplierDTO> response =
                supplierService.getAllSuppliers();


        log.info(
                "CONTROLLER - suppliers fetched successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Suppliers fetched successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // UPDATE SUPPLIER
    // ==================================================

    @PutMapping("/{publicId}")
    public ResponseEntity<APIResponse<ResponseSupplierDTO>>
    updateSupplier(
            @PathVariable UUID publicId,
            @Valid @RequestBody RequestSupplierDTO dto
    ) {

        log.info(
                "CONTROLLER - request came in updateSupplier..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        ResponseSupplierDTO response =
                supplierService.updateSupplier(
                        publicId,
                        dto
                );


        log.info(
                "CONTROLLER - supplier updated successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Supplier updated successfully...",
                                response
                        )
                );
    }


    // ==================================================
    // DEACTIVATE SUPPLIER
    // ==================================================

    @PatchMapping("/{publicId}/deactivate")
    public ResponseEntity<APIResponse<Void>>
    deactivateSupplier(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in deactivateSupplier..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        supplierService.deactivateSupplier(publicId);


        log.info(
                "CONTROLLER - supplier deactivated successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Supplier deactivated successfully...",
                                null
                        )
                );
    }


    // ==================================================
    // ACTIVATE SUPPLIER
    // ==================================================

    @PatchMapping("/{publicId}/activate")
    public ResponseEntity<APIResponse<Void>>
    activateSupplier(
            @PathVariable UUID publicId
    ) {

        log.info(
                "CONTROLLER - request came in activateSupplier..."
        );


        log.info(
                "CONTROLLER - calling supplier service..."
        );

        supplierService.activateSupplier(publicId);


        log.info(
                "CONTROLLER - supplier activated successfully..."
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Supplier activated successfully...",
                                null
                        )
                );
    }
}