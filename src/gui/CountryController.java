package gui;

import javafx.scene.Scene;
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

public class CountryController extends ChartsController {
    private Country country;

    public Scene getScene() {
        return scene;
    }

    private void addUniChart(String dataField, int column, int row, String name) throws ReflectiveOperationException, SecurityException {
        List<PieChart.Data> pieChartData = new ArrayList<>();

        Comparator<ProductStorage> comparator = ProductStorage.getComparator(dataField);
        (country.storage).sort(comparator);

        final Field storageData = ProductStorage.class.getField(dataField);
        Field countryData = Country.class.getField(dataField);

        for (ProductStorage everyGood : country.storage) {

            if ((float) storageData.get(everyGood) * everyGood.product.price > 0) {

                PieChart.Data temp = new PieChart.Data(everyGood.product.getName(), (float) storageData.get(everyGood) * everyGood.product.price);
                pieChartData.add(temp);
            }
        }

        Consumer<PieChart.Data> onClick = data -> {
            Product foundProduct = report.findProduct(data.getName());
            if (foundProduct != null) {
                Main.showProduct(report, foundProduct);
            }
        };

        Function<PieChart.Data, String> onEnter = data -> {
            ProductStorage thisCountry = country.findStorage(data.getName());
            if (thisCountry != null) {
                String toFill = "failed to found product...";
                try {
                    toFill = data.getName() + ": " + String.valueOf((long) data.getPieValue()) + "£ (" + String.valueOf((long) storageData.getFloat(thisCountry)) + " items)";
                } catch (IllegalArgumentException | IllegalAccessException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                return toFill;
            } else return "findStorage(data.getName()) returned NULL";
        };

        String title = name + " of " + country.getOfficialName() + " (" + (long) countryData.getFloat(country) + "£)";
        addChart(pieChartData, column, row, title, onEnter, onClick);
    }

    CountryController(final Report report, final Country country) {
        super(report);
        this.country = country;

        try {
            addUniChart("actualSupply", 0, 0, "Production");
            addUniChart("actualDemand", 0, 2, "Consumption");
            addUniChart("exported", 1, 0, "Export");
            addUniChart("imported", 1, 2, "Import");
            //addUniChart(report, country, "MaxDemand",2,0, "MaxDemand");
            addUniChart("savedCountrySupply", 0, 4, "Total Supply");
        } catch (SecurityException | ReflectiveOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

