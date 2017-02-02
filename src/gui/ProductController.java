package gui;

import javafx.scene.chart.PieChart;
import main.Country;
import main.Product;
import main.ProductStorage;
import main.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProductController extends ChartsController {
    private final Product product;

    private void addUniChart(Function<ProductStorage, Float> getter, int column, int row, String name) {
        List<PieChart.Data> pieChartData = new ArrayList<>();

        float total = 0;
        float totalSum;

        for (Country country : report.getCountryList()) {
            if (country.getTag().equals(Report.TOTAL_TAG))
                continue;
            ProductStorage productStorage = country.findStorage(product.getName());
            if (productStorage == null)
                continue;

            PieChart.Data temp = new PieChart.Data(country.getTag(), getter.apply(productStorage));
            pieChartData.add(temp);
            total += getter.apply(productStorage);
        }

        totalSum = total * product.price;
        String title = String.format("%s %s (%.1f items, %.1f£)", name, product.getName(), total, totalSum);

        Function<PieChart.Data, String> onEnter = data ->
                String.format("%s: %.2f items, %.2f£",
                        report.getCountry(data.getName()).getOfficialName(), data.getPieValue(), data.getPieValue() * product.price);

        Consumer<PieChart.Data> onClick = data -> Main.showCountry(report, report.getCountry(data.getName()));

        addChart(pieChartData, column, row, title, onEnter, onClick);
    }

    ProductController(final Report report, Product product) {
        super(report);
        this.product = product;

        addUniChart(ProductStorage::getActualSupply, 0, 0, "Producers of ");
        addUniChart(ProductStorage::getActualDemand, 0, 2, "Consumers of ");
        addUniChart(ProductStorage::getExported, 1, 0, "Exporters of ");
        addUniChart(ProductStorage::getImported, 1, 2, "Importers of ");
        //addUniChart("MaxDemand",2,0, "MaxDemand ");
/*
        addUniChart("worldmarketPool", 0, 4, "worldmarketPool ");
        addUniChart("actualSoldWorld", 1, 4, "actualSoldWorld ");
*/
        addUniChart(ProductStorage::getTotalSupply, 0, 6, "Total Supply of ");

    }
}
