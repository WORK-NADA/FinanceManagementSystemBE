package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCustomerDTO {

    // =========================================================
    // CUSTOMER DETAILS
    // =========================================================

    @NotBlank(message = "Customer name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Customer name must be between 2 and 150 characters"
    )
    private String customerName;


    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Mobile number must contain exactly 10 digits"
    )
    private String mobileNumber;


    @Size(
            max = 100,
            message = "Contact person cannot exceed 100 characters"
    )
    private String contactPerson;


    @Pattern(
            regexp = "^$|^[6-9][0-9]{9}$",
            message = "Alternate mobile number must contain exactly 10 digits"
    )
    private String alternateMobileNumber;


    // =========================================================
    // EMAIL
    // =========================================================

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(
            max = 150,
            message = "Email cannot exceed 150 characters"
    )
    private String email;


    // =========================================================
    // GST
    // =========================================================

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$",
            message = "Invalid GST number"
    )
    private String gstNumber;


    // =========================================================
    // OPENING BALANCE
    // =========================================================

    @NotNull(message = "Opening balance is required")
    @DecimalMin(
            value = "0.00",
            message = "Opening balance cannot be negative"
    )
    @Digits(
            integer = 13,
            fraction = 2,
            message = "Opening balance must have maximum 13 integer digits and 2 decimal places"
    )
    private BigDecimal openingBalance;


    // =========================================================
    // PAYMENT TERMS
    // =========================================================

    @NotNull(message = "Payment terms are required")
    @Min(
            value = 0,
            message = "Payment terms cannot be negative"
    )
    @Max(
            value = 365,
            message = "Payment terms cannot exceed 365 days"
    )
    private Integer paymentTerms;


    // =========================================================
    // ADDRESS
    // =========================================================

    @NotNull(message = "Address is required")
    @Valid
    private CustomerAddressDTO address;


    // =========================================================
    // CUSTOMER ADDRESS DTO
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerAddressDTO {

        @NotBlank(message = "Address line 1 is required")
        @Size(
                min = 3,
                max = 150,
                message = "Address line 1 must be between 3 and 150 characters"
        )
        private String addressLine1;


        @Size(
                max = 150,
                message = "Address line 2 cannot exceed 150 characters"
        )
        private String addressLine2;


        @NotBlank(message = "City is required")
        @Size(
                min = 2,
                max = 100,
                message = "City must be between 2 and 100 characters"
        )
        private String city;


        @NotBlank(message = "State is required")
        @Size(
                min = 2,
                max = 100,
                message = "State must be between 2 and 100 characters"
        )
        private String state;


        @NotBlank(message = "Country is required")
        @Size(
                min = 2,
                max = 100,
                message = "Country must be between 2 and 100 characters"
        )
        private String country = "India";


        @NotBlank(message = "Pincode is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "Pincode must contain exactly 6 digits"
        )
        private String pincode;
    }
}