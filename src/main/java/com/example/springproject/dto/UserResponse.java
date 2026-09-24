package com.example.springproject.dto;

import com.example.springproject.entity.UserRole;

public record UserResponse(
        Long userId,
        String name,
        String email,
        UserRole role,
        Boolean banned
) {
}
