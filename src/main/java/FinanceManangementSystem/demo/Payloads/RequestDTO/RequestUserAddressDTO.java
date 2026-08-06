package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserAddressDTO {

    @NotBlank(message = "House number is required.")
    @Size(max = 20, message = "House number cannot exceed 20 characters.")
    private String houseNo;

    @NotBlank(message = "Society name is required.")
    @Size(min = 2, max = 100, message = "Society name must be between 2 and 100 characters.")
    private String societyName;

    @NotBlank(message = "Area is required.")
    @Size(min = 2, max = 100, message = "Area must be between 2 and 100 characters.")
    private String area;

    @NotBlank(message = "City is required.")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters.")
    private String city;

    @NotBlank(message = "Pincode is required.")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Pincode must be a valid 6-digit Indian pincode."
    )
    private String pincode;

    @NotBlank(message = "State is required.")
    @Size(min = 2, max = 100, message = "State must be between 2 and 100 characters.")
    private String state;

    @Size(max = 100, message = "Country cannot exceed 100 characters.")
    private String country;

}