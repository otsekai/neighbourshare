package com.example.springproject.controller;

import com.example.springproject.dto.ItemRequest;
import com.example.springproject.dto.ItemResponse;
import com.example.springproject.entity.Item;
import com.example.springproject.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("")
    public ResponseEntity<List<ItemResponse>> findAvailableItemsForUser() {
        try {
            List<Item> items = itemService.findAvailableItemsForUser();

            return getListResponseEntity(items);
        } catch (final Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<ItemResponse>> findMyItems() {
        try {
            List<Item> items = itemService.findItemsForOwner();

            return getListResponseEntity(items);
        } catch (final Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody ItemRequest request) {
        itemService.createItem(request.title(), request.category());
        return ResponseEntity.ok("Item created successfully");
    }

    @NonNull
    private ResponseEntity<List<ItemResponse>> getListResponseEntity(List<Item> items) {
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ItemResponse> response = items.stream()
                .map(item -> new ItemResponse(
                        item.getItemId(),
                        item.getTitle(),
                        item.getCategory(),
                        item.getActualStatus(),
                        item.getOwner().getUserId(),
                        item.getOwner().getName()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
