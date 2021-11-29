package org.victoria2.tools.vic2sgea.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.victoria2.tools.vic2sgea.watcher.CsvExporter;
import org.victoria2.tools.vic2sgea.watcher.Watch;
import org.victoria2.tools.vic2sgea.watcher.Watcher;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/14/17 3:26 PM
 */
public class ExportController extends BaseController implements Initializable {
    @FXML
    public TextField tfCountryTag;
    @FXML
    public FilePrompt fpOutputDir;
    @FXML
    public ChoiceBox<String> fieldName;

    private Watch watch;

    private static Map<String, String> fieldMapping = new HashMap<>();
    static {
        fieldMapping.put("gdp", "GDP");
        fieldMapping.put("gdpPerCapita", "GDP per capita");
        fieldMapping.put("population", "Population");
        fieldMapping.put("employmentFactory", "Factory employed");
        fieldMapping.put("employmentRGO", "RGO employed");
        fieldMapping.put("workforceFactory", "Factory workforce");
        fieldMapping.put("workforceRGO", "RGO workforce");
        fieldMapping.put("unemploymentRate", "Unemployment rate");
        fieldMapping.put("unemploymentRateRGO", "Unemployment rate RGO");
        fieldMapping.put("unemploymentRateFactory", "Unemployment rate factory");
        fieldMapping.put("goldIncome", "Gold income");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fieldName.setItems(FXCollections.observableList(new ArrayList<>(fieldMapping.keySet())));
        fieldName.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return fieldMapping.get(object);
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });
    }

    public void exportCsv() {
        try {
            CsvExporter.exportCountry(watch, tfCountryTag.getText(), fpOutputDir.getPath().resolve("output.csv"));
        } catch (IOException e) {
            errorAlert(e, "Unable to export");
            e.printStackTrace();
        }
    }

    public void exportAll() {
        try {
            CsvExporter.exportAll(watch, fieldName.getValue(), fpOutputDir.getPath().resolve("output.csv"));
        } catch (IOException e) {
            errorAlert(e, "Unable to export");
            e.printStackTrace();
        }
    }

    public void setWatcher(Watcher watcher) {
        this.watch = watcher.getWatch();
        fpOutputDir.setPath(watcher.getHistoryFile().getParent());
    }
}
