package com.example.config;

import java.util.Properties;
import java.io.InputStream;

/**
 * Central application configuration loader.
 *
 * <p>Loads key-value settings from {@code app.properties} located on the
 * application classpath and provides static access to configuration values.</p>
 */
public class AppConfig {

    private static final Properties properties = new Properties();

    static {
        // Load configuration once when the class is first used
        try (InputStream input = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("app.properties")) {

            if (input == null) {
                // Application cannot continue without required config
                throw new RuntimeException("app.properties not found");
            }

            properties.load(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a configuration value for the supplied key.
     *
     * @param key property name
     * @return property value, or {@code null} if the key does not exist
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }
}