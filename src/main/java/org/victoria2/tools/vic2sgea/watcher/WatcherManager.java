package org.victoria2.tools.vic2sgea.watcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Singleton for managing Watchers
 */
public class WatcherManager {

    private static WatcherManager instance;

    public static WatcherManager getInstance() {
        if (instance == null)
            instance = new WatcherManager();
        return instance;
    }

    final private ObservableList<Watcher> watcherList = FXCollections.observableArrayList();

    private WatcherManager() {
    }

    public void add(Watcher watcher) {
        watcherList.add(watcher);
        watcher.start();
    }

    public void remove(Watcher watcher) {
        watcherList.remove(watcher);
        if (watcher != null)
            watcher.interrupt();
    }

    public ObservableList<Watcher> getWatcherList() {
        return watcherList;
    }
}
