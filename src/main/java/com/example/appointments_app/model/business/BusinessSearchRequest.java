package com.example.appointments_app.model.business;

public class BusinessSearchRequest {
    private String query;
    private BusinessFilters filters;
    private int from;

    public BusinessSearchRequest() {
    }

    public BusinessFilters getFilters() {
        return filters;
    }

    public void setFilters(BusinessFilters filters) {
        this.filters = filters;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
}
