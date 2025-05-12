package com.example.otpservice.api.admin;

import com.example.otpservice.api.HttpUtil;
import com.example.otpservice.api.JsonHandler;
import com.example.otpservice.model.OtpConfig;
import com.example.otpservice.service.AdminService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class OtpConfigAdminApi extends JsonHandler {
    private final AdminService adminService;

    public OtpConfigAdminApi(AdminService adminService) {
        this.adminService = adminService;
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
                    OtpConfig config = adminService.getOtpConfig();
                    sendResponse(exchange, config, 200);
                    break;
                case "PUT":
                    OtpConfig updateRequest = parseRequest(exchange, OtpConfig.class);
                    OtpConfig updatedConfig = adminService.updateOtpConfig(updateRequest);
                    sendResponse(exchange, updatedConfig, 200);
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
}