package com.example.otpservice.service.channel;

import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.extra.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;

    public SmsService() {
        Properties config = loadConfig();
        this.host = config.getProperty("smpp.host");
        this.port = Integer.parseInt(config.getProperty("smpp.port"));
        this.systemId = config.getProperty("smpp.system_id");
        this.password = config.getProperty("smpp.password");
        this.systemType = config.getProperty("smpp.system_type");
    }

    public void sendCode(String phoneNumber, String code) {
        SMPPSession session = new SMPPSession();

        try {
            session.connectAndBind(
                    host,
                    port,
                    new BindParameter(
                            BindType.BIND_TX,
                            systemId,
                            password,
                            systemType,
                            TypeOfNumber.UNKNOWN,
                            NumberingPlanIndicator.UNKNOWN,
                            null
                    )
            );

            if (session.getSessionState() == SessionState.BOUND_TX) {
                String message = "Your code: " + code;
                session.submitShortMessage(
                        "CMT",
                        TypeOfNumber.UNKNOWN,
                        NumberingPlanIndicator.UNKNOWN,
                        "OTPService",
                        TypeOfNumber.UNKNOWN,
                        NumberingPlanIndicator.UNKNOWN,
                        phoneNumber,
                        new ESMClass(),
                        (byte)0,
                        (byte)1,
                        null,
                        null,
                        new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                        (byte)0,
                        new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
                        (byte)0,
                        message.getBytes()
                );
                logger.info("SMS sent to {}", phoneNumber);
            }
        } catch (Exception e) {
            logger.error("Failed to send SMS", e);
        } finally {
            if (session != null && session.getSessionState() != SessionState.CLOSED) {
                session.unbindAndClose();
            }
        }
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("sms.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SMS config", e);
        }
    }
}