package com.example.springproject.dto;

import com.example.springproject.entity.Category;
import com.example.springproject.entity.ItemStatus;

public record ItemResponse(
        Long id,
        String title,
        Category category,
        ItemStatus actualStatus,
        Long ownerId,
        String ownerName
) {
}
