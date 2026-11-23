package se.yrgo.user_service.dto;

public record UserDto(
        String firstName,
        String lastName,
        String email
) {
}
