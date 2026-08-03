package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserAddressDTO {
    private String houseNo;

    private String societyName;

    private String area;

    private String city;

    private String pincode;

    private String state;

    private String country;
}