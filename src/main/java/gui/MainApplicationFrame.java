package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.HashMap;



/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame implements WindowState
{

    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        // Load saved state
        StateManager sm = new StateManager("geordane");
        Map<String, String> global = sm.load();

// Extract only main window state
        Map<String, String> mainState = new HashMap<>();
        for (var e : global.entrySet()) {
            if (e.getKey().startsWith("main.")) {
                mainState.put(e.getKey().substring(5), e.getValue());
            }
        }

// Apply state if available
        if (!mainState.isEmpty()) {
            loadState(mainState);
        } else {
            // Default position if no saved state
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset,
                    screenSize.width - inset * 2,
                    screenSize.height - inset * 2);
        }

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

    }


    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(createExitMenu());
        return menuBar;
    }
    private JMenu createExitMenu() {
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> onExit());
        fileMenu.add(exitItem);
        return fileMenu;
    }


    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
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
            dispose(); // close it
        } else {
            // nothing if "Нет"
        }

    }
    private void saveAllStates() {
        StateManager sm = new StateManager("geordane"); // ton dossier perso
        Map<String, String> global = new HashMap<>();

        // Save main window state
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
