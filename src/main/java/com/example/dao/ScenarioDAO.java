package com.example.dao;

import com.example.db.DatabaseManager;
import com.example.model.Scenario;
import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;
import com.example.model.ScenarioStage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScenarioDAO {

    public List<Scenario> getAllScenarios() {
        String sql = """
                SELECT id, scenario_order, type, title, intro_text, learning_outcomes
                FROM scenarios
                ORDER BY scenario_order
                """;

        List<Scenario> scenarios = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                scenarios.add(mapScenario(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scenarios;
    }

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

    private ScenarioStage mapStage(ResultSet rs) throws SQLException {
        Boolean success = null;
        int rawSuccess = rs.getInt("is_success");

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

    private ScenarioOption mapOption(ResultSet rs) throws SQLException {
        Integer nextStageOrder = null;
        int rawNextStageOrder = rs.getInt("next_stage_order");

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