package com.example.otpservice.api;

import com.sun.net.httpserver.HttpExchange;

public class HttpUtil {
    public static String getPathParam(HttpExchange exchange, String paramName) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(paramName) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }

    public static String getQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }

    public static void handleCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}