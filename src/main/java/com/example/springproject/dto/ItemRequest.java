package com.example.springproject.dto;

import com.example.springproject.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Category is required")
        Category category
) {
}
