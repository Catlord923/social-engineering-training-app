package com.example.dao;

import com.example.db.DatabaseManager;
import com.example.model.TheoryPage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * Data Access Object (DAO) for the theory_pages table.
 * Handles the mapping between database rows and TheoryPage objects.
 */
public class TheoryDAO {

    /**
     * Fetches all theory pages from the database, sorted by their sequence order.
     * @return A list of TheoryPage objects; returns an empty list if no pages exist or an error occurs.
     */
    public List<TheoryPage> getAllPages() {
        String sql = "SELECT id, page_order, title, body FROM theory_pages ORDER BY page_order";
        List<TheoryPage> pages = new ArrayList<>();

        // try with resources ensures Connection, Statement, and ResultSet are closed automatically
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Mapping database columns to a new TheoryPage object
                TheoryPage page = new TheoryPage(
                        rs.getInt("id"),
                        rs.getInt("page_order"),
                        rs.getString("title"),
                        rs.getString("body")
                );
                pages.add(page);
            }

        } catch (SQLException e) {
            // Logs database-related failures
            e.printStackTrace();
        }

        return pages;
    }

    /**
     * Retrieves a single theory page based on its unique assigned display order.
     * This value determines the page's position in the learning sequence.
     *
     * @param pageOrder The integer value assigned to the page (e.g., 1 for the first topic).
     * @return The matching TheoryPage, or null if no page is assigned to this position.
     */
    public TheoryPage getPageByOrder(int pageOrder) {
        String sql = "SELECT id, page_order, title, body FROM theory_pages WHERE page_order = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Parameterized query to prevent SQL injection
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

        return null; // Explicitly returning null to indicate no result found
    }
}