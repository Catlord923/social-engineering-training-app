package com.example.db;

import com.example.config.AppConfig;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Manages the lifecycle of the database connection.
 * Uses a Lazy Singleton pattern to ensure only one connection is active at a time.
 */
public class DatabaseManager {

    // Cached connection instance shared across the application
    private static Connection connection;

    /**
     * Retrieves the active database connection, creating it if it doesn't exist.
     * @return An active Connection object.
     * @throws SQLException If the connection fails or the URL is invalid.
     */
    public static Connection getConnection() throws SQLException {
        // Lazy initialization: only connect when first requested or
        // if the previous connection died
        if (connection == null || connection.isClosed()) {

            // Retrieves the JDBC URL (e.g., jdbc:sqlite:app.db) from the config loader
            String url = AppConfig.get("db.url");
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }
}