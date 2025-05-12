package com.example.otpservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class JsonHandler implements HttpHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T parseRequest(HttpExchange exchange, Class<T> valueType) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return objectMapper.readValue(is, valueType);
        }
    }

    protected void sendResponse(HttpExchange exchange, Object response, int statusCode) throws IOException {
        byte[] responseBytes = objectMapper.writeValueAsBytes(response);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected void sendError(HttpExchange exchange, String message, int statusCode) throws IOException {
        ErrorResponse error = new ErrorResponse(message);
        sendResponse(exchange, error, statusCode);
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}