package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;

@Entity(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int supplierId;

    private String name;

    private long contact;

    private String email;

    private String gstNo;

    private String panNo;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "supplier")
    private SupplierAddress address;

    public Supplier() {
    }

    public Supplier(String name, long contact, String email, String GSTno, String PANno, SupplierAddress address) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.gstNo = GSTno;
        this.panNo = PANno;
        this.address = address;
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

    public SupplierAddress getAddress() {
        return address;
    }

    public void setAddress(SupplierAddress address) {
        this.address = address;
    }
}
