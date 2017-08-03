package org.victoria2.tools.vic2sgea.watcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Singleton for managing Watchers
 */
public class WatcherManager {

    private static WatcherManager instance;
    private Watcher activeWatcher;

    public static WatcherManager getInstance() {
        if (instance == null)
            instance = new WatcherManager();
        return instance;
    }

    final private ObservableList<Watcher> watcherList = FXCollections.observableArrayList();

    private WatcherManager() {
    }

    public void startWatching(Path historyFile, Path saveDir) throws IOException {
        if(activeWatcher != null)
            activeWatcher.interrupt();
        activeWatcher = new Watcher(historyFile, saveDir);
        activeWatcher.start();
    }

    public void stopWatching() {
        if(activeWatcher != null)
            activeWatcher.interrupt();
        activeWatcher = null;
    }

    public Watcher getActiveWatcher() {
        return activeWatcher;
    }

}
