package com.example.otpservice.api.user;

import com.example.otpservice.api.JsonHandler;
import com.example.otpservice.dto.OtpRequest;
import com.example.otpservice.dto.OtpVerificationDto;
import com.example.otpservice.service.OtpService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class OtpApi extends JsonHandler {
    private final OtpService otpService;

    public OtpApi(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 5) {
                sendError(exchange, "Invalid path", 400);
                return;
            }

            int userId = Integer.parseInt(parts[4]);

            if ("POST".equals(exchange.getRequestMethod())) {
                if (path.endsWith("/generate")) {
                    OtpRequest request = parseRequest(exchange, OtpRequest.class);
                    String code = otpService.generateOtpCode(userId, request);
                    sendResponse(exchange, Map.of("code", code), 200);
                }
                else if (path.endsWith("/verify")) {
                    OtpVerificationDto request = parseRequest(exchange, OtpVerificationDto.class);
                    boolean isValid = otpService.verifyOtp(userId, request);
                    sendResponse(exchange, Map.of("valid", isValid), 200);
                }
            } else {
                sendError(exchange, "Method not allowed", 405);
            }
        } catch (NumberFormatException e) {
            sendError(exchange, "Invalid user ID", 400);
        } catch (Exception e) {
            sendError(exchange, "Internal server error", 500);
        }
    }
}