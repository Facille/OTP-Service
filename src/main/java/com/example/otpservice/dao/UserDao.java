package com.example.otpservice.dao;

import com.example.otpservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsAdmin();
    void delete(Integer userId);
    List<User> findAll(); // Добавьте этот метод
    String getRole(Integer userId);
    Optional<User> findById(Integer id);
}

