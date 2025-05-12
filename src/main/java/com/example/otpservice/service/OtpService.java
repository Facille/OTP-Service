package com.example.otpservice.service;

import com.example.otpservice.dao.OtpCodeDao;
import com.example.otpservice.dao.OtpConfigDao;
import com.example.otpservice.dao.UserDao;
import com.example.otpservice.dto.OtpRequest;
import com.example.otpservice.dto.OtpVerificationDto;
import com.example.otpservice.model.OtpCode;
import com.example.otpservice.model.OtpConfig;
import com.example.otpservice.model.User;
import com.example.otpservice.service.channel.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OtpService {
    private static final Random random = new Random();
    private final OtpCodeDao otpCodeDao;
    private final OtpConfigDao otpConfigDao;
    private final UserDao userDao;
    private final EmailService emailService;
    private final SmsService smsService;
    private final TelegramService telegramService;
    private final FileService fileService;

    public OtpService(OtpCodeDao otpCodeDao, OtpConfigDao otpConfigDao,
                      UserDao userDao, EmailService emailService,
                      SmsService smsService, TelegramService telegramService,
                      FileService fileService) {
        this.otpCodeDao = otpCodeDao;
        this.otpConfigDao = otpConfigDao;
        this.userDao = userDao;
        this.emailService = emailService;
        this.smsService = smsService;
        this.telegramService = telegramService;
        this.fileService = fileService;
    }

    public String generateOtpCode(Integer userId, OtpRequest request) {

        OtpConfig config = otpConfigDao.getConfig()
                .orElseThrow(() -> new IllegalStateException("OTP config not found"));

        String code = generateRandomCode(config.getCodeLength());

        OtpCode otpCode = new OtpCode();
        otpCode.setCode(code);
        otpCode.setUserId(userId);
        otpCode.setOperationId(request.getOperationId());
        otpCode.setStatus("ACTIVE");
        otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(config.getExpirationMinutes()));
        otpCode.setChannel(request.getChannel());
        otpCodeDao.save(otpCode);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        switch (request.getChannel()) {
            case "EMAIL":
                emailService.sendCode(user.getUsername(), code);
                break;
            case "SMS":
                smsService.sendCode(user.getUsername(), code);
                break;
            case "TELEGRAM":
                telegramService.sendCode(user.getUsername(), code);
                break;
            case "FILE":
                fileService.saveCode(code);
                break;
            default:
                throw new IllegalArgumentException("Invalid channel");
        }

        return code;
    }

    public boolean verifyOtp(Integer userId, OtpVerificationDto dto) {
        Optional<OtpCode> otpOpt = otpCodeDao.findActiveByUserAndOperation(userId, dto.getOperationId());
        if (otpOpt.isEmpty()) return false;

        OtpCode otp = otpOpt.get();
        if (!otp.getCode().equals(dto.getCode())) return false;
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpCodeDao.updateStatus(otp.getId(), "EXPIRED");
            return false;
        }

        otpCodeDao.updateStatus(otp.getId(), "USED");
        return true;
    }

    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
