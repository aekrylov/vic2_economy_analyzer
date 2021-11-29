package org.victoria2.tools.vic2sgea.export;

import java.util.HashMap;
import java.util.Map;

public class ExportUtils {
    
    public static Map<String, String> COUNTRY_FIELDS = new HashMap<>();
    static {
        COUNTRY_FIELDS.put("gdp", "GDP");
        COUNTRY_FIELDS.put("gdpPerCapita", "GDP per capita");
        COUNTRY_FIELDS.put("population", "Population");
        COUNTRY_FIELDS.put("employmentFactory", "Factory employed");
        COUNTRY_FIELDS.put("employmentRGO", "RGO employed");
        COUNTRY_FIELDS.put("workforceFactory", "Factory workforce");
        COUNTRY_FIELDS.put("workforceRGO", "RGO workforce");
        COUNTRY_FIELDS.put("unemploymentRateRGO", "Unemployment rate RGO");
        COUNTRY_FIELDS.put("unemploymentRateFactory", "Unemployment rate factory");
        COUNTRY_FIELDS.put("goldIncome", "Gold income");
    }

}
