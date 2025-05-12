package com.example.otpservice.dao.impl;

import com.example.otpservice.dao.UserDao;
import com.example.otpservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresUserDao implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(PostgresUserDao.class);
    private final DataSource dataSource;

    public PostgresUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: " + username, e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql;
        if (user.getId() == null) {
            sql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        } else {
            sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ? RETURNING created_at";
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());

            if (user.getId() == null) {
                // Добавьте это для INSERT:
                statement.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt()));
            } else {
                statement.setInt(4, user.getId());
            }

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                if (user.getId() == null) {
                    user.setId(rs.getInt("id"));
                }
                user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                return user;
            }
            throw new SQLException("Creating/updating user failed, no rows affected");
        } catch (SQLException e) {
            logger.error("Error saving user", e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking if username exists: " + username, e);
        }
        return false;
    }

    @Override
    public boolean existsAdmin() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking if admin exists", e);
        }
        return false;
    }

    @Override
    public void delete(Integer userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting user with id: " + userId, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
                user.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
    @Override
    public String getRole(Integer userId) {
        final String sql = "SELECT role FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
            }

        } catch (SQLException e) {

            System.err.println("Error getting role for user ID: " + userId);
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
                user.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}