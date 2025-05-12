package com.example.otpservice;

import com.example.otpservice.api.HttpServerConfig;
import com.example.otpservice.config.DatabaseInitializer;
import com.example.otpservice.config.SimpleDataSource;
import com.example.otpservice.dao.*;
import com.example.otpservice.dao.impl.*;
import com.example.otpservice.service.*;
import com.example.otpservice.service.channel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Загрузка конфигурации
            Properties props = loadProperties();

            // 2. Инициализация DataSource
            SimpleDataSource dataSource = new SimpleDataSource(
                    props.getProperty("db.url"),
                    props.getProperty("db.username"),
                    props.getProperty("db.password")
            );

            // 3. Инициализация DAO
            UserDao userDao = new PostgresUserDao(dataSource);
            OtpCodeDao otpCodeDao = new PostgresOtpCodeDao(dataSource);
            OtpConfigDao otpConfigDao = new PostgresOtpConfigDao(dataSource);

            // 4. Инициализация сервисов
            AuthService authService = new AuthService(userDao);
            EmailService emailService = new EmailService();
            SmsService smsService = new SmsService();
            TelegramService telegramService = new TelegramService();
            FileService fileService = new FileService();

            OtpService otpService = new OtpService(
                    otpCodeDao,
                    otpConfigDao,
                    userDao,
                    emailService,
                    smsService,
                    telegramService,
                    fileService
            );

            AdminService adminService = new AdminService(userDao, otpConfigDao);

            // 5. Настройка HTTP сервера
            HttpServerConfig serverConfig = new HttpServerConfig(authService, otpService, adminService);
            serverConfig.start(8080);
            DatabaseInitializer.initialize();
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            props.load(input);
        }
        return props;
    }
}