package PomodorrorMM.userinterface;

import de.saxsys.javafx.test.JfxRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JfxRunner.class)
public class LabelsTest {

    @Before
    public void setUp(){

    }

    @Test
    public void breakTimerText(){

        assertThat(Labels.BREAK_TIMER.getText()).isEqualTo("");
    }

    @Test
    public void durationBreakText(){

        assertThat(Labels.DURATION_BREAK.getText()).isEqualTo("I will break for ");
    }

    @Test
    public void durationWorkText(){

        assertThat(Labels.DURATION_WORK.getText()).isEqualTo("I will work for ");
    }

    @Test
    public void instructionsText(){

        assertThat(Labels.INSTRUCTIONS.getText()).isEqualTo("Press ESCAPE to exit, press ENTER to start.\n" +
                "During the break period, ESCAPE will restart the cycle.");
    }

    @Test
    public void minutesTensText(){

        assertThat(Labels.MINUTES_TENS.getText()).isEqualTo(" minutes");
    }

    @Test
    public void minutesOnesText(){

        assertThat(Labels.MINUTES_ONES.getText()).isEqualTo(" minutes");
    }

    @Test
    public void percentText(){

        assertThat(Labels.PERCENT.getText()).isEqualTo(" %");
    }

    @Test
    public void setOpacityText(){

        assertThat(Labels.SET_OPACITY.getText()).isEqualTo("Set opacity to ");
    }
}