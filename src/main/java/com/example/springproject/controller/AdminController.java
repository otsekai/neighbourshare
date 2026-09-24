package com.example.springproject.controller;

import com.example.springproject.dto.UserResponse;
import com.example.springproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<String> toggleBan(@PathVariable Long id) {
        userService.toggleBan(id);
        return ResponseEntity.ok("User ban successfully");
    }
}
