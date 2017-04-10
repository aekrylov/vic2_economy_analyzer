package org.victoria2.tools.vic2sgea.watcher;

import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/10/17 9:19 PM
 */
public class CsvExporter {

    public static void exportCountry(Watch watch, String tag, Path filename) throws IOException {

        final String[] fieldMapping = new String[] {
                "date",
                "country.gdp",
                "country.population",
                "country.GDPPart"
        };

        CsvDozerBeanWriter writer = new CsvDozerBeanWriter(Files.newBufferedWriter(filename), CsvPreference.STANDARD_PREFERENCE);
        writer.configureBeanMapping(CountryCsvBean.class, fieldMapping);
        writer.writeHeader(fieldMapping);

        List<CountryCsvBean> items = watch.getHistory().entrySet().stream()
                .sorted((Comparator.comparing(Map.Entry::getKey)))
                .map(entry -> new CountryCsvBean(
                                entry.getValue().getCountries()
                                        .stream()
                                        .filter(country -> country.getTag().equals(tag))
                                        .findFirst()
                                        .orElse(null),
                                entry.getKey()
                        )
                )
                .collect(Collectors.toList());

        for(CountryCsvBean item: items) {
            writer.write(item);
        }
    }
}
