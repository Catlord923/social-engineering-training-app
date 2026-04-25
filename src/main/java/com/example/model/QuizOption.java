package com.example.model;

/**
 * Immutable model representing a selectable answer option for a quiz question.
 *
 * <p>Each option belongs to a specific question and stores the displayed text
 * together with whether it is the correct answer.</p>
 */
public class QuizOption {
    private final int id;
    private final int questionId;
    private final int optionOrder;
    private final String optionText;
    private final boolean correct;

    /**
     * Creates a quiz option model using values loaded from the database.
     *
     * @param id database identifier
     * @param questionId parent question identifier
     * @param optionOrder database sequence value for the option
     * @param optionText text shown to the user
     * @param correct {@code true} if this is the correct answer;
     *                {@code false} otherwise
     */
    public QuizOption(int id, int questionId, int optionOrder, String optionText, boolean correct) {
        this.id = id;
        this.questionId = questionId;
        this.optionOrder = optionOrder;
        this.optionText = optionText;
        this.correct = correct;
    }

    /**
     * Returns the option id.
     *
     * @return option id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the text shown to the user.
     *
     * @return option label text
     */
    public String getOptionText() {
        return optionText;
    }

    /**
     * Returns whether this option is correct.
     *
     * @return {@code true} if correct, otherwise {@code false}
     */
    public boolean isCorrect() {
        return correct;
    }
}