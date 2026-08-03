package FinanceManangementSystem.demo.Service.Implementations;

import FinanceManangementSystem.demo.Model.RefreshToken;
import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.RefreshTokenRepository;
import FinanceManangementSystem.demo.Service.RefreshTokenInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class RefreshTokenService implements RefreshTokenInterface {

    private final RefreshTokenRepository refreshRepo;



    @Override
    public RefreshToken createToken(User user) {

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(7)
                        )
                        .build();


        return refreshRepo.save(refreshToken);
    }

    @Override
    public RefreshToken verifyToken(RefreshToken token) {

        if(token.getExpiryDate()
                .isBefore(LocalDateTime.now())){


            refreshRepo.delete(token);

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        return token;
    }
}
