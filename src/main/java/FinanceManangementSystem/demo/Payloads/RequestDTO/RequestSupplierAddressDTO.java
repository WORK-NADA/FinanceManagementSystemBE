package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSupplierAddressDTO {

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