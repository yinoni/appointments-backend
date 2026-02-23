package com.example.appointments_app.kafka;

import com.example.appointments_app.model.UserEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper om;

    public UserProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper om){
        this.kafkaTemplate = kafkaTemplate;
        this.om = om;
    }

    public void userRegisteredEvent(UserEventDTO event){
        try{
            String value = om.writeValueAsString(event);

            kafkaTemplate.send("user-registered", value)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✅ Sent successfully! Topic: " + result.getRecordMetadata().topic() +
                                    " | Offset: " + result.getRecordMetadata().offset());
                        } else {
                            System.err.println("❌ Failed to send: " + ex.getMessage());
                        }
                    });
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
