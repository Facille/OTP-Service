package com.example.otpservice.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String operationId;
    private String channel; // EMAIL, SMS, TELEGRAM, FILE
}