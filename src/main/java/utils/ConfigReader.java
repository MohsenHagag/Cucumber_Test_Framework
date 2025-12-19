package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("src/main/java/config/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("⚠ Failed to load config.properties file", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
