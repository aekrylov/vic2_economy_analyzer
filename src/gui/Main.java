package gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	  //SaveGame saveGame = new SaveGame();
	 public static Stage goodsStage;
	 public static Stage countryStage;
	 public static Stage window;
	 public  static void main(String[] args) {
		 launch(args);
	 }
	 @Override
	 public void start(Stage stage) throws Exception {
		 window=stage;
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

	            windowController.setGoodsListController((GoodsListController) productListLoader.getController());

	            goodsStage = new Stage();
	            goodsStage.setTitle("Product list window");
	            goodsStage.getIcons().add(new Image("/flags/EST.png"));  //Cause I'm Estonian, thats why 
	            goodsStage.setScene(new Scene(root));

	            //hide this current window (if this is whant you want
	            //((Node)(event.getSource())).getScene().getWindow().hide();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	 }



 }
