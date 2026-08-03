package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseLoginDTO {
    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private UUID publicId;

    private String ownerName;

    private String userName;

    private String email;

    private String role;

    private Boolean firstLogin;

    private LocalDateTime loginTime;
}
