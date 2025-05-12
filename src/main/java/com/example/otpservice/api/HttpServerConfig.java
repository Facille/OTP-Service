package com.example.otpservice.api;

import com.example.otpservice.api.admin.*;
import com.example.otpservice.api.user.*;
import com.example.otpservice.service.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerConfig {
    private final AuthService authService;
    private final OtpService otpService;
    private final AdminService adminService;
    private HttpServer server;

    public HttpServerConfig(AuthService authService, OtpService otpService, AdminService adminService) {
        this.authService = authService;
        this.otpService = otpService;
        this.adminService = adminService;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // User APIs
        server.createContext("/api/auth", new AuthApi(authService));
        server.createContext("/api/users", new OtpApi(otpService));

        // Admin APIs
        server.createContext("/api/admin/users", new UserAdminApi(adminService, authService));
        server.createContext("/api/admin/otp-config", new OtpConfigAdminApi(adminService));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("Server started on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
