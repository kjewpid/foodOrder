package se.yrgo.user_service.dto;

public record CreateUserDto(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
