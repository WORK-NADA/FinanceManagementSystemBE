package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestPartnerDTO {

    @NotBlank(message = "Partner name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Partner name must be between 2 and 150 characters"
    )
    private String partnerName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Invalid mobile number format"
    )
    private String mobileNumber;

    @Email(message = "Invalid email address format")
    @Size(
            max = 150,
            message = "Email cannot exceed 150 characters"
    )
    private String email;

    @NotNull(message = "Share percentage is required")
    @DecimalMin(
            value = "0.01",
            message = "Share percentage must be at least 0.01"
    )
    @DecimalMax(
            value = "100.00",
            message = "Share percentage cannot exceed 100.00"
    )
    private BigDecimal sharePercentage;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
}
