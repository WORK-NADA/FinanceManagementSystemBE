package FinanceManangementSystem.demo.Payloads.ResponseDTO;

public class ResponseSufficientSupplierDTO {
    private int supplierId;

    private String name;

    private long contact;

    public ResponseSufficientSupplierDTO() {
    }

    public ResponseSufficientSupplierDTO(int supplierId, String supplierName, long supplierContact) {
        this.supplierId = supplierId;
        this.name = supplierName;
        this.contact = supplierContact;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }
}
