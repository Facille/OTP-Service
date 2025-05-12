package com.example.otpservice.util;

import java.security.MessageDigest;
import java.util.Base64;

public class PasswordHasher {
    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    public boolean verify(String inputPassword, String storedHash) {
        String hashedInput = hash(inputPassword);
        return hashedInput.equals(storedHash);
    }
}