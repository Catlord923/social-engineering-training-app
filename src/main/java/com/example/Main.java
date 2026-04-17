package com.example;

import com.example.db.DatabaseManager;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        System.out.println("Connected!");

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM theory_pages");

        while (rs.next()) {
            System.out.println(rs.getString("title"));
        }


    }
}