package org.victoria2.tools.vic2sgea.gui;

import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.EconomySubject;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.entities.ProductStorage;
import org.victoria2.tools.vic2sgea.main.Report;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountryController extends ChartsController {
    private final Country country;
    private final Map<String, ProductStorage> storageMap;

    private void addUniChart(Function<EconomySubject, Float> getter, String chartName) {
        List<ChartSlice> slices = storageMap.values().stream()
                .filter(productStorage -> getter.apply(productStorage) > 0)
                .map(productStorage -> {
                    String name = productStorage.product.getName();
                    double value = getter.apply(productStorage) * productStorage.getPrice();
                    Color color = productStorage.product.getColor();

                    ChartSlice slice = new ChartSlice(name, value, color);

                    return slice;
                })
                .collect(Collectors.toList());

        Consumer<PieChart.Data> onClick = data -> {
            Product foundProduct = report.findProduct(data.getName());
            if (foundProduct != null) {
                Main.showProduct(report, foundProduct);
            }
        };

        Function<PieChart.Data, String> onEnter = data -> {
            ProductStorage productStorage = storageMap.get(data.getName());
            return String.format("%s: %.1f£ (%.1f items)", data.getName(), data.getPieValue(), getter.apply(productStorage));
        };

        String title = String.format("%s of %s (%.1f£)", chartName, country.getOfficialName(), getter.apply(country));
        addChart(slices, title, onEnter, onClick);
    }

    CountryController(final Report report, final Country country) {
        super(report);
        this.country = country;
        this.storageMap = country.getStorage();

        addUniChart(EconomySubject::getGdp, "GDP");
        addUniChart(EconomySubject::getBought, "Consumption");
        addUniChart(EconomySubject::getExported, "Export");
        addUniChart(EconomySubject::getImported, "Import");
        //addUniChart(report, country, "maxDemand",2,0, "maxDemand");
        addUniChart(EconomySubject::getTotalSupply, "Total Supply");
        addUniChart(EconomySubject::getSold, "Actual supply");
    }
}

