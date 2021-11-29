package org.victoria2.tools.vic2sgea.watcher;

import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.victoria2.tools.vic2sgea.entities.Country;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        final CellProcessor[] processors = new CellProcessor[] {
                new FmtDate("yyyy.MM.dd"),
                null,
                null,
                null
        };

        CsvDozerBeanWriter writer = new CsvDozerBeanWriter(Files.newBufferedWriter(filename), CsvPreference.STANDARD_PREFERENCE);
        writer.configureBeanMapping(CountryCsvBean.class, fieldMapping);
        writer.writeHeader(fieldMapping);

        List<CountryCsvBean> items = watch.getHistory().entrySet().stream()
                .map(entry -> new CountryCsvBean(
                                entry.getValue().getCountries()
                                        .stream()
                                        .filter(country -> country.getTag().equals(tag))
                                        .findFirst()
                                        .orElse(null),

                                entry.getKey()
                        )
                )
                .sorted(Comparator.comparing(CountryCsvBean::getDate))
                .collect(Collectors.toList());

        for(CountryCsvBean item: items) {
            writer.write(item, processors);
        }
        writer.close();
    }

    public static void exportAll(Watch watch, String fieldPath, Path filename) throws IOException {
        List<CountryHistoryCsvBean> items = watch.getHistory().values().stream()
                .flatMap(state -> state.getCountries().stream().map(Country::getTag))
                .distinct()
                .sorted()
                .map(tag -> new CountryHistoryCsvBean(watch, tag))
                .collect(Collectors.toList());

        List<String> dates = new ArrayList<>(watch.getHistory().keySet());

        List<String> fields = IntStream.range(0, watch.getHistory().size())
                .boxed()
                .map(idx -> String.format("historyList[%d].%s", idx, fieldPath))
                .collect(Collectors.toList());
        fields.add(0, "tag");

        List<String> headers = new ArrayList<>(dates);
        headers.add(0, "tag");

        CsvDozerBeanWriter writer = new CsvDozerBeanWriter(Files.newBufferedWriter(filename), CsvPreference.STANDARD_PREFERENCE);
        writer.configureBeanMapping(CountryHistoryCsvBean.class, fields.toArray(new String[0]));
        writer.writeHeader(headers.toArray(new String[0]));

        final CellProcessor[] processors = fields.stream().map(f -> null)
                .toArray(CellProcessor[]::new);

        for(CountryHistoryCsvBean item: items) {
            writer.write(item, processors);
        }
        writer.close();
    }
}
