package com.example.model;

/**
 * Immutable model representing a single theory topic.
 * Acts as a Data Transfer Object (DTO) between the DAO and the Controller.
 */
public class TheoryPage {
    private final int id;
    private final int pageOrder;
    private final String title;
    private final String body;

    public TheoryPage(int id, int pageOrder, String title, String body) {
        this.id = id;
        this.pageOrder = pageOrder;
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}