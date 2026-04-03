package com.example.appointments_app.model.business;

public enum BusinessCategory {
    HAIR_SALON("Hair Salon"),
    BEAUTY_CLINIC( "Beauty Clinic"),
    MEDICAL_CENTER("Medical Center"),
    GYM("Gym"),
    SPA("SPA"),
    MECHANIC("Mechanic"),
    FOOD("Food"),
    LIFESTYLE("Lifestyle");

    private final String displayName;

    public static BusinessCategory fromString(String value) {
        try {
            return BusinessCategory.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return HAIR_SALON; // או לזרוק שגיאה מותאמת אישית, או להחזיר null
        }
    }

    BusinessCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
