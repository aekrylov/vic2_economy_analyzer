package gui;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.Country;
import main.Product;
import main.GoodsStorage;
import main.Report;

public class GUICountry {
    GridPane grid;

    void addUniChart(final Report inReport, final Country inCountry, String dataField, int column, int row, String name) throws ReflectiveOperationException, SecurityException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        //pieChartData.setAll(country.storage);

        final Class[] subCalses = GoodsStorage.class.getClasses();

        Comparator comparator = null;

        if (dataField.equalsIgnoreCase("actualSupply")) comparator = new GoodsStorage.actualSupplySort();
        if (dataField.equalsIgnoreCase("export")) comparator = new GoodsStorage.exportSort();
        if (dataField.equalsIgnoreCase("actualDemand")) comparator = new GoodsStorage.actualDemandSort();
        if (dataField.equalsIgnoreCase("savedCountrySupply")) comparator = new GoodsStorage.savedCountrySupplySort();
        if (dataField.equalsIgnoreCase("imported")) comparator = new GoodsStorage.importedSort();


        //GoodsStorage.actualSupply comparator= new GoodsStorage.actualSupply();

        Collections.sort(inCountry.storage, comparator);


        final Field storageData = GoodsStorage.class.getField(dataField);
        Field countryData = Country.class.getField(dataField);

        for (GoodsStorage everyGood : inCountry.storage) {

            if ((float) storageData.get(everyGood) * everyGood.item.price > 0) {

                PieChart.Data temp = new PieChart.Data(everyGood.item.getName(), (float) storageData.get(everyGood) * everyGood.item.price);
                pieChartData.add(temp);
            }
        }

        //Collections.sort(pieChartData,actualSupply);

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(name + " of " + inCountry.getOfficialName() + " (" + (long) countryData.getFloat(inCountry) + "£)");
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
                            GoodsStorage thisCountry = inCountry.findStorage(data.getName());
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

                            Product foundProduct = inReport.findProduct(data.getName());
                            if (foundProduct != null) {
                                GUIGoods goodsChart = new GUIGoods(inReport, inReport.countryList, foundProduct);

                            } else caption.setText("inReport.findProduct(data.getName() returned NULL");
                        }
                    });
        }
        grid.add(chart, column, row);
        grid.add(caption, column, row + 1);
    }

    GUICountry(final Report inReport, final Country inCountry) {

        Main.countryStage = new Stage();
        Main.countryStage.setTitle(inCountry.getOfficialName() + " - Country window");
        Main.countryStage.getIcons().add(new Image("/flags/EST.png")); /* Cause I'm Estonian, thats why */


        grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //addSupplyChart(inReport, inCountry);
        //AddExportChart(inReport, inCountry);
        try {
            addUniChart(inReport, inCountry, "actualSupply", 0, 0, "Production");
            addUniChart(inReport, inCountry, "actualDemand", 0, 2, "Consumption");
            addUniChart(inReport, inCountry, "export", 1, 0, "Export");
            addUniChart(inReport, inCountry, "imported", 1, 2, "Import");
            //addUniChart(inReport, inCountry, "MaxDemand",2,0, "MaxDemand");
            //addUniChart(inReport, inCountry, "savedCountrySupply",2,0, "Total Supply");
            addUniChart(inReport, inCountry, "savedCountrySupply", 0, 4, "Total Supply");
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


        //Scene scene = new Scene(grid, 650, 550);
        Scene scene = new Scene(scrollPane, 1200, 950);


        //grid.autosize();
        //scrollPane.autosize();

        Main.countryStage.setScene(scene);
        Main.countryStage.show();
    }
}

