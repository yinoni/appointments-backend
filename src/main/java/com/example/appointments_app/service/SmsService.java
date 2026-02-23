package com.example.appointments_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SmsService {

    private final RestTemplate restTemplate;
    private String url = "https://api.sms4free.co.il/ApiSMS/v2/SendSMS";

    @Value("${api.sms.key}")
    private String API_SMS_KEY;

    @Value("${api.sms.phone}")
    private String API_SMS_PHONE;

    @Value("${api.sms.pass}")
    private String API_SMS_PASS;

    public SmsService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void sendSMS(String to, String msg){
        Map<String, String> body = Map.of("key", API_SMS_KEY,
                "user", API_SMS_PHONE,
                "pass", API_SMS_PASS,
                "sender", "AppointMe",
                "recipient", to,
                "msg", msg);

        System.out.println("The response is ===> " + restTemplate.postForEntity(url, body, String.class));

    }

}
