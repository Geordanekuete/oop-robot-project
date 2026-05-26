package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application window.
 * Task 1 requirements:
 * - Split the menu creation into smaller methods.
 * - Add a menu item to exit the application.
 * - Centralize exit logic in one method with confirmation dialog.
 */
public class MainApplicationFrame extends JFrame implements WindowState {

    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {

        // Load saved state for main window
        StateManager sm = new StateManager("geordane");
        Map<String, String> global = sm.load();

        Map<String, String> mainState = new HashMap<>();
        for (var e : global.entrySet()) {
            if (e.getKey().startsWith("main.")) {
                mainState.put(e.getKey().substring(5), e.getValue());
            }
        }

        if (!mainState.isEmpty()) {
            loadState(mainState);
        } else {
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset,
                    screenSize.width - inset * 2,
                    screenSize.height - inset * 2);
        }

        setContentPane(desktopPane);

        // Internal windows
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        // Menu
        setJMenuBar(createMenuBar());

        // Exit handling
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    /**
     * Creates and configures the log window.
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Adds an internal window to the desktop pane.
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Builds the main menu bar by delegating to smaller methods.
     */
    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(createLookAndFeelMenu());
        bar.add(createTestMenu());
        bar.add(createExitMenu());
        return bar;
    }

    /**
     * Creates the "Look and Feel" menu.
     */
    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");

        JMenuItem system = new JMenuItem("Системная схема", KeyEvent.VK_S);
        system.addActionListener(e -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        JMenuItem cross = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        cross.addActionListener(e -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });

        menu.add(system);
        menu.add(cross);
        return menu;
    }

    /**
     * Creates the "Tests" menu.
     */
    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");

        JMenuItem addLog = new JMenuItem("Сообщение в лог");
        addLog.addActionListener(e -> Logger.debug("Новая строка"));

        menu.add(addLog);
        return menu;
    }

    /**
     * Creates the "File" menu with an exit option.
     */
    private JMenu createExitMenu() {
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> onExit());

        fileMenu.add(exitItem);
        return fileMenu;
    }

    /**
     * Applies the selected Look and Feel.
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }

    /**
     * Centralized exit logic with confirmation dialog.
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
     * Saves only the main window state (Task 2 not completed here).
     */
    private void saveAllStates() {
        StateManager sm = new StateManager("geordane");
        Map<String, String> global = new HashMap<>();

        Map<String, String> mainState = saveState();
        mainState.forEach((k, v) -> global.put("main." + k, v));

        sm.save(global);
    }

    @Override
    public Map<String, String> saveState() {
        Map<String, String> map = new HashMap<>();
        map.put("x", String.valueOf(getX()));
        map.put("y", String.valueOf(getY()));
        map.put("width", String.valueOf(getWidth()));
        map.put("height", String.valueOf(getHeight()));
        map.put("state", String.valueOf(getExtendedState()));
        return map;
    }

    @Override
    public void loadState(Map<String, String> state) {
        try {
            int x = Integer.parseInt(state.get("x"));
            int y = Integer.parseInt(state.get("y"));
            int w = Integer.parseInt(state.get("width"));
            int h = Integer.parseInt(state.get("height"));
            int st = Integer.parseInt(state.get("state"));

            setBounds(x, y, w, h);
            setExtendedState(st);
        } catch (Exception ignored) {}
    }
}
