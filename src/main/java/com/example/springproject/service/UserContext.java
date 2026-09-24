package com.example.springproject.service;

import com.example.springproject.entity.User;
import com.example.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserContext {
    private final UserRepository userRepository;

    @NonNull User getCurrentUser() {
        String currentUserEmail = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User with email " + currentUserEmail + " not found"));
    }

    public void checkNotBanned() {
        User currentUser = getCurrentUser();
        if (currentUser.getBanned()) {
            throw new AccessDeniedException("User is banned");
        }
    }
}
