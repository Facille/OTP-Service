package com.example.otpservice.service;

import com.example.otpservice.dao.UserDao;
import com.example.otpservice.dto.AuthRequest;
import com.example.otpservice.dto.AuthResponse;
import com.example.otpservice.dto.UserRegistrationDto;
import com.example.otpservice.model.User;
import com.example.otpservice.util.PasswordHasher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserDao userDao;
    private final PasswordHasher passwordHasher;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
        this.passwordHasher = new PasswordHasher();
    }

    public Optional<AuthResponse> authenticate(AuthRequest request) {
        Optional<User> userOpt = userDao.findByUsername(request.getUsername());
        if (userOpt.isEmpty() || !passwordHasher.verify(request.getPassword(), userOpt.get().getPassword())) {
            return Optional.empty();
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString(); // Временный токен

        return Optional.of(new AuthResponse(token, user.getUsername(), user.getRole()));
    }

    public User registerUser(UserRegistrationDto dto) {
        if (userDao.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if ("ADMIN".equals(dto.getRole()) && userDao.existsAdmin()) {
            throw new IllegalStateException("Admin already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordHasher.hash(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setCreatedAt(LocalDateTime.now());

        return userDao.save(user);
    }
}