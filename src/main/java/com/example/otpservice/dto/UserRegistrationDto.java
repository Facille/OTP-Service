package com.example.otpservice.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String role; // ADMIN или USER
}
