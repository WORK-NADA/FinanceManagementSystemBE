package FinanceManangementSystem.demo.RequestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;


public class RequestAddressDTO {
    @NotNull(message = "House number is required")
    @Positive(message = "House number must be greater than 0")
    private Integer houseNo;

    @NotBlank(message = "Society name is required")
    @Size(min = 3, max = 100, message = "Society name must be between 3 and 100 characters")
    private String societyName;

    @NotBlank(message = "Area is required")
    @Size(min = 3, max = 100, message = "Area must be between 3 and 100 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 .,-]+$",
            message = "Area contains invalid characters"
    )
    private String area;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "City should contain only alphabets and spaces"
    )
    private String city;

//    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Pincode must be a valid 6-digit Indian pincode"
    )
    private String pincode;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "State should contain only alphabets and spaces"
    )
    private String state;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'India'")
    private String country;

    public RequestAddressDTO() {
    }

    public RequestAddressDTO(int houseNo, String societyName, String area,
                   String city, String pincode, String state,
                   String country) {
        this.houseNo = houseNo;
        this.societyName = societyName;
        this.area = area;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.country = country;
    }

    public Integer getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(Integer houseNo) {
        this.houseNo = houseNo;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}