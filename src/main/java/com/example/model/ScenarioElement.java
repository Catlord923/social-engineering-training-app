package com.example.model;

public class ScenarioElement {
    private final int id;
    private final int stageId;
    private final int elementOrder;
    private final String elementType;
    private final String label;
    private final String value;
    private final String extraValue;

    public ScenarioElement(int id, int stageId, int elementOrder, String elementType, String label, String value, String extraValue) {
        this.id = id;
        this.stageId = stageId;
        this.elementOrder = elementOrder;
        this.elementType = elementType;
        this.label = label;
        this.value = value;
        this.extraValue = extraValue;
    }

    public int getId() {
        return id;
    }

    public int getStageId() {
        return stageId;
    }

    public int getElementOrder() {
        return elementOrder;
    }

    public String getElementType() {
        return elementType;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getExtraValue() {
        return extraValue;
    }
}