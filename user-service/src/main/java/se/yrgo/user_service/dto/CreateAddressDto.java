package se.yrgo.user_service.dto;

public record CreateAddressDto(
        String label,
        String streetName,
        String city,
        String zipCode,
        String country
) {}