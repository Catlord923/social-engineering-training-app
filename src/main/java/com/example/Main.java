package com.example;

import com.example.model.TheoryPage;
import com.example.dao.TheoryDAO;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        TheoryDAO dao = new TheoryDAO();

        for (TheoryPage page : dao.getAllPages()) {
            System.out.println(page.getPageOrder() + " - " + page.getTitle());
        }

        Runnable task = () -> {
            try {
                TheoryPage page = dao.getPageByOrder(3);
                System.out.println(page.getPageOrder() + " - " + page.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        task.run();
    }
}