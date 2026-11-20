package se.yrgo.user_service.entity;

import jakarta.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streetName;
    private String city;
    private String zipCode;
    private String country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(){}

    public Address(String streetName, String city, String zipCode, String country, User user) {
        this.streetName = streetName;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
