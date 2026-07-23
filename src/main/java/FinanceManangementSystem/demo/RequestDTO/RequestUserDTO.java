package FinanceManangementSystem.demo.RequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


public class RequestUserDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Name should contain only alphabets and spaces"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")

    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String password;

    @NotNull(message = "Contact number is required")
    @Min(value = 1000000000L, message = "Contact number must be 10 digits")
    @Max(value = 9999999999L, message = "Contact number must be 10 digits")
    private Long contact;

    @Valid
    @NotNull(message = "Address is required")
    private RequestAddressDTO address;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getContact() {
        return contact;
    }

    public void setContact(Long contact) {
        this.contact = contact;
    }

    public RequestAddressDTO getAddress() {
        return address;
    }

    public void setAddress(RequestAddressDTO address) {
        this.address = address;
    }
}