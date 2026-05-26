package gui;

import java.util.Map;
/**
* implementing the window state class to save states
 *
* */

public interface WindowState {
    Map<String, String> saveState();
    void loadState(Map<String, String> state);
}
