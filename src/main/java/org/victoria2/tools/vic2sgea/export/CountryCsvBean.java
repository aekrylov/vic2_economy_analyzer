package org.victoria2.tools.vic2sgea.export;

import org.victoria2.tools.vic2sgea.entities.Country;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/10/17 9:36 PM
 */
public class CountryCsvBean {

    private final Country country;
    private final String date;

    public CountryCsvBean(Country country, String dateStr) {
        this.country = country;
        this.date = dateStr;
    }

    public Country getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }
}
