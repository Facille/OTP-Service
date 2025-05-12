package com.example.otpservice.dao.impl;

import com.example.otpservice.dao.OtpConfigDao;
import com.example.otpservice.model.OtpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class PostgresOtpConfigDao implements OtpConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(PostgresOtpConfigDao.class);
    private final DataSource dataSource;

    public PostgresOtpConfigDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<OtpConfig> getConfig() {
        String sql = "SELECT id, code_length, expiration_minutes, updated_at FROM otp_config LIMIT 1";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                OtpConfig config = new OtpConfig();
                config.setId(rs.getInt("id"));
                config.setCodeLength(rs.getInt("code_length"));
                config.setExpirationMinutes(rs.getInt("expiration_minutes"));
                config.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
                return Optional.of(config);
            }
        } catch (SQLException e) {
            logger.error("Error getting OTP config", e);
        }
        return Optional.empty();
    }

    @Override
    public OtpConfig updateConfig(OtpConfig config) {
        String sql = "UPDATE otp_config SET code_length = ?, expiration_minutes = ? WHERE id = ? " +
                "RETURNING updated_at";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, config.getCodeLength());
            statement.setInt(2, config.getExpirationMinutes());
            statement.setInt(3, config.getId());

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                config.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
                return config;
            }
            throw new SQLException("Updating OTP config failed, no rows affected");
        } catch (SQLException e) {
            logger.error("Error updating OTP config", e);
            throw new RuntimeException("Failed to update OTP config", e);
        }
    }
}