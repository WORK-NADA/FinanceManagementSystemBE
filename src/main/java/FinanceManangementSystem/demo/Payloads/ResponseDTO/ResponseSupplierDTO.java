package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSupplierDTO {
    private String name;

    private long contact;

    private String email;

    private String gstNo;

    private String panNo;

    private ResponseUserAddressDTO address;


}
