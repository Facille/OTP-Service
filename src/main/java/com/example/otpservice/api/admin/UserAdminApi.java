package com.example.otpservice.api.admin;

import com.example.otpservice.api.HttpUtil;
import com.example.otpservice.api.JsonHandler;
import com.example.otpservice.dto.UserRegistrationDto;
import com.example.otpservice.model.User;
import com.example.otpservice.service.AdminService;
import com.example.otpservice.service.AuthService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserAdminApi extends JsonHandler {
    private final AdminService adminService;
    private final AuthService authService;

    public UserAdminApi(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.handleCors(exchange);

        try {
            if (!checkAdminAuth(exchange)) {
                sendError(exchange, "Unauthorized", 401);
                return;
            }

            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                case "OPTIONS":
                    exchange.sendResponseHeaders(200, -1);
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        } catch (Exception e) {
            sendError(exchange, "Internal server error", 500);
        }
    }

    private boolean checkAdminAuth(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<User> users = adminService.getAllRegularUsers();
        sendResponse(exchange, users, 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        UserRegistrationDto dto = parseRequest(exchange, UserRegistrationDto.class);
        User user = authService.registerUser(dto);
        sendResponse(exchange, user, 201);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String userIdStr = HttpUtil.getPathParam(exchange, "users");
        if (userIdStr == null) {
            sendError(exchange, "User ID is required", 400);
            return;
        }

        int userId = Integer.parseInt(userIdStr);
        adminService.deleteUser(userId);
        sendResponse(exchange, Map.of("message", "User deleted"), 200);
    }
}
