package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseSupplierDTO {

    private UUID publicId;

    private String supplierName;

    private String mobileNumber;

    private String contactPerson;

    private String alternateMobileNumber;

    private String email;

    private String gstNumber;

    private BigDecimal openingBalance;

    private Integer paymentTerms;

    private Boolean isActive;

    private ResponseSupplierAddressDTO address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}