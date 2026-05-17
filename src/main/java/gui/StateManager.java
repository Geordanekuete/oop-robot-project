package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StateManager {

    private final File file;

    public StateManager(String name) {
        String home = System.getProperty("user.home");
        File dir = new File(home, name);
        if (!dir.exists()) dir.mkdirs();
        this.file = new File(dir, "state.cfg");
    }

    public void save(Map<String, String> data) {
        try (FileWriter fw = new FileWriter(file)) {
            for (var entry : data.entrySet()) {
                fw.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException ignored) {}
    }

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
