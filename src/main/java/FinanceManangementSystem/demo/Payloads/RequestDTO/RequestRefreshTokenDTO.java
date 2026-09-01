package FinanceManangementSystem.demo.Payloads.RequestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class RequestRefreshTokenDTO {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
