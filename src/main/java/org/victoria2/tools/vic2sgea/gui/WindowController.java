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
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.main.PathKeeper;
import org.victoria2.tools.vic2sgea.main.Report;
import org.victoria2.tools.vic2sgea.main.TableRowDoubleClickFactory;
import org.victoria2.tools.vic2sgea.main.Wrapper;

import java.net.URL;
import java.nio.file.Path;
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
    FilePrompt fpSaveGame;
    @FXML
    FilePrompt fpGamePath;
    @FXML
    FilePrompt fpModPath;
    @FXML
    public Pane progressWrap;
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
    TableColumn<Country, Float> colGDPPer;
    @FXML
    TableColumn<Country, Integer> colGDPPlace;
    @FXML
    TableColumn<Country, Float> colGDPPart;
    @FXML
    TableColumn<Country, Long> colGoldIncome;
    @FXML
    TableColumn<Country, Long> colWorkForceRgo;
    @FXML
    TableColumn<Country, Long> colWorkForceFactory;
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

    private Report report;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        //add row click handler
        mainTable.setRowFactory(new TableRowDoubleClickFactory<>(country -> Main.showCountry(report, country)));
        mainTable.setItems(countryTableContent);

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
        setFactory(colActualSupply, Country::getSold);
        setFactory(colGdp, Country::getGdp);
        setFactory(colConsumption, Country::getBought);
        setFactory(colGDPPer, Country::getGdpPerCapita);
        setFactory(colGDPPlace, Country::getGDPPlace);
        setFactory(colGDPPart, Country::getGDPPart);
        setFactory(colGoldIncome, Country::getGoldIncome);

        setFactory(colWorkForceRgo, Country::getWorkforceRgo);
        setFactory(colWorkForceFactory, Country::getWorkforceFactory);
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
        setCellFactory(colWorkForceRgo, new KmgConverter<>());
        setCellFactory(colWorkForceFactory, new KmgConverter<>());
        setCellFactory(colEmployment, new KmgConverter<>());
        setCellFactory(colUnemploymentRate, new PercentageConverter());
        setCellFactory(colUnemploymentRateFactory, new PercentageConverter());

        colConsumption.setVisible(false);
        colActualSupply.setVisible(false);
        colWorkForceRgo.setVisible(false);
        colWorkForceFactory.setVisible(false);
        colEmployment.setVisible(false);
        colExport.setVisible(false);
        colImport.setVisible(false);

        /*try {
            Config config = new Config();
        } catch (IOException e) {
            e.printStackTrace();
            errorAlert(e, "Couldn't load config");
        }*/
        PathKeeper.init();

        PathKeeper.getSavePath().ifPresent(fpSaveGame::setPath);
        PathKeeper.getLocalisationPath().ifPresent(fpGamePath::setPath);
        PathKeeper.getModPath().ifPresent(fpModPath::setPath);

        lblPlayer.setOnMouseClicked(e -> {
            if (report != null) {
                Main.showCountry(report, report.getPlayerCountry());
            }

        });

    }

    public final void onGoods(ActionEvent event) {
        Main.showProductList();
    }

    private void setInterfaceEnabled(boolean isEnabled) {
        progressWrap.setVisible(!isEnabled);
        progressWrap.toFront();
    }

    public void onLoad() {

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
                    Path savePath = fpSaveGame.getPath();
                    Path modPath = fpModPath.getPath();
                    Path gamePath = fpGamePath.getPath();

                    PathKeeper.save(savePath, gamePath, modPath);

                    report = new Report(savePath.toString(), gamePath.toString(), modPath.toString());

                    countryTableContent.setAll(report.getCountryList());
                    productListController.setReport(report);

                    float res = ((float) System.nanoTime() - startTime) / 1000000000;
                    System.out.println("Nash: total time is " + res + " seconds");
                    Platform.runLater(() -> setLabels(report));

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

    private void setLabels(Report report) {
        lblCurrentDate.setText(report.getCurrentDate());
        lblPlayer.setText(report.getPlayerCountry().getOfficialName());
        lblStartDate.setText(report.getStartDate());
        lblPopCount.setText(report.popCount + " pops total");
    }

    public void setProductListController(ProductListController productListController) {
        this.productListController = productListController;
    }

    public void createNewHistory() {
    }

    public void onWatcherWindow() {
        //prompt file name and dir to scan
        Main.showWatcherWindow();
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