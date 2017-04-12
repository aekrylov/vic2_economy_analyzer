package org.victoria2.tools.vic2sgea.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.main.Properties;
import org.victoria2.tools.vic2sgea.main.Report;

import java.io.IOException;
  
public class Main extends Application {

    private static Stage productListWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader windowLoader = new FXMLLoader(getClass().getResource("/gui/Window.fxml"));
        Parent root = windowLoader.load();
        root.getStylesheets().add("/gui/style.css");
        WindowController windowController = windowLoader.getController();

        Properties props = new Properties();

        stage.setTitle("Victoria II SGEA v" + props.getVersion());
        stage.setScene(new Scene(root));
        //stage.setMinWidth(700);
        //stage.setMinHeight(500);
        stage.show();
        stage.getIcons().add(new Image("/flags/EST.png")); /* Cause I'm Estonian, thats why */

        try {
            FXMLLoader productListLoader = new FXMLLoader(getClass().getResource("/gui/ProductList.fxml"));
            root = productListLoader.load();
            root.getStylesheets().add("/gui/style.css");

            windowController.setProductListController(productListLoader.getController());

            productListWindow = new Stage();
            productListWindow.setTitle("Product list");
            productListWindow.getIcons().add(new Image("/flags/EST.png"));  //Cause I'm Estonian, thats why
            productListWindow.setScene(new Scene(root));

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
        productListWindow.show();
    }

    public static void hideProductList() {
        productListWindow.hide();
    }

    public static void showWatcherWindow() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/Watchers.fxml"));
        Parent root;
        try {
            root = loader.load();
            root.getStylesheets().add("/gui/style.css");

            Stage window = new Stage();
            window.setTitle("Watchers");
            window.setScene(new Scene(root));
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
