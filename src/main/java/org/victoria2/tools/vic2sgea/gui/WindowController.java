package org.victoria2.tools.vic2sgea.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.victoria2.tools.vic2sgea.main.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author nashet
 */
public class WindowController extends BaseController implements Initializable {

    @FXML
    Button btnLoad;
    @FXML
    Button btnGoods;
    @FXML
    Button btnBrowseSave;
    @FXML
    Button btnBrowseLocal;
    @FXML
    Button btnBrowseMod;
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

    private ProductListController productListController;

    @FXML
    public TableColumn<Country, ImageView> colImage;
    @FXML
    public TableColumn<Country, String> colCountry;
    @FXML
    public TableColumn<Country, Long> colPopulation;
    @FXML
    TableColumn<Country, Float> colConsumption;
    @FXML
    TableColumn<Country, Float> colActualSupply;
    @FXML
    TableColumn<Country, Float> colGdp;
    @FXML
    TableColumn<Country, Float> colActualSupplyWithDeductions;
    @FXML
    TableColumn<Country, Float> colGDPPer;
    @FXML
    TableColumn<Country, Integer> colGDPPlace;
    @FXML
    TableColumn<Country, Float> colGDPPart;
    @FXML
    TableColumn<Country, Long> colGoldIncome;
    @FXML
    TableColumn<Country, Long> colWorkforce;
    @FXML
    TableColumn<Country, Long> colEmployment;
    @FXML
    TableColumn<Country, Float> colExport;
    @FXML
    TableColumn<Country, Float> colImport;
    @FXML
    TableColumn<Country, Float> colUnemploymentRate;
    @FXML
    TableColumn<Country, Float> colUnemploymentRateFactory;

    private void fillMainTable() {
        countryTableContent.clear();
        countryTableContent.addAll(report.getCountryList());
        mainTable.setItems(countryTableContent);
    }

    private WindowController self;

    private Report report;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        //add row click handler
        mainTable.setRowFactory(new TableRowDoubleClickFactory<>(country -> Main.showCountry(report, country)));

        colImage.setCellValueFactory(features -> {
            String tag = features.getValue().getTag();
            URL url = getClass().getResource("/flags/" + tag + ".png");
            if (url == null)
                return null;

            Image image = new Image(url.toString());
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);
            iv.setFitHeight(20);
            iv.getStyleClass().add("flag");
            return new SimpleObjectProperty<>(iv);
        });

        setFactory(colCountry, Country::getOfficialName);
        setFactory(colPopulation, Country::getPopulation);
        setFactory(colActualSupply, Country::getActualSupply);
        setFactory(colGdp, Country::getGdp);
        setFactory(colActualSupplyWithDeductions, Country::getActualSupplyWithDeductions);
        setFactory(colConsumption, Country::getActualDemand);
        setFactory(colGDPPer, Country::getGdpPerCapita);
        setFactory(colGDPPlace, Country::getGDPPlace);
        setFactory(colGDPPart, Country::getGDPPart);
        setFactory(colGoldIncome, Country::getGoldIncome);

        setFactory(colWorkforce, Country::getWorkforce);
        setFactory(colEmployment, Country::getEmployment);

        setFactory(colExport, Country::getExported);
        setFactory(colImport, Country::getImported);

        setFactory(colUnemploymentRate, Country::getUnemploymentRateRgo);
        setFactory(colUnemploymentRateFactory, Country::getUnemploymentRateFactory);

        setCellFactory(colPopulation, new KmgConverter<>());
        setCellFactory(colActualSupply, new KmgConverter<>());
        setCellFactory(colGdp, new KmgConverter<>());
        setCellFactory(colGDPPart, new PercentageConverter());
        setCellFactory(colGDPPer, new NiceFloatConverter());
        setCellFactory(colActualSupplyWithDeductions, new KmgConverter<>());
        setCellFactory(colWorkforce, new KmgConverter<>());
        setCellFactory(colEmployment, new KmgConverter<>());
        setCellFactory(colUnemploymentRate, new PercentageConverter());
        setCellFactory(colUnemploymentRateFactory, new PercentageConverter());

        colConsumption.setVisible(false);
        colWorkforce.setVisible(false);
        colEmployment.setVisible(false);
        colExport.setVisible(false);
        colImport.setVisible(false);
        colActualSupplyWithDeductions.setVisible(false);

        /*try {
            Config config = new Config();
        } catch (IOException e) {
            e.printStackTrace();
            errorAlert(e, "Couldn't load config");
        }*/
        PathKeeper.checkPaths();
        tfLocalization.setText(PathKeeper.LOCALISATION_PATH);
        tfSaveGame.setText(PathKeeper.SAVE_PATH);
        tfModPath.setText(PathKeeper.MOD_PATH);

        lblPlayer.setOnMouseClicked(e -> {
            if (report != null) {
                Main.showCountry(report, report.getPlayerCountry());
            }

        });

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
                    String savePath = tfSaveGame.getText();
                    String modPath = tfModPath.getText();
                    String gamePath = tfLocalization.getText();

                    PathKeeper.save();

                    report = new Report(savePath, gamePath, modPath);

                    fillMainTable();
                    productListController.setReport(report);
                    productListController.fillTable(report.getProductList());

                    float res = ((float) System.nanoTime() - startTime) / 1000000000;
                    System.out.println("Nash: total time is " + res + " seconds");
                    Platform.runLater(() -> self.setLabels());

                } catch (Exception e) {
                    e.printStackTrace();
                    errorAlert(e, "Exception while loading savegame");
                } finally {
                    Platform.runLater(() -> setInterfaceEnabled(true));
                }


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
                File initialFile = new File(PathKeeper.SAVE_PATH).getParentFile();
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
            File file;
            if (PathKeeper.LOCALISATION_PATH != null) {
                file = new File(PathKeeper.LOCALISATION_PATH);
                if (file.exists())
                    dirChooser.setInitialDirectory(file);
            }
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
                if (file.exists())
                    dirChooser.setInitialDirectory(file);
            }
            file = dirChooser.showDialog(null);
            String temp = file.getPath().replace("\\", "/"); // Usable path
            tfModPath.setText(temp);
            PathKeeper.MOD_PATH = temp;
        } catch (NullPointerException ignored) {
        }
    }

    public void setProductListController(ProductListController productListController) {
        this.productListController = productListController;
    }

    private static void errorAlert(Throwable e, String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(text);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.show();
        });
    }
}

class KmgConverter<T extends Number> extends StringConverter<T> {

    @Override
    public String toString(T object) {
        return Wrapper.toKMG(object);
    }

    //don't need this
    @Override
    public T fromString(String string) {
        return null;
    }
}

class PercentageConverter extends StringConverter<Float> {

    @Override
    public String toString(Float object) {
        return Wrapper.toPercentage(object);
    }

    //don't need this
    @Override
    public Float fromString(String string) {
        return null;
    }
}

class NiceFloatConverter extends StringConverter<Float> {

    @Override
    public String toString(Float object) {
        return String.format("%6.2f", object);
    }

    //don't need this
    @Override
    public Float fromString(String string) {
        return null;
    }
}