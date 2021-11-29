package org.victoria2.tools.vic2sgea.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.victoria2.tools.vic2sgea.main.PathKeeper;
import org.victoria2.tools.vic2sgea.watcher.Watcher;
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
    public TableColumn<Watcher, Path> colHistoryFile;
    @FXML
    public TableColumn<Watcher, Path> colSaveDir;
    @FXML
    public TableColumn<Watcher, String> colStatus;
    @FXML
    public TableColumn<Watcher, Void> colActions;

    @FXML
    TableView<Watcher> tvWatcherList;

    @FXML
    FilePrompt fpHistoryFile;
    @FXML
    FilePrompt fpSaveDir;

    @FXML
    Button btnStart;

    private WatcherManager watcherManager = WatcherManager.getInstance();

    public void startWatcher() {
        Path historyFile = fpHistoryFile.getPath();
        Path saveDir = fpSaveDir.getPath();

        try {
            Watcher watcher = new Watcher(historyFile, saveDir);
            watcherManager.add(watcher);
        } catch (IOException e) {
            e.printStackTrace();
            errorAlert(e, "Error while starting watcher");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PathKeeper.getSavePath().ifPresent(p -> fpSaveDir.setPath(p.getParent()));
        colHistoryFile.setCellValueFactory(new PropertyValueFactory<>("historyFile"));
        colSaveDir.setCellValueFactory(new PropertyValueFactory<>("saveDir"));

        colActions.setCellFactory(column -> new WatcherActionCell());

        tvWatcherList.setItems(watcherManager.getWatcherList());
    }

    static class RemoveWatcherButton extends Button {

        private Watcher watcher;

        public RemoveWatcherButton(Watcher watcher) {
            super("Stop");
            this.watcher = watcher;
        }

        @Override
        public void fire() {
            super.fire();
            WatcherManager.getInstance().remove(watcher);
        }
    }

    static class ExportWatcherButton extends Button {

        private Watcher watcher;

        public ExportWatcherButton(Watcher watcher) {
            super("Export");
            this.watcher = watcher;
        }

        @Override
        public void fire() {
            super.fire();
            //show export dialog
            Main.showExportWindow(watcher.getWatch());
        }
    }

    static class WatcherActionCell extends TableCell<Watcher, Void> {

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                Watcher watcher = (Watcher) getTableRow().getItem();
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(new HBox(new ExportWatcherButton(watcher), new RemoveWatcherButton(watcher)));
            }
        }
    }
}
