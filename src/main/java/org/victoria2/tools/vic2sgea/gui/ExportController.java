package org.victoria2.tools.vic2sgea.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.victoria2.tools.vic2sgea.watcher.CsvExporter;
import org.victoria2.tools.vic2sgea.watcher.Watch;

import java.io.IOException;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/14/17 3:26 PM
 */
public class ExportController extends BaseController {
    @FXML
    public TextField tfCountryTag;
    @FXML
    public FilePrompt fpOutputFile;

    private Watch watch;

    public void exportCsv() {
        try {
            CsvExporter.exportCountry(watch, tfCountryTag.getText(), fpOutputFile.getPath());
        } catch (IOException e) {
            errorAlert(e, "Unable to export");
            e.printStackTrace();
        }
    }
    public void setWatch(Watch watch) {
        this.watch = watch;
    }
}
