package com.example.ui;

import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;

import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.function.Consumer;
import java.util.List;

/**
 * Factory-style UI builder responsible for constructing reusable JavaFX
 * widgets used in the scenario screen.
 *
 * <p>This class does not store scenario progress or application state. It only
 * holds callback references that are attached to generated controls.</p>
 *
 * <p>It does not access the database, manage scenario flow, or perform screen
 * navigation.</p>
 */
public class ScenarioWidgetFactory {

    private final Consumer<ScenarioOption> onOptionSelected;
    private final Runnable onBonusSubmit;
    private final Runnable onBonusSkip;

    /**
     * Creates a scenario widget factory.
     *
     * @param onOptionSelected callback triggered when a scenario option is selected
     * @param onBonusSubmit callback triggered when the bonus form is submitted successfully
     * @param onBonusSkip callback triggered when the bonus form is skipped
     */
    public ScenarioWidgetFactory(
            Consumer<ScenarioOption> onOptionSelected,
            Runnable onBonusSubmit,
            Runnable onBonusSkip) {
        this.onOptionSelected = onOptionSelected;
        this.onBonusSubmit = onBonusSubmit;
        this.onBonusSkip = onBonusSkip;
    }

    /**
     * Builds an email-style widget from grouped email scenario elements.
     *
     * @param parts elements containing email sender, subject, and body content
     * @return email-style JavaFX card
     */
    public VBox buildEmailWidget(List<ScenarioElement> parts) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);"
        );

        // Title bar
        HBox titleBar = new HBox(8);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(10, 16, 10, 16));
        titleBar.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: transparent transparent #d0d0d0 transparent;"
        );
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

        // Header rows
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

        // Body
        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("EMAIL_BODY")) {
                Label body = new Label(el.getValue());
                body.setWrapText(true);
                body.setMaxWidth(Double.MAX_VALUE);
                body.setPadding(new Insets(18, 24, 22, 24));
                body.setStyle(
                        "-fx-font-size: 17px;" +
                                "-fx-text-fill: #1a1a1a;" +
                                "-fx-line-spacing: 5px;"
                );
                card.getChildren().add(body);
            }
        }
        return card;
    }

    /**
     * Builds a mobile SMS-style widget.
     *
     * @param parts elements containing SMS sender and message content
     * @return SMS-style JavaFX card
     */
    public VBox buildSmsWidget(List<ScenarioElement> parts) {
        VBox phone = new VBox();
        phone.setMaxWidth(520);
        phone.setStyle(
                "-fx-background-color: #1c1c1e;" +
                        "-fx-border-color: #3a3a3c;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 14, 0, 0, 4);"
        );

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

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label time = new Label("now");
        time.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e8e93;");

        Region gap = new Region();
        gap.setMinWidth(10);
        statusBar.getChildren().addAll(backArrow, gap, senderLabel, sp, time);
        phone.getChildren().add(statusBar);

        VBox bubbleArea = new VBox(10);
        bubbleArea.setPadding(new Insets(10, 16, 20, 16));
        bubbleArea.setAlignment(Pos.CENTER_LEFT);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("SMS_MESSAGE")) {
                VBox bubble = new VBox();
                bubble.setMaxWidth(380);
                bubble.setPadding(new Insets(10, 14, 10, 14));
                bubble.setStyle("-fx-background-color: #2c2c2e; -fx-background-radius: 18 18 18 4;");
                Label msg = new Label(el.getValue());
                msg.setWrapText(true);
                msg.setMaxWidth(360);
                msg.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-line-spacing: 4px;");
                bubble.getChildren().add(msg);
                bubbleArea.getChildren().add(bubble);
            }
        }
        phone.getChildren().add(bubbleArea);
        return phone;
    }

    /**
     * Builds a fake security popup window widget.
     *
     * @param parts elements containing popup title and body text
     * @return popup-style JavaFX card
     */
    public VBox buildPopupWidget(List<ScenarioElement> parts) {
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

        HBox titleBar = new HBox(8);
        titleBar.setPadding(new Insets(6, 10, 6, 10));
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: #c0392b; -fx-background-radius: 3 3 0 0;");
        Label icon = new Label("🛡");
        icon.setStyle("-fx-font-size: 14px;");
        Label winTitle = new Label("Windows Security Alert");
        winTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        titleBar.getChildren().addAll(icon, winTitle, sp);
        for (String sym : new String[]{"—", "□", "✕"}) {
            Label btn = new Label(sym);
            btn.setStyle("-fx-font-size: 12px; -fx-text-fill: #ffffff; -fx-padding: 2 7 2 7;");
            titleBar.getChildren().add(btn);
        }
        dialog.getChildren().add(titleBar);

        HBox body = new HBox(16);
        body.setPadding(new Insets(20));
        body.setAlignment(Pos.TOP_LEFT);
        Label warningIcon = new Label("⚠");
        warningIcon.setStyle("-fx-font-size: 48px; -fx-text-fill: #e67e22;");
        VBox textArea = new VBox(10);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("POPUP_TITLE")) {
                Label t = new Label(el.getValue());
                t.setWrapText(true);
                t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #b00020;");
                textArea.getChildren().add(t);
            } else if (el.getElementType().equals("POPUP_BODY")) {
                Label b = new Label(el.getValue());
                b.setWrapText(true);
                b.setStyle("-fx-font-size: 16px; -fx-text-fill: #1a1a1a; -fx-line-spacing: 5px;");
                textArea.getChildren().add(b);
            }
        }

        body.getChildren().addAll(warningIcon, textArea);
        dialog.getChildren().add(body);

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(8, 12, 10, 12));
        buttonRow.setStyle("-fx-border-color: #cccccc transparent transparent transparent; -fx-background-color: #f0f0f0;");
        Label okBtn = new Label("  OK  ");
        okBtn.setStyle("-fx-font-size: 13px; -fx-background-color: #e1e1e1; -fx-border-color: #aaaaaa; -fx-border-radius: 3; -fx-background-radius: 3; -fx-padding: 4 18 4 18;");
        buttonRow.getChildren().add(okBtn);
        dialog.getChildren().add(buttonRow);

        return dialog;
    }

    /**
     * Builds a chat-message style widget.
     *
     * @param parts elements containing chat sender and message content
     * @return chat-style JavaFX widget
     */
    public VBox buildChatWidget(List<ScenarioElement> parts) {
        VBox wrapper = new VBox(4);
        wrapper.setMaxWidth(560);
        wrapper.setAlignment(Pos.TOP_LEFT);
        wrapper.setPadding(new Insets(8, 0, 4, 0));

        String senderName = parts.stream()
                .filter(e -> e.getElementType().equals("CHAT_SENDER"))
                .map(ScenarioElement::getValue)
                .findFirst().orElse("Friend");

        HBox avatarRow = new HBox(10);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        VBox avatarStack = new VBox();
        avatarStack.setPrefSize(36, 36);
        avatarStack.setMinSize(36, 36);
        avatarStack.setMaxSize(36, 36);
        avatarStack.setAlignment(Pos.CENTER);
        avatarStack.setStyle("-fx-background-color: #5865f2; -fx-background-radius: 18;");
        Label avatarInitial = new Label(senderName.substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        avatarStack.getChildren().add(avatarInitial);

        Label nameLabel = new Label(senderName);
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");
        Label timeLabel = new Label("just now");
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        avatarRow.getChildren().addAll(avatarStack, nameLabel, timeLabel);
        wrapper.getChildren().add(avatarRow);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("CHAT_MESSAGE")) {
                HBox bubbleRow = new HBox();
                bubbleRow.setPadding(new Insets(0, 0, 0, 46));
                VBox bubble = new VBox();
                bubble.setPadding(new Insets(10, 14, 10, 14));
                bubble.setMaxWidth(440);
                bubble.setStyle(
                        "-fx-background-color: #2d3748;" +
                                "-fx-background-radius: 4 18 18 18;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 4, 0, 0, 1);"
                );
                Label msg = new Label(el.getValue());
                msg.setWrapText(true);
                msg.setMaxWidth(420);
                msg.setStyle("-fx-font-size: 17px; -fx-text-fill: #e2e8f0; -fx-line-spacing: 4px;");
                bubble.getChildren().add(msg);
                bubbleRow.getChildren().add(bubble);
                wrapper.getChildren().add(bubbleRow);
            }
        }
        return wrapper;
    }

    /**
     * Builds a warning-style accent box.
     *
     * @param el scenario element containing label and body text
     * @return styled warning box
     */
    public VBox buildWarningBox(ScenarioElement el) {
        return buildAccentBox(el, "#1a1200", "#f59e0b", "⚠", "#fbbf24", "#fde68a");
    }

    /**
     * Builds an information-style accent box.
     *
     * @param el scenario element containing label and body text
     * @return styled information box
     */
    public VBox buildInfoBox(ScenarioElement el) {
        return buildAccentBox(el, "#0c1a2e", "#3b82f6", "ℹ", "#60a5fa", "#bfdbfe");
    }

    /**
     * Shared helper for building coloured accent boxes.
     */
    private VBox buildAccentBox(ScenarioElement el, String bg, String stripeColor,
                                String iconChar, String iconColor, String textColor) {
        HBox outer = new HBox(0);
        outer.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-border-color: " + stripeColor + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        Region stripe = new Region();
        stripe.setPrefWidth(5);
        stripe.setMinWidth(5);
        stripe.setStyle("-fx-background-color: " + stripeColor + "; -fx-background-radius: 8 0 0 8;");

        VBox content = new VBox(8);
        content.setPadding(new Insets(14, 18, 14, 14));
        HBox.setHgrow(content, Priority.ALWAYS);

        if (el.getLabel() != null && !el.getLabel().isBlank()) {
            HBox heading = new HBox(8);
            heading.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(iconChar);
            icon.setStyle("-fx-font-size: 17px; -fx-text-fill: " + iconColor + ";");
            Label title = new Label(el.getLabel());
            title.setWrapText(true);
            title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
            heading.getChildren().addAll(icon, title);
            content.getChildren().add(heading);
        }

        Label body = new Label(el.getValue());
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setStyle("-fx-font-size: 16px; -fx-text-fill: " + textColor + "; -fx-line-spacing: 4px;");
        content.getChildren().add(body);
        outer.getChildren().addAll(stripe, content);

        return new VBox(outer);
    }

    /**
     * Builds a hyperlink-style label for scenario links.
     *
     * @param el scenario element containing the displayed link text
     * @return styled link label
     */
    public Label buildLinkLabel(ScenarioElement el) {
        Label label = new Label("🔗  " + el.getValue());
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-text-fill: #60a5fa;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;"
        );
        return label;
    }

    /**
     * Renders selectable scenario options into the provided container.
     *
     * <p>Small option sets are displayed in a horizontal row. Larger option
     * sets are displayed vertically to preserve readability.</p>
     *
     * @param container container that receives the option buttons
     * @param options scenario options to render
     */
    public void renderOptionsInto(VBox container, List<ScenarioOption> options) {
        int count = options.size();
        // Use a horizontal layout for small option sets, vertical layout otherwise
        if (count >= 2 && count <= 3) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER);
            for (ScenarioOption option : options) {
                Button button = makeOptionButton(option.getOptionText());
                HBox.setHgrow(button, Priority.ALWAYS);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setOnAction(event -> onOptionSelected.accept(option));
                row.getChildren().add(button);
            }
            container.getChildren().add(row);
        } else {
            for (ScenarioOption option : options) {
                Button button = makeOptionButton(option.getOptionText());
                button.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(button, Priority.NEVER);
                button.setOnAction(event -> onOptionSelected.accept(option));
                container.getChildren().add(button);
            }
        }
    }

    /**
     * Builds a styled option button.
     */
    private Button makeOptionButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(48);
        final String normalStyle =
                "-fx-background-color: #1e293b;" +
                        "-fx-text-fill: #e2e8f0;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: CENTER-LEFT;" +
                        "-fx-padding: 0 20 0 20;";
        final String hoverStyle =
                "-fx-background-color: #334155;" +
                        "-fx-text-fill: #f1f5f9;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #3b82f6;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: CENTER-LEFT;" +
                        "-fx-padding: 0 20 0 20;";
        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        return button;
    }

    /**
     * Builds a result card shown after a terminal scenario outcome.
     *
     * @param accentColor colour used for the border and icon
     * @param bgColor card background colour
     * @param resultIcon result symbol, usually {@code ✓} or {@code ✕}
     * @param tagText short outcome label
     * @param title result heading
     * @param bodyText explanatory result text
     * @return styled result card
     */
    public VBox buildResultCard(String accentColor, String bgColor,
                                String resultIcon, String tagText,
                                String title, String bodyText) {
        VBox card = new VBox(16);
        card.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 28 32 28 32;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 14, 0, 0, 3);"
        );

        HBox badgeRow = new HBox(14);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(resultIcon);
        iconLabel.setMinSize(44, 44);
        iconLabel.setPrefSize(44, 44);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        "-fx-background-color: " + accentColor + "22;" +
                        "-fx-background-radius: 22;"
        );

        VBox badgeText = new VBox(3);
        Label tagLabel = new Label(tagText);
        tagLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        "-fx-letter-spacing: 1;"
        );
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #f1f5f9;"
        );
        badgeText.getChildren().addAll(tagLabel, titleLbl);
        badgeRow.getChildren().addAll(iconLabel, badgeText);
        card.getChildren().add(badgeRow);

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: " + accentColor + "44;");
        card.getChildren().add(divider);

        Label bodyLabel = new Label(bodyText);
        bodyLabel.setWrapText(true);
        bodyLabel.setMaxWidth(Double.MAX_VALUE);
        bodyLabel.setStyle(
                "-fx-font-size: 19px;" +
                        "-fx-text-fill: #cbd5e1;" +
                        "-fx-line-spacing: 6px;"
        );
        card.getChildren().add(bodyLabel);

        return card;
    }

    /**
     * Builds a styled action button used below result cards.
     *
     * @param text button text
     * @param accentColor button background colour
     * @return styled action button
     */
    public Button buildActionButton(String text, String accentColor) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(50);
        button.setStyle(
                "-fx-background-color: " + accentColor + ";" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    /**
     * Builds the temporary feedback panel shown after certain choices.
     *
     * <p>The Continue button is created here, but its action is assigned by
     * the caller because the controller owns scenario progression.</p>
     *
     * @param feedbackText feedback message to display
     * @return feedback toast containing message and Continue button
     */
    public VBox buildFeedbackToast(String feedbackText) {
        VBox toast = new VBox(10);
        toast.setStyle(
                "-fx-background-color: #0f2a1e;" +
                        "-fx-border-color: #22c55e;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 18 14 18;"
        );

        HBox toastHeader = new HBox(10);
        toastHeader.setAlignment(Pos.CENTER_LEFT);
        Label toastIcon = new Label("💡");
        toastIcon.setStyle("-fx-font-size: 18px;");
        Label toastTitle = new Label("Observation");
        toastTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #4ade80;");
        toastHeader.getChildren().addAll(toastIcon, toastTitle);

        Label toastBody = new Label(feedbackText);
        toastBody.setWrapText(true);
        toastBody.setMaxWidth(Double.MAX_VALUE);
        toastBody.setStyle("-fx-font-size: 15px; -fx-text-fill: #86efac; -fx-line-spacing: 3px;");

        // Note: the Continue button's action is set by the caller (ScenarioController),
        // because it needs to remove the toast from the live scene graph and trigger navigation.
        Button continueBtn = new Button("Continue →");
        continueBtn.setStyle(
                "-fx-background-color: #16a34a;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 18 8 18;"
        );
        toast.getChildren().addAll(toastHeader, toastBody, continueBtn);
        return toast;
    }

    /**
     * Builds the fake registration form used as the bonus phishing scenario.
     *
     * <p>The form intentionally looks like a routine account prompt. Submitting
     * the form represents falling for the trap, while choosing the secondary
     * skip link represents the safer action.</p>
     *
     * @return fake registration form widget
     */
    public VBox buildRegistrationFormWidget() {
        VBox card = new VBox();
        card.setMaxWidth(620);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);"
        );

        // Chrome bar
        HBox chrome = new HBox(8);
        chrome.setAlignment(Pos.CENTER_LEFT);
        chrome.setPadding(new Insets(10, 16, 10, 16));
        chrome.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: transparent transparent #d0d0d0 transparent;"
        );
        for (String colour : new String[]{"#ff5f57", "#febc2e", "#28c840"}) {
            Region dot = new Region();
            dot.setPrefSize(12, 12);
            dot.setMinSize(12, 12);
            dot.setStyle("-fx-background-color:" + colour + "; -fx-background-radius: 6;");
            chrome.getChildren().add(dot);
        }
        Region chromeSpacer = new Region();
        HBox.setHgrow(chromeSpacer, Priority.ALWAYS);
        chrome.getChildren().add(chromeSpacer);
        Label chromeLabel = new Label("🔒  Training Results Portal");
        chromeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555; -fx-font-weight: bold;");
        chrome.getChildren().add(chromeLabel);
        card.getChildren().add(chrome);

        // Form body
        VBox body = new VBox(16);
        body.setPadding(new Insets(28, 32, 28, 32));

        Label intro = new Label("Create a free account to save your progress and view your detailed results.");
        intro.setWrapText(true);
        intro.setMaxWidth(Double.MAX_VALUE);
        intro.setStyle("-fx-font-size: 15px; -fx-text-fill: #444444; -fx-line-spacing: 4px;");
        body.getChildren().add(intro);
        body.getChildren().add(new Separator());

        // Fields
        TextField nameField     = new TextField();
        TextField emailField    = new TextField();
        PasswordField passField = new PasswordField();

        Label nameError  = makeErrorLabel("Full name is required.");
        Label emailError = makeErrorLabel("Please enter a valid email address.");
        Label passError  = makeErrorLabel("Password must be at least 8 characters.");

        body.getChildren().add(new VBox(4, makeFormRow("Full Name", nameField), nameError));
        body.getChildren().add(new VBox(4, makeFormRow("Email address", emailField), emailError));
        body.getChildren().add(new VBox(4, makeFormRow("Password", passField), passError));

        Label terms = new Label("By creating an account you agree to our Terms of Service and Privacy Policy.");
        terms.setWrapText(true);
        terms.setMaxWidth(Double.MAX_VALUE);
        terms.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        body.getChildren().add(terms);

        // Submit button - always visible; validation fires on click
        Button submitBtn = new Button("Create Account & View Results");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setPrefHeight(44);
        submitBtn.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        java.util.regex.Pattern emailPattern =
                java.util.regex.Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

        submitBtn.setOnAction(e -> {
            boolean nameOk  = !nameField.getText().trim().isEmpty();
            boolean emailOk = emailPattern.matcher(emailField.getText().trim()).matches();
            boolean passOk  = passField.getText().length() >= 8;

            setError(nameError,  !nameOk);
            setError(emailError, !emailOk);
            setError(passError,  !passOk);

            if (nameOk && emailOk && passOk) {
                onBonusSubmit.run();
            }
        });

        // Hide validation errors as soon as the user fixes each field
        nameField.textProperty().addListener((obs, o, n) -> {
            if (nameError.isVisible() && !n.trim().isEmpty()) setError(nameError, false);
        });
        emailField.textProperty().addListener((obs, o, n) -> {
            if (emailError.isVisible() && emailPattern.matcher(n.trim()).matches()) setError(emailError, false);
        });
        passField.textProperty().addListener((obs, o, n) -> {
            if (passError.isVisible() && n.length() >= 8) setError(passError, false);
        });

        body.getChildren().add(submitBtn);

        // The safer action is intentionally styled as a secondary option
        Label skipLink = new Label("Skip for now");
        skipLink.setMaxWidth(Double.MAX_VALUE);
        skipLink.setAlignment(Pos.CENTER);
        skipLink.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #888888;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;"
        );
        skipLink.setOnMouseClicked(e -> onBonusSkip.run());
        body.getChildren().add(skipLink);

        card.getChildren().add(body);
        return card;
    }

    /**
     * Builds a standard body text label used for plain scenario content.
     *
     * @param text label text
     * @return styled body label
     */
    public Label makeBodyLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.TOP_LEFT);
        label.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #cbd5e1;" +
                        "-fx-line-spacing: 6px;"
        );
        return label;
    }

    /**
     * Builds a labelled input row for the bonus form.
     */
    private HBox makeFormRow(String labelText, javafx.scene.control.TextInputControl field) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(labelText);
        lbl.setMinWidth(130);
        lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");

        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 10 8 10;"
        );
        HBox.setHgrow(field, Priority.ALWAYS);

        row.getChildren().addAll(lbl, field);
        return row;
    }

    /**
     * Builds a hidden validation error label.
     */
    private Label makeErrorLabel(String message) {
        Label lbl = new Label(message);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc2626;");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    /**
     * Shows or hides a validation error label.
     *
     * <p>{@code managed} is updated with visibility so hidden errors do not
     * reserve space in the form layout.</p>
     */
    private void setError(Label errorLabel, boolean show) {
        errorLabel.setVisible(show);
        errorLabel.setManaged(show);
    }
}
