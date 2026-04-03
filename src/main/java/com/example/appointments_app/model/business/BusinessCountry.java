package com.example.appointments_app.model.business;

public enum BusinessCountry {

    // Americas
    UNITED_STATES("United States"),
    CANADA("Canada"),
    MEXICO("Mexico"),
    BRAZIL("Brazil"),
    ARGENTINA("Argentina"),
    CHILE("Chile"),
    COLOMBIA("Colombia"),
    PERU("Peru"),
    VENEZUELA("Venezuela"),
    ECUADOR("Ecuador"),
    BOLIVIA("Bolivia"),
    PARAGUAY("Paraguay"),
    URUGUAY("Uruguay"),
    GUATEMALA("Guatemala"),
    COSTA_RICA("Costa Rica"),
    PANAMA("Panama"),
    DOMINICAN_REPUBLIC("Dominican Republic"),
    CUBA("Cuba"),
    JAMAICA("Jamaica"),
    TRINIDAD_AND_TOBAGO("Trinidad and Tobago"),

    // Europe
    GERMANY("Germany"),
    FRANCE("France"),
    ITALY("Italy"),
    SPAIN("Spain"),
    NETHERLANDS("Netherlands"),
    BELGIUM("Belgium"),
    AUSTRIA("Austria"),
    PORTUGAL("Portugal"),
    GREECE("Greece"),
    IRELAND("Ireland"),
    FINLAND("Finland"),
    LUXEMBOURG("Luxembourg"),
    MALTA("Malta"),
    CYPRUS("Cyprus"),
    SLOVAKIA("Slovakia"),
    SLOVENIA("Slovenia"),
    ESTONIA("Estonia"),
    LATVIA("Latvia"),
    LITHUANIA("Lithuania"),
    CROATIA("Croatia"),
    MONACO("Monaco"),
    ANDORRA("Andorra"),
    SAN_MARINO("San Marino"),
    VATICAN("Vatican"),
    MONTENEGRO("Montenegro"),
    KOSOVO("Kosovo"),
    UNITED_KINGDOM("United Kingdom"),
    SWITZERLAND("Switzerland"),
    NORWAY("Norway"),
    SWEDEN("Sweden"),
    DENMARK("Denmark"),
    ICELAND("Iceland"),
    POLAND("Poland"),
    HUNGARY("Hungary"),
    CZECH_REPUBLIC("Czech Republic"),
    ROMANIA("Romania"),
    BULGARIA("Bulgaria"),
    SERBIA("Serbia"),
    BOSNIA_AND_HERZEGOVINA("Bosnia and Herzegovina"),
    NORTH_MACEDONIA("North Macedonia"),
    ALBANIA("Albania"),
    UKRAINE("Ukraine"),
    BELARUS("Belarus"),
    MOLDOVA("Moldova"),
    RUSSIA("Russia"),
    TURKEY("Turkey"),
    LIECHTENSTEIN("Liechtenstein"),

    // Middle East
    ISRAEL("Israel"),
    UAE("UAE"),
    SAUDI_ARABIA("Saudi Arabia"),
    QATAR("Qatar"),
    KUWAIT("Kuwait"),
    OMAN("Oman"),
    BAHRAIN("Bahrain"),
    JORDAN("Jordan"),
    LEBANON("Lebanon"),
    IRAQ("Iraq"),
    IRAN("Iran"),
    YEMEN("Yemen"),

    // Asia
    CHINA("China"),
    JAPAN("Japan"),
    SOUTH_KOREA("South Korea"),
    INDIA("India"),
    PAKISTAN("Pakistan"),
    BANGLADESH("Bangladesh"),
    SRI_LANKA("Sri Lanka"),
    NEPAL("Nepal"),
    INDONESIA("Indonesia"),
    THAILAND("Thailand"),
    VIETNAM("Vietnam"),
    MALAYSIA("Malaysia"),
    SINGAPORE("Singapore"),
    PHILIPPINES("Philippines"),
    HONG_KONG("Hong Kong"),
    TAIWAN("Taiwan"),
    MYANMAR("Myanmar"),
    CAMBODIA("Cambodia"),
    LAOS("Laos"),
    MONGOLIA("Mongolia"),
    KAZAKHSTAN("Kazakhstan"),
    UZBEKISTAN("Uzbekistan"),
    AZERBAIJAN("Azerbaijan"),
    GEORGIA("Georgia"),
    ARMENIA("Armenia"),
    AFGHANISTAN("Afghanistan"),

    // Africa
    SOUTH_AFRICA("South Africa"),
    NIGERIA("Nigeria"),
    EGYPT("Egypt"),
    KENYA("Kenya"),
    GHANA("Ghana"),
    MOROCCO("Morocco"),
    ETHIOPIA("Ethiopia"),
    TANZANIA("Tanzania"),
    UGANDA("Uganda"),
    ALGERIA("Algeria"),
    TUNISIA("Tunisia"),
    SENEGAL("Senegal"),
    CAMEROON("Cameroon"),
    ZAMBIA("Zambia"),
    ZIMBABWE("Zimbabwe"),

    // Oceania
    AUSTRALIA("Australia"),
    NEW_ZEALAND("New Zealand"),
    FIJI("Fiji");

    private final String displayName;

    BusinessCountry(String displayName) {
        this.displayName = displayName;
    }

    public static BusinessCountry fromString(String value) {
        try {
            return BusinessCountry.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return UNITED_STATES; // או לזרוק שגיאה מותאמת אישית, או להחזיר null
        }
    }

    public String getDisplayName() {
        return displayName;
    }
}
