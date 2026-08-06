package FinanceManangementSystem.demo.Controller;

import FinanceManangementSystem.demo.Model.RefreshToken;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestRefreshTokenDTO;
import FinanceManangementSystem.demo.Repository.RefreshTokenRepository;
import FinanceManangementSystem.demo.Security.JwtUtil;
import FinanceManangementSystem.demo.Service.Implementations.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenRepository refreshTokenRepo;

    private final JwtUtil jwtUtil;


    @PostMapping("refresh")
    public ResponseEntity<?> refresh(@RequestBody RequestRefreshTokenDTO request){
        log.info("CONTROLLER - request came in refresh token controller...");

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
        log.info("CONTROLLER - refresh token in not expired...");


        String newAccessToken =
                jwtUtil.generateToken(
                        refreshToken.getUser()
                );

        log.info("CONTROLLER - new access token sent successfully...");
        return ResponseEntity.ok(
                Map.of(
                        "accessToken",
                        newAccessToken
                )
        );
    }
}
