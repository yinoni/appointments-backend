package com.example.appointments_app.model.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessFilters {
    private String address;
    private String country;
    private Double rating;
    private String category;

    public BusinessFilters() {
    }

    public BusinessFilters(String address, String country, String category, Double rating) {
        this.address = address;
        this.country = country;
        this.category = category;
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Map<String, Object>> generateFilterList(){
        List<Map<String, Object>> filters = new ArrayList<>();

        if(this.country != null && !this.country.trim().isEmpty())
            filters.add(Map.of("term", Map.of("country.keyword", this.country)));

        if(this.address != null && !this.address.trim().isEmpty())
            filters.add(Map.of("term", Map.of("address.keyword", this.address)));

        if((this.category != null && !this.category.trim().isEmpty())) {
            if(!this.category.toUpperCase().equals("ALL"))
                filters.add(Map.of("term", Map.of("category.keyword", this.category)));
        }
        if(this.rating != null)
            filters.add(Map.of("range", Map.of("rating", Map.of("gte", this.rating))));

        return filters;
    }
}
