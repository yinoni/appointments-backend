package com.example.appointments_app.kafka;

import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.model.AppointmentEventDTO;
import com.example.appointments_app.model.AppointmentIndex;
import com.example.appointments_app.service.SmsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AppointmentConsumer {

    private final ObjectMapper om;
    private final SmsService smsService;
    private final ElasticSearchService elasticSearchService;

    public AppointmentConsumer(ObjectMapper om,
                               SmsService smsService,
                               ElasticSearchService elasticSearchService){

        this.om = om;
        this.smsService = smsService;
        this.elasticSearchService = elasticSearchService;
    }

    @KafkaListener(topics = "appointment-topic", groupId = "appointments-group-final-1")
    public void sendAppointmentDetailsSMS(String event) {
        try{
            AppointmentEventDTO dto = om.readValue(event, AppointmentEventDTO.class);
            AppointmentIndex index = dto.toIndex();


            String msg = "Business Name: " + dto.getBusinessName() +
                    "\nService: " + dto.getServiceName() +
                    "\n\nDate: " + dto.getDate().toString() +
                    "\nTime: " + dto.getTime().toString() +
                    "\n\nWe are waiting for you :)";

            smsService.sendSMS(dto.getPhone(), msg);
            elasticSearchService.insertDocument("appointments_history", index);
        }
        catch(Exception e){
            System.out.println("Error ! => " + e.getMessage());
        }
    }
}

