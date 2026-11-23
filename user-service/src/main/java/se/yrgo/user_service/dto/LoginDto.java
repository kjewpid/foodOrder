package se.yrgo.user_service.dto;

public record LoginDto(
        String email,
        String password
) {
}
