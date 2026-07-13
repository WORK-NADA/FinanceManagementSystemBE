package FinanceManangementSystem.demo.ResponseDTO;

import FinanceManangementSystem.demo.Model.Address;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;

public class ResponseUserDTO {
    private String name;

    private String email;

    private String password;

    private long contact;

    private String role;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
