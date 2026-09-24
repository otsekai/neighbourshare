package com.example.springproject.dto;

public record AuthResponse(
        String token,
        String role
) {
}
