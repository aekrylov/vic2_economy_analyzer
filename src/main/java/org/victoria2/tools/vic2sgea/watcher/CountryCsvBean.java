package org.victoria2.tools.vic2sgea.watcher;

import org.victoria2.tools.vic2sgea.entities.Country;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/10/17 9:36 PM
 */
public class CountryCsvBean {

    private Country country;
    private String date;

    public CountryCsvBean(Country country, String date) {
        this.country = country;
        this.date = date;
    }

    public Country getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }
}
