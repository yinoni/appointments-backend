package com.example.appointments_app.kafka;

import com.example.appointments_app.model.user.User;
import com.example.appointments_app.model.user.UserEventDTO;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.UserRepository;
import com.example.appointments_app.service.SmsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class UserConsumer {

    private final ObjectMapper om;
    private SecureRandom secureRandom = new SecureRandom();
    private final SmsService smsService;
    private final Redis redis;
    private final UserRepository userRepository;

    public UserConsumer(ObjectMapper om,
                        SmsService smsService,
                        Redis redis,
                        UserRepository userRepository) {
        this.om = om;
        this.smsService = smsService;
        this.redis = redis;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-registered", groupId = "appointments-group-final-1")
    public void handleUserRegistered(String event){
        int code = secureRandom.nextInt(10000);

        String privateCode = String.format("%04d", code);

        try{
            UserEventDTO dto = om.readValue(event, UserEventDTO.class);

            System.out.println("Your 4-digit code is: " + privateCode);
            System.out.println("Sending code -> " + privateCode + " To: " + dto.getPhone());
            redis.saveOtp(dto.getPhone(), privateCode);

        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    @KafkaListener(topics = "phone-verified", groupId = "appointments-group-final-1")
    public void handlePhoneVerified(String event){
        try{
            UserEventDTO dto = om.readValue(event, UserEventDTO.class);

            System.out.println("Sending email to: " + dto.getEmail() + "\nHi " + dto.getFullName() + "!\nHave fun!");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
