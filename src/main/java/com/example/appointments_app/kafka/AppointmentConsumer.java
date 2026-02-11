package com.example.appointments_app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AppointmentConsumer {

    private final ObjectMapper om;

    public AppointmentConsumer(ObjectMapper om){
        this.om = om;
    }

    // בלי ObjectMapper כרגע, רק לראות שזה נכנס לפונקציה
    @KafkaListener(topics = "appointment-topic", groupId = "appointments-group-final-1")
    public void listen(String message) {
        //Send message here
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("I FINALLY RECEIVED SOMETHING: " + message);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


}
