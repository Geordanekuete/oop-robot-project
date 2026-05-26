package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Game window with state saving support (Task 2).
 */
public class GameWindow extends JInternalFrame implements WindowState {

    private final GameVisualizer gameVisualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);

        gameVisualizer = new GameVisualizer();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
    }

    @Override
    public Map<String, String> saveState() {
        Map<String, String> m = new HashMap<>();
        m.put("x", "" + getX());
        m.put("y", "" + getY());
        m.put("width", "" + getWidth());
        m.put("height", "" + getHeight());
        return m;
    }

    @Override
    public void loadState(Map<String, String> s) {
        try {
            int x = Integer.parseInt(s.get("x"));
            int y = Integer.parseInt(s.get("y"));
            int w = Integer.parseInt(s.get("width"));
            int h = Integer.parseInt(s.get("height"));

            setBounds(x, y, w, h);

        } catch (Exception ignored) {}
    }
}
