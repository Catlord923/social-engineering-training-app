package com.example.session;

/**
 * Stores temporary progress data for the current application session.
 *
 * <p>This class uses a singleton instance so controllers and services can access
 * shared progress data while the application is running. The data is kept only
 * in memory and is not persisted between application launches.</p>
 */
public class AppSession {

    // Singleton
    private static final AppSession INSTANCE = new AppSession();
    private AppSession() {}
    public static AppSession getInstance() { return INSTANCE; }

    // Scenario scores
    private int bestChoiceCount   = 0;
    private int safeChoiceCount   = 0;
    private int unsafeChoiceCount = 0;
    private int scenariosCompleted = 0;

    /**
     * Stores the result of the bonus scenario.
     *
     * <p>A value of {@code true} means the user correctly skipped the form,
     * {@code false} means the user submitted it, and {@code null} means the
     * bonus scenario has not yet been completed.</p>
     */
    private Boolean bonusSkipped = null;

    /**
     * Records the outcome of one normal scenario terminal stage.
     *
     * @param success {@code true} if the player chose a successful path.
     * @param isBest  {@code true} if the terminal is the "Best Choice" tier.
     */
    public void recordScenarioOutcome(boolean success, boolean isBest) {
        scenariosCompleted++;
        if (success) {
            if (isBest) bestChoiceCount++;
            else        safeChoiceCount++;
        } else {
            unsafeChoiceCount++;
        }
    }

    /**
     * Records whether the user skipped or submitted the bonus phishing form.
     *
     * @param skipped {@code true} if the user skipped the form; {@code false}
     *                            if the user submitted it
     */
    public void recordBonusOutcome(boolean skipped) {
        this.bonusSkipped = skipped;
    }

    public int     getBestChoiceCount()    { return bestChoiceCount;    }
    public int     getSafeChoiceCount()    { return safeChoiceCount;    }
    public int     getUnsafeChoiceCount()  { return unsafeChoiceCount;  }
    /**
     * Returns how many standard scenarios have been completed.
     *
     * @return completed scenario count
     */
    public int     getScenariosCompleted() { return scenariosCompleted; }
    public Boolean getBonusSkipped()       { return bonusSkipped;       }

    // Quiz scores
    private int     quizScore = 0;
    private int     quizTotal = 0;
    private boolean quizDone  = false;

    /**
     * Called by QuizService when the final question is answered and the quiz ends.
     */
    public void setQuizScore(int score, int total) {
        this.quizScore = score;
        this.quizTotal = total;
        this.quizDone  = true;
    }

    public int     getQuizScore() { return quizScore; }
    public int     getQuizTotal() { return quizTotal; }
    public boolean isQuizDone()   { return quizDone;  }
}
