package com.example.otpservice.service.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final String filePath;

    public FileService() {
        Properties props = loadConfig();
        this.filePath = props.getProperty("file.path", "otp_codes.txt");
    }

    public void saveCode(String code) {
        try {
            Path path = Paths.get(filePath);
            String content = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                    " - OTP Code: " + code + "\n";

            if (Files.exists(path)) {
                Files.write(path, content.getBytes(), StandardOpenOption.APPEND);
            } else {
                Files.write(path, content.getBytes(), StandardOpenOption.CREATE);
            }

            logger.info("OTP code saved to file: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to save OTP code to file", e);
            throw new RuntimeException("Failed to save OTP code to file", e);
        }
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(FileService.class.getClassLoader()
                    .getResourceAsStream("application.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file configuration", e);
        }
    }
}
