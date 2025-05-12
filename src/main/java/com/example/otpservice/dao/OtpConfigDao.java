package com.example.otpservice.dao;

import com.example.otpservice.model.OtpConfig;
import java.util.Optional;

public interface OtpConfigDao {
    Optional<OtpConfig> getConfig();
    OtpConfig updateConfig(OtpConfig config);
}