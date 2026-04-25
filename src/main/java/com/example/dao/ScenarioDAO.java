package com.example.dao;

import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;
import com.example.model.ScenarioStage;
import com.example.db.DatabaseManager;
import com.example.model.Scenario;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * Data Access Object for retrieving scenario-related data.
 *
 * <p>This class loads scenarios, stages, interface elements, and selectable
 * options from the database and maps them to model objects used by the
 * scenario module.</p>
 */
public class ScenarioDAO {

    /**
     * Returns the total number of scenarios stored in the database.
     *
     * <p>Used to generate a dynamic progress indicator rather than relying
     * on a hardcoded scenario count.</p>
     *
     * @return total number of scenarios, or {@code 0} if unavailable
     */
    public int getScenarioCount() {
        String sql = "SELECT COUNT(*) FROM scenarios";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves a scenario using its display order.
     *
     * @param scenarioOrder scenario sequence number
     * @return matching {@link Scenario}, or {@code null} if not found
     */
    public Scenario getScenarioByOrder(int scenarioOrder) {
        String sql = """
                SELECT id, scenario_order, type, title, intro_text, learning_outcomes
                FROM scenarios
                WHERE scenario_order = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scenarioOrder);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapScenario(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves a specific stage belonging to a scenario.
     *
     * @param scenarioId parent scenario id
     * @param stageOrder stage sequence number within the scenario
     * @return matching {@link ScenarioStage}, or {@code null} if not found
     */
    public ScenarioStage getStageByOrder(int scenarioId, int stageOrder) {
        String sql = """
                SELECT id, scenario_id, stage_order, stage_title, prompt,
                       is_terminal, is_success, feedback_title, feedback_text
                FROM scenario_stages
                WHERE scenario_id = ? AND stage_order = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scenarioId);
            stmt.setInt(2, stageOrder);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapStage(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all content elements associated with a stage.
     *
     * <p>Elements are returned in {@code element_order} sequence so they appear
     * in the intended order within the scenario interface.</p>
     *
     * @param stageId stage identifier
     * @return ordered list of {@link ScenarioElement} objects
     */
    public List<ScenarioElement> getElementsForStage(int stageId) {
        String sql = """
                SELECT id, stage_id, element_order, element_type, label, value, extra_value
                FROM scenario_elements
                WHERE stage_id = ?
                ORDER BY element_order
                """;

        List<ScenarioElement> elements = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stageId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    elements.add(mapElement(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return elements;
    }

    /**
     * Retrieves all selectable options for a specific stage.
     *
     * <p>Options are returned in {@code option_order} sequence so they appear
     * in the intended order within the scenario interface.</p>
     *
     * @param stageId stage identifier
     * @return ordered list of {@link ScenarioOption} objects
     */
    public List<ScenarioOption> getOptionsForStage(int stageId) {
        String sql = """
                SELECT id, stage_id, option_order, option_text, next_stage_order, immediate_feedback
                FROM scenario_options
                WHERE stage_id = ?
                ORDER BY option_order
                """;

        List<ScenarioOption> options = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Select options belonging to the requested stage
            stmt.setInt(1, stageId);

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
     * Maps the current row of a result set to a Scenario object.
     */
    private Scenario mapScenario(ResultSet rs) throws SQLException {
        return new Scenario(
                rs.getInt("id"),
                rs.getInt("scenario_order"),
                rs.getString("type"),
                rs.getString("title"),
                rs.getString("intro_text"),
                rs.getString("learning_outcomes")
        );
    }

    /**
     * Maps the current row of a result set to a ScenarioStage object.
     *
     * <p>The nullable database field {@code is_success} is converted to
     * {@link Boolean}, where {@code null} indicates no success state.</p>
     */
    private ScenarioStage mapStage(ResultSet rs) throws SQLException {
        Boolean success = null;
        int rawSuccess = rs.getInt("is_success");

        // Convert SQL is_success:
        // 1 = true, 0 = false, NULL = no outcome for this stage
        if (!rs.wasNull()) {
            success = rawSuccess == 1;
        }

        return new ScenarioStage(
                rs.getInt("id"),
                rs.getInt("scenario_id"),
                rs.getInt("stage_order"),
                rs.getString("stage_title"),
                rs.getString("prompt"),
                rs.getInt("is_terminal") == 1,
                success,
                rs.getString("feedback_title"),
                rs.getString("feedback_text")
        );
    }

    /**
     * Maps the current row of a result set to a ScenarioElement object.
     */
    private ScenarioElement mapElement(ResultSet rs) throws SQLException {
        return new ScenarioElement(
                rs.getInt("id"),
                rs.getInt("stage_id"),
                rs.getInt("element_order"),
                rs.getString("element_type"),
                rs.getString("label"),
                rs.getString("value"),
                rs.getString("extra_value")
        );
    }

    /**
     * Maps the current row of a result set to a ScenarioOption object.
     *
     * <p>The nullable database field {@code next_stage_order} maps to
     * {@link Integer}, where {@code null} indicates that the option ends
     * the scenario and does not lead to another stage.</p>
     */
    private ScenarioOption mapOption(ResultSet rs) throws SQLException {
        Integer nextStageOrder = null;
        int rawNextStageOrder = rs.getInt("next_stage_order");

        // Convert SQL next_stage_order:
        // numeric value = next stage to load,
        // NULL = terminal option with no following stage.
        if (!rs.wasNull()) {
            nextStageOrder = rawNextStageOrder;
        }

        return new ScenarioOption(
                rs.getInt("id"),
                rs.getInt("stage_id"),
                rs.getInt("option_order"),
                rs.getString("option_text"),
                nextStageOrder,
                rs.getString("immediate_feedback")
        );
    }
}