package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCustomerDTO {

    // =========================================================
    // CUSTOMER
    // =========================================================

    private UUID publicId;

    private String customerName;

    private String mobileNumber;

    private String contactPerson;

    private String alternateMobileNumber;

    private String email;

    private String gstNumber;


    // =========================================================
    // OPENING BALANCE
    // =========================================================

    private BigDecimal openingBalance;


    // =========================================================
    // PAYMENT TERMS
    // =========================================================

    private Integer paymentTerms;


    // =========================================================
    // ADDRESS
    // =========================================================

    private CustomerAddressDetails address;


    // =========================================================
    // STATUS
    // =========================================================

    private Boolean isActive;


    // =========================================================
    // AUDIT
    // =========================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =========================================================
    // CUSTOMER ADDRESS DETAILS
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerAddressDetails {

        private String addressLine1;

        private String addressLine2;

        private String city;

        private String state;

        private String country;

        private String pincode;
    }
}