package FinanceManangementSystem.demo.ResponseDTO;

public class ResponseSupplierDTO {
    private String name;

    private long contact;

    private String email;

    private String gstNo;

    private String panNo;

    private ResponseAddressDTO address;

    public ResponseSupplierDTO() {
    }

    public ResponseSupplierDTO(String name, long contact, String email, String GSTno, String PANno, ResponseAddressDTO address) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.gstNo = GSTno;
        this.panNo = PANno;
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public ResponseAddressDTO getAddress() {
        return address;
    }

    public void setAddress(ResponseAddressDTO address) {
        this.address = address;
    }
}
