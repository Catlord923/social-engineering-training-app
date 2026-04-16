package com.example;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:C:\\Users\\nakok\\IdeaProjects\\SocialEngineeringTrainingApp\\app.db";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM theory_pages");

        while (rs.next()) {
            System.out.println(rs.getString("title"));
        }
    }
}