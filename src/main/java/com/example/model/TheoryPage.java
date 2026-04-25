package com.example.model;

/**
 * Immutable model representing a single theory page.
 *
 * <p>This class is used to transfer theory content between the
 * data access layer and the user interface layer.</p>
 */
public class TheoryPage {
    private final int id;
    private final int pageOrder;
    private final String title;
    private final String body;

    /**
     * Creates a new theory page object.
     *
     * @param id unique database identifier
     * @param pageOrder display order within the theory module
     * @param title page title
     * @param body main page content
     */
    public TheoryPage(int id, int pageOrder, String title, String body) {
        this.id = id;
        this.pageOrder = pageOrder;
        this.title = title;
        this.body = body;
    }

    /**
     * Returns the page title.
     *
     * @return theory page title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the main page content.
     *
     * @return theory page body text
     */
    public String getBody() {
        return body;
    }
}