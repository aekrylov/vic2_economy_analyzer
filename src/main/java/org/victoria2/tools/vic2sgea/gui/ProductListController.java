package org.victoria2.tools.vic2sgea.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.victoria2.tools.vic2sgea.main.Product;
import org.victoria2.tools.vic2sgea.main.Report;
import org.victoria2.tools.vic2sgea.main.TableRowDoubleClickFactory;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;

/**
 * @author nashetovich
 */
public class ProductListController extends BaseController implements Initializable {
    @FXML
    TableView<Product> productsTable;
    private static final ObservableList<Product> productsTableItems = FXCollections.observableArrayList();
    @FXML
    TableColumn<Product, String> colName;
    @FXML
    TableColumn<Product, Float> colConsumption;
    @FXML
    TableColumn<Product, Float> colRealSupply;
    @FXML
    TableColumn<Product, Float> colActualSupply;
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

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        //add double click listener
        productsTable.setRowFactory(new TableRowDoubleClickFactory<>(product -> Main.showProduct(report, product)));

        setFactory(colName, Product::getName);
        setFactory(colConsumption, Product::getConsumption, new NiceFloatConverter());
        setFactory(colRealSupply, Product::getSupply, new NiceFloatConverter());
        setFactory(colActualSupply, Product::getActualSupply, new NiceFloatConverter());
        colActualSupply.setVisible(false);
        setFactory(colAffordable, Product::getDemand, new NiceFloatConverter());
        setFactory(colMaxDemand, Product::getMaxDemand, new NiceFloatConverter());
        setFactory(colBasePrice, Product::getBasePrice, new NiceFloatConverter());
        setFactory(colMinPrice, Product::getMinPrice, new NiceFloatConverter());
        setFactory(colMaxPrice, Product::getMaxPrice, new NiceFloatConverter());
        setFactory(colPrice, Product::getPrice, new NiceFloatConverter());
        setFactory(colTrend, Product::getTrend);
        setFactory(colOverproduced, Product::getOverproduced, new NiceFloatConverter());
        setFactory(colInflation, Product::getInflation, new NiceFloatConverter());
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void fillTable(Collection<Product> products) {
        productsTableItems.clear();

        //todo is it necessary?
        Product total = new Product("Total (pounds)");
        for (Product product : products) {
            total.supply += product.getSupply() * product.price;
            total.consumption += product.getConsumption() * product.price;
            total.demand += product.getDemand() * product.price;
            total.maxDemand += product.getMaxDemand() * product.price;
            total.basePrice += product.getBasePrice();
            total.price += product.getPrice();
            total.actualSupply += product.getActualSupply() * product.price;

        }
        total.basePrice = total.basePrice / products.size();
        total.price = total.price / products.size();

        productsTableItems.addAll(products);

        productsTableItems.add(total);
        productsTableItems.sort(Comparator.comparing(Product::getName));

        productsTable.setItems(productsTableItems);
    }
}
