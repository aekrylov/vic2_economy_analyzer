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

public class ProductController extends ChartsController {
    private GridPane grid;
    private Report report;
    private Product product;

    private Scene scene;

    private void addUniChart(String dataField, int column, int row, String name) throws ReflectiveOperationException, SecurityException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        //Collections.sort(product.);

        final Field storageData = GoodsStorage.class.getField(dataField);
        //Field goodsData=Product.class.getField(dataField);

        float total = 0;
        float totalSum = 0;

        //Comparator comparator= new Product.NameComparator();
        //Collections.sort(productsTableItems,comparator);

        for (Country country : report.getCountryList()) {
            if (!country.getOfficialName().equalsIgnoreCase("Total"))
                for (GoodsStorage everyStorage : country.storage) {
                    if (everyStorage.item == product) {
                        PieChart.Data temp = new PieChart.Data(country.getOfficialName(), storageData.getFloat(everyStorage));
                        pieChartData.add(temp);
                        total += storageData.getFloat(everyStorage);
                    }
                }
        }

        //Collections.sort(pieChartData);

        final PieChart chart = new PieChart(pieChartData);
        //chart.setLabelsVisible(false);
        //chart.setScaleX(1.6);
        //chart.setScaleY(1.6);
        totalSum = total * product.price;

        chart.setTitle(name + product.getName() + " (" + (long) total + " items by " + (long) totalSum + "£)");
        /*float result=0;
		for (Product storageData.get(everyStorage): report.availableGoods){
			if (everygood==product){
				result+=storageData.get(everyStorage).
			}

		}*/


        chart.setStartAngle(90);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(false);

        //chart.minHeightProperty().set(400);
        //chart.minWidthProperty().set(400);


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
                            //GoodsStorage temp=countries.findStorage(data.getName());
                            //if (temp!=null)	{
                            //String toFill=data.getName()+": "+String.valueOf((long)data.getPieValue())+" pounds "+String.valueOf((long)temp.savedCountrySupply)+ " items";
                            //String toFill=data.getName()+": "+String.valueOf((long)data.getPieValue())+"£ ("+String.valueOf((long)temp.actualSupply)+ " items, consumption is "+String.valueOf((long)temp.actualDemand+")");

                            caption.setText(data.getName() + ": " + String.valueOf((long) data.getPieValue()) + " items");
                            //caption.getClass();
                            //}
                            //else caption.setText("findStorage(data.getName()) returned NULL");
                        }
                    });
        }
        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    //MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            for (Country everyCountry : report.getCountryList()) {
                                if (everyCountry.getOfficialName().equalsIgnoreCase(data.getName())) {
                                    CountryController Window = new CountryController(report, everyCountry);
                                    //todo
                                    break;
                                }
                            }
                        }
                    });
        }


        grid.add(chart, column, row);
        grid.add(caption, column, row + 1);
    }

    ProductController(final Report report, Product product) {
        this.report = report;
        this.product = product;

        grid = new GridPane();

        //grid.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        try {
            addUniChart("actualSupply", 0, 0, "Producers of ");
            addUniChart("actualDemand", 0, 2, "Consumers of ");
            addUniChart("export", 1, 0, "Exporters of ");
            addUniChart("imported", 1, 2, "Importers of ");
            //addUniChart("MaxDemand",2,0, "MaxDemand ");
            addUniChart("worldmarketPool", 0, 4, "worldmarketPool ");
            addUniChart("actualSoldWorld", 1, 4, "actualSoldWorld ");
            addUniChart("savedCountrySupply", 0, 6, "savedCountrySupply ");
        } catch (SecurityException | ReflectiveOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);

		/*scrollPane.setFitToHeight(false);
		scrollPane.setFitToWidth(true);
		scrollPane.autosize();*/

        scene = new Scene(scrollPane, 1200, 950);
    }

    public Scene getScene() {
        return scene;
    }
}
