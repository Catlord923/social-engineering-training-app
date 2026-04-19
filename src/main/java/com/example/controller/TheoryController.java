package com.example.controller;

import com.example.model.TheoryPage;
import com.example.dao.TheoryDAO;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

import java.util.List;

public class TheoryController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label bodyLabel;

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

        bodyLabel.setWrapText(true);

        if (pages != null && !pages.isEmpty()) {
            showPage(currentIndex);
        } else {
            titleLabel.setText("No theory pages found");
            bodyLabel.setText("");
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
        bodyLabel.setText(page.getBody());

        previousButton.setDisable(index == 0);
        nextButton.setDisable(index == pages.size() - 1);

        if (pageIndicatorLabel != null) {
            pageIndicatorLabel.setText((index + 1) + " / " + pages.size());
        }
    }
}