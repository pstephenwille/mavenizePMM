package PomodorrorMM;

import javafx.application.Platform;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class Blink1ToolCommand {

    private static final Logger LOG = LoggerFactory.getLogger(Blink1ToolCommand.class);
    private static final String OPERATING_SYSTEM = System.getProperty("os.name");
    private static final String BACKGROUND_COLOR_BREAK = "--rgb=100,100,00";
    private static final String BACKGROUND_COLOR_WORKING = "--rgb=100,00,00";
    private static final String BACKGROUND_COLOR_OFF = "--rgb=00,00,00";
    private static final String OPERATING_SYSTEM_OSX = "Mac OS X";

    private static String blinkPath = "";


    static {

        setBlinkPathFromSystemPath();
        LOG.info("blink1 tool path set to {}", blinkPath);
    }

    public static void turnOffLight(){
        changeColor(BACKGROUND_COLOR_OFF);
    }
    public static void changeColorToWorking(){
        changeColor(BACKGROUND_COLOR_WORKING);
    }
    public static void changeColorToBreak(){
        changeColor(BACKGROUND_COLOR_BREAK);
    }

    private static boolean isBlinkDeviceConnected() {

        LOG.debug("OS : {}", OPERATING_SYSTEM);

        String[] command;

        if(isRunningOnMac()){
            command = getDeviceListCommandForOsx();
        }
        else{
            command = getDeviceListCommandForWin();
        }


        boolean deviceIsConnected = false;

        // TODO: Look into using the streaming api to pull this off
        try {
            Process process = Runtime.getRuntime().exec(command);
            InputStream stream = process.getInputStream();
            String responseFromTool = IOUtils.toString(stream, "UTF-8");
            stream.close();

            if (!responseFromTool.contains("no blink(1) devices found")){
                LOG.info("A connected blink device was found.");
                deviceIsConnected = true;
            }
            else{
                LOG.info("No connected blink1-tool devices found");
            }

        } catch (IOException e) {
            LOG.error("Encountered a problem listing out connected blink1-tool devices", e);
        }


        return deviceIsConnected;
    }

    private static void changeColor(String color) {

        if (isBlinkDeviceConnected()) {

            Platform.runLater(() -> {

                try {

                    LOG.info("blink device found. Executing instruction");
                    String[] command;

                    if (isRunningOnMac()) {
                        command = new String[]{"blink1-tool " + color};
                    } else {
                        command = new String[]{"cmd", "/c", "cd " + blinkPath + " && blink1-tool " + color};
                    }

                    LOG.info("changing color of blink-tool to {}", color);
                    Runtime.getRuntime().exec(command);


                } catch (IOException e) {
                    LOG.error("Problem accessing blink-tool. Exception: {}", e.getMessage());
                }
            });
        }
    }

    private static boolean isRunningOnMac() {
        return OPERATING_SYSTEM.equals(OPERATING_SYSTEM_OSX);
    }

    // TODO: We could simplify this by requiring the user set BLINK1_TOOL_HOME on the PATH
    private static void setBlinkPathFromSystemPath() {

        String splitChar = isRunningOnMac() ? ":" : ";";

        String[] path = System.getenv("PATH").split(splitChar);
        for (String aPath : path) {
            if (aPath.matches("(?i:.*blink1-tool.*)")) {
                blinkPath = aPath;
            }
        }
    }

    private static String[] getDeviceListCommandForOsx() {
        return new String[]{blinkPath,"--list"};
    }

    private static String[] getDeviceListCommandForWin() {
        return new String[]{"cmd", "/c", "cd " + blinkPath + " && blink1-tool --list"};
    }
}
