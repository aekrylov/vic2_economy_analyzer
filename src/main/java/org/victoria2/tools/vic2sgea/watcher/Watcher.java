package org.victoria2.tools.vic2sgea.watcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.victoria2.tools.vic2sgea.main.Report;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Watches some directory for changes in a separate thread.
 * When a file is created or modified, adds a new {@link WorldState} instance to {@link Watch} object
 * and saves it
 */
public class Watcher extends Thread {

    private WatchService watchService = FileSystems.getDefault().newWatchService();
    private Path historyFile;
    private Path saveDir;
    private Watch watch;

    public Watcher(Path historyFile, Path saveDir) throws IOException {
        if (!Files.exists(historyFile))
            Files.createFile(historyFile);

        this.watch = read(historyFile);
        if (watch == null) {
            watch = new Watch();
        }
        this.historyFile = historyFile;
        this.saveDir = saveDir;

        saveDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public Watch getWatch() {
        return watch;
    }

    @Override
    public void run() {
        System.out.println("Started watcher for file " + historyFile);
        while (true) {
            //todo fix duplicate loading
            try {
                WatchKey key = watchService.take();
                List<WatchEvent<?>> events = key.pollEvents();

                events.forEach(event -> {
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path lastSave = saveDir.resolve(pathEvent.context());
                    try {
                        System.out.println(event.kind() + " " + Files.size(lastSave));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                //assume that events are rare, so that one series of events corresponds to one file
                //in that case, we only need the last event
                WatchEvent<Path> event = (WatchEvent<Path>) events.get(events.size() - 1);
                Path lastSave = saveDir.resolve(event.context());
                if (Files.size(lastSave) > 0) {
                    Report report = new Report(lastSave.toString(), null, null);
                    WorldState state = new WorldState(report);
                    watch.addState(report.getCurrentDate(), state);
                    //write watch
                    write(watch, historyFile);
                }

                key.reset();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private static Watch read(Path historyPath) throws IOException {
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(new NonSerializableExclusionStrategy())
                .create();

        return gson.fromJson(Files.newBufferedReader(historyPath), Watch.class);
    }

    private static void write(Watch watch, Path historyPath) throws IOException {
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new NonSerializableExclusionStrategy())
                .create();

        String json = gson.toJson(watch, Watch.class);
        Files.write(historyPath, json.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Path getHistoryFile() {
        return historyFile;
    }

    public Path getSaveDir() {
        return saveDir;
    }
}
