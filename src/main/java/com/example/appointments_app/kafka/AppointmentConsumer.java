package com.example.appointments_app.kafka;

import com.example.appointments_app.model.AppointmentEventDTO;
import com.example.appointments_app.service.SmsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AppointmentConsumer {

    private final ObjectMapper om;
    private final SmsService smsService;

    public AppointmentConsumer(ObjectMapper om, SmsService smsService){

        this.om = om;
        this.smsService = smsService;
    }

    @KafkaListener(topics = "appointment-topic", groupId = "appointments-group-final-1")
    public void sendAppointmentDetailsSMS(String event) {
        try{
            AppointmentEventDTO dto = om.readValue(event, AppointmentEventDTO.class);
            String msg = "Business Name: " + dto.getBusinessName() +
                    "\nService: " + dto.getServiceName() +
                    "\n\nDate: " + dto.getDate().toString() +
                    "\nTime: " + dto.getTime().toString() +
                    "\n\nWe are waiting for you :)";

            smsService.sendSMS(dto.getPhone(), msg);
        }
        catch(Exception e){
            System.out.println("Error ! => " + e.getMessage());
        }

    }


}
