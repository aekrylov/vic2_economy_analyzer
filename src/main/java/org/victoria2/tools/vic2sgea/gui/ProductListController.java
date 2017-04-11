package org.victoria2.tools.vic2sgea.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.main.Report;
import org.victoria2.tools.vic2sgea.main.TableRowDoubleClickFactory;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author nashetovich
 */
public class ProductListController extends BaseController implements Initializable {
    @FXML
    ToggleButton btnHideZeros;
    @FXML
    TableView<Product> productsTable;
    private static final ObservableList<Product> productsTableItems = FXCollections.observableArrayList();

    private Collection<Product> products;
    private Collection<Product> productsFiltered;

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

        btnHideZeros.setOnMouseClicked(e -> {
            boolean selected = btnHideZeros.isSelected();
            fillTable(selected ? productsFiltered : products);
        });

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
        this.products = report.getProductList();
        this.productsFiltered = products.stream()
                .filter(product -> product.getActualSupply() > 0)
                .collect(Collectors.toList());
    }

    public void fillTable(Collection<Product> products) {
        productsTableItems.clear();

        productsTableItems.addAll(products);
        productsTableItems.sort(Comparator.comparing(Product::getName));

        productsTable.setItems(productsTableItems);
    }
}
