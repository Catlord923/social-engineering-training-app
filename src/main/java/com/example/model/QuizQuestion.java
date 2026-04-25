package com.example.model;

/**
 * Immutable model representing a quiz question.
 *
 * <p>Each question stores the text presented to the user together with an
 * optional explanation that can be shown after answering.</p>
 */
public class QuizQuestion {
    private final int id;
    private final int questionOrder;
    private final String questionText;
    private final String explanation;

    /**
     * Creates a quiz question model using values loaded from the database.
     *
     * @param id question id
     * @param questionOrder stored order of the question
     * @param questionText text shown to the user
     * @param explanation optional feedback explanation
     */
    public QuizQuestion(int id, int questionOrder, String questionText, String explanation) {
        this.id = id;
        this.questionOrder = questionOrder;
        this.questionText = questionText;
        this.explanation = explanation;
    }

    /**
     * Returns the question id.
     *
     * @return question id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the text shown to the user.
     *
     * @return question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Returns the explanation associated with the question.
     *
     * @return explanation text, or {@code null}
     */
    public String getExplanation() {
        return explanation;
    }
}