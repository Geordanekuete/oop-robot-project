package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
/**
 *Manager for saving and loading windows states
 * */
public class StateManager {

    private final File file;

    /**
     * Create  a folder and add a config file
     */
    public StateManager() {
        String name="geordane";
        String home = System.getProperty("user.home");
        File dir = new File(home, name);
        if (!dir.exists()) dir.mkdirs();
        this.file = new File(dir, "state.cfg");
    }

    /**
     * Save values in the configuration file
     *
     */
    public void save(Map<String, String> data) {
        try (FileWriter fw = new FileWriter(file)) {
            for (var entry : data.entrySet()) {
                fw.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException ignored) {}
    }

    /**
     * Load values from the configuration file
     *
     */
    public Map<String, String> load() {
        Map<String, String> map = new HashMap<>();
        if (!file.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("=");
                if (p.length == 2) map.put(p[0], p[1]);
            }
        } catch (IOException ignored) {}

        return map;
    }
}
