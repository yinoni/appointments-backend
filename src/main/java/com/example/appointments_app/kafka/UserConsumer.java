package com.example.appointments_app.kafka;

import com.example.appointments_app.model.user.OtpTaskCode;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.model.user.UserEventDTO;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.UserRepository;
import com.example.appointments_app.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class UserConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);
    private final ObjectMapper om;
    private SecureRandom secureRandom = new SecureRandom();
    private final SmsService smsService;
    private final Redis redis;
    private final String OTP_MESSAGE = "Your 4-digit code is: ";

    public UserConsumer(ObjectMapper om,
                        SmsService smsService,
                        Redis redis) {
        this.om = om;
        this.smsService = smsService;
        this.redis = redis;
    }

    @KafkaListener(topics = "user-registered", groupId = "appointments-group-final-1")
    public void handleUserRegistered(String event){
        int code = secureRandom.nextInt(10000);

        String privateCode = String.format("%04d", code);

        try{
            UserEventDTO dto = om.readValue(event, UserEventDTO.class);

            //smsService.sendSMS(dto.getPhone(), OTP_MESSAGE + privateCode);
            System.out.println(privateCode);
            redis.saveOtp(dto.getPhone(), privateCode);
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "phone-verified", groupId = "appointments-group-final-1")
    public void handlePhoneVerified(String event){
        try{
            UserEventDTO dto = om.readValue(event, UserEventDTO.class);

            System.out.println("Sending email to: " + dto.getEmail() + "\nHi " + dto.getFullName() + "!\nHave fun!");
        }
        catch (Exception e){
            UserConsumer.log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "resend-otp-code", groupId = "appointments-group-final-1")
    public void resendOTPCode(String event){
        try{
            OtpTaskCode otpCode = om.readValue(event, OtpTaskCode.class);
            String message = OTP_MESSAGE + otpCode.getCode();
            //smsService.sendSMS(otpCode.getPhone(), message);
            System.out.println(message);
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
