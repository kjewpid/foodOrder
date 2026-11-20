package se.yrgo.user_service.dto;

public class AddressDto {
    private Long id;
    private String streetName;
    private String city;
    private String zipCode;
    private String country;

    public AddressDto(){

    }

    public AddressDto(Long id, String streetName, String city, String zipCode, String country) {
        this.id = id;
        this.streetName = streetName;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
