package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application window.
 */
public class MainApplicationFrame extends JFrame implements WindowState {

    private final JDesktopPane desktopPane = new JDesktopPane();

    private LogWindow logWindow;
    private GameWindow gameWindow;
    /**
     * Window state manager
     */
    private StateManager sm = new StateManager();
    public MainApplicationFrame() {


        Map<String, String> global = sm.load();

        loadState(extract(global, "main."));

        setContentPane(desktopPane);

        logWindow = new LogWindow(Logger.getDefaultLogSource());
        gameWindow = new GameWindow();

        addWindow(logWindow);
        addWindow(gameWindow);

        logWindow.loadState(extract(global, "log."));
        gameWindow.loadState(extract(global, "game."));

        setJMenuBar(createMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    /**
     * Extract
     *
     */
    private Map<String, String> extract(Map<String, String> global, String prefix) {
        Map<String, String> out = new HashMap<>();
        global.forEach((k, v) -> {
            if (k.startsWith(prefix)) {
                out.put(k.substring(prefix.length()), v);
            }
        });
        return out;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * It Creates a menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(createLookAndFeelMenu());
        bar.add(createTestMenu());
        bar.add(createExitMenu());
        return bar;
    }

    /**
     * Creates the menu and his items
     * implements the default look and feel
     */
    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");

        JMenuItem system = new JMenuItem("Системная схема");
        system.addActionListener(e -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));

        JMenuItem cross = new JMenuItem("Универсальная схема");
        cross.addActionListener(e -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));

        menu.add(system);
        menu.add(cross);
        return menu;
    }

    /**
     * creates the test menu items.
     *
     */

    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");

        JMenuItem addLog = new JMenuItem("Сообщение в лог");
        addLog.addActionListener(e -> Logger.debug("Новая строка"));

        menu.add(addLog);
        return menu;
    }

    /**
     * Creates the menu with exit button
     */
    private JMenu createExitMenu() {
        JMenu menu = new JMenu("Файл");

        JMenuItem exit = new JMenuItem("Выход");
        exit.addActionListener(e -> onExit());

        menu.add(exit);
        return menu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }

    /**
     * Show a pop-up with exit options
     * Ask for confirmation to exit
     */
    private void onExit() {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            saveAllStates();
            dispose();
            System.exit(0);
        }
    }

    /**
     * Saves all windows states
     */
    private void saveAllStates() {

        Map<String, String> global = new HashMap<>();

        saveState().forEach((k, v) -> global.put("main." + k, v));
        logWindow.saveState().forEach((k, v) -> global.put("log." + k, v));
        gameWindow.saveState().forEach((k, v) -> global.put("game." + k, v));

        sm.save(global);
    }

    @Override
    public Map<String, String> saveState() {
        Map<String, String> m = new HashMap<>();
        m.put("x", "" + getX());
        m.put("y", "" + getY());
        m.put("width", "" + getWidth());
        m.put("height", "" + getHeight());
        m.put("state", "" + getExtendedState());
        return m;
    }

    @Override
    public void loadState(Map<String, String> s) {
        try {
            setBounds(
                    Integer.parseInt(s.get("x")),
                    Integer.parseInt(s.get("y")),
                    Integer.parseInt(s.get("width")),
                    Integer.parseInt(s.get("height"))
            );

            if (s.containsKey("state")) {
                setExtendedState(Integer.parseInt(s.get("state")));
            }

        } catch (Exception ignored) {}
    }
}
