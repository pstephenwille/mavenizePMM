package PomodorrorMM;

import PomodorrorMM.userinterface.Labels;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class App extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final int MAX_DIGITS_DISPLAYED = 3;
    private static final int DEFAULT_INSETS = 4;
    private static final int ROWSPAN_ONE = 1;
    private static final int COLSPAN_THREE = 3;
    private static final int COLSPAN_ONE = 1;
    private static final double TXT_INPUT_MAX_LENGTH = 55;
    private static final int VIEW_WIDTH = 540;
    private static final int VIEW_HEIGHT = 340;

    private TextField breakMinutesText = new TextField();
    private TextField workMinutesText = new TextField();
    private TextField opacityText = new TextField();
    private Stage appContainer = new Stage();

    private List<BreakPeriodView> timeoutStages = new ArrayList<>();

    private static Long minutesForBreak = 10L;
    private static Long minutesForWork = 25L;
    static Double opacity = 0.8;

    static List<Screen> allScreens;
    static Long timerText;
    static Long trayTimerCounter;
    static Timeline displayTimer;
    static Timeline breakPeriodTimeline;
    static Timeline workPeriodTimeLine;
    private static Stage app;
    static Timeline trayTimer;
    String minutesText;
    String secondsText;
    String millisText;
    String trayMinutesText;
    Long trayCycleMillis = 1000L;
    SystemTrayIcon tray;

    public static void main(String[] args) { launch(args); }

    public void start(Stage app) throws Exception {

        App.app = app;

        makeFormFields();

        GridPane gridPane = initializeGridPane();

        Scene scene = new Scene(gridPane, VIEW_WIDTH, VIEW_HEIGHT);
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

                /* stop to reset values */
                if (timeoutStages.size() > 0) {

                    pauseApp();
                }

                /* if not already milliseconds */
                if (minutesForWork.toString().length() < 4) {
                    minutesForWork *= 60L * 1000L;
                }

                if (minutesForBreak.toString().length() < 4) {
                    minutesForBreak *= 60L * 1000L;
                }
                timerText = minutesForBreak;
                trayTimerCounter = minutesForWork;

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

    // Layout GridPane (add inputs/labels and their lengths/widths)
    private GridPane initializeGridPane() {

        GridPane gridPane = new GridPane();

        gridPane.setVgap(DEFAULT_INSETS);
        gridPane.setHgap(DEFAULT_INSETS);
        gridPane.setPadding(
                new Insets(DEFAULT_INSETS, DEFAULT_INSETS, DEFAULT_INSETS, DEFAULT_INSETS)
        );

        /* position form fields */
        /* row 0 */
        gridPane.add(Labels.DURATION_WORK, 0, 1);
        workMinutesText.setMaxWidth(TXT_INPUT_MAX_LENGTH);
        gridPane.add(workMinutesText, 1, 1, COLSPAN_ONE, ROWSPAN_ONE);
        gridPane.add(Labels.MINUTES_TENS, 2, 1);

        /* row 1 */
        gridPane.add(Labels.DURATION_BREAK, 0, 2);
        breakMinutesText.setMaxWidth(TXT_INPUT_MAX_LENGTH);
        gridPane.add(breakMinutesText, 1, 2, COLSPAN_ONE, ROWSPAN_ONE);
        gridPane.add(Labels.MINUTES_ONES, 2, 2);

        /* row 2 */
        gridPane.add(Labels.SET_OPACITY, 0, 3);
        opacityText.setMaxWidth(TXT_INPUT_MAX_LENGTH);
        gridPane.add(opacityText, 1, 3, COLSPAN_ONE, ROWSPAN_ONE);
        gridPane.add(Labels.PERCENT, 2, 3);

        /* row 3 */
        gridPane.add(Labels.INSTRUCTIONS_HEADER, 0, 4, COLSPAN_THREE, ROWSPAN_ONE);
        gridPane.add(Labels.INSTRUCTIONS_BODY, 0, 5, COLSPAN_THREE, ROWSPAN_ONE);
        return gridPane;
    }

    public void makeFormFields() {

        ChangeListener parseField = (observable, oldValue, newValue) -> {

            /* parse fieldID form event string */
            String fieldID = observable.toString().split(" ")[2].split("=")[1].replace(",", "");

            String _digits = newValue.toString().replaceAll("\\D+", "");

            /* limit to 3 digits */
            _digits = (_digits.length() > MAX_DIGITS_DISPLAYED) ? _digits.substring(0, MAX_DIGITS_DISPLAYED) : _digits;

            if (_digits.length() > 0) {
                if (fieldID.equals("workMinutes")) {
                    minutesForWork = Long.parseLong(_digits);
                }
                if (fieldID.equals("breakMinutes")) {
                    minutesForBreak = Long.parseLong(_digits);
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

        /* work minutes, input */
        workMinutesText.setText("25");
        workMinutesText.setId("workMinutes");
        workMinutesText.textProperty().addListener(parseField);

        /* break minutes, input */
        breakMinutesText.setText("10");
        breakMinutesText.setId("breakMinutes");
        breakMinutesText.textProperty().addListener(parseField);

        /* opacity input */
        opacityText.setText("80");
        opacityText.setId("opacity");
        opacityText.textProperty().addListener(parseField);
    }


    public void makeSysTrayIcon() {
        if (tray == null) {
            tray = new SystemTrayIcon(this);
        }
    }

    public void makeBreakScreens() {
        if (timeoutStages.size() == 0) {

            allScreens = Screen.getScreens();
            allScreens.forEach(s -> timeoutStages.add(
                    new BreakPeriodView(s, opacity, Labels.BREAK_TIMER)));

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
        breakPeriodTimeline = new Timeline(new KeyFrame(Duration.millis(minutesForBreak),
                even -> hideBreakPeriodStages()));

        /* hide break stages */
        workPeriodTimeLine = new Timeline(new KeyFrame(Duration.millis(minutesForWork),
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

                    Labels.updateBreakTimer(minutesText, secondsText, millisText);

                    if (timerText <= 0) {
                        timerText = minutesForWork;
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

        Blink1ToolCommand.changeColorToWorking();

        timeoutStages.forEach(s -> s.getStage().hide());
        displayTimer.pause();

        Integer _minutes = Integer.parseInt(workMinutesText.getText()) - 1;
        updateTrayDigits(_minutes.toString());
        trayTimerCounter = minutesForWork;
        trayTimer.playFromStart();
        workPeriodTimeLine.playFromStart();
    }

    /* break period */
    public void showBreakPeriodStages() {

        Blink1ToolCommand.changeColorToBreak();

        updateTrayDigits("00");
        trayTimer.pause();

        timeoutStages.forEach(s -> s.getStage().show());

        /* gets focus to accept 'escape' key presses */
        Platform.runLater(() -> timeoutStages.get(0).getStage().requestFocus());

        /* reset timer */
        timerText = minutesForBreak;
        displayTimer.playFromStart();

        breakPeriodTimeline.playFromStart();
        appContainer.toFront();
    }

    public void setMinWidth(double width){
        app.setMinWidth(width);
    }

    public void setMinHeight(int height){
        app.setMinHeight(height);
    }

    public void setOpacity(double opacity){
        app.setOpacity(opacity);
    }

    public void requestFocus(){
        app.requestFocus();
    }

    public void close(){
        app.close();
    }

    public static void restartApp() {
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

    public void shutDown() {
        timeoutStages.forEach(s -> s.getStage().close());
        Blink1ToolCommand.turnOffLight();
        app.close();
        System.exit(0);
    }
}

