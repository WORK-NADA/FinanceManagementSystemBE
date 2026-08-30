package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePartnerDTO {

    private UUID publicId;

    private String partnerName;

    private String mobileNumber;

    private String email;

    private BigDecimal sharePercentage;

    private LocalDate joiningDate;

    private Boolean isActive;

    private BigDecimal lifetimeEarnings;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
