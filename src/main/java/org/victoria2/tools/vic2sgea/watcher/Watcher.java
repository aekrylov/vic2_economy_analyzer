package org.victoria2.tools.vic2sgea.watcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        this.historyFile = historyFile;
        this.saveDir = saveDir;

        if (!Files.exists(historyFile)) {
            Files.createFile(historyFile);
        } else {
            this.watch = read(historyFile);
        }

        if (watch == null) {
            watch = new Watch();
        }

        saveDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public Watch getWatch() {
        return watch;
    }

    public void addState(Path savePath) {
        WatchUtil.addState(watch, savePath);
        //write watch
        try {
            write(watch, historyFile);
        } catch (IOException e) {
            throw new RuntimeException(e); //todo proper exception handling
        }
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
                    addState(lastSave);
                }

                key.reset();
            } catch (InterruptedException e) {
                //interrupted exception can be thrown while thread is waiting
                e.printStackTrace();
                break;
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
