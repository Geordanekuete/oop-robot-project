package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Log window with state saving support (Task 2).
 */
public class LogWindow extends JInternalFrame implements LogChangeListener, WindowState {

    private final LogWindowSource logSource;
    private final TextArea logContent;

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);

        this.logSource = logSource;
        this.logSource.registerListener(this);

        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }


    //  TASK 2 : WindowState


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
