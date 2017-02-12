package org.victoria2.tools.vic2sgea.watcher;

import java.util.List;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Container class for a history file
 */
public class Watch {

    private List<WorldState> history;

    public Watch(List<WorldState> history) {
        this.history = history;
    }

    public void addState(WorldState state) {
        history.add(state);
    }
}
