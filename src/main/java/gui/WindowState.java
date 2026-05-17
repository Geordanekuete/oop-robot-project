package gui;

import java.util.Map;

public interface WindowState {
    Map<String, String> saveState();
    void loadState(Map<String, String> state);
}
