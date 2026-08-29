package FinanceManangementSystem.demo.Payloads.RequestDTO;

import FinanceManangementSystem.demo.Enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class RequestSalePaymentDTO {

    // =========================================================
    // SALE
    // =========================================================

    @NotNull(message = "Sale is required")
    private UUID salePublicId;


    // =========================================================
    // AMOUNT
    // =========================================================

    @NotNull(message = "Payment amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Payment amount must be greater than zero"
    )
    @Digits(
            integer = 13,
            fraction = 2,
            message = "Payment amount must have maximum 13 integer digits and 2 decimal places"
    )
    private BigDecimal amount;


    // =========================================================
    // PAYMENT METHOD
    // =========================================================

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;


    // =========================================================
    // REFERENCE NUMBER
    // =========================================================

    @Size(
            max = 100,
            message = "Reference number cannot exceed 100 characters"
    )
    private String referenceNumber;


    // =========================================================
    // PAYMENT DATE
    // =========================================================

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;


    // =========================================================
    // NOTES
    // =========================================================

    @Size(
            max = 500,
            message = "Notes cannot exceed 500 characters"
    )
    private String notes;
}