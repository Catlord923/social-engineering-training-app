package com.example.controller;

import com.example.model.TheoryPage;
import com.example.dao.TheoryDAO;

import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.fxml.FXML;

import java.util.List;

/**
 * Controller for the TheoryScreen view.
 * Handles navigation between pages and dynamically renders raw text into
 * stylized UI components (headings, bullets, paragraphs).
 */
public class TheoryController {

    @FXML
    private Label titleLabel;

    @FXML
    private ScrollPane bodyScrollPane;

    @FXML
    private VBox bodyContainer;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Label pageIndicatorLabel;

    private final TheoryDAO theoryDAO = new TheoryDAO();
    private List<TheoryPage> pages;
    private int currentIndex = 0;

    /**
     * Initializes the view by fetching data and displaying the first page.
     * Handles the "empty state" if no data is found.
     */
    @FXML
    public void initialize() {
        pages = theoryDAO.getAllPages();

        if (pages != null && !pages.isEmpty()) {
            showPage(currentIndex);
        } else {
            handleEmptyState();
        }
    }

    private void handleEmptyState() {
        titleLabel.setText("No theory pages found");
        bodyContainer.getChildren().clear();
        previousButton.setDisable(true);
        nextButton.setDisable(true);

        if (pageIndicatorLabel != null) {
            pageIndicatorLabel.setText("0 / 0");
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            showPage(currentIndex);
        }
    }

    @FXML
    private void handleNext() {
        if (pages != null && currentIndex < pages.size() - 1) {
            currentIndex++;
            showPage(currentIndex);
        }
    }

    /**
     * Updates all UI elements to reflect the page at the given index.
     * @param index The position of the page in the list.
     */
    private void showPage(int index) {
        TheoryPage page = pages.get(index);

        titleLabel.setText(page.getTitle());
        renderBody(page.getBody());

        // Resets scroll position to the top. Platform.runLater ensures
        // the UI has finished layout updates before scrolling.
        Platform.runLater(() -> bodyScrollPane.setVvalue(0));

        // Disable buttons at the start/end of the list
        previousButton.setDisable(index == 0);
        nextButton.setDisable(index == pages.size() - 1);

        if (pageIndicatorLabel != null) {
            pageIndicatorLabel.setText((index + 1) + " / " + pages.size());
        }
    }

    /**
     * Main parser: Splits raw text into blocks and converts them to JavaFX Labels.
     * Blocks are separated by double newlines.
     * @param bodyText The raw string from the database.
     */
    private void renderBody(String bodyText) {
        bodyContainer.getChildren().clear();

        if (bodyText == null || bodyText.isBlank()) {
            return;
        }

        String cleanedText = cleanText(bodyText);
        // Split text whenever there are two or more newlines
        // with whitespace in between (paragraph breaks)
        String[] blocks = cleanedText.split("\\n\\s*\\n");

        for (String block : blocks) {
            String trimmedBlock = block.trim();

            if (trimmedBlock.isEmpty()) {
                continue;
            }

            // Determine block type based on content structure
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
     * Sanitizes input text to normalize line breaks and remove indentation.
     * @return A clean string ready for parsing.
     */
    private String cleanText(String text) {
        return text
                .replace("\r\n", "\n") // Convert CRLF line endings to LF
                .replace("\r", "\n") // Convert CR line endings to LF
                .replaceAll("(?m)^[ \\t]+", "") // Remove leading tabs/spaces per line - removes indentation
                .replaceAll("\\n{3,}", "\n\n") // Collapse excessive spacing - 2+ lines
                .trim();
    }

    private boolean isBulletBlock(String block) {
        // A block is considered a bullet list if it contains lines starting with a dash
        String[] lines = block.split("\\n");
        int bulletCount = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("- ")) {
                bulletCount++;
            }
        }

        return bulletCount > 0;
    }

    /**
     * Heuristic for headings: Must be one line, < 60 chars, and end with a colon.
     */
    private boolean isShortHeading(String block) {
        return !block.contains("\n")
                && block.length() <= 60
                && block.endsWith(":");
    }

    // --- UI Methods ---
    // These methods create stylized Labels to keep the text rendering logic clean.

    private void addParagraph(String text) {
        Label paragraphLabel = new Label(text);
        paragraphLabel.setWrapText(true);
        paragraphLabel.setMaxWidth(Double.MAX_VALUE);
        paragraphLabel.setAlignment(Pos.TOP_LEFT);
        paragraphLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-text-fill: #2b2b2b;" +
                        "-fx-line-spacing: 6px;"
        );

        bodyContainer.getChildren().add(paragraphLabel);
    }

    private void addHeading(String text) {
        Label headingLabel = new Label(text);
        headingLabel.setWrapText(true);
        headingLabel.setMaxWidth(Double.MAX_VALUE);
        headingLabel.setAlignment(Pos.TOP_LEFT);
        headingLabel.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1f1f1f;"
        );

        bodyContainer.getChildren().add(headingLabel);
    }

    private void addBulletBlock(String block) {
        String[] lines = block.split("\\n");

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.startsWith("- ")) {
                // Convert "- text" into "• text" with indentation
                Label bulletLabel = new Label("• " + trimmed.substring(2));
                bulletLabel.setWrapText(true);
                bulletLabel.setMaxWidth(Double.MAX_VALUE);
                bulletLabel.setAlignment(Pos.TOP_LEFT);
                bulletLabel.setStyle(
                        "-fx-font-size: 21px;" +
                                "-fx-text-fill: #2b2b2b;" +
                                "-fx-line-spacing: 4px;" +
                                "-fx-padding: 0 0 0 12;"
                );
                bodyContainer.getChildren().add(bulletLabel);
            } else {
                addParagraph(trimmed); // Handle non-bullet lines within a bullet list block
            }
        }
    }
}