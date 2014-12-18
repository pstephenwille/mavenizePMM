package pomodoroblinktool.userinterface;

import javafx.scene.control.Label;

public class Labels {

    private static final String TXT_DURATION_BREAK = "I will break for ";
    private static final String TXT_DURATION_WORK = "I will work for ";
    private static final String TXT_INSTRUCTIONS_HEADER = "Instructions:";
    private static final String TXT_INSTRUCTIONS_BODY = "Press ENTER to start, ESC to exit.\nDuring the break period, ESC will restart the cycle.";

    private static final String TXT_MINUTES_TENS = " minutes";
    private static final String TXT_MINUTES_ONES = " minutes";
    private static final String TXT_PERCENTAGE = " %";
    private static final String TXT_SET_OPACITY = "Set opacity to ";

    public static final Label BREAK_TIMER;
    public static final Label DURATION_BREAK;
    public static final Label DURATION_WORK;
    public static final Label INSTRUCTIONS_HEADER;
    public static final Label INSTRUCTIONS_BODY;
    public static final Label MINUTES_TENS;
    public static final Label MINUTES_ONES;
    public static final Label PERCENT;
    public static final Label SET_OPACITY;

    static {

        BREAK_TIMER = new Label();
        DURATION_BREAK = new Label(TXT_DURATION_BREAK);
        DURATION_WORK = new Label(TXT_DURATION_WORK);

        INSTRUCTIONS_HEADER = new Label(TXT_INSTRUCTIONS_HEADER);
        INSTRUCTIONS_HEADER.setId(Styles.INSTRUCTIONS_HEADER);

        INSTRUCTIONS_BODY = new Label(TXT_INSTRUCTIONS_BODY);
        INSTRUCTIONS_BODY.setId(Styles.INSTRUCTIONS_BODY);


        MINUTES_TENS = new Label(TXT_MINUTES_TENS);
        MINUTES_ONES = new Label(TXT_MINUTES_ONES);
        PERCENT = new Label(TXT_PERCENTAGE);
        SET_OPACITY = new Label(TXT_SET_OPACITY);
    }

    public static void updateBreakTimer(String minutesText, String secondsText, String millisText) {
        BREAK_TIMER.setText(minutesText + ":" +
                secondsText + ":" +
                millisText.substring(0, 2));
    }
}
