package org.victoria2.tools.vic2sgea.watcher;

import org.victoria2.tools.vic2sgea.entities.Country;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/10/17 9:36 PM
 */
public class CountryCsvBean {

    private Country country;
    private Date date;

    public CountryCsvBean(Country country, String date) {
        this.country = country;
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        try {
            this.date = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Country getCountry() {
        return country;
    }

    public Date getDate() {
        return date;
    }
}
