package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.RefreshTokenRepository;
import FinanceManangementSystem.demo.Repository.UserRepository;
import FinanceManangementSystem.demo.Payloads.RequestDTO.RequestLoginDTO;
import FinanceManangementSystem.demo.Payloads.ResponseDTO.ResponseLoginDTO;
import FinanceManangementSystem.demo.Security.JwtUtil;
import FinanceManangementSystem.demo.Service.CommonServiceInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CommonService implements CommonServiceInterface {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepo;

    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenRepository refreshRepo;


    @Override
    public ResponseLoginDTO login(RequestLoginDTO dto) {
        System.out.println("Login initiated...");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        Optional<User> checkUser = userRepo.findByEmail(dto.getEmail());
        if(checkUser.isEmpty()){
            throw new RuntimeException("User not found...");
        }

        User user = checkUser.get();

        // Access Token
        String token =
                jwtUtil.generateToken(user);

        //delete old refresh token when not expired and login again.
        refreshRepo.deleteByUser(user.getUserId());

        // Refresh Token
        return ResponseLoginDTO.builder()

                .accessToken(token)

                .refreshToken(
                        refreshTokenService.createToken(user).getToken()
                )

                .tokenType("Bearer")

                .publicId(
                        user.getPublicId()
                )

                .ownerName(
                        user.getOwnerName()
                )

                .userName(
                        user.getUsername()
                )

                .email(
                        user.getEmail()
                )

                .role(
                        user.getRole().name()
                )

                .firstLogin(
                        user.getFirstLogin()
                )

                .loginTime(
                        LocalDateTime.now()
                )

                .build();
    }
}
