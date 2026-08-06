package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestSupplierDTO {
    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "Supplier name can contain only alphabets and single spaces"
    )
    private String name;

    @Min(value = 6000000000L, message = "Contact number must be a valid 10-digit Indian mobile number")
    @Max(value = 9999999999L, message = "Contact number must be a valid 10-digit Indian mobile number")
    private Long contact;

//    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$",
            message = "Enter a valid GST number"
    )
    private String gstNo;

    @NotBlank(message = "PAN number is required")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$",
            message = "Enter a valid PAN number"
    )
    private String panNo;

//    @NotNull(message = "Address is required")
    @Valid
    private RequestUserAddressDTO address;

}
