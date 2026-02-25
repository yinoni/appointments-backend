package com.example.appointments_app.kafka;


import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.model.business.BusinessDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class BusinessConsumer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper om;

    private final ElasticSearchService elasticSearchService;


    public BusinessConsumer(KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper om,
                            ElasticSearchService elasticSearchService){
        this.kafkaTemplate = kafkaTemplate;
        this.om = om;
        this.elasticSearchService = elasticSearchService;
    }

    @KafkaListener(topics = "business-created", groupId = "appointments-group-final-1")
    public void businessCreatedEvent(String event) throws IOException {
        try{
            BusinessDTO businessDTO = om.readValue(event, BusinessDTO.class);
            elasticSearchService.indexDocument("businesses", String.valueOf(businessDTO.getId()), businessDTO);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
