package com.example.db;

import com.example.config.AppConfig;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseManager {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {

            String url = AppConfig.get("db.url");
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }
}