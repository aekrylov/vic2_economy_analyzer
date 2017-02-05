package gui;

import javafx.scene.chart.PieChart;
import main.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountryController extends ChartsController {
    private final Country country;
    private final Map<String, ProductStorage> storageMap;

    private void addUniChart(Function<EconomySubject, Float> getter, int column, int row, String name) {
        List<PieChart.Data> pieChartData = storageMap.values().stream()
                .filter(productStorage -> getter.apply(productStorage) > 0)
                .map(productStorage -> new PieChart.Data(productStorage.product.getName(), getter.apply(productStorage) * productStorage.getPrice()))
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

        String title = String.format("%s of %s (%.1f£)", name, country.getOfficialName(), getter.apply(country));
        addChart(pieChartData, column, row, title, onEnter, onClick);
    }

    CountryController(final Report report, final Country country) {
        super(report);
        this.country = country;
        this.storageMap = country.getStorage();

        addUniChart(EconomySubject::getGdp, 0, 0, "GDP");
        addUniChart(EconomySubject::getActualDemand, 0, 2, "Consumption");
        addUniChart(EconomySubject::getExported, 1, 0, "Export");
        addUniChart(EconomySubject::getImported, 1, 2, "Import");
        //addUniChart(report, country, "maxDemand",2,0, "maxDemand");
        addUniChart(EconomySubject::getTotalSupply, 0, 4, "Total Supply");
        addUniChart(EconomySubject::getActualSupply, 1, 4, "Actual supply");
    }
}

