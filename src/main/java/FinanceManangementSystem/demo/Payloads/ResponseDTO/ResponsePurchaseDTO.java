package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePurchaseDTO {

    private ResponseSufficientSupplierDTO supplier;

    private String item;

    private double price;

    private double weight;

    private LocalDate date;

    private double amount;

    private double gst;

    private double withGstAmount;

}
