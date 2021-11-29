package org.victoria2.tools.vic2sgea.watcher;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Container class for a history file
 */
public class Watch {

    private SortedMap<String, WorldState> history = new TreeMap<>();

    public Watch() {
    }

    public Watch(SortedMap<String, WorldState> history) {
        this.history = history;
    }

    public void addState(String date, WorldState state) {
        history.put(date, state);
    }

    public SortedMap<String, WorldState> getHistory() {
        return history;
    }
}
