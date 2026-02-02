package com.example.appointments_app.model;

import static com.example.appointments_app.model.BusinessBuilder.aBusiness;

public class BusinessInput {

    private String businessName;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Business toBusiness() {
        return aBusiness()
                .withBusinessName(this.businessName)
                .build();
    }
}
