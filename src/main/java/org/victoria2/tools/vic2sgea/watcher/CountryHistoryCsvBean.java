package org.victoria2.tools.vic2sgea.watcher;

import org.victoria2.tools.vic2sgea.entities.Country;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/10/17 9:36 PM
 */
public class CountryHistoryCsvBean {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

    private final String tag;
    private final Map<String, Country> history = new HashMap<>();
    private final List<Country> historyList = new ArrayList<>();

    public CountryHistoryCsvBean(Watch watch, String tag) {
        this.tag = tag;
        watch.getHistory().forEach((dateStr, worldState) -> {
            Country country = worldState.getCountries().stream()
                    .filter(c -> c.getTag().equals(tag))
                    .findFirst()
                    .orElseGet(() -> {
                        System.out.printf("Country %s not found for date %s%n", tag, dateStr);
                        return null;
                    });

            history.put(dateStr, country);
            historyList.add(country);
        });
    }

    public String getTag() {
        return tag;
    }

    public Map<String, Country> getHistory() {
        return history;
    }

    public List<Country> getHistoryList() {
        return historyList;
    }
}
