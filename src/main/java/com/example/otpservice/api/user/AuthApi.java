// src/main/java/com/example/otpservice/api/user/AuthApi.java
package com.example.otpservice.api.user;

import com.example.otpservice.api.JsonHandler;
import com.example.otpservice.dto.AuthRequest;
import com.example.otpservice.dto.AuthResponse;
import com.example.otpservice.dto.UserRegistrationDto;
import com.example.otpservice.service.AuthService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class AuthApi extends JsonHandler {
    private final AuthService authService;

    public AuthApi(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();

                if (path.endsWith("/auth/login")) {
                    AuthRequest request = parseRequest(exchange, AuthRequest.class);
                    AuthResponse response = authService.authenticate(request)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
                    sendResponse(exchange, response, 200);
                }
                else if (path.endsWith("/auth/register")) {
                    UserRegistrationDto request = parseRequest(exchange, UserRegistrationDto.class);
                    authService.registerUser(request);
                    sendResponse(exchange, Map.of("message", "User registered"), 201);
                }
            } else {
                sendError(exchange, "Method not allowed", 405);
            }
        } catch (IllegalArgumentException e) {
            sendError(exchange, e.getMessage(), 400);
        } catch (Exception e) {
            sendError(exchange, "Internal server error", 500);
        }
    }
}