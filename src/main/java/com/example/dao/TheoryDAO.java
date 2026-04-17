package com.example.dao;

import com.example.db.DatabaseManager;
import com.example.model.TheoryPage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class TheoryDAO {

    public List<TheoryPage> getAllPages() {
        String sql = "SELECT id, page_order, title, body FROM theory_pages ORDER BY page_order";
        List<TheoryPage> pages = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TheoryPage page = new TheoryPage(
                        rs.getInt("id"),
                        rs.getInt("page_order"),
                        rs.getString("title"),
                        rs.getString("body")
                );
                pages.add(page);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pages;
    }

    public TheoryPage getPageByOrder(int pageOrder) {
        String sql = "SELECT id, page_order, title, body FROM theory_pages WHERE page_order = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageOrder);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TheoryPage(
                            rs.getInt("id"),
                            rs.getInt("page_order"),
                            rs.getString("title"),
                            rs.getString("body")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}