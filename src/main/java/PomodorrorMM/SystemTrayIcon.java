package PomodorrorMM;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SystemTrayIcon {

    private static final int MIN_VIEW_WIDTH = 400;
    private static final int MIN_VIEW_HEIGHT = 200;
    private static final double DEFAULT_OPACITY = 1.0;

    static PopupMenu popup;
    static SystemTray sysTray;
    static ActionListener listener;
    static String command;

    Label trayDigits;
    Scene trayScene;
    BufferedImage buffTrayIcon;
    WritableImage wim;
    TrayIcon trayIcon = null;

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

            trayPane.setStyle("-fx-background-color: #000000;");
            trayPane.setOpacity(0.8);

            trayDigits = new Label();
            trayDigits.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 12px; ");
            trayDigits.setOpacity(1);

            trayPane.getChildren().addAll(trayDigits);
            trayScene = new Scene(trayPane, null);

            /* awt thread: make tray icon */
            buffTrayIcon = new BufferedImage(width.intValue(), height.intValue(), 2);
            SwingFXUtils.fromFXImage(trayScene.snapshot(wim), buffTrayIcon);

            /* tray popup events */
            listener = (ActionEvent e) ->
            {
                command = e.getActionCommand().toLowerCase();

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
                if (command.equals("reset")) {
                    Platform.runLater(() -> {
                        app.setMinWidth(MIN_VIEW_WIDTH);
                        app.setMinHeight(MIN_VIEW_HEIGHT);
                        app.setOpacity(DEFAULT_OPACITY);
                        app.requestFocus();
                    });
                }
                if (command.equals("exit")) {
                    sysTray.remove(trayIcon);/* awt thread */

                    Platform.runLater(app::shutDown);
                }
            };

            MenuItem pause = new MenuItem("Pause");
            pause.addActionListener(listener);

            MenuItem restart = new MenuItem("Restart");
            restart.addActionListener(listener);

            MenuItem reset = new MenuItem("Reset");
            reset.addActionListener(listener);

            MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(listener);

            /* add tray menu options */
            popup = new PopupMenu();
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
            } catch (AWTException except) {
                app.close();
            }
        }
    }
}
