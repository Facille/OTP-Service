package com.example.otpservice.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OtpCode {
    private Integer id;
    private String code;
    private Integer userId;
    private String operationId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String channel;
}