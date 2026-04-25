package com.example.service;

import com.example.session.AppSession;
import com.example.model.QuizQuestion;
import com.example.model.QuizOption;
import com.example.dao.QuizDAO;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for the quiz module.
 *
 * <p>Owns quiz state, scoring logic, question progression, and quiz data
 * retrieval.</p>
 *
 * <p>The controller forwards user actions to this class, while screen updates
 * are returned through {@link QuizCallback}.</p>
 *
 * <p>Answer options are shuffled when each question is loaded so the correct
 * answer does not remain in a predictable position.</p>
 */
public class QuizService {

    /**
     * Callback contract implemented by the controller.
     *
     * <p>Allows the service layer to request UI updates without depending on
     * JavaFX classes directly.</p>
     */
    public interface QuizCallback {
        /**
         * Requests display of a question and its options.
         */
        void onShowQuestion(QuizQuestion question, List<QuizOption> options,
                            int questionNum, int totalQuestions);

        /**
         * Requests display of the selected answer result.
         */
        void onAnswerResult(int selectedIndex, int correctIndex,
                            boolean wasCorrect, String explanation);

        /**
         * Requests display of final score results.
         */
        void onShowFinalResults(int score, int total);

        /**
         * Requests navigation to the results screen.
         */
        void onNavigateToResults();

        /**
         * Requests display of an error state.
         */
        void onError(String message);
    }

    private final QuizDAO      dao;
    private final QuizCallback callback;

    private List<QuizQuestion> questions;

    /**
     * Shuffled options for the active question.
     *
     * <p>This matches the order shown on screen so selected indexes remain
     * accurate after shuffling.</p>
     */
    private List<QuizOption>   currentOptions;
    private int                currentIndex = 0;
    private int                score        = 0;
    private boolean            answered     = false;

    /**
     * Creates the quiz service.
     *
     * @param callback controller callback used for UI updates
     */
    public QuizService(QuizCallback callback) {
        this.dao      = new QuizDAO();
        this.callback = callback;
    }

    /**
     * Starts the quiz.
     *
     * <p>Loads all questions and displays the first one.</p>
     */
    public void start() {
        questions = dao.getAllQuestions();
        if (questions == null || questions.isEmpty()) {
            callback.onError("No quiz questions found.");
            return;
        }
        showQuestion(currentIndex);
    }

    /**
     * Handles answer selection for the current question.
     *
     * @param selectedIndex selected option index in the displayed order
     */
    public void handleAnswerSelected(int selectedIndex) {
        // Prevent multiple submissions for one question
        if (answered) return;
        answered = true;

        boolean wasCorrect = currentOptions.get(selectedIndex).isCorrect();
        if (wasCorrect) score++;

        int correctIndex = findCorrectIndex(currentOptions);
        callback.onAnswerResult(selectedIndex, correctIndex, wasCorrect,
                questions.get(currentIndex).getExplanation());

        // Save final score immediately after last question is answered
        if (currentIndex == questions.size() - 1) {
            AppSession.getInstance().setQuizScore(score, questions.size());
        }
    }

    /**
     * Handles presses of the Next or Finish button.
     *
     * <p>If more questions remain, the next question is loaded.
     * Otherwise, the final results view is shown.</p>
     */
    public void handleNext() {
        if (!answered) return;
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            answered = false;
            showQuestion(currentIndex);
        } else {
            callback.onShowFinalResults(score, questions.size());
        }
    }

    /**
     * Handles completion of the quiz results screen.
     *
     * <p>Requests navigation to the application results module.</p>
     */
    public void handleFinish() {
        callback.onNavigateToResults();
    }

    /**
     * Loads and displays a question.
     *
     * <p>Answer options are copied into a new list and shuffled so the
     * original database order is not modified.</p>
     *
     * @param index question index
     */
    private void showQuestion(int index) {
        QuizQuestion question = questions.get(index);
        List<QuizOption> shuffled = new ArrayList<>(dao.getOptionsForQuestion(question.getId()));
        Collections.shuffle(shuffled);
        currentOptions = shuffled;
        callback.onShowQuestion(question, currentOptions, index + 1, questions.size());
    }

    /**
     * Finds the index of the correct option in the displayed list.
     *
     * @param options displayed answer options
     * @return correct option index, or {@code -1} if none found
     */
    private int findCorrectIndex(List<QuizOption> options) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).isCorrect()) return i;
        }
        return -1;
    }
}
