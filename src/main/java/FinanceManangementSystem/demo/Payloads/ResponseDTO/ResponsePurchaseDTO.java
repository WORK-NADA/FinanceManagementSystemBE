package FinanceManangementSystem.demo.Payloads.ResponseDTO;

import java.time.LocalDate;

public class ResponsePurchaseDTO {

    private ResponseSufficientSupplierDTO supplier;

    private String item;

    private double price;

    private double weight;

    private LocalDate date;

    private double amount;

    private double gst;

    private double withGstAmount;

    public ResponsePurchaseDTO(ResponseSufficientSupplierDTO supplier, String item, double price, double weight, LocalDate date, double amount, double gst, double withGstAmount) {
        this.supplier = supplier;
        this.item = item;
        this.price = price;
        this.weight = weight;
        this.date = date;
        this.amount = amount;
        this.gst = gst;
        this.withGstAmount = withGstAmount;
    }

    public ResponsePurchaseDTO() {
    }

    public ResponseSufficientSupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(ResponseSufficientSupplierDTO supplier) {
        this.supplier = supplier;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getGst() {
        return gst;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    public double getWithGstAmount() {
        return withGstAmount;
    }

    public void setWithGstAmount(double withGstAmount) {
        this.withGstAmount = withGstAmount;
    }
}
