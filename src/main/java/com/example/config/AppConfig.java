package com.example.config;

import java.util.Properties;
import java.io.InputStream;

/**
 * Global configuration manager.
 * Loads settings from app.properties on class initialization.
 */
public class AppConfig {

    private static final Properties properties = new Properties();

    static {
        // Loads resource from the classpath (src/main/resources in Maven)
        try (InputStream input = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("app.properties")) {

            if (input == null) {
                // Fail-fast if config is missing; the app shouldn't start without settings
                throw new RuntimeException("app.properties not found");
            }

            properties.load(input);

        } catch (Exception e) {
            // Log stack trace for debugging
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a configuration value by its key.
     * @return Value as String, or null if the key doesn't exist.
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }
}