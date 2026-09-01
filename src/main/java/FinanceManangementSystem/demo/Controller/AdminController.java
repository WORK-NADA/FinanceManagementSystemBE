package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseUserDTO;
import FinanceManangementSystem.demo.Service.Implementations.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    AdminService service;

    @PostMapping("register")
    public ResponseEntity<APIResponse<ResponseUserDTO>> register(@Valid @RequestBody RequestUserDTO dto){
        log.info("CONTROLLER - request came in register controller...");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>("Registered successfully...",
                                service.registration(dto))
                );
    }
    @GetMapping("users")
    public ResponseEntity<APIResponse<java.util.List<ResponseUserDTO>>> listUsers() {
        log.info("CONTROLLER - request came in listUsers...");
        return ResponseEntity.ok(new APIResponse<>("Users fetched successfully...", service.listAllUsers()));
    }
}
