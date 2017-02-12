package org.victoria2.tools.vic2sgea.watcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Container class for a history file
 */
public class Watch {

    private Map<String, WorldState> history = new HashMap<>();

    public Watch() {
    }

    public Watch(Map<String, WorldState> history) {
        this.history = history;
    }

    public void addState(String date, WorldState state) {
        history.put(date, state);
    }
}
