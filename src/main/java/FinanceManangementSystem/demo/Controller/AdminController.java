package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.RequestDTO.RequestUserDTO;
import FinanceManangementSystem.demo.ResponseDTO.ResponseUserDTO;
import FinanceManangementSystem.demo.Service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    AdminService service;

    @PostMapping("register")
    public ResponseEntity<APIResponse<ResponseUserDTO>> register(@Valid @RequestBody RequestUserDTO dto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>("Registered successfully...",
                                service.registration(dto))
                );
    }
}
