package com.example.appointments_app.kafka;

import com.example.appointments_app.model.user.UserEventDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserConsumer {

    private final ObjectMapper om;

    public UserConsumer(ObjectMapper om) {
        this.om = om;
    }

    @KafkaListener(topics = "user-registered", groupId = "appointments-group-final-1")
    public void handleUserRegistered(String event){
        try{
            UserEventDTO dto = om.readValue(event, UserEventDTO.class);

            System.out.println("New user registered: ");
            System.out.println("------------------------------------");
            System.out.println("Full name: " + dto.getFullName() +"\nEmail: " + dto.getEmail() + "\nPhone number: " + dto.getPhone());
            System.out.println("------------------------------------");
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }

    }
}
