package com.example.otpservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String INIT_SCRIPT = "db/init.sql";

    public static void initialize() {
        try {
            Properties props = new Properties();
            props.load(DatabaseInitializer.class.getClassLoader().getResourceAsStream("application.properties"));

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            // Читаем SQL-скрипт
            InputStream inputStream = DatabaseInitializer.class.getClassLoader()
                    .getResourceAsStream(INIT_SCRIPT);
            if (inputStream == null) {
                throw new RuntimeException("SQL initialization script not found: " + INIT_SCRIPT);
            }

            String sqlScript = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                logger.info("Executing database initialization script");
                logger.info("Executing SQL script:\n" + sqlScript);
                statement.execute(sqlScript);
                logger.info("Database initialized successfully");
            }
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            throw new RuntimeException("Failed to initialize database", e);
        }

    }
}