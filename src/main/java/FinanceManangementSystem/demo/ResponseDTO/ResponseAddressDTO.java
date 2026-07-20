package FinanceManangementSystem.demo.ResponseDTO;

public class ResponseAddressDTO {
    private int houseNo;

    private String societyName;

    private String area;

    private String city;

    private String pincode;

    private String state;

    private String country;

    public ResponseAddressDTO() {
    }

    public ResponseAddressDTO(int houseNo, String societyName, String area,
                              String city, String pincode, String state,
                              String country) {
        this.houseNo = houseNo;
        this.societyName = societyName;
        this.area = area;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.country = country;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
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
}