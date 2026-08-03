package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.Model.RefreshToken;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestRefreshTokenDTO;
import FinanceManangementSystem.demo.Repository.RefreshTokenRepository;
import FinanceManangementSystem.demo.Security.JwtUtil;
import FinanceManangementSystem.demo.Service.Implementations.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenRepository refreshTokenRepo;

    private final JwtUtil jwtUtil;


    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody RequestRefreshTokenDTO request){
        RefreshToken refreshToken =
                refreshTokenRepo
                        .findByToken(
                                request.getRefreshToken()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );


        refreshTokenService.verifyToken(refreshToken);



        String newAccessToken =
                jwtUtil.generateToken(
                        refreshToken.getUser()
                );


        return ResponseEntity.ok(
                Map.of(
                        "accessToken",
                        newAccessToken
                )
        );
    }
}
