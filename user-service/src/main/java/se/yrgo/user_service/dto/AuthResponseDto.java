package se.yrgo.user_service.dto;

public record AuthResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {}