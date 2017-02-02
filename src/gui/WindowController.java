/**
 *
 */
package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
public class WindowController extends BaseController implements Initializable {

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
    TextField tfModPath;

    @FXML
    ProgressIndicator piLoad;

    private static final ObservableList<Country> countryTableContent = FXCollections.observableArrayList();

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

    private void fillMainTable() {
        countryTableContent.clear();

        for (Country country : report.getCountryList()) {
                countryTableContent.add(country);
        }

        mainTable.setItems(countryTableContent);
    }

    public Label getErrorLabel() {
        return lblEerror;
    }

    private WindowController self;

    private Report report;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        //add row click handler
        mainTable.setRowFactory(new TableRowDoubleClickFactory<>(country -> Main.showCountry(report, country)));


        setFactory(colCountry, "officialName");
        setFactory(colPopulation, "PopulationTV");
        setFactory(colGDP, "actualSupply");
        setFactory(colConsumption, "actualDemand");
        colConsumption.setVisible(false);
        setFactory(colGDPPer, "GDPPerCapitaTV");
        setFactory(colGDPPlace, "GDPPlace");
        setFactory(colGDPPart, "GDPPartTV");
        setFactory(colGoldIncome, "goldIncome");

        setFactory(colWorkforce, "workforce");
        colWorkforce.setVisible(false);

        setFactory(colEmployment, "employment");
        colEmployment.setVisible(false);
        setFactory(colExport, "exported");
        colExport.setVisible(false);
        setFactory(colImport, "imported");
        colImport.setVisible(false);

        setFactory(colUnemploymentRate, "UnemploymentProcentTV");
        setFactory(colUnemploymentRateFactory, "unemploymentProcentFactoryTV");

        PathKeeper.checkPaths();
        tfLocalization.setText(PathKeeper.LOCALISATION_PATH);
        tfSaveGame.setText(PathKeeper.SAVE_PATH);
        tfModPath.setText(PathKeeper.MOD_PATH);

        lblPlayer.setOnMouseClicked(e -> {
            if (report != null) {
                new CountryController(report, report.getPlayerCountry());
                //todo show country
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

        self = this;
    }

    public final void onGoods(ActionEvent event) {
        Main.showProductList();
    }
    
    private void setInterfaceEnabled(boolean isEnabled) {
        this.btnBrowseLocal.setDisable(!isEnabled);
        this.btnBrowseSave.setDisable(!isEnabled);
        this.btnBrowseMod.setDisable(!isEnabled);
        this.btnGoods.setDisable(!isEnabled);
        this.btnLoad.setDisable(!isEnabled);

        this.tfLocalization.setDisable(!isEnabled);
        this.tfModPath.setDisable(!isEnabled);
        this.tfSaveGame.setDisable(!isEnabled);

        this.mainTable.setDisable(!isEnabled);
        this.lblPlayer.setDisable(!isEnabled);
        this.piLoad.setVisible(!isEnabled);

    }

    public final void onLoad(ActionEvent event) {

        //Main.hideProductList();
        setInterfaceEnabled(false);

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                System.out.println();
                System.out.println("Nash: calc thread started...");
                float startTime = System.nanoTime();
                //float startTime=0;

                try {
                    Report.setModPath(tfModPath.getText());
                    Report.setLocalisationPath(tfLocalization.getText());

                    PathKeeper.save();

                    report = new Report(tfSaveGame.getText());

                    System.out.println("Nash: filling table...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
                    fillMainTable();
                    goodsListController.setReport(report);
                    goodsListController.fillTable(report.getProductList());

                    float res = ((float) System.nanoTime() - startTime) / 1000000000;
                    System.out.println("Nash: total time is " + res + " seconds");

                } catch (Exception e) {
                    // TODO copy from
                    e.printStackTrace();
                    System.out.println("Nash: ups... " + e.getLocalizedMessage());
                }

                Platform.runLater(() -> {
                    self.setLabels();
                    self.setInterfaceEnabled(true);
                });

                return 0;
            }
        };
        Thread th = new Thread(task);
        th.start();

    }

    private void setLabels() {
        lblCurrentDate.setText(report.getCurrentDate());
        lblPlayer.setText(report.getPlayerCountry().getOfficialName());
        lblStartDate.setText(report.getStartDate());
        lblPopCount.setText(report.popCount + " pops total");
    }

    public final void onBrowseSave(ActionEvent event) {
        // Throws error when user cancels selection
        // SaveGame saveGame=new SaveGame();
        try {
            FileChooser fileChooser = new FileChooser();


            if (!PathKeeper.SAVE_PATH.isEmpty()) {
                //initialFile
                File initialFile = new File(PathKeeper.SAVE_PATH);
                initialFile = new File(initialFile.getParent());

                fileChooser.setInitialDirectory(initialFile);
            }

            File file = fileChooser.showOpenDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfSaveGame.setText(temp);
            PathKeeper.SAVE_PATH = temp;

        } catch (NullPointerException ignored) {
        }


    }

    public final void onBrowseLocal(ActionEvent event) {
        // Throws error when user cancels selection
        // SaveGame saveGame=new SaveGame();
        try {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File file = new File(PathKeeper.LOCALISATION_PATH);
            if (!PathKeeper.LOCALISATION_PATH.isEmpty()) dirChooser.setInitialDirectory(file);
            file = dirChooser.showDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfLocalization.setText(temp);
            PathKeeper.LOCALISATION_PATH = temp;
        } catch (NullPointerException ignored) {
        }


    }

    public final void onBrowseMod(ActionEvent event) {
        // Throws error when user cancels selection
        // SaveGame saveGame=new SaveGame();
        try {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File file;
            if (PathKeeper.MOD_PATH != null) {
                file = new File(PathKeeper.MOD_PATH);
                if (!PathKeeper.MOD_PATH.isEmpty()) dirChooser.setInitialDirectory(file);
            }
            file = dirChooser.showDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfModPath.setText(temp);
            PathKeeper.MOD_PATH = temp;
        } catch (NullPointerException ignored) {
        }
    }

    public void setGoodsListController(GoodsListController goodsListController) {
        this.goodsListController = goodsListController;
    }
}