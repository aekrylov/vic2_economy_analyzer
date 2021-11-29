package org.victoria2.tools.vic2sgea.watcher;

import org.victoria2.tools.vic2sgea.main.Report;

import java.nio.file.Path;
import java.util.List;

public class WatchUtil {

    public static void addState(Watch watch, Path savePath) {
        Report report = new Report(savePath.toString(), null, null);
        WorldState state = new WorldState(report);
        watch.addState(report.getCurrentDate(), state);
    }

    public static Watch fromExisting(List<Path> saveFiles) {
        Watch watch = new Watch();
        for (Path saveFile : saveFiles) {
            addState(watch, saveFile);
        }
        return watch;
    }
}
