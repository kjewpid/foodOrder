package se.yrgo.user_service.dto;


public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
) {
}
