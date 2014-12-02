package PomodorrorMM;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SystemTrayIcon extends App {

    static PopupMenu popup;
    static SystemTray sysTray;
    static ActionListener listener;
    static String command;
    Label trayDigits;
    Scene trayScene;
    BufferedImage buffTrayIcon;
    WritableImage wim;
    TrayIcon trayIcon = null;

    public SystemTrayIcon() {

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
            listener = e ->
            {
                command = e.getActionCommand().toLowerCase();
                if (command.equals("pause")) {
                    Platform.runLater(() -> {
                        changeColor(pauseColor);
                        pauseApp();
                    });/*fx thread */
                }
                if (command.equals("restart")) {
                    changeColor(workingColor);

                    Platform.runLater(() -> restartApp());
                }
                if (command.equals("reset")) {
                    Platform.runLater(() -> {
                        app.setMinWidth(400);
                        app.setMinHeight(200);
                        app.setOpacity(1.0);
                        app.requestFocus();
                    });
                }
                if (command.equals("exit")) {
                    sysTray.remove(trayIcon);/* awt thread */

                    Platform.runLater(() -> {/* fx thread */
                        timeoutStages.forEach(s -> s.getStage().close());
                        changeColor(offColor);

                        app.close();
                    });
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
