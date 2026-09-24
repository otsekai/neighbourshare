package com.example.springproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
