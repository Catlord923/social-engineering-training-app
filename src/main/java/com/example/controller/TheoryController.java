package com.example.controller;

import com.example.model.TheoryPage;
import com.example.dao.TheoryDAO;

import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;

import java.util.List;

/**
 * Controller for the theory learning module.
 *
 * <p>Retrieves theory pages through {@link TheoryDAO}, displays each page
 * in sequence, and manages navigation through the learning module.</p>
 *
 * <p>The controller also converts plain stored text into formatted JavaFX
 * components such as headings, paragraphs, and bullet sections.</p>
 */
public class TheoryController {

    @FXML private Label titleLabel;
    @FXML private ScrollPane bodyScrollPane;
    @FXML private VBox bodyContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label pageIndicatorLabel;

    private final TheoryDAO theoryDAO = new TheoryDAO();
    private List<TheoryPage> pages;
    private int currentIndex = 0;

    /**
     * Initializes the screen after FXML loading.
     *
     * <p>Retrieves theory pages, applies ScrollPane styling,
     * and displays the first available page.</p>
     */
    @FXML
    public void initialize() {
        pages = theoryDAO.getAllPages();

        // Applies custom CSS class to remove default ScrollPane edges
        bodyScrollPane.getStyleClass().add("edge-to-edge");

        if (pages != null && !pages.isEmpty()) {
            showPage(currentIndex);
        } else {
            handleEmptyState();
        }
    }

    /**
     * Displays a fallback state when no theory pages are available.
     */
    private void handleEmptyState() {
        titleLabel.setText("No theory pages found");
        bodyContainer.getChildren().clear();
        previousButton.setDisable(true);
        nextButton.setDisable(true);
        if (pageIndicatorLabel != null) pageIndicatorLabel.setText("0 / 0");
    }

    /**
     * Navigates to the previous theory page.
     */
    @FXML
    private void handlePrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            showPage(currentIndex);
        }
    }

    /**
     * Navigates to the next theory page.
     *
     * <p>If the current page is the last page, the transition
     * screen for scenarios is opened instead.</p>
     *
     * @throws Exception if the next screen cannot be loaded
     */
    @FXML
    private void handleNext() throws Exception {
        if (pages == null) return;

        if (currentIndex < pages.size() - 1) {
            currentIndex++;
            showPage(currentIndex);
        } else {
            openTransitionScreen();
        }
    }

    /**
     * Opens the transition screen leading into the scenario module.
     *
     * @throws Exception if the FXML file cannot be loaded
     */
    private void openTransitionScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransitionScreen.fxml"));
        Parent root = loader.load();

        TransitionController controller = loader.getController();
        controller.configure(
                "Practice Scenarios",
                "You have completed the learning section.\n\n" +
                        "Next, you will face realistic situations based on common social engineering attacks.\n\n" +
                        "Read each scenario carefully and choose how you would respond.",
                "Start Scenarios",
                "/view/ScenarioScreen.fxml"
        );

        Stage stage = (Stage) nextButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Displays the requested theory page and updates navigation controls.
     *
     * @param index index of the page to display
     */
    private void showPage(int index) {
        TheoryPage page = pages.get(index);
        boolean isLastPage = index == pages.size() - 1;

        titleLabel.setText(page.getTitle());
        renderBody(page.getBody());

        // Resets scroll position after content rendering completes.
        Platform.runLater(() -> bodyScrollPane.setVvalue(0));

        previousButton.setDisable(index == 0);
        // Dim previous button when disabled
        previousButton.setOpacity(index == 0 ? 0.35 : 1.0);
        nextButton.setDisable(false);

        nextButton.setText(isLastPage ? "Start Scenarios →" : "Next →");
        nextButton.setPrefWidth(isLastPage ? 200 : 160);

        if (pageIndicatorLabel != null) {
            pageIndicatorLabel.setText((index + 1) + " / " + pages.size());
        }
    }

    /**
     * Converts raw body text into styled UI blocks.
     *
     * @param bodyText raw page text from the database
     */
    private void renderBody(String bodyText) {
        bodyContainer.getChildren().clear();

        if (bodyText == null || bodyText.isBlank()) return;

        String cleanedText = cleanText(bodyText);
        String[] blocks = cleanedText.split("\\n\\s*\\n");

        for (String block : blocks) {
            String trimmedBlock = block.trim();
            if (trimmedBlock.isEmpty()) continue;

            if (isBulletBlock(trimmedBlock)) {
                addBulletBlock(trimmedBlock);
            } else if (isShortHeading(trimmedBlock)) {
                addHeading(trimmedBlock);
            } else {
                addParagraph(trimmedBlock);
            }
        }
    }

    /**
     * Normalizes text formatting before rendering.
     *
     * @param text raw text input
     * @return cleaned text
     */
    private String cleanText(String text) {
        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("(?m)^[ \\t]+", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    /**
     * Determines whether a block contains bullet list entries.
     *
     * @param block text block
     * @return {@code true} if bullet formatting is detected
     */
    private boolean isBulletBlock(String block) {
        for (String line : block.split("\\n")) {
            if (line.trim().startsWith("- ")) return true;
        }
        return false;
    }

    /**
     * Determines whether a block should be styled as a heading.
     *
     * @param block text block
     * @return {@code true} if the block matches heading rules
     */
    private boolean isShortHeading(String block) {
        return !block.contains("\n") && block.length() <= 60 && block.endsWith(":");
    }

    /**
     * Adds a styled paragraph label to the body container.
     *
     * @param text paragraph text
     */
    private void addParagraph(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.TOP_LEFT);
        label.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #cbd5e1;" +
                        "-fx-line-spacing: 7px;"
        );
        bodyContainer.getChildren().add(label);
    }

    /**
     * Adds a styled heading label to the body container.
     *
     * @param text heading text
     */
    private void addHeading(String text) {
        // Strip trailing colon for display
        String display = text.endsWith(":") ? text.substring(0, text.length() - 1) : text;

        Label label = new Label(display);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.TOP_LEFT);
        label.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #f1f5f9;" +
                        "-fx-padding: 12 0 2 0;"
        );
        bodyContainer.getChildren().add(label);
    }

    /**
     * Adds a styled bullet-card container to the body content area.
     *
     * @param block block-containing bullet lines
     */
    private void addBulletBlock(String block) {
        // Wrap bullets in a styled card container
        VBox bulletCard = new VBox(12);
        bulletCard.setStyle(
                "-fx-background-color: #1e293b;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 18 22 18 22;"
        );

        for (String line : block.split("\\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("- ")) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.TOP_LEFT);

                // Bullet dot
                Region dot = new Region();
                dot.setPrefSize(7, 7);
                dot.setMinSize(7, 7);
                dot.setMaxSize(7, 7);
                dot.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 4;");
                // Nudge dot down to align with text cap height
                dot.setTranslateY(8);

                Label text = new Label(trimmed.substring(2));
                text.setWrapText(true);
                text.setMaxWidth(Double.MAX_VALUE);
                text.setStyle(
                        "-fx-font-size: 19px;" +
                                "-fx-text-fill: #cbd5e1;" +
                                "-fx-line-spacing: 4px;"
                );
                HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);

                row.getChildren().addAll(dot, text);
                bulletCard.getChildren().add(row);
            } else {
                // Non-bullet line within a bullet block - treated as sub-heading
                Label lbl = new Label(trimmed);
                lbl.setWrapText(true);
                lbl.setMaxWidth(Double.MAX_VALUE);
                lbl.setStyle(
                        "-fx-font-size: 19px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #94a3b8;"
                );
                bulletCard.getChildren().add(lbl);
            }
        }

        bodyContainer.getChildren().add(bulletCard);
    }
}
