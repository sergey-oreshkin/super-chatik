package home.serg.chatik.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHolder {

    private static final String PROPERTIES_FILE_NAME = "app.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesHolder() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (InputStream resource = PropertiesHolder.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            PROPERTIES.load(resource);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
