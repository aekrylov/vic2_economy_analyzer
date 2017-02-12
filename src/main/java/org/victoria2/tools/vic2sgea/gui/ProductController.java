package org.victoria2.tools.vic2sgea.gui;

import javafx.scene.chart.PieChart;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.entities.ProductStorage;
import org.victoria2.tools.vic2sgea.main.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProductController extends ChartsController {
    private final Product product;

    private void addUniChart(Function<ProductStorage, Float> getter, String name) {
        List<ChartSlice> slices = new ArrayList<>();

        float total = 0;
        float totalSum;

        for (Country country : report.getCountryList()) {
            if (country.getTag().equals(Report.TOTAL_TAG))
                continue;
            ProductStorage productStorage = country.findStorage(product);
            if (productStorage == null)
                continue;

            float value = getter.apply(productStorage);

            slices.add(new ChartSlice(country.getTag(), value));
            total += value;
        }

        totalSum = total * product.price;
        String title = String.format("%s %s (%.1f items, %.1f£)", name, product.getName(), total, totalSum);

        Function<PieChart.Data, String> onEnter = data ->
                String.format("%s: %.2f items, %.2f£",
                        report.getCountry(data.getName()).getOfficialName(), data.getPieValue(), data.getPieValue() * product.price);

        Consumer<PieChart.Data> onClick = data -> Main.showCountry(report, report.getCountry(data.getName()));

        addChart(slices, title, onEnter, onClick);
    }

    ProductController(final Report report, Product product) {
        super(report);
        this.product = product;
        addUniChart(ProductStorage::getGdp, "GDP of");
        addUniChart(ProductStorage::getBought, "Consumers of ");
        addUniChart(ProductStorage::getExported, "Exporters of ");
        addUniChart(ProductStorage::getImported, "Importers of ");
        //addUniChart("maxDemand",2,0, "maxDemand ");
/*
        addUniChart("worldmarketPool", 0, 4, "worldmarketPool ");
        addUniChart("actualSoldWorld", 1, 4, "actualSoldWorld ");
*/
        addUniChart(ProductStorage::getTotalSupply, "Total Supply of ");
        addUniChart(ProductStorage::getSold, "Actual Supply of ");

    }
}
