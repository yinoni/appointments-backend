package com.example.appointments_app.model;

import static com.example.appointments_app.model.BusinessBuilder.aBusiness;

public class BusinessInput {

    private String businessName;
    private String businessDesc;
    private String city;
    private String street;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Business toBusiness() {
        return aBusiness()
                .withBusinessName(this.businessName)
                .withDescription(this.businessDesc)
                .withCity(this.city)
                .withStreet(this.street)
                .build();
    }
}
