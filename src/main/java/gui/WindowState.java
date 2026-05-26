package gui;

import java.util.Map;

/**
 * Can save and Load states
 */
public interface WindowState {
    /**
     * To save window state
     *
     */
    Map<String, String> saveState();

    /**
     *To load window state
     *
     */
    void loadState(Map<String, String> state);
}
