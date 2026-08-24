package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSupplierDTO {

    @NotBlank(message = "Supplier name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Supplier name must be between 2 and 150 characters"
    )
    private String supplierName;


    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Mobile number must contain exactly 10 digits and start with 6-9"
    )
    private String mobileNumber;


    @Size(
            max = 100,
            message = "Contact person cannot exceed 100 characters"
    )
    private String contactPerson;


    @Pattern(
            regexp = "^$|^[6-9][0-9]{9}$",
            message = "Alternate mobile number must contain exactly 10 digits and start with 6-9"
    )
    private String alternateMobileNumber;


    @Email(message = "Enter a valid email address")
    @Size(
            max = 150,
            message = "Email cannot exceed 150 characters"
    )
    private String email;


    @Pattern(
            regexp = "^$|^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$",
            message = "Enter a valid GST number"
    )
    private String gstNumber;


    @DecimalMin(
            value = "0.00",
            message = "Opening balance cannot be negative"
    )
    @Digits(
            integer = 13,
            fraction = 2,
            message = "Opening balance can have maximum 13 integer digits and 2 decimal places"
    )
    private BigDecimal openingBalance = BigDecimal.ZERO;


    @NotNull(message = "Payment terms are required")
    @Min(
            value = 0,
            message = "Payment terms cannot be negative"
    )
    @Max(
            value = 365,
            message = "Payment terms cannot exceed 365 days"
    )
    private Integer paymentTerms = 30;


    @Valid
    private RequestSupplierAddressDTO address;
}