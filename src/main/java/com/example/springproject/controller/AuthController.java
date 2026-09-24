package com.example.springproject.controller;

import com.example.springproject.component.JwtCore;
import com.example.springproject.dto.AuthResponse;
import com.example.springproject.dto.LoginRequest;
import com.example.springproject.dto.RegisterRequest;
import com.example.springproject.entity.User;
import com.example.springproject.exception.AppException;
import com.example.springproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtCore jwtCore;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request.name(), request.email(), request.password());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserDetails userDetails = userService.verifyUser(request.email(), request.password());

            String token = jwtCore.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                    .findFirst()
                    .orElse("USER")));
        } catch (final RuntimeException e) {
            throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
