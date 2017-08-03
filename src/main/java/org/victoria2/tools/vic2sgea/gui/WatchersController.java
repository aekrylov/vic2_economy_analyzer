package org.victoria2.tools.vic2sgea.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
    public Label activeWatcherFile;
    @FXML
    public Label activeWatcherSaveDir;
    @FXML
    FilePrompt fpHistoryFile;
    @FXML
    FilePrompt fpSaveDir;

    private WatcherManager watcherManager = WatcherManager.getInstance();

    public void startWatcher() {
        Path historyFile = Paths.get(fpHistoryFile.getPath());
        Path saveDir = Paths.get(fpSaveDir.getPath());

        try {
            watcherManager.startWatching(historyFile, saveDir);

            activeWatcherFile.setText(historyFile.toString());
            activeWatcherSaveDir.setText(saveDir.toString());
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

    public void stopWatcher() {
        WatcherManager.getInstance().stopWatching();
        activeWatcherFile.setText("");
        activeWatcherSaveDir.setText("");
    }
}
