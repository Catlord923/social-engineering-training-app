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
 * Data Access Object for retrieving theory content from the
 * {@code theory_pages} database table.
 *
 * <p>This class is responsible for converting database rows into
 * {@link TheoryPage} model objects used by the application.</p>
 */
public class TheoryDAO {

    /**
     * Retrieves all theory pages from the database in display order.
     *
     * <p>The returned list is ordered using the {@code page_order} column so
     * pages appear in the intended learning sequence.</p>
     *
     * @return a list of {@link TheoryPage} objects, or an empty list if no
     * pages exist or an error occurs
     */
    public List<TheoryPage> getAllPages() {
        String sql = "SELECT id, page_order, title, body FROM theory_pages ORDER BY page_order";
        List<TheoryPage> pages = new ArrayList<>();

        /*
         * Try with resources automatically closes the Connection,
         * PreparedStatement, and ResultSet.
         */
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
            // Prints database errors for debugging purposes.
            e.printStackTrace();
        }

        return pages;
    }
}