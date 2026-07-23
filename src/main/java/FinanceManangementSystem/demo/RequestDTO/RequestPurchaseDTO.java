package FinanceManangementSystem.demo.RequestDTO;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class RequestPurchaseDTO {

    @NotNull(message = "Supplier is required")
    @Positive(message = "Invalid supplier ID")
    private Integer supplierId;

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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

}
