package game.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadPropertyFile {
    private static final String PROPERTY_FILE_PATH_INTERNAL = "/config.properties";
    private static final String PROPERTY_FILE_PATH_EXTERNAL = "config.properties";
    private static Properties prop = new Properties();

    public ReadPropertyFile() {

        try {
            prop.load(new FileInputStream(PROPERTY_FILE_PATH_EXTERNAL));
        } catch (NullPointerException | IOException npe) {
            try {
                prop.load(getClass().getResourceAsStream(PROPERTY_FILE_PATH_INTERNAL));
            } catch (IOException ignored) {
            }
        }
    }

    private static void storeOutputStream() {
        try {
            prop.store(new FileOutputStream(PROPERTY_FILE_PATH_EXTERNAL), null);
        } catch (IOException ignored) {
        }
    }

    public static double getDoubleValue(String entry) {
        return Double.parseDouble(prop.getProperty(entry));
    }

    public static int getIntValue(String entry) {
        return Integer.parseInt(prop.getProperty(entry));
    }

    public static boolean getBoolValue(String entry) {
        return Boolean.parseBoolean(prop.getProperty(entry));
    }

    public static String getStringValue(String entry) {
        return prop.getProperty(entry);
    }

    public static void setIntValue(String entry, int value) {
        prop.setProperty(entry, String.valueOf(value));
        storeOutputStream();
    }

    public static void setBoolValue(String entry, boolean bool) {
        if (bool) {
            prop.setProperty(entry, "true");
        } else {
            prop.setProperty(entry, "false");
        }
        storeOutputStream();
    }

    public static void setDoubleValue(String entry, double value) {
        prop.setProperty(entry, String.valueOf(value));
        storeOutputStream();
    }

    public static void setStringValue(String entry, String value) {
        prop.setProperty(entry, value);
        storeOutputStream();
    }
}
