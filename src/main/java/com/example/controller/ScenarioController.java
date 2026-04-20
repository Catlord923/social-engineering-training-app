package com.example.controller;

import com.example.dao.ScenarioDAO;
import com.example.model.Scenario;
import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;
import com.example.model.ScenarioStage;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ScenarioController {

    @FXML private Label titleLabel;
    @FXML private Label promptLabel;
    @FXML private Label progressLabel;
    @FXML private VBox contentContainer;
    @FXML private VBox optionsContainer;

    private final ScenarioDAO scenarioDAO = new ScenarioDAO();

    private Scenario currentScenario;
    private ScenarioStage currentStage;
    private int currentScenarioOrder = 1;

    @FXML
    public void initialize() {
        loadScenario(currentScenarioOrder);
    }

    // ─── Loading ────────────────────────────────────────────────────────────────

    private void loadScenario(int scenarioOrder) {
        currentScenario = scenarioDAO.getScenarioByOrder(scenarioOrder);
        if (currentScenario == null) { showErrorState("Scenario not found."); return; }
        loadStage(1);
    }

    private void loadStage(int stageOrder) {
        currentStage = scenarioDAO.getStageByOrder(currentScenario.getId(), stageOrder);
        if (currentStage == null) { showErrorState("Scenario stage not found."); return; }
        if (currentStage.isTerminal()) { showTerminalStage(); return; }
        renderStage();
    }

    // ─── Stage Rendering ────────────────────────────────────────────────────────

    private void renderStage() {
        titleLabel.setText(currentScenario.getTitle());
        promptLabel.setText(currentStage.getPrompt());
        progressLabel.setText("Scenario " + currentScenario.getScenarioOrder() + " of 4");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        List<ScenarioElement> elements = scenarioDAO.getElementsForStage(currentStage.getId());
        List<ScenarioOption> options   = scenarioDAO.getOptionsForStage(currentStage.getId());

        renderElements(elements);
        renderOptions(options);
    }

    // ─── Element Rendering ──────────────────────────────────────────────────────

    private void renderElements(List<ScenarioElement> elements) {
        // Group consecutive email/SMS/chat/popup parts so they render as one widget.
        List<ScenarioElement> emailParts  = new ArrayList<>();
        List<ScenarioElement> smsParts    = new ArrayList<>();
        List<ScenarioElement> popupParts  = new ArrayList<>();
        List<ScenarioElement> chatParts   = new ArrayList<>();

        for (ScenarioElement el : elements) {
            switch (el.getElementType()) {
                case "EMAIL_FROM", "EMAIL_SUBJECT", "EMAIL_BODY" -> emailParts.add(el);
                case "SMS_SENDER", "SMS_MESSAGE"                 -> smsParts.add(el);
                case "POPUP_TITLE", "POPUP_BODY"                 -> popupParts.add(el);
                case "CHAT_SENDER", "CHAT_MESSAGE"               -> chatParts.add(el);

                // Standalone elements rendered immediately
                case "WARNING_BOX" -> contentContainer.getChildren().add(buildWarningBox(el));
                case "INFO_BOX"    -> contentContainer.getChildren().add(buildInfoBox(el));
                case "LINK"        -> contentContainer.getChildren().add(buildLinkLabel(el));
                default            -> contentContainer.getChildren().add(makeBodyLabel(el.getValue()));
            }
        }

        // Flush grouped widgets (inserted in element-order order above; add composites after)
        if (!emailParts.isEmpty())  contentContainer.getChildren().add(buildEmailWidget(emailParts));
        if (!smsParts.isEmpty())    contentContainer.getChildren().add(buildSmsWidget(smsParts));
        if (!popupParts.isEmpty())  contentContainer.getChildren().add(buildPopupWidget(popupParts));
        if (!chatParts.isEmpty())   contentContainer.getChildren().add(buildChatWidget(chatParts));
    }

    // ─── Email Widget ───────────────────────────────────────────────────────────

    /**
     * Renders a realistic email client card:
     *
     *  ┌─────────────────────────────────────────────┐
     *  │ ● ● ●                          [email icon] │  ← title bar
     *  ├─────────────────────────────────────────────┤
     *  │ From:    sender@domain.com                  │
     *  │ Subject: Urgent: Verify now                 │
     *  ├─────────────────────────────────────────────┤
     *  │                                             │
     *  │  Body text …                                │
     *  │                                             │
     *  └─────────────────────────────────────────────┘
     */
    private VBox buildEmailWidget(List<ScenarioElement> parts) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 3);"
        );

        // ── Title bar
        HBox titleBar = new HBox(8);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(10, 16, 10, 16));
        titleBar.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: transparent transparent #d0d0d0 transparent;"
        );

        // Traffic-light dots
        for (String colour : new String[]{"#ff5f57", "#febc2e", "#28c840"}) {
            Region dot = new Region();
            dot.setPrefSize(12, 12);
            dot.setMinSize(12, 12);
            dot.setStyle("-fx-background-color:" + colour + "; -fx-background-radius: 6;");
            titleBar.getChildren().add(dot);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().add(spacer);

        Label appLabel = new Label("✉  Mail");
        appLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666; -fx-font-weight: bold;");
        titleBar.getChildren().add(appLabel);

        card.getChildren().add(titleBar);

        // ── Header rows (From / Subject)
        VBox headers = new VBox(4);
        headers.setPadding(new Insets(12, 20, 12, 20));
        headers.setStyle("-fx-background-color: #f9f9f9;");

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("EMAIL_FROM") || el.getElementType().equals("EMAIL_SUBJECT")) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);

                Label lbl = new Label(el.getLabel() != null ? el.getLabel() + ":" : "");
                lbl.setMinWidth(70);
                lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #555555;");

                Label val = new Label(el.getValue());
                val.setWrapText(true);
                val.setStyle("-fx-font-size: 15px; -fx-text-fill: #222222;");

                row.getChildren().addAll(lbl, val);
                headers.getChildren().add(row);
            }
        }

        if (!headers.getChildren().isEmpty()) {
            card.getChildren().add(headers);
            card.getChildren().add(new Separator());
        }

        // ── Body
        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("EMAIL_BODY")) {
                Label body = new Label(el.getValue());
                body.setWrapText(true);
                body.setMaxWidth(Double.MAX_VALUE);
                body.setPadding(new Insets(18, 24, 22, 24));
                body.setStyle(
                        "-fx-font-size: 17px;" +
                                "-fx-text-fill: #1a1a1a;" +
                                "-fx-line-spacing: 5px;" +
                                "-fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;"
                );
                card.getChildren().add(body);
            }
        }

        return card;
    }

    // ─── SMS Widget ─────────────────────────────────────────────────────────────

    /**
     * Renders a phone-style SMS conversation:
     *
     *  ┌─────────────────────┐
     *  │  ← ParcelTrack      │  ← status bar style header
     *  ├─────────────────────┤
     *  │  [bubble] message   │
     *  └─────────────────────┘
     */
    private VBox buildSmsWidget(List<ScenarioElement> parts) {
        VBox phone = new VBox();
        phone.setMaxWidth(520);
        phone.setStyle(
                "-fx-background-color: #1c1c1e;" +
                        "-fx-border-color: #3a3a3c;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 14, 0, 0, 4);"
        );

        // Status bar
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(12, 20, 6, 20));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #1c1c1e; -fx-background-radius: 20 20 0 0;");

        Label backArrow = new Label("←");
        backArrow.setStyle("-fx-font-size: 18px; -fx-text-fill: #0a84ff;");

        String senderName = parts.stream()
                .filter(e -> e.getElementType().equals("SMS_SENDER"))
                .map(ScenarioElement::getValue)
                .findFirst().orElse("Unknown");

        Label senderLabel = new Label(senderName);
        senderLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label time = new Label("now");
        time.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e8e93;");

        statusBar.getChildren().addAll(backArrow, new Region(){{setMinWidth(10);}}, senderLabel, sp, time);
        phone.getChildren().add(statusBar);

        // Message bubbles area
        VBox bubbleArea = new VBox(10);
        bubbleArea.setPadding(new Insets(10, 16, 20, 16));
        bubbleArea.setAlignment(Pos.CENTER_LEFT);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("SMS_MESSAGE")) {
                VBox bubble = new VBox();
                bubble.setMaxWidth(380);
                bubble.setPadding(new Insets(10, 14, 10, 14));
                bubble.setStyle(
                        "-fx-background-color: #2c2c2e;" +
                                "-fx-background-radius: 18 18 18 4;"
                );

                Label msg = new Label(el.getValue());
                msg.setWrapText(true);
                msg.setMaxWidth(360);
                msg.setStyle(
                        "-fx-font-size: 16px;" +
                                "-fx-text-fill: #ffffff;" +
                                "-fx-line-spacing: 4px;"
                );
                bubble.getChildren().add(msg);
                bubbleArea.getChildren().add(bubble);
            }
        }

        phone.getChildren().add(bubbleArea);
        return phone;
    }

    // ─── Popup Widget ────────────────────────────────────────────────────────────

    /**
     * Renders a Windows-style system alert dialog:
     *
     *  ╔══════════════════════════════════╗
     *  ║  🛡  Windows Security Alert       ║  ← red title bar
     *  ╠══════════════════════════════════╣
     *  ║  ⚠  POPUP_TITLE                  ║
     *  ║     POPUP_BODY text…             ║
     *  ╚══════════════════════════════════╝
     */
    private VBox buildPopupWidget(List<ScenarioElement> parts) {
        VBox dialog = new VBox();
        dialog.setMaxWidth(680);
        dialog.setStyle(
                "-fx-background-color: #f0f0f0;" +
                        "-fx-border-color: #888888;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 18, 0, 3, 5);"
        );

        // Title bar
        HBox titleBar = new HBox(8);
        titleBar.setPadding(new Insets(6, 10, 6, 10));
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: #c0392b; -fx-background-radius: 3 3 0 0;");

        Label icon = new Label("🛡");
        icon.setStyle("-fx-font-size: 14px;");
        Label winTitle = new Label("Windows Security Alert");
        winTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        for (String sym : new String[]{"—", "□", "✕"}) {
            Label btn = new Label(sym);
            btn.setStyle(
                    "-fx-font-size: 12px; -fx-text-fill: #ffffff;" +
                            "-fx-padding: 2 7 2 7;" +
                            "-fx-background-color: transparent;"
            );
            titleBar.getChildren().add(btn);
        }
        titleBar.getChildren().addAll(0, List.of(icon, winTitle, sp));
        dialog.getChildren().add(titleBar);

        // Body area
        HBox body = new HBox(16);
        body.setPadding(new Insets(20, 20, 20, 20));
        body.setAlignment(Pos.TOP_LEFT);

        Label warningIcon = new Label("⚠");
        warningIcon.setStyle("-fx-font-size: 48px; -fx-text-fill: #e67e22;");

        VBox textArea = new VBox(10);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("POPUP_TITLE")) {
                Label title = new Label(el.getValue());
                title.setWrapText(true);
                title.setStyle(
                        "-fx-font-size: 20px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #b00020;"
                );
                textArea.getChildren().add(title);
            } else if (el.getElementType().equals("POPUP_BODY")) {
                Label bodyText = new Label(el.getValue());
                bodyText.setWrapText(true);
                bodyText.setStyle(
                        "-fx-font-size: 16px;" +
                                "-fx-text-fill: #1a1a1a;" +
                                "-fx-line-spacing: 5px;"
                );
                textArea.getChildren().add(bodyText);
            }
        }

        body.getChildren().addAll(warningIcon, textArea);
        dialog.getChildren().add(body);

        // Bottom bar (Windows-style button row)
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(8, 12, 10, 12));
        buttonRow.setStyle("-fx-border-color: #cccccc transparent transparent transparent; -fx-background-color: #f0f0f0;");

        Label okBtn = new Label("  OK  ");
        okBtn.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-background-color: #e1e1e1;" +
                        "-fx-border-color: #aaaaaa;" +
                        "-fx-border-radius: 3;" +
                        "-fx-background-radius: 3;" +
                        "-fx-padding: 4 18 4 18;"
        );
        buttonRow.getChildren().add(okBtn);
        dialog.getChildren().add(buttonRow);

        return dialog;
    }

    // ─── Chat Widget ─────────────────────────────────────────────────────────────

    /**
     * Renders a social-media / messenger-style chat bubble:
     *
     *   Alex
     *   ╭──────────────────────────────────╮
     *   │  Is this you in this photo? …   │
     *   ╰──────────────────────────────────╯
     */
    private VBox buildChatWidget(List<ScenarioElement> parts) {
        VBox wrapper = new VBox(4);
        wrapper.setMaxWidth(560);
        wrapper.setAlignment(Pos.TOP_LEFT);
        wrapper.setPadding(new Insets(8, 0, 4, 0));

        String senderName = parts.stream()
                .filter(e -> e.getElementType().equals("CHAT_SENDER"))
                .map(ScenarioElement::getValue)
                .findFirst().orElse("Friend");

        // Avatar row
        HBox avatarRow = new HBox(10);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        Region avatar = new Region();
        avatar.setPrefSize(36, 36);
        avatar.setMinSize(36, 36);
        avatar.setStyle(
                "-fx-background-color: #5865f2;" +
                        "-fx-background-radius: 18;"
        );

        Label avatarInitial = new Label(senderName.substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Stack avatar letter on top of the circle using an HBox trick
        HBox avatarBox = new HBox(avatar);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setMinWidth(36);
        avatarBox.setMaxWidth(36);

        // We'll overlay the letter using a StackPane-like approach with padding
        VBox avatarStack = new VBox();
        avatarStack.setPrefSize(36, 36);
        avatarStack.setAlignment(Pos.CENTER);
        avatarStack.setStyle("-fx-background-color: #5865f2; -fx-background-radius: 18;");
        avatarStack.getChildren().add(avatarInitial);

        Label nameLabel = new Label(senderName);
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");

        Label timeLabel = new Label("just now");
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");

        avatarRow.getChildren().addAll(avatarStack, nameLabel, timeLabel);
        wrapper.getChildren().add(avatarRow);

        // Message bubbles
        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("CHAT_MESSAGE")) {
                HBox bubbleRow = new HBox();
                bubbleRow.setPadding(new Insets(0, 0, 0, 46)); // indent under avatar

                VBox bubble = new VBox();
                bubble.setPadding(new Insets(10, 14, 10, 14));
                bubble.setMaxWidth(440);
                bubble.setStyle(
                        "-fx-background-color: #f0f0f3;" +
                                "-fx-background-radius: 4 18 18 18;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 4, 0, 0, 1);"
                );

                Label msg = new Label(el.getValue());
                msg.setWrapText(true);
                msg.setMaxWidth(420);
                msg.setStyle(
                        "-fx-font-size: 17px;" +
                                "-fx-text-fill: #1a1a1a;" +
                                "-fx-line-spacing: 4px;"
                );
                bubble.getChildren().add(msg);
                bubbleRow.getChildren().add(bubble);
                wrapper.getChildren().add(bubbleRow);
            }
        }

        return wrapper;
    }

    // ─── Warning / Info Boxes ────────────────────────────────────────────────────

    private VBox buildWarningBox(ScenarioElement el) {
        return buildAccentBox(el, "#fff3cd", "#f59e0b", "⚠", "#92400e");
    }

    private VBox buildInfoBox(ScenarioElement el) {
        return buildAccentBox(el, "#dbeafe", "#3b82f6", "ℹ", "#1e3a5f");
    }

    private VBox buildAccentBox(ScenarioElement el, String bg, String accentColor, String iconChar, String textColor) {
        HBox outer = new HBox(0);
        outer.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );

        // Left accent stripe
        Region stripe = new Region();
        stripe.setPrefWidth(5);
        stripe.setMinWidth(5);
        stripe.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 6 0 0 6;");

        VBox content = new VBox(6);
        content.setPadding(new Insets(14, 18, 14, 14));
        HBox.setHgrow(content, Priority.ALWAYS);

        if (el.getLabel() != null && !el.getLabel().isBlank()) {
            HBox heading = new HBox(8);
            heading.setAlignment(Pos.CENTER_LEFT);

            Label icon = new Label(iconChar);
            icon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + accentColor + ";");

            Label title = new Label(el.getLabel());
            title.setWrapText(true);
            title.setStyle(
                    "-fx-font-size: 17px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: " + textColor + ";"
            );
            heading.getChildren().addAll(icon, title);
            content.getChildren().add(heading);
        }

        Label body = new Label(el.getValue());
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-line-spacing: 4px;"
        );
        content.getChildren().add(body);

        outer.getChildren().addAll(stripe, content);

        // Wrap in a VBox so the return type is consistent
        VBox wrap = new VBox(outer);
        return wrap;
    }

    // ─── Link Label ──────────────────────────────────────────────────────────────

    private Label buildLinkLabel(ScenarioElement el) {
        Label label = new Label("🔗  " + el.getValue());
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-text-fill: #1a5fb4;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;"
        );
        return label;
    }

    // ─── Options ─────────────────────────────────────────────────────────────────

    private void renderOptions(List<ScenarioOption> options) {
        for (ScenarioOption option : options) {
            Button button = new Button(option.getOptionText());
            button.setMaxWidth(Double.MAX_VALUE);
            button.setPrefHeight(44);
            button.setStyle("-fx-font-size: 18px;");
            VBox.setVgrow(button, Priority.NEVER);
            button.setOnAction(event -> handleOptionSelected(option));
            optionsContainer.getChildren().add(button);
        }
    }

    private void handleOptionSelected(ScenarioOption option) {
        Integer nextStageOrder = option.getNextStageOrder();
        if (nextStageOrder == null) { showErrorState("This option is missing a next stage."); return; }
        loadStage(nextStageOrder);
    }

    // ─── Terminal Stage ──────────────────────────────────────────────────────────

    private void showTerminalStage() {
        titleLabel.setText(currentScenario.getTitle());
        promptLabel.setText(currentStage.getFeedbackTitle());
        progressLabel.setText("Scenario " + currentScenario.getScenarioOrder() + " of 4");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        Label feedbackLabel = makeBodyLabel(currentStage.getFeedbackText());
        feedbackLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-text-fill: #2b2b2b;" +
                        "-fx-line-spacing: 6px;"
        );
        contentContainer.getChildren().add(feedbackLabel);

        Button continueButton = new Button("Continue");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setPrefHeight(44);
        continueButton.setStyle("-fx-font-size: 18px;");
        continueButton.setOnAction(event -> handleContinueAfterTerminal());
        optionsContainer.getChildren().add(continueButton);
    }

    private void handleContinueAfterTerminal() {
        if (currentScenarioOrder < 4) {
            currentScenarioOrder++;
            loadScenario(currentScenarioOrder);
        } else {
            titleLabel.setText("Scenarios Complete");
            promptLabel.setText("Next step: bonus screen or quiz transition.");
            progressLabel.setText("Complete");
            contentContainer.getChildren().clear();
            optionsContainer.getChildren().clear();
        }
    }

    // ─── Error State ─────────────────────────────────────────────────────────────

    private void showErrorState(String message) {
        titleLabel.setText("Scenario Error");
        promptLabel.setText(message);
        progressLabel.setText("");
        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();
    }

    // ─── Shared Helpers ──────────────────────────────────────────────────────────

    private Label makeBodyLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.TOP_LEFT);
        label.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #2b2b2b;" +
                        "-fx-line-spacing: 6px;"
        );
        return label;
    }
}
