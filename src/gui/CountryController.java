package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.Country;
import main.GoodsStorage;
import main.Product;
import main.Report;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;

public class CountryController extends ChartsController {
    private final Report report;
    private final Scene scene;
    private Country country;
    private GridPane grid;

    public Scene getScene() {
        return scene;
    }

    private void addUniChart(String dataField, int column, int row, String name) throws ReflectiveOperationException, SecurityException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        //pieChartData.setAll(country.storage);

        final Class[] subCalses = GoodsStorage.class.getClasses();

        Comparator<GoodsStorage> comparator = null;

        if (dataField.equalsIgnoreCase("actualSupply")) comparator = new GoodsStorage.actualSupplySort();
        if (dataField.equalsIgnoreCase("export")) comparator = new GoodsStorage.exportSort();
        if (dataField.equalsIgnoreCase("actualDemand")) comparator = new GoodsStorage.actualDemandSort();
        if (dataField.equalsIgnoreCase("savedCountrySupply")) comparator = new GoodsStorage.savedCountrySupplySort();
        if (dataField.equalsIgnoreCase("imported")) comparator = new GoodsStorage.importedSort();


        //GoodsStorage.actualSupply comparator= new GoodsStorage.actualSupply();

        (country.storage).sort(comparator);


        final Field storageData = GoodsStorage.class.getField(dataField);
        Field countryData = Country.class.getField(dataField);

        for (GoodsStorage everyGood : country.storage) {

            if ((float) storageData.get(everyGood) * everyGood.item.price > 0) {

                PieChart.Data temp = new PieChart.Data(everyGood.item.getName(), (float) storageData.get(everyGood) * everyGood.item.price);
                pieChartData.add(temp);
            }
        }

        //Collections.sort(pieChartData,actualSupply);

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(name + " of " + country.getOfficialName() + " (" + (long) countryData.getFloat(country) + "£)");
        //chart.setScaleX(0.6);
        //chart.setScaleY(0.6);
        //chart.setPrefSize(300, 300);
        chart.setStartAngle(90);
        chart.setLegendVisible(false);

        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 20 arial;");

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_MOVED,
                    //MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            //caption.setTranslateX(e.getSceneX());
                            // caption.setTranslateY(e.getSceneY());
                            GoodsStorage thisCountry = country.findStorage(data.getName());
                            if (thisCountry != null) {
                                String toFill = "failed to found item...";
                                try {

                                    //toFill = data.getName()+": "+String.valueOf((long)data.getPieValue())+"£ ("+String.valueOf( (long)storageData.get(thisCountry))+ " items)";
                                    toFill = data.getName() + ": " + String.valueOf((long) data.getPieValue()) + "£ (" + String.valueOf((long) storageData.getFloat(thisCountry)) + " items)";
                                } catch (IllegalArgumentException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } catch (IllegalAccessException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                caption.setText(toFill);
                            } else caption.setText("findStorage(data.getName()) returned NULL");
                        }
                    });
        }

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {

                            Product foundProduct = report.findProduct(data.getName());
                            if (foundProduct != null) {
                                ProductController goodsChart = new ProductController(report, foundProduct);
                                //todo

                            } else caption.setText("report.findProduct(data.getName() returned NULL");
                        }
                    });
        }
        grid.add(chart, column, row);
        grid.add(caption, column, row + 1);
    }

    CountryController(final Report report, final Country country) {
        this.report = report;
        this.country = country;

        grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //addSupplyChart(report, country);
        //AddExportChart(report, country);
        try {
            addUniChart("actualSupply", 0, 0, "Production");
            addUniChart("actualDemand", 0, 2, "Consumption");
            addUniChart("export", 1, 0, "Export");
            addUniChart("imported", 1, 2, "Import");
            //addUniChart(report, country, "MaxDemand",2,0, "MaxDemand");
            //addUniChart(report, country, "savedCountrySupply",2,0, "Total Supply");
            addUniChart("savedCountrySupply", 0, 4, "Total Supply");
        } catch (SecurityException | ReflectiveOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);

        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(true);

		/*scrollPane.getContent().autosize();
		
		scrollPane.autosize();
		grid.autosize();*/

        scene = new Scene(scrollPane, 1200, 950);
    }
}

