package org.victoria2.tools.vic2sgea.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.victoria2.tools.vic2sgea.export.CsvExporter;
import org.victoria2.tools.vic2sgea.export.ExportUtils;
import org.victoria2.tools.vic2sgea.watcher.Watch;
import org.victoria2.tools.vic2sgea.watcher.Watcher;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fieldName.setItems(FXCollections.observableList(new ArrayList<>(ExportUtils.COUNTRY_FIELDS.keySet())));
        fieldName.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return ExportUtils.COUNTRY_FIELDS.get(object);
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
