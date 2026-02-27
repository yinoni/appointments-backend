package com.example.appointments_app.kafka;

import com.example.appointments_app.model.business.BusinessDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class BusinessProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper om;


    public BusinessProducer(KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper om){
        this.kafkaTemplate = kafkaTemplate;
        this.om = om;
    }

    public void sendBusinessCreatedEvent(BusinessDTO bDTO){
        String msg = om.writeValueAsString(bDTO);

        kafkaTemplate.send("business-created", msg);
    }

    public void sendBusinessUpdatedEvent(BusinessDTO bDTO){
        String msg = om.writeValueAsString(bDTO);

        kafkaTemplate.send("business-updated", msg);
    }

}
