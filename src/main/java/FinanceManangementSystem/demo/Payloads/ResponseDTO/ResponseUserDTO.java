package FinanceManangementSystem.demo.Payloads.ResponseDTO;

public class ResponseUserDTO {
    private String name;

    private String email;

    private String password;

    private long contact;

    private String role;

    private ResponseAddressDTO address;

    public ResponseUserDTO() {
    }

    public ResponseUserDTO(String name, String email, String password, long contact, String role, ResponseAddressDTO address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.address = address;
    }

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

    public ResponseAddressDTO getAddress() {
        return address;
    }

    public void setAddress(ResponseAddressDTO address) {
        this.address = address;
    }
}
