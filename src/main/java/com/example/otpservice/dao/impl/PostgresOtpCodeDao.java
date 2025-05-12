package com.example.otpservice.dao.impl;

import com.example.otpservice.dao.OtpCodeDao;
import com.example.otpservice.model.OtpCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresOtpCodeDao implements OtpCodeDao {
    private static final Logger logger = LoggerFactory.getLogger(PostgresOtpCodeDao.class);
    private final DataSource dataSource;

    public PostgresOtpCodeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public OtpCode save(OtpCode otpCode) {
        String sql;
        if (otpCode.getId() == null) {
            sql = "INSERT INTO otp_codes (code, user_id, operation_id, status, expires_at, channel) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        } else {
            sql = "UPDATE otp_codes SET code = ?, user_id = ?, operation_id = ?, status = ?, " +
                    "expires_at = ?, channel = ? WHERE id = ? RETURNING created_at";
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, otpCode.getCode());
            statement.setInt(2, otpCode.getUserId());
            statement.setString(3, otpCode.getOperationId());
            statement.setString(4, otpCode.getStatus());
            statement.setObject(5, otpCode.getExpiresAt());
            statement.setString(6, otpCode.getChannel());

            if (otpCode.getId() != null) {
                statement.setInt(7, otpCode.getId());
            }

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                if (otpCode.getId() == null) {
                    otpCode.setId(rs.getInt("id"));
                }
                otpCode.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                return otpCode;
            }
            throw new SQLException("Creating/updating OTP code failed, no rows affected");
        } catch (SQLException e) {
            logger.error("Error saving OTP code", e);
            throw new RuntimeException("Failed to save OTP code", e);
        }
    }

    @Override
    public Optional<OtpCode> findActiveByUserAndOperation(Integer userId, String operationId) {
        String sql = "SELECT id, code, user_id, operation_id, status, created_at, expires_at, channel " +
                "FROM otp_codes WHERE user_id = ? AND operation_id = ? AND status = 'ACTIVE'";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, operationId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                OtpCode otpCode = new OtpCode();
                otpCode.setId(rs.getInt("id"));
                otpCode.setCode(rs.getString("code"));
                otpCode.setUserId(rs.getInt("user_id"));
                otpCode.setOperationId(rs.getString("operation_id"));
                otpCode.setStatus(rs.getString("status"));
                otpCode.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                otpCode.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
                otpCode.setChannel(rs.getString("channel"));
                return Optional.of(otpCode);
            }
        } catch (SQLException e) {
            logger.error("Error finding active OTP code for user {} and operation {}", userId, operationId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<OtpCode> findByUser(Integer userId) {
        String sql = "SELECT id, code, operation_id, status, created_at, expires_at, channel " +
                "FROM otp_codes WHERE user_id = ?";
        List<OtpCode> codes = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                OtpCode otpCode = new OtpCode();
                otpCode.setId(rs.getInt("id"));
                otpCode.setCode(rs.getString("code"));
                otpCode.setOperationId(rs.getString("operation_id"));
                otpCode.setStatus(rs.getString("status"));
                otpCode.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                otpCode.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
                otpCode.setChannel(rs.getString("channel"));
                codes.add(otpCode);
            }
        } catch (SQLException e) {
            logger.error("Error finding OTP codes for user: " + userId, e);
        }
        return codes;
    }

    @Override
    public void updateStatus(Integer otpCodeId, String status) {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, otpCodeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating status for OTP code: " + otpCodeId, e);
            throw new RuntimeException("Failed to update OTP code status", e);
        }
    }

    @Override
    public void markExpiredCodes() {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' " +
                "WHERE status = 'ACTIVE' AND expires_at < CURRENT_TIMESTAMP";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            int count = statement.executeUpdate(sql);
            logger.info("Marked {} OTP codes as expired", count);
        } catch (SQLException e) {
            logger.error("Error marking expired OTP codes", e);
            throw new RuntimeException("Failed to mark expired OTP codes", e);
        }
    }
}