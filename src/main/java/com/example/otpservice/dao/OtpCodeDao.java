package com.example.otpservice.dao;

import com.example.otpservice.model.OtpCode;
import java.util.List;
import java.util.Optional;

public interface OtpCodeDao {
    OtpCode save(OtpCode otpCode);
    Optional<OtpCode> findActiveByUserAndOperation(Integer userId, String operationId);
    List<OtpCode> findByUser(Integer userId);
    void updateStatus(Integer otpCodeId, String status);
    void markExpiredCodes();
}