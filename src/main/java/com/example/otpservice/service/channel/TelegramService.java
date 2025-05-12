package com.example.otpservice.service.channel;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TelegramService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);
    private final String botToken;
    private final String chatId;
    private final String apiUrl;

    public TelegramService() {
        Properties config = loadConfig();
        this.botToken = config.getProperty("telegram.bot.token");
        this.chatId = config.getProperty("telegram.chat.id");
        this.apiUrl = config.getProperty("telegram.api.url");
    }

    public void sendCode(String username, String code) {
        String message = String.format("Hello %s, your confirmation code is: %s", username, code);
        String url = String.format("%s%s/sendMessage?chat_id=%s&text=%s",
                apiUrl,
                botToken,
                chatId,
                urlEncode(message));

        sendTelegramRequest(url);
    }

    private void sendTelegramRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    logger.error("Telegram API error. Status code: {}", statusCode);
                } else {
                    logger.info("Telegram message sent successfully");
                }
            }
        } catch (IOException e) {
            logger.error("Error sending Telegram message: {}", e.getMessage());
            throw new RuntimeException("Failed to send Telegram message", e);
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(TelegramService.class.getClassLoader()
                    .getResourceAsStream("telegram.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Telegram configuration", e);
        }
    }
}
