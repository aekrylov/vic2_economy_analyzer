package gui;

import javafx.scene.chart.PieChart;
import main.Country;
import main.Product;
import main.ProductStorage;
import main.Report;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProductController extends ChartsController {
    private Product product;

    private void addUniChart(String fieldName, int column, int row, String name) {
        List<PieChart.Data> pieChartData = new ArrayList<>();

        final Field field;
        try {
            field = ProductStorage.class.getField(fieldName);
        } catch (NoSuchFieldException e) {
            System.err.println("Unable to get field Product." + fieldName);
            e.printStackTrace();
            return;
        }

        float total = 0;
        float totalSum = 0;

        try {
            for (Country country : report.getCountryList()) {
                if (country.getTag().equals(Report.TOTAL_TAG))
                    continue;
                for (ProductStorage everyStorage : country.storage) {
                    if (everyStorage.product == product) {
                        PieChart.Data temp = new PieChart.Data(country.getTag(), field.getFloat(everyStorage));
                        pieChartData.add(temp);
                        total += field.getFloat(everyStorage);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.err.println("Unable to access field Product." + fieldName);
            e.printStackTrace();
        }

        pieChartData.sort(Comparator.comparing(PieChart.Data::getPieValue).reversed());

        totalSum = total * product.price;
        String title = name + product.getName() + " (" + total + " items by " + totalSum + "£)";

        Function<PieChart.Data, String> onEnter = data ->
                report.getCountry(data.getName()).getOfficialName() + ": " + data.getPieValue() + " items";

        Consumer<PieChart.Data> onClick = data -> Main.showCountry(report, report.getCountry(data.getName()));


        addChart(pieChartData, column, row, title, onEnter, onClick);
    }

    ProductController(final Report report, Product product) {
        super(report);
        this.product = product;

        addUniChart("actualSupply", 0, 0, "Producers of ");
        addUniChart("actualDemand", 0, 2, "Consumers of ");
        addUniChart("exported", 1, 0, "Exporters of ");
        addUniChart("imported", 1, 2, "Importers of ");
        //addUniChart("MaxDemand",2,0, "MaxDemand ");
        addUniChart("worldmarketPool", 0, 4, "worldmarketPool ");
        addUniChart("actualSoldWorld", 1, 4, "actualSoldWorld ");
        addUniChart("savedCountrySupply", 0, 6, "savedCountrySupply ");

    }
}
