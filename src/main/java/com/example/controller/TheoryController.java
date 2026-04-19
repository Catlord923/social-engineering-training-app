package com.example.controller;

import com.example.model.TheoryPage;
import com.example.dao.TheoryDAO;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

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

    @FXML
    public void initialize() {
        pages = theoryDAO.getAllPages();

        if (pages != null && !pages.isEmpty()) {
            showPage(currentIndex);
        } else {
            titleLabel.setText("No theory pages found");
            bodyContainer.getChildren().clear();
            previousButton.setDisable(true);
            nextButton.setDisable(true);

            if (pageIndicatorLabel != null) {
                pageIndicatorLabel.setText("0 / 0");
            }
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

    private void showPage(int index) {
        TheoryPage page = pages.get(index);

        titleLabel.setText(page.getTitle());
        renderBody(page.getBody());

        Platform.runLater(() -> bodyScrollPane.setVvalue(0));

        previousButton.setDisable(index == 0);
        nextButton.setDisable(index == pages.size() - 1);

        if (pageIndicatorLabel != null) {
            pageIndicatorLabel.setText((index + 1) + " / " + pages.size());
        }
    }

    private void renderBody(String bodyText) {
        bodyContainer.getChildren().clear();

        if (bodyText == null || bodyText.isBlank()) {
            return;
        }

        String cleanedText = cleanText(bodyText);
        String[] blocks = cleanedText.split("\\n\\s*\\n");

        for (String block : blocks) {
            String trimmedBlock = block.trim();

            if (trimmedBlock.isEmpty()) {
                continue;
            }

            if (isBulletBlock(trimmedBlock)) {
                addBulletBlock(trimmedBlock);
            } else if (isShortHeading(trimmedBlock)) {
                addHeading(trimmedBlock);
            } else {
                addParagraph(trimmedBlock);
            }
        }
    }

    private String cleanText(String text) {
        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("(?m)^[ \\t]+", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private boolean isBulletBlock(String block) {
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

    private boolean isShortHeading(String block) {
        return !block.contains("\n")
                && block.length() <= 60
                && block.endsWith(":");
    }

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
                addParagraph(trimmed);
            }
        }
    }
}