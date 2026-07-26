package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;

@Entity(name = "supplier_address")
public class SupplierAddress{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int houseNo;

    private String societyName;

    private String area;

    private String city;

    private int pincode;

    private String state;

    private String country;

    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @PrePersist
    public void prePersist(){
        if(this.country == null){
            this.country = "India";
        }
    }

    public SupplierAddress() {
    }

    public SupplierAddress(int houseNo, String societyName, String area, String city, int pincode, String state, String country, Supplier supplier) {
        this.houseNo = houseNo;
        this.societyName = societyName;
        this.area = area;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.country = country;
        this.supplier = supplier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(int houseNo) {
        this.houseNo = houseNo;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
