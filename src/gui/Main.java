package gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.Country;
import main.Product;
import main.Report;

public class Main extends Application {

    private static Stage productsWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader windowLoader = new FXMLLoader(getClass().getResource("Window.fxml"));
        Parent root = windowLoader.load();
        WindowController windowController = windowLoader.getController();

        stage.setTitle("Victoria II SGEA: main window");
        stage.setScene(new Scene(root));
        //stage.setMinWidth(700);
        //stage.setMinHeight(500);
        stage.show();
        stage.getIcons().add(new Image("/flags/EST.png")); /* Cause I'm Estonian, thats why */

        // Throws error when user cancels selection


        try {
            FXMLLoader productListLoader = new FXMLLoader(getClass().getResource("GoodsList.fxml"));
            root = productListLoader.load();

            windowController.setGoodsListController(productListLoader.getController());

            productsWindow = new Stage();
            productsWindow.setTitle("Product list window");
            productsWindow.getIcons().add(new Image("/flags/EST.png"));  //Cause I'm Estonian, thats why
            productsWindow.setScene(new Scene(root));

            //hide this current window (if this is whant you want
            //((Node)(event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void showCountry(Report report, Country country) {
        CountryController controller = new CountryController(report, country);

        Stage window = new Stage();
        window.setTitle(country.getOfficialName() + " - Country window");
        window.setScene(controller.getScene());

        window.show();
    }

    public static void showProduct(Report report, Product product) {
        ProductController controller = new ProductController(report, product);

        Stage productWindow = new Stage();
        productWindow.setTitle(product.getName() + " - Product window");
        productWindow.setScene(controller.getScene());

        productWindow.show();
    }

    public static void showProductList() {
        productsWindow.show();
    }

    public static void hideProductList() {
        productsWindow.hide();
    }
}
