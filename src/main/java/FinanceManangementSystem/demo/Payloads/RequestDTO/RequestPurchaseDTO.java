package FinanceManangementSystem.demo.Payloads.RequestDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestPurchaseDTO {

    @NotNull(message = "Supplier is required")
    @Positive(message = "Invalid supplier ID")
    private Long supplierId;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 100, message = "Item name must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9]+(?:[ A-Za-z0-9()-]*)?$",
            message = "Item name can contain letters, numbers, spaces, parentheses and hyphens only"
    )
    private String item;

    @Positive(message = "Price must be greater than 0")
    @DecimalMax(value = "10000000.00", message = "Price is too large")
    private Double price;

    @Positive(message = "Weight must be greater than 0")
    @DecimalMax(value = "100000.00", message = "Weight is too large")
    private Double weight;

    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate date;

    @PositiveOrZero(message = "GST cannot be negative")
    @DecimalMax(value = "100.00", message = "GST cannot exceed 100%")
    private Double gst;

}
