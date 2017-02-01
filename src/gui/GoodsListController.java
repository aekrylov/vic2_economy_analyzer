/**
 *
 */
package gui;

import java.net.URL;
import java.util.*;

import main.Product;
import main.Report;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.TableRowDoubleClickFactory;

/**
 * @author nashetovich
 */
public class GoodsListController extends BaseController implements Initializable {
    @FXML
    TableView<Product> productsTable;
    static final ObservableList<Product> productsTableItems = FXCollections.observableArrayList();
    @FXML
    TableColumn<Product, String> colName;
    @FXML
    TableColumn<Product, Float> colConsumption;
    @FXML
    TableColumn<Product, Float> colRealSupply;
    @FXML
    TableColumn<Product, Float> colActualBought;
    @FXML
    TableColumn<Product, Float> colAffordable;
    @FXML
    TableColumn<Product, Float> colMaxDemand;
    @FXML
    TableColumn<Product, Float> colBasePrice;
    @FXML
    TableColumn<Product, Float> colMinPrice;
    @FXML
    TableColumn<Product, Float> colMaxPrice;
    @FXML
    TableColumn<Product, Float> colPrice;
    @FXML
    TableColumn<Product, String> colTrend;
    @FXML
    TableColumn<Product, Float> colOverproduced;
    @FXML
    TableColumn<Product, Float> colInflation;
    private Report report;

    /*GoodsListController(Report inReport){
        report=inReport;
    }*/
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
        //add double click listener
        productsTable.setRowFactory(new TableRowDoubleClickFactory<>(product -> {
            Main.showProduct(report, product);
        }));
        
        setFactory(colName, "name");
        setFactory(colConsumption, "Consumption");
        setFactory(colRealSupply, "RealSupply");
        setFactory(colActualBought, "ActualBought");
        colActualBought.setVisible(false);
        setFactory(colAffordable, "Affordable");
        setFactory(colMaxDemand, "MaxDemand");
        setFactory(colBasePrice, "BasePrice");
        setFactory(colMinPrice, "MinPrice");
        setFactory(colMaxPrice, "MaxPrice");
        setFactory(colPrice, "Price");
        setFactory(colTrend, "Trend");
        setFactory(colOverproduced, "Overproduced");
        setFactory(colInflation, "Inflation");
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void fillTable(Collection<Product> products) {
        productsTableItems.clear();

        Product total = new Product("Total (pounds)", 0);
        for (Product product : products) {
            total.supply += product.supply * product.price;
            total.consumption += product.consumption * product.price;
            total.affordable += product.affordable * product.price;
            total.maxDemand += product.maxDemand * product.price;
            total.basePrice += product.basePrice;
            total.price += product.price;
            total.actualBought += product.actualBought * product.price;

        }
        total.basePrice = total.basePrice / products.size();
        total.price = total.price / products.size();

        productsTableItems.addAll(products);

        //Collections.sort(total);
        productsTableItems.add(total);
        Comparator comparator = new Product.NameComparator();
        productsTableItems.sort(comparator);

        productsTable.setItems(productsTableItems);
    }
}
