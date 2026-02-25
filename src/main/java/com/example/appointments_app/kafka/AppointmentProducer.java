package com.example.appointments_app.kafka;

import com.example.appointments_app.model.appointment.AppointmentEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AppointmentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper om;

    public AppointmentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper om){
        this.kafkaTemplate = kafkaTemplate;
        this.om = om;
    }

    public void sendAppointmentEvent(AppointmentEventDTO event) {
        try {
            String jsonMessage = om.writeValueAsString(event);

            // הוספת CompletableFuture כדי לראות מה קורה
            kafkaTemplate.send("appointment-topic", jsonMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("✅ Sent successfully! Topic: " + result.getRecordMetadata().topic() +
                                    " | Offset: " + result.getRecordMetadata().offset());
                        } else {
                            System.err.println("❌ Failed to send: " + ex.getMessage());
                        }
                    });

        } catch (Exception e) {
            System.err.println("❌ Serialization error: " + e.getMessage());
        }
    }

}
