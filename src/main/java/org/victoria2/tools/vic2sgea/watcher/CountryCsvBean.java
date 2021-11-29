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

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

    private final Country country;
    private final Date date;

    public CountryCsvBean(Country country, String dateStr) {
        this.country = country;

        Date date;
        try {
            date = DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.date = date;
    }

    public Country getCountry() {
        return country;
    }

    public Date getDate() {
        return date;
    }
}
