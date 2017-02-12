package org.victoria2.tools.vic2sgea.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

    private Map<Path, Watcher> watcherMap = new HashMap<>();

    private WatcherManager() {
    }

    public void add(Path historyFile, Path saveDir) throws IOException {
        Watcher watcher = new Watcher(historyFile, saveDir);
        watcherMap.put(historyFile, watcher);
        watcher.start();
    }

    public Watcher remove(Path historyFile) {
        Watcher watcher = watcherMap.remove(historyFile);
        if (watcher != null) {
            watcher.interrupt();
        }

        return watcher;
    }

}
