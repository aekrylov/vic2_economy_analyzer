/**
 * 
 */
package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.LabeledText;
import com.sun.javafx.scene.control.skin.TableCellSkin;

import main.Product;
import main.Report;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * @author nashetovich
 *
 */
public class GoodsListController implements Initializable {
	 @FXML
	 TableView<Product> goodsTable;
	static final ObservableList<Product> PRODUCT_TABLE_CONTENT = FXCollections.observableArrayList();
	@FXML	TableColumn<Product, String> colName;
	@FXML	TableColumn<Product, Float> colConsumption;
	@FXML	TableColumn<Product, Float> colRealSupply;
	@FXML	TableColumn<Product, Float> colActualBought;
	@FXML	TableColumn<Product, Float> colAffordable;
	@FXML	TableColumn<Product, Float> colMaxDemand;
	@FXML	TableColumn<Product, Float> colBasePrice;
	@FXML	TableColumn<Product, Float> colMinPrice;
	@FXML	TableColumn<Product, Float> colMaxPrice;
	@FXML	TableColumn<Product, Float> colPrice;
	@FXML	TableColumn<Product, String> colTrend;
	@FXML	TableColumn<Product, Float> colOverproduced;
	@FXML	TableColumn<Product, Float> colInflation;
	public static Report thisSave;
	/*GoodsListController(Report inReport){
		thisSave=inReport;
	}*/
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		colName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		colConsumption.setCellValueFactory(new PropertyValueFactory<Product, Float>("Consumption"));
		colRealSupply.setCellValueFactory(new PropertyValueFactory<Product, Float>("RealSupply"));
		colActualBought.setCellValueFactory(new PropertyValueFactory<Product, Float>("ActualBought"));
		colActualBought.setVisible(false);
		colAffordable.setCellValueFactory(new PropertyValueFactory<Product, Float>("Affordable"));
		colMaxDemand.setCellValueFactory(new PropertyValueFactory<Product, Float>("MaxDemand"));
		colBasePrice.setCellValueFactory(new PropertyValueFactory<Product, Float>("BasePrice"));
		colMinPrice.setCellValueFactory(new PropertyValueFactory<Product, Float>("MinPrice"));
		colMaxPrice.setCellValueFactory(new PropertyValueFactory<Product, Float>("MaxPrice"));
		colPrice.setCellValueFactory(new PropertyValueFactory<Product, Float>("Price"));
		colTrend.setCellValueFactory(new PropertyValueFactory<Product, String>("Trend"));
		colOverproduced.setCellValueFactory(new PropertyValueFactory<Product, Float>("Overproduced"));
		colInflation.setCellValueFactory(new PropertyValueFactory<Product, Float>("Inflation"));
	}

	public void setReport(Report report){
		thisSave= report;
	}
	
	public void fillGoodsTable(ArrayList<Product> inGoods){
		PRODUCT_TABLE_CONTENT.clear();
		
		Product total = new Product("Total (pounds)", 0);
		for(Product everyGood: inGoods ){
			total.supply+=everyGood.supply*everyGood.price;
			total.consumption+=everyGood.consumption*everyGood.price;		
			total.affordable+=everyGood.affordable*everyGood.price;
			total.maxDemand+=everyGood.maxDemand*everyGood.price;
			total.basePrice+=everyGood.basePrice;
			total.price+=everyGood.price;
			total.actualBought+=everyGood.actualBought*everyGood.price;
			
		}
		total.basePrice=total.basePrice/inGoods.size();
		total.price=total.price/inGoods.size();
		
		for(Product iterator: inGoods ){
				PRODUCT_TABLE_CONTENT.add(iterator);
		}
		//Collections.sort(total);
		PRODUCT_TABLE_CONTENT.add(total);
		Comparator comparator= new Product.NameComparator();
		Collections.sort(PRODUCT_TABLE_CONTENT,comparator);
		
		
		/*for(int i=0;i<thisSave.countryList.size();i++){
			//mainTable.getItems().add(Game.saveGame.countryList.get(i));			
			countryTableContent.add(thisSave.countryList.get(i));		

		}	*/	

		goodsTable.setItems(PRODUCT_TABLE_CONTENT);
	}
	public final void onTableClicked(MouseEvent event) {

		this.getClass();

		String foundName = null;
		if (event!=null && event.getTarget()!=null)
		{
			if (event.getTarget() instanceof TableCellSkin){
				TableCellSkin fer;
				fer=(TableCellSkin) event.getTarget();	

				foundName=fer.bindings.getText();		

				this.getClass();
			}
			if (event.getTarget() instanceof LabeledText){
				LabeledText fer;
				fer=(LabeledText) event.getTarget();

				foundName=fer.getText();

				this.getClass();
			}
			if (foundName!=null && !foundName.isEmpty()){
				if (!foundName.equalsIgnoreCase("Total (pounds)")){
					Product foundProduct =thisSave.findProduct(foundName);
					
					if (foundProduct !=null){
						foundProduct.getClass();
						GUIGoods window = new GUIGoods(thisSave, thisSave.countryList, foundProduct);

					}
				}
				else{					
				}
			}
		}
	}

}
