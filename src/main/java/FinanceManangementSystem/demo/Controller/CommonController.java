package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.APIResponse.APIResponse;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestLoginDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseLoginDTO;
import FinanceManangementSystem.demo.Service.Implementations.CommonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class CommonController {

    @Autowired
    CommonService service;

    @PostMapping("login")
    public ResponseEntity<APIResponse<ResponseLoginDTO>> login(@Valid @RequestBody RequestLoginDTO dto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new APIResponse<>("Logged in successfully...",
                                service.login(dto))
                );
    }
}
