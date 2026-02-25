package com.example.appointments_app.model.business;

public enum BusinessCategory {
    HAIR_SALON("מספרה"),
    BEAUTY_CLINIC("מכון יופי"),
    MEDICAL_CENTER("מרפאה"),
    GYM("חדר כושר"),
    SPA("ספא");

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
