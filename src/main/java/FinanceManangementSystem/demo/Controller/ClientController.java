package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestPurchaseDTO;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestSupplierDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponsePurchaseDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseSupplierDTO;
import FinanceManangementSystem.demo.Service.Implementations.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
public class ClientController {

    @Autowired
    ClientService service;

    @PostMapping("addSupplier")
    public ResponseEntity<APIResponse<ResponseSupplierDTO>> addSupplier(@Valid @RequestBody RequestSupplierDTO dto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Supplier added successfully...",
                                service.addSupplier(dto)
                        )
                );
    }

    @PostMapping("addPurchase")
    public ResponseEntity<APIResponse<ResponsePurchaseDTO>> addPurchase(@Valid @RequestBody RequestPurchaseDTO dto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>(
                                "Purchased successfully...",
                                service.addPurchase(dto)
                        )
                );
    }
}
