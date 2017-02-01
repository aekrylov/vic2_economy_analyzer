/**
 *
 */
package gui;

import com.sun.javafx.scene.control.skin.LabeledText;
import com.sun.javafx.scene.control.skin.TableCellSkin;
import eug.shared.GenericObject;
import eug.shared.ObjectVariable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.*;

import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

/**
 * @author nashet
 */
public class WindowController implements Initializable {

    @FXML
    Button btnOldLoader;
    @FXML
    Button btnReLoad;
    @FXML
    Button btnLoad;
    @FXML
    Button btnTest;
    @FXML
    Button btnGoods;
    @FXML
    Button btnBrowseSave;
    @FXML
    Button btnBrowseLocal;
    @FXML
    Button btnBrowseMod;
    @FXML
    public Label lblEerror;
    @FXML
    public Label lblStartDate;
    @FXML
    public Label lblCurrentDate;
    @FXML
    public Label lblPlayer;
    @FXML
    public Label lblPopCount;
    @FXML
    TableView<Country> mainTable;
    @FXML
    TextField tfSaveGame;
    @FXML
    TextField tfLocalization;
    @FXML
    TextField tfModPatch;

    @FXML
    ProgressIndicator piLoad;

    static final ObservableList<Country> countryTableContent = FXCollections.observableArrayList();

    private GoodsListController goodsListController;

    @FXML
    public TableColumn<Country, String> colCountry;
    @FXML
    public TableColumn<Country, String> colPopulation;
    @FXML
    TableColumn<Country, String> colGDP;
    @FXML
    TableColumn<Country, Double> colConsumption;
    @FXML
    TableColumn<Country, String> colGDPPer;
    @FXML
    TableColumn<Country, Integer> colGDPPlace;
    @FXML
    TableColumn<Country, String> colGDPPart;
    @FXML
    TableColumn<Country, Long> colGoldIncome;
    @FXML
    TableColumn<Country, Long> colWorkforce;
    @FXML
    TableColumn<Country, Long> colEmployment;
    @FXML
    TableColumn<Country, Long> colExport;
    @FXML
    TableColumn<Country, Long> colImport;
    @FXML
    TableColumn<Country, String> colUnemploymentRate;
    @FXML
    TableColumn<Country, String> colUnemploymentRateFactory;

    public void fillMainTable() {
        countryTableContent.clear();

        for (Country iterator : report.countryList) {
            if (iterator.population > 0)
                countryTableContent.add(iterator);
        }

		/*for(int i=0;i<thisSave.countryList.size();i++){
            //mainTable.getItems().add(Game.saveGame.countryList.get(i));
			countryTableContent.add(thisSave.countryList.get(i));		

		}	*/

        mainTable.setItems(countryTableContent);
    }

    public Label getErrorLabel() {
        return lblEerror;
    }

    private WindowController self;

    private Report report;// = new SaveGame();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub

		/* Listening to selections in warTable */
        // final ObservableList<MyCountry> warTableSelection = mainTable.getSelectionModel().getSelectedItems();
        //warTableSelection.addListener(tableSelectionChanged);


        colCountry.setCellValueFactory(new PropertyValueFactory<Country, String>("officialName"));
        //colPopulation.setCellValueFactory(new PropertyValueFactory<Country, Long>("population"));
        colPopulation.setCellValueFactory(new PropertyValueFactory<Country, String>("PopulationTV"));
        colGDP.setCellValueFactory(new PropertyValueFactory<Country, String>("actualSupply"));
        colConsumption.setCellValueFactory(new PropertyValueFactory<Country, Double>("actualDemand"));
        colConsumption.setVisible(false);
        colGDPPer.setCellValueFactory(new PropertyValueFactory<Country, String>("GDPPerCapitaTV"));
        colGDPPlace.setCellValueFactory(new PropertyValueFactory<Country, Integer>("GDPPlace"));
        colGDPPart.setCellValueFactory(new PropertyValueFactory<Country, String>("GDPPartTV"));
        colGoldIncome.setCellValueFactory(new PropertyValueFactory<Country, Long>("goldIncome"));

        colWorkforce.setCellValueFactory(new PropertyValueFactory<Country, Long>("workforce"));
        colWorkforce.setVisible(false);

        colEmployment.setCellValueFactory(new PropertyValueFactory<Country, Long>("employment"));
        colEmployment.setVisible(false);
        colExport.setCellValueFactory(new PropertyValueFactory<Country, Long>("export"));
        colExport.setVisible(false);
        colImport.setCellValueFactory(new PropertyValueFactory<Country, Long>("imported"));
        colImport.setVisible(false);

        colUnemploymentRate.setCellValueFactory(new PropertyValueFactory<Country, String>("UnemploymentProcentTV"));
        colUnemploymentRateFactory.setCellValueFactory(new PropertyValueFactory<Country, String>("unemploymentProcentFactoryTV"));

        PathKeeper.checkPaths();
        tfLocalization.setText(PathKeeper.LOCALIZATIONPATCH);
        tfSaveGame.setText(PathKeeper.SAVEGAMEPATH);
        tfModPatch.setText(PathKeeper.MODPATCH);

        lblPlayer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (report != null) {
                    String country = lblPlayer.getText().replaceFirst(" :playeR", "");
                    Country foundCountry = report.findCountry(country);
                    if (foundCountry != null)
                        new GUICountry(report, foundCountry);
                }

            }
        });

        class FloatComparator implements Comparator<String> {

            @Override
            public int compare(String arg0, String arg1) {
                float first = Wrapper.fromPercentage(arg0);
                float second = Wrapper.fromPercentage(arg1);

                return Float.compare(first, second);

            }
        }

        class KmgComparator implements Comparator<String> {

            @Override
            public int compare(String o1, String o2) {
                float f1 = Wrapper.fromKMG(o1);
                float f2 = Wrapper.fromKMG(o2);
                return Float.compare(f1, f2);
            }
        }

        colUnemploymentRateFactory.setComparator(new FloatComparator());
        colGDPPer.setComparator(new FloatComparator());
        colGDPPart.setComparator(new FloatComparator());
        colGDP.setComparator(new KmgComparator());

        colPopulation.setComparator(new KmgComparator());
        colUnemploymentRate.setComparator(new FloatComparator());
        //Game.window.

        self = this;
    }

    public final void onGoods(ActionEvent event) {


        Main.goodsStage.show();

    }

    /*public class LoadThread extends Service<String>  {
        WindowController from;
        LoadThread(WindowController infrom){
            from=infrom;
        }

        @Override
        protected Task<String> createTask() {

            // TODO Auto-generated method stub
            return new Task<String>(){

                @Override
                protected String call() throws Exception {
                    System.out.println("Nash: "+ this.getClass().getName()+" started");
                    float startTime=System.nanoTime();
                    Game.goodsStage.hide();
                    //try {

                        countryTableContent.clear();
                        report = new Report();
                        report.modPatch=tfModPatch.getText();
                        Runtime.getRuntime().gc();
                        System.out.println();
                        System.out.println("Nash: starting...");

                        System.out.println("Nash: reading EUG... free memory is "+Runtime.getRuntime().freeMemory());
                        report.readSaveBody(tfSaveGame.getText(),tfModPatch.getText(),tfLocalization.getText(),from);

                        System.out.println("Nash: reading localizations...  free memory is "+Runtime.getRuntime().freeMemory());
                        report.LocalisationReader();

                        lblPlayer.setText(report.findOfficalName(lblPlayer.getText())+ " :playeR");

                        Runtime.getRuntime().gc();


                    //}
                    catch (Exception e) {
                        // TODO copy from
                        e.printStackTrace();
                        System.out.println("Nash: ups... "+e.getLocalizedMessage());
                    }
                    PathKeeper.save();
                    System.out.println("Nash: processing data...  free memory is "+Runtime.getRuntime().freeMemory());
                    report.process();
                    System.out.println("Nash: filling table...  free memory is "+Runtime.getRuntime().freeMemory());
                    fillMainTable();
                    GoodsListController.setReport(report);
                    GoodsListController.fillGoodsTable(report.availableGoods);
                    btnGoods.setDisable(false);

                    float res=((float)System.nanoTime()-startTime)/1000000000;
                    System.out.println("Nash: total time is "+res+" secunds");
                    return null;
                }};

        }


    }*/
    public void disable() {
        this.btnBrowseLocal.setDisable(true);
        this.btnBrowseSave.setDisable(true);
        this.btnBrowseMod.setDisable(true);
        this.btnGoods.setDisable(true);
        this.btnLoad.setDisable(true);

        this.tfLocalization.setDisable(true);
        this.tfModPatch.setDisable(true);
        this.tfSaveGame.setDisable(true);

        this.mainTable.setDisable(true);
        this.lblPlayer.setDisable(true);
        this.piLoad.setVisible(true);
    }

    public void enable() {
        this.btnBrowseLocal.setDisable(false);
        this.btnBrowseSave.setDisable(false);
        this.btnBrowseMod.setDisable(false);
        this.btnGoods.setDisable(false);
        this.btnLoad.setDisable(false);

        this.tfLocalization.setDisable(false);
        this.tfModPatch.setDisable(false);
        this.tfSaveGame.setDisable(false);

        this.mainTable.setDisable(false);
        this.lblPlayer.setDisable(false);
        this.piLoad.setVisible(false);
    }

    public final void onLoad(ActionEvent event) {

		/*LoadThread loadThread = new LoadThread(this);
		loadThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("done :("  );
            }
        });

		Thread th = new Thread(loadThread.createTask());

        th.start();


		loadThread.start();
		 */
        //loadThread.createTask();
        //Game.window.getScene().getWindow().


        Main.goodsStage.hide();
        this.disable();

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                System.out.println();
                System.out.println("Nash: calc thread started...");
                float startTime = System.nanoTime();
                //float startTime=0;

                try {

                    countryTableContent.clear();
                    report = new Report();
                    report.modPatch = tfModPatch.getText();
                    //Runtime.getRuntime().gc();

                    System.out.println("Nash: reading EUG head... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    GenericObject head = report.readSaveHead(tfSaveGame.getText());

                    System.out.println("Nash: reading EUG... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    Vic2SaveGameNash save = report.readSaveBody(tfSaveGame.getText(), head);
                    System.out.println("Nash: processing POPs... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    report.populationProcess(save);

                    if (!report.readPrices(tfModPatch.getText()))
                        report.readPrices(tfLocalization.getText());

                    System.out.println("Nash: reading localizations...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    report.LocalisationReader(tfLocalization.getText());

                    //Runtime.getRuntime().gc();

                    PathKeeper.save();
                    System.out.println("Nash: processing data...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    report.process();
                    System.out.println("Nash: filling table...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    fillMainTable();
                    goodsListController.setReport(report);
                    goodsListController.fillGoodsTable(report.availableGoods);
                    //btnGoods.setDisable(false);

                    float res = ((float) System.nanoTime() - startTime) / 1000000000;
                    System.out.println("Nash: total time is " + res + " seconds");

                    //SetLabels();

                } catch (Exception e) {
                    // TODO copy from
                    e.printStackTrace();
                    System.out.println("Nash: ups... " + e.getLocalizedMessage());
                }
                //self.notify();
                //self.SetLabels();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        self.SetLabels();
                        enable();
                    }
                });

                return 0;
            }
        };
        Thread th = new Thread(task);
        th.start();

        //lblPlayer.setText(thisSave.findOfficalName(lblPlayer.getText())+ " :playeR");

    }

    public void SetLabels() {
        for (ObjectVariable everyHead : report.Head) {
            if (everyHead.varname.equalsIgnoreCase("date"))
                lblCurrentDate.setText("Current date: " + everyHead.getValue());
            if (everyHead.varname.equalsIgnoreCase("player"))
                lblPlayer.setText(report.findOfficalName(everyHead.getValue()) + " :playeR");
            //lblPlayer.setText(everyHead.getValue());
            if (everyHead.varname.equalsIgnoreCase("start_date")) {
                lblStartDate.setText("Start date: " + everyHead.getValue());
                break;
            }
        }
        lblPopCount.setText(report.popCount + " :pop counT");

    }

    public final void onBrowseSave(ActionEvent event) {
        // Throws error when user cancels selection
        // SaveGame saveGame=new SaveGame();
        try {
            FileChooser fileChooser = new FileChooser();


            if (!PathKeeper.SAVEGAMEPATH.isEmpty()) {
                //initialFile
                File initialFile = new File(PathKeeper.SAVEGAMEPATH);
                initialFile = new File(initialFile.getParent());

                fileChooser.setInitialDirectory(initialFile);
            }

            File file = fileChooser.showOpenDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfSaveGame.setText(temp);
            PathKeeper.SAVEGAMEPATH = temp;

        } catch (NullPointerException e) {
        }


    }

    public final void onBrowseLocal(ActionEvent event) {
        // Throws error when user cancels selection
        // SaveGame saveGame=new SaveGame();
        try {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File file = new File(PathKeeper.LOCALIZATIONPATCH);
            if (!PathKeeper.LOCALIZATIONPATCH.isEmpty()) dirChooser.setInitialDirectory(file);
            file = dirChooser.showDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfLocalization.setText(temp);
            PathKeeper.LOCALIZATIONPATCH = temp;
        } catch (NullPointerException ignored) {
        }


    }

    public final void onBrowseMod(ActionEvent event) {
        // Throws error when user cancels selection
        // SaveGame saveGame=new SaveGame();
        try {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File file;
            if (PathKeeper.MODPATCH != null) {
                file = new File(PathKeeper.MODPATCH);
                if (!PathKeeper.MODPATCH.isEmpty()) dirChooser.setInitialDirectory(file);
            }
            file = dirChooser.showDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfModPatch.setText(temp);
            PathKeeper.MODPATCH = temp;
        } catch (NullPointerException ignored) {
        }
    }

    public final void onTableClicked(MouseEvent event) {

        this.getClass();

        String offName = null;
        if (event != null && event.getTarget() != null) {
            if (event.getTarget() instanceof TableCellSkin) {
                TableCellSkin fer;
                fer = (TableCellSkin) event.getTarget();

                offName = fer.bindings.getText();

                this.getClass();
            }
            if (event.getTarget() instanceof LabeledText) {
                LabeledText fer;
                fer = (LabeledText) event.getTarget();

                offName = fer.getText();

                this.getClass();
            }
            if (offName != null && !offName.isEmpty()) {
                //if (!offName.equalsIgnoreCase("Total")){
                Country country = report.findCountry(offName);
                if (country != null) {
                    country.getClass();
                    GUICountry window = new GUICountry(report, country);

                    //todo country window
                }
                //}
                //else{

                //}
            }
        }
    }


    public void setGoodsListController(GoodsListController goodsListController) {
        this.goodsListController = goodsListController;
    }
}