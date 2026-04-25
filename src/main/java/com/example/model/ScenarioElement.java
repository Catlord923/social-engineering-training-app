package com.example.model;

/**
 * Immutable model representing a single content element within a scenario stage.
 *
 * <p>Scenario elements are database-driven pieces of content used to build
 * the scenario interface dynamically. Examples include plain text, warning
 * boxes, links, email fields, SMS messages, popups, and chat messages.</p>
 *
 * <p>Multiple elements may be combined to form larger composite widgets
 * such as an email or mobile message.</p>
 */
public class ScenarioElement {
    private final int id;
    private final int stageId;
    private final int elementOrder;
    private final String elementType;
    private final String label;
    private final String value;
    private final String extraValue;

    /**
     * Creates a scenario element model using values loaded from the database.
     *
     * @param id database identifier
     * @param stageId parent stage identifier
     * @param elementOrder display order within the stage
     * @param elementType element category used for rendering
     * @param label optional heading or field label
     * @param value primary content value
     * @param extraValue optional secondary value reserved for future use
     */
    public ScenarioElement(int id, int stageId, int elementOrder, String elementType, String label, String value, String extraValue) {
        this.id = id;
        this.stageId = stageId;
        this.elementOrder = elementOrder;
        this.elementType = elementType;
        this.label = label;
        this.value = value;
        this.extraValue = extraValue;
    }

    /**
     * Returns the database identifier.
     *
     * @return element id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the element type used by the UI renderer.
     *
     * @return element type
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * Returns the optional label text.
     *
     * @return label text or {@code null}
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the primary content value.
     *
     * @return element value
     */
    public String getValue() {
        return value;
    }
}