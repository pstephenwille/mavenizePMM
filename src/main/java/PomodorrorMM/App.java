package PomodorrorMM;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class App extends Application {
    static Double opacity = 0.8;
    static List<Screen> allScreens;
    static List<BreakPeriodStage> timeoutStages = new ArrayList<>();
    static Long breakForMinutes = 10L;
    static Long workForMinutes = 25L;
    static Long timerText;
    static Long trayTimerCounter;
    static Timeline displayTimer;
    static Timeline breakPeriodTimeline;
    static Timeline workPeriodTimeLine;
    static Stage app;
    static Timeline trayTimer;
    Label minutes02Lbl = new Label();
    Label setOpacityTo = new Label();
    Label breakTimerLbl = new Label();
    Label willWorkFor = new Label();
    Label instructionTxt;
    Label percentLbl = new Label();
    Label breakfor = new Label();
    Label minutes01Lbl = new Label();
    TextField breakMinutesText = new TextField();
    TextField opacityText = new TextField();
    TextField workMinutesText = new TextField();
    TrayIcon trayIcon = null;
    Stage appContainer = new Stage();
    String minutesText;
    String secondsText;
    String millisText;
    String trayMinutesText;
    Long trayCycleMillis = 1000L;
    SystemTrayIcon tray;
    String pauseColor = "--rgb=100,100,00";
    String onBreakColor = "--rgb=00,100,00";
    String workingColor = "--rgb=100,00,00";
    String offColor = "--rgb=00,00,00";
    String blinkPath = "";


    public static void main(String[] args) { launch(args); }
    public void start(Stage app) throws Exception {
        App.app = app;

        setBlinkPath();

        makeFormFields();

        GridPane userInputs = new GridPane();

        /* position form fields */
        /* row 1*/
        userInputs.add(willWorkFor, 1, 0);
        userInputs.add(workMinutesText, 5, 0);
        userInputs.add(minutes01Lbl, 6, 0);

        /* row 2*/
        userInputs.add(breakfor, 1, 1);
        userInputs.add(breakMinutesText, 5, 1);
        userInputs.add(minutes02Lbl, 6, 1);

        /* row 3 */
        userInputs.add(setOpacityTo, 1, 2);
        userInputs.add(opacityText, 5, 2);
        userInputs.add(percentLbl, 6, 2);

        /* row 4, instruction text */
        userInputs.add(instructionTxt, 1, 4, 6, 1);


        Scene scene = new Scene(userInputs, 400, 200);
//        System.out.println(getClass().getResource("../main.css").toExternalForm() );
//C:/WWW/mavenizePMM/target/classes/main.css


        scene.getStylesheets().add("main.css");


        app.addEventHandler(KeyEvent.KEY_RELEASED, key ->
        {
            String code = key.getCode().toString().toLowerCase();

            if (code.equals("escape") || code.equals("esc")) {
                if (timeoutStages.size() == 0) {
                    app.close();
                } else {
                    app.setMaxWidth(0.0);
                    app.setMaxHeight(0.0);
                    app.setOpacity(0.0);
                }
            }


            if (code.equals("enter")) {
                /* user has submitted the form, set the values and,
                * start the app */

                /* stop to reset values */
                if (timeoutStages.size() > 0) pauseApp();

                /* if not already milliseconds */
                if (workForMinutes.toString().length() < 4) {
                    workForMinutes *= 60L * 1000L;
                }
                if (breakForMinutes.toString().length() < 4) {
                    breakForMinutes *= 60L * 1000L;
                }
                timerText = breakForMinutes;
                trayTimerCounter = workForMinutes;

                if (timeoutStages.size() > 0) {
                    /* reset countdown clock opacity */
                    timeoutStages.forEach(s -> {
                        s.getLayout()
                                .setStyle("-fx-background-color: rgba(0, 0, 0," + opacity + ")");
                    });
                }
                /* leave app container running, to give the stages something to run in. */
                app.setMaxWidth(0.0);
                app.setMaxHeight(0.0);
                app.setOpacity(0.0);

                makeSysTrayIcon();
                makeBreakScreens();
                makeTimers();
                hideBreakPeriodStages();
            }
        });


        app.setTitle("Pomodoro - multi monitor");
        app.setScene(scene);
        app.initStyle(StageStyle.UTILITY);
        app.show();
    }

    public void makeFormFields() {
        ChangeListener parseField = (observable, oldValue, newValue) -> {
            /* parse fieldID form event string */
            String fieldID = observable.toString().split(" ")[2].split("=")[1].replace(",", "");

            String _digits = newValue.toString().replaceAll("\\D+", "");

            /* limit to 3 digits */
            _digits = (_digits.length() > 3) ? _digits.substring(0, 3) : _digits;

            if (_digits.length() > 0) {
                if (fieldID.equals("workMinutes")) {
                    workForMinutes = Long.parseLong(_digits);
                }
                if (fieldID.equals("breakMinutes")) {
                    breakForMinutes = Long.parseLong(_digits);
                }
                if (fieldID.equals("opacity")) {
                    opacity = Double.parseDouble(_digits) / 100.00;
                }
            } else {
                _digits = "";
            }

            if (fieldID.equals("workMinutes")) {
                workMinutesText.setText(_digits);
            }
            if (fieldID.equals("breakMinutes")) {
                breakMinutesText.setText(_digits);
            }
            if (fieldID.equals("opacity")) {
                opacityText.setText(_digits);
            }
        };

        /* i will work for */
        willWorkFor.setText("I will work for ");

        /* work minutes, input */
        workMinutesText.setText("25");
        workMinutesText.setId("workMinutes");
        workMinutesText.textProperty().addListener(parseField);

        /* minutes */
        minutes01Lbl.setText(" minutes");

        /*i will break for */
        breakfor.setText("I will break for ");

        /* break minutes, input */
        breakMinutesText.setText("10");
        breakMinutesText.setId("breakMinutes");
        breakMinutesText.textProperty().addListener(parseField);

        /* minutes */
        minutes02Lbl.setText(" minutes");

        /* set opacity to */
        setOpacityTo.setText("Set opacity to ");

        /* opacity input */
        opacityText.setText("80");
        opacityText.setId("opacity");
        opacityText.textProperty().addListener(parseField);


        /* % */
        percentLbl.setText(" %");

        instructionTxt = new Label("Pres ESCAPE to exit, press ENTER to start.\nDuring the " +
                "break period, ESCAPE will restart the cycle.");
        instructionTxt.setId("instructionTxt");
    }


    public void makeSysTrayIcon() {
        if (tray == null) {
            tray = new SystemTrayIcon();
        }
    }

    public void makeBreakScreens() {
        if (timeoutStages.size() == 0) {

            allScreens = Screen.getScreens();
            allScreens.forEach(s -> timeoutStages.add(
                    new BreakPeriodStage(s, opacity, breakTimerLbl)));

            timeoutStages.forEach(s -> {
                s.getStage().getScene().addEventHandler(KeyEvent.KEY_RELEASED, escape -> {
                    String key = escape.getCode().toString().toLowerCase();
                    if (key.equals("escape") || key.equals("esc")) {
                        hideBreakPeriodStages();
                    }
                });
            });
        }
    }

    public void makeTimers() {
        /* show break stages */
        breakPeriodTimeline = new Timeline(new KeyFrame(Duration.millis(breakForMinutes),
                even -> hideBreakPeriodStages()));

        /* hide break stages */
        workPeriodTimeLine = new Timeline(new KeyFrame(Duration.millis(workForMinutes),
                event -> showBreakPeriodStages()));

        /* update count down clock */
        displayTimer = new Timeline(new KeyFrame(
                Duration.millis(250),
                event -> {
                    timerText -= 250L;
                    Long _minutes = TimeUnit.MILLISECONDS.toMinutes(timerText) % 60;
                    Long _seconds = TimeUnit.MILLISECONDS.toSeconds(timerText) % 60;
                    Long _millis = TimeUnit.MILLISECONDS.toMillis(timerText) % 1000;

                    minutesText = _minutes.toString();
                    secondsText = _seconds.toString();
                    millisText = _millis.toString();

                    if (minutesText.length() == 1) {
                        minutesText = "0" + minutesText;
                    }
                    if (secondsText.length() == 1) {
                        secondsText = "0" + secondsText;
                    }
                    if (millisText.length() == 1) {
                        millisText = "0" + millisText;
                    }

                    breakTimerLbl.setText(minutesText + ":" +
                            secondsText + ":" +
                            millisText.substring(0, 2));
                    if (timerText <= 0) {
                        timerText = workForMinutes;
                    }
                }));
        displayTimer.setCycleCount(Timeline.INDEFINITE);

        /* update tray clock */
        trayTimer = new Timeline(new KeyFrame(
                Duration.millis(trayCycleMillis),
                e -> {
                    trayTimerCounter -= trayCycleMillis;
                    Long _minutes = TimeUnit.MILLISECONDS.toMinutes(trayTimerCounter) % 60;

                    if (_minutes < 1) {
                        _minutes = TimeUnit.MILLISECONDS.toSeconds(trayTimerCounter) % 60;
                    }
                    trayMinutesText = _minutes.toString();

                    updateTrayDigits(trayMinutesText);
                }));
        trayTimer.setCycleCount(Timeline.INDEFINITE);
    }

    /* work period */
    public void hideBreakPeriodStages() {
        changeColor(workingColor);

        timeoutStages.forEach(s -> s.getStage().hide());
        displayTimer.pause();

        Integer _minutes = Integer.parseInt(workMinutesText.getText()) - 1;
        updateTrayDigits(_minutes.toString());
        trayTimerCounter = workForMinutes;
        trayTimer.playFromStart();
        workPeriodTimeLine.playFromStart();
    }

    /* break period */
    public void showBreakPeriodStages() {
        changeColor(onBreakColor);

        updateTrayDigits("00");
        trayTimer.pause();

        timeoutStages.forEach(s -> s.getStage().show());

        /* gets focus to accept 'escape' key presses */
        Platform.runLater(() -> timeoutStages.get(0).getStage().requestFocus());

        /* reset timer */
        timerText = breakForMinutes;
        displayTimer.playFromStart();

        breakPeriodTimeline.playFromStart();

        appContainer.toFront();
    }

    public void restartApp() {
        trayTimer.play();
        workPeriodTimeLine.play();
    }

    public void pauseApp() {
        workPeriodTimeLine.pause();
        trayTimer.pause();
    }

    public void updateTrayDigits(String minutes) {
        tray.trayDigits.setText(minutes);
        /* remake image */
        SwingFXUtils.fromFXImage(tray.trayScene.snapshot(tray.wim), tray.buffTrayIcon);

        /* awt update trayIcon */
        tray.trayIcon.setImage(tray.buffTrayIcon);
    }

    public void setBlinkPath() {
        String[] path = System.getenv("PATH").split(";");
        for (int i = 0; i < path.length; i++) {
            if (path[i].matches("(?i:.*blink1-tool.*)")) {
                blinkPath = path[i];
            }
        }
    }

    public void changeColor(String color) {
        Platform.runLater(() -> {
            try {
                String[] cmmd = {"cmd", "/c", "cd " + blinkPath + " && blink1-tool " + color};
                Process p = Runtime.getRuntime().exec(cmmd);
            } catch (IOException e) {
                System.out.println(e);
            }
        });
    }
}

