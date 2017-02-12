package org.victoria2.tools.vic2sgea.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.victoria2.tools.vic2sgea.main.PathKeeper;
import org.victoria2.tools.vic2sgea.watcher.WatcherManager;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Created by anth on 12.02.2017.
 */
public class WatchersController extends BaseController implements Initializable {
    @FXML
    FilePrompt fpHistoryFile;

    @FXML
    FilePrompt fpSaveDir;

    @FXML
    Button btnStart;


    public void startWatcher() {
        Path historyFile = Paths.get(fpHistoryFile.getPath());
        Path saveDir = Paths.get(fpSaveDir.getPath());

        try {
            WatcherManager.getInstance().add(historyFile, saveDir);
        } catch (IOException e) {
            e.printStackTrace();
            errorAlert(e, "Error while starting watcher");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String savePath = PathKeeper.SAVE_PATH;
        fpSaveDir.setPath(Paths.get(savePath).getParent().toString());
    }
}
