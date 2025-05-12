package com.example.otpservice.service;

import com.example.otpservice.dao.OtpConfigDao;
import com.example.otpservice.dao.UserDao;
import com.example.otpservice.model.OtpConfig;
import com.example.otpservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final UserDao userDao;
    private final OtpConfigDao otpConfigDao;

    public AdminService(UserDao userDao, OtpConfigDao otpConfigDao) {
        this.userDao = userDao;
        this.otpConfigDao = otpConfigDao;
    }

    public OtpConfig updateOtpConfig(OtpConfig config) {
        OtpConfig updated = otpConfigDao.updateConfig(config);
        logger.info("OTP configuration updated: codeLength={}, expirationMinutes={}",
                updated.getCodeLength(), updated.getExpirationMinutes());
        return updated;
    }

    public List<User> getAllRegularUsers() {
        return userDao.findAll().stream()
                .filter(user -> !"ADMIN".equals(user.getRole()))
                .toList();
    }

    public void deleteUser(Integer userId) {
        userDao.delete(userId);
        logger.info("User deleted: id={}", userId);
    }

    public OtpConfig getOtpConfig() {
        return otpConfigDao.getConfig()
                .orElseThrow(() -> new IllegalStateException("OTP configuration not found"));
    }
}