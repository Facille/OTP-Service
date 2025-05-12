package com.example.otpservice.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OtpConfig {
    private Integer id;
    private int codeLength;
    private int expirationMinutes;
    private LocalDateTime updatedAt;
}