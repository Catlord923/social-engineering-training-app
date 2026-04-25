package com.example.dao;

import com.example.db.DatabaseManager;
import com.example.model.QuizQuestion;
import com.example.model.QuizOption;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * Data access object for quiz questions and answer options.
 *
 * <p>Retrieves quiz content from the database and maps result rows into
 * immutable model objects used by the quiz module.</p>
 */
public class QuizDAO {

    /**
     * Retrieves all quiz questions in display order.
     *
     * @return ordered list of {@link QuizQuestion} objects
     */
    public List<QuizQuestion> getAllQuestions() {
        String sql = """
                SELECT id, question_order, question_text, explanation
                FROM quiz_questions
                ORDER BY question_order
                """;

        List<QuizQuestion> questions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                questions.add(mapQuestion(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    /**
     * Retrieves all answer options for a question.
     *
     * <p>Options are loaded in {@code option_order} sequence from the database.
     * Presentation order may later be adjusted by the calling layer.</p>
     *
     * @param questionId question identifier
     * @return list of {@link QuizOption} objects
     */
    public List<QuizOption> getOptionsForQuestion(int questionId) {
        String sql = """
                SELECT id, question_id, option_order, option_text, is_correct
                FROM quiz_options
                WHERE question_id = ?
                ORDER BY option_order
                """;

        List<QuizOption> options = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    options.add(mapOption(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return options;
    }

    /**
     * Maps the current result row to a quiz question model.
     *
     * @param rs active result set positioned on a row
     * @return mapped {@link QuizQuestion}
     * @throws SQLException if column access fails
     */
    private QuizQuestion mapQuestion(ResultSet rs) throws SQLException {
        return new QuizQuestion(
                rs.getInt("id"),
                rs.getInt("question_order"),
                rs.getString("question_text"),
                rs.getString("explanation")
        );
    }

    /**
     * Maps the current result row to a quiz option model.
     *
     * <p>The integer database field {@code is_correct} is converted to a
     * boolean value where {@code 1 = true} and {@code 0 = false}.</p>
     *
     * @param rs active result set positioned on a row
     * @return mapped {@link QuizOption}
     * @throws SQLException if column access fails
     */
    private QuizOption mapOption(ResultSet rs) throws SQLException {
        return new QuizOption(
                rs.getInt("id"),
                rs.getInt("question_id"),
                rs.getInt("option_order"),
                rs.getString("option_text"),
                rs.getInt("is_correct") == 1
        );
    }
}