package com.example.otpservice.service.scheduler;

import com.example.otpservice.dao.OtpCodeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OtpExpirationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OtpExpirationScheduler.class);
    private final OtpCodeDao otpCodeDao;
    private final ScheduledExecutorService scheduler;
    private final int checkIntervalMinutes;

    public OtpExpirationScheduler(OtpCodeDao otpCodeDao, int checkIntervalMinutes) {
        this.otpCodeDao = otpCodeDao;
        this.checkIntervalMinutes = checkIntervalMinutes;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
                this::markExpiredCodes,
                0,
                checkIntervalMinutes,
                TimeUnit.MINUTES);

        logger.info("OTP expiration scheduler started with interval {} minutes", checkIntervalMinutes);
    }

    public void shutdown() {
        scheduler.shutdown();
        logger.info("OTP expiration scheduler stopped");
    }

    private void markExpiredCodes() {
        try {
            otpCodeDao.markExpiredCodes();
        } catch (Exception e) {
            logger.error("Error in OTP expiration scheduler", e);
        }
    }
}
