package com.example.db;

import com.example.config.AppConfig;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Provides access to the application's database connection.
 *
 * <p>This class uses lazy initialization to create the connection only when it
 * is first requested. The same connection instance is then reused while valid.</p>
 */
public class DatabaseManager {

    // Cached connection instance shared across the application
    private static Connection connection;

    /**
     * Returns an active database connection.
     *
     * <p>If no connection exists, or the previous connection has been closed,
     * a new connection is created using the JDBC URL defined in
     * {@code app.properties}.</p>
     *
     * @return active {@link Connection} instance
     * @throws SQLException if the connection cannot be created
     */
    public static Connection getConnection() throws SQLException {
        // Create a connection only when first needed or after closure
        if (connection == null || connection.isClosed()) {

            // Retrieves the JDBC URL from the config loader
            String url = AppConfig.get("db.url");
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }
}