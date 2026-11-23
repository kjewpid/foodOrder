package se.yrgo.user_service.dto;

public record AddressDto(
        Long id,
        String label,
        String streetName,
        String city,
        String zipCode,
        String country
) {
}
