package PomodorrorMM.userinterface;

import javafx.scene.control.Label;

public class Labels {

    private static final String TXT_DURATION_BREAK = "I will break for ";
    private static final String TXT_DURATION_WORK = "I will work for ";
    private static final String TXT_INSTRUCTIONS = "Press ESCAPE to exit, press ENTER to start." +
            "\nDuring the break period, ESCAPE will restart the cycle.";
    private static final String ID_INSTRUCTIONS = "INSTRUCTIONS";
    private static final String TXT_MINUTES_TENS = " minutes";
    private static final String TXT_MINUTES_ONES = " minutes";
    private static final String TXT_PERCENTAGE = " %";
    private static final String TXT_SET_OPACITY = "Set opacity to ";

    public static final Label BREAK_TIMER = new Label();;
    public static final Label DURATION_BREAK = new Label(TXT_DURATION_BREAK);;
    public static final Label DURATION_WORK = new Label(TXT_DURATION_WORK);;
    public static final Label INSTRUCTIONS = new Label(TXT_INSTRUCTIONS);;
    public static final Label MINUTES_TENS = new Label(TXT_MINUTES_TENS);;
    public static final Label MINUTES_ONES = new Label(TXT_MINUTES_ONES);;
    public static final Label PERCENT = new Label(TXT_PERCENTAGE);;
    public static final Label SET_OPACITY = new Label(TXT_SET_OPACITY);;

    public static void updateBreakTimer(String minutesText, String secondsText, String millisText) {
        BREAK_TIMER.setText(minutesText + ":" +
                secondsText + ":" +
                millisText.substring(0, 2));
    }
}
