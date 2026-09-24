package com.example.springproject.service;

import com.example.springproject.dto.UserResponse;
import com.example.springproject.entity.UserRole;
import com.example.springproject.entity.User;
import com.example.springproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserContext context;

    public void registerUser(String name, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        userRepository.save(user);
    }

    public UserDetails verifyUser(String email, String rawPassword) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        return buildUserDetails(user);
    }

    public UserDetails loadUserByEmail(String email) {
        User user = findByEmail(email);

        return buildUserDetails(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getUserId(), user.getName(), user.getEmail(), user.getRole(), user.getBanned()))
                .toList();
    }

    public void toggleBan(Long userId) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found"));
        User current = context.getCurrentUser();

        if (current.getUserId().equals(target.getUserId())) {
            throw new RuntimeException("You cannot ban yourself");
        }

        target.setBanned(!target.getBanned());
        userRepository.save(target);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
