package com.example.otpservice.dto;

import lombok.Data;

@Data
public class OtpVerificationDto {
    private String operationId;
    private String code;
}