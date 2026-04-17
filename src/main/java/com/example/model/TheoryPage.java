package com.example.model;

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

    public int getId() {
        return id;
    }

    public int getPageOrder() {
        return pageOrder;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}