package gui;

import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import main.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountryController extends ChartsController {
    private final Country country;

    public Scene getScene() {
        return scene;
    }

    private void addUniChart(Function<EconomySubject, Float> getter, int column, int row, String name) {
        List<PieChart.Data> pieChartData = country.getStorage().stream()
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
            ProductStorage productStorage = country.findStorage(data.getName());
            if (productStorage != null) {
                return String.format("%s: %.1f£ (%.1f items)", data.getName(), data.getPieValue(), getter.apply(productStorage));
            } else return "findStorage(data.getName()) returned NULL";
        };

        String title = String.format("%s of %s (%.1f£)", name, country.getOfficialName(), getter.apply(country));
        addChart(pieChartData, column, row, title, onEnter, onClick);
    }

    CountryController(final Report report, final Country country) {
        super(report);
        this.country = country;

        addUniChart(EconomySubject::getActualSupply, 0, 0, "Production");
        addUniChart(EconomySubject::getActualDemand, 0, 2, "Consumption");
        addUniChart(EconomySubject::getExported, 1, 0, "Export");
        addUniChart(EconomySubject::getImported, 1, 2, "Import");
        //addUniChart(report, country, "MaxDemand",2,0, "MaxDemand");
        addUniChart(EconomySubject::getTotalSupply, 0, 4, "Total Supply");
    }
}

