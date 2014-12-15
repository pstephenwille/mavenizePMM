package pomodoroblinktool.userinterface;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pomodoroblinktool.App;
import pomodoroblinktool.Blink1ToolCommand;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SystemTrayIcon {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTrayIcon.class);

    private static final int MIN_VIEW_WIDTH = 400;
    private static final int MIN_VIEW_HEIGHT = 200;
    private static final double DEFAULT_OPACITY = 1.0;

    private static final String MENU_ITEM_TXT_PAUSE = "Pause";
    private static final String MENU_ITEM_TXT_RESUME = "Resume";
    private static final String MENU_ITEM_TXT_RESET = "Reset";
    private static final String MENU_ITEM_TXT_EXIT = "Exit";

    private SystemTray sysTray;
    private Label trayDigits;
    private Scene trayScene;
    private BufferedImage buffTrayIcon;
    private WritableImage wim;
    private TrayIcon trayIcon;

    public SystemTrayIcon(App app) {

        if (SystemTray.isSupported() && sysTray == null) {

            sysTray = SystemTray.getSystemTray();
            Double width = sysTray.getTrayIconSize().getWidth();
            Double height = sysTray.getTrayIconSize().getHeight();
            wim = new WritableImage(width.intValue(), height.intValue());
            /* fx thread: set up tray digits */
            StackPane trayPane = new StackPane();
            trayPane.setMinWidth(width);
            trayPane.setMinHeight(height);

            trayPane.setStyle(Styles.SYSTEM_TRAY_BACKGROUND_COLOR);
            trayPane.setOpacity(Styles.SYSTEM_TRAY_OPACITY);

            trayDigits = new Label();
            trayDigits.setStyle(Styles.SYSTEM_TRAY_FONT_FILL);
            trayDigits.setOpacity(Styles.SYSTEM_TRAY_FONT_OPACITY);

            trayPane.getChildren().addAll(trayDigits);
            trayScene = new Scene(trayPane, null);

            /* awt thread: make tray icon */
            buffTrayIcon = new BufferedImage(width.intValue(), height.intValue(), 2);
            SwingFXUtils.fromFXImage(trayScene.snapshot(wim), buffTrayIcon);

            /* tray popup events */
            ActionListener listener = (ActionEvent event) ->
            {

                String command = event.getActionCommand().toLowerCase();

                if (command.equals("pause")) {

                    Platform.runLater(() -> {

                        Blink1ToolCommand.changeColorToBreak();
                        app.pauseApp();

                    });/*fx thread */
                }
                if (command.equals("restart")) {

                    Blink1ToolCommand.changeColorToWorking();
                    Platform.runLater(App::restartApp);
                }
                if (command.equals("reset")) Platform.runLater(() -> {

                    Blink1ToolCommand.turnOffLight();
                    app.setMinWidth(MIN_VIEW_WIDTH);
                    app.setMinHeight(MIN_VIEW_HEIGHT);
                    app.setOpacity(DEFAULT_OPACITY);
                    app.requestFocus();

                });
                if (command.equals("exit")) {
                    sysTray.remove(trayIcon);/* awt thread */

                    Platform.runLater(app::shutDown);
                }
            };

            MenuItem pause = new MenuItem(MENU_ITEM_TXT_PAUSE);
            pause.addActionListener(listener);

            MenuItem restart = new MenuItem(MENU_ITEM_TXT_RESUME);
            restart.addActionListener(listener);

            MenuItem reset = new MenuItem(MENU_ITEM_TXT_RESET);
            reset.addActionListener(listener);

            MenuItem exit = new MenuItem(MENU_ITEM_TXT_EXIT);
            exit.addActionListener(listener);

            /* add tray menu options */
            PopupMenu popup = new PopupMenu();
            popup.add(pause);
            popup.addSeparator();
            popup.add(restart);
            popup.addSeparator();
            popup.add(reset);
            popup.addSeparator();
            popup.add(exit);

            trayIcon = new TrayIcon(buffTrayIcon, "Pomodoro Timer", popup);
            trayIcon.addActionListener(listener);

            try {

                sysTray.add(trayIcon);

            } catch (AWTException e) {

                LOG.error("Encountered an AWT exception", e);
                app.close();
            }
        }
    }

    public void updateDisplay(String minutes) {

        trayDigits.setText(minutes);

        /* rebuild image */
        SwingFXUtils.fromFXImage(trayScene.snapshot(wim), buffTrayIcon);

        /* awt update trayIcon */
        trayIcon.setImage(buffTrayIcon);
    }
}
