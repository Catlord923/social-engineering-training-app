package com.example.ui;

import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.function.Consumer;
import java.util.ArrayList;
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
     * Creates a scenario widget factory object.
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
        // White card with a light gray border and a subtle drop shadow to lift it off the dark background
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);"
        );

        // Title bar: gray strip across the top mimicking a macOS window chrome
        HBox titleBar = new HBox(8);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(10, 16, 10, 16));
        titleBar.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 8 8 0 0;" +
                        // only the bottom edge has a border to create a separator line below the bar
                        "-fx-border-color: transparent transparent #d0d0d0 transparent;"
        );
        // Traffic light dots in macOS red/yellow/green order
        for (String color : new String[]{"#ff5f57", "#febc2e", "#28c840"}) {
            Region dot = new Region();
            dot.setPrefSize(12, 12);
            dot.setMinSize(12, 12);
            dot.setMaxSize(12, 12); // prevents HBox from stretching the region, which would break the circle
            // background-radius: 6 makes a 12x12 region appear as a circle
            dot.setStyle("-fx-background-color:" + color + "; -fx-background-radius: 6;");
            titleBar.getChildren().add(dot);
        }
        // Spacer pushes the app label to the right edge
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().add(spacer);
        // App name label in the top-right corner, styled to look like a system menu title
        Label appLabel = new Label("✉  Mail");
        appLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666; -fx-font-weight: bold;");
        titleBar.getChildren().add(appLabel);
        card.getChildren().add(titleBar);

        // Header section: light gray background to distinguish metadata from body, like a real mail client
        VBox headers = new VBox(4);
        headers.setPadding(new Insets(12, 20, 12, 20));
        headers.setStyle("-fx-background-color: #f9f9f9;");
        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("EMAIL_FROM") || el.getElementType().equals("EMAIL_SUBJECT")) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                // Fixed-width bold label (e.g. "From:") keeps all fields visually aligned
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
        // Only add the header section if there are header elements; avoids an orphaned gray block
        if (!headers.getChildren().isEmpty()) {
            card.getChildren().add(headers);
            // Horizontal rule separating headers from the email body, matching the native mail client look
            card.getChildren().add(new Separator());
        }

        // Body area: padded white region containing the email text, inline links, and sign-off
        VBox bodyArea = new VBox(4);
        bodyArea.setPadding(new Insets(18, 24, 16, 24));

        for (ScenarioElement el : parts) {
            if (!el.getElementType().equals("EMAIL_BODY")) continue;

            // Strip each line individually to remove SQL formatting indentation from the seed data
            String[] rawLines = el.getValue().split("\n");
            List<String> lines = new ArrayList<>();
            for (String raw : rawLines) {
                lines.add(raw.strip());
            }

            // Detect the sign-off line so the link URL can be inserted above it
            int signOffLine = lines.size();
            for (int i = 0; i < lines.size(); i++) {
                String lower = lines.get(i).toLowerCase();
                if (lower.startsWith("regards") || lower.startsWith("sincerely")
                        || lower.startsWith("thanks") || lower.startsWith("best")) {
                    signOffLine = i;
                    break;
                }
            }

            // Render body lines before the sign-off; collapse consecutive blanks to a single small spacer
            boolean lastWasBlank = false;
            for (int i = 0; i < signOffLine; i++) {
                String line = lines.get(i);
                if (line.isEmpty()) {
                    if (!lastWasBlank) {
                        // Use a Region instead of an empty Label so it doesn't take up full line height
                        Region blankSpacer = new Region();
                        blankSpacer.setPrefHeight(6);
                        bodyArea.getChildren().add(blankSpacer);
                    }
                    lastWasBlank = true;
                } else {
                    Label l = new Label(line);
                    l.setWrapText(true);
                    l.setMaxWidth(Double.MAX_VALUE);
                    // Near-black text on white background, slightly smaller than the app's base font
                    l.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a1a1a; -fx-line-spacing: 4px;");
                    bodyArea.getChildren().add(l);
                    lastWasBlank = false;
                }
            }

            // Insert URL links between the body and the sign-off, as they appear in a real email
            for (ScenarioElement link : parts) {
                if (!link.getElementType().equals("LINK")) continue;
                if (!link.getValue().startsWith("http")) continue;
                // Small spacer above the link to separate it visually from the body text above
                Region preLinkSpacer = new Region();
                preLinkSpacer.setPrefHeight(4);
                bodyArea.getChildren().add(preLinkSpacer);
                // Blue underlined label styled to look like a clickable hyperlink in a mail client
                Label linkLabel = new Label(link.getValue());
                linkLabel.setWrapText(true);
                linkLabel.setMaxWidth(Double.MAX_VALUE);
                linkLabel.setStyle(
                        "-fx-font-size: 15px;" +
                                "-fx-text-fill: #1a56db;" +
                                "-fx-underline: true;"
                );
                bodyArea.getChildren().add(linkLabel);
            }

            // Render the sign-off block with a slightly larger gap above it to mirror real email spacing
            if (signOffLine < lines.size()) {
                Region signOffSpacer = new Region();
                signOffSpacer.setPrefHeight(8);
                bodyArea.getChildren().add(signOffSpacer);
            }
            for (int i = signOffLine; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) continue; // blank lines within the sign-off block are skipped
                Label l = new Label(line);
                l.setWrapText(true);
                l.setMaxWidth(Double.MAX_VALUE);
                l.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a1a1a; -fx-line-spacing: 4px;");
                bodyArea.getChildren().add(l);
            }
        }

        card.getChildren().add(bodyArea);

        // File attachment chips: rendered below the body for LINK elements whose value is a filename
        for (ScenarioElement el : parts) {
            if (!el.getElementType().equals("LINK")) continue;
            if (!el.getValue().startsWith("http")) {
                HBox attachmentRow = new HBox(8);
                attachmentRow.setPadding(new Insets(0, 20, 16, 24));
                attachmentRow.setAlignment(Pos.CENTER_LEFT);
                // Light gray pill badge with a border, mimicking a file attachment chip in a mail client
                HBox chip = new HBox(8);
                chip.setAlignment(Pos.CENTER_LEFT);
                chip.setPadding(new Insets(6, 12, 6, 10));
                chip.setStyle(
                        "-fx-background-color: #f0f0f0;" +
                                "-fx-border-color: #cccccc;" +
                                "-fx-border-radius: 6;" +
                                "-fx-background-radius: 6;"
                );
                Label icon = new Label("📎");
                icon.setStyle("-fx-font-size: 14px;");
                Label name = new Label(el.getValue());
                name.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                chip.getChildren().addAll(icon, name);
                attachmentRow.getChildren().add(chip);
                card.getChildren().add(attachmentRow);
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
        // Dark rounded card simulating the iOS Messages conversation view
        VBox phone = new VBox();
        phone.setMaxWidth(520);
        phone.setStyle(
                "-fx-background-color: #1c1c1e;" +
                        "-fx-border-color: #3a3a3c;" +
                        // large border-radius gives phone-screen-like appearance
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 14, 0, 0, 4);"
        );

        // Status bar: back arrow on the left, sender name in the centre, timestamp on the right
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(12, 20, 6, 20));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        // Same background as the card so it blends into the top of the widget
        statusBar.setStyle("-fx-background-color: #1c1c1e; -fx-background-radius: 20 20 0 0;");

        // iOS-style blue back arrow, non-interactive; purely decorative
        Label backArrow = new Label("←");
        backArrow.setStyle("-fx-font-size: 18px; -fx-text-fill: #0a84ff;");

        String senderName = parts.stream()
                .filter(e -> e.getElementType().equals("SMS_SENDER"))
                .map(ScenarioElement::getValue)
                .findFirst().orElse("Unknown");

        Label senderLabel = new Label(senderName);
        senderLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Spacer pushes the timestamp to the right edge
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        // Muted gray timestamp, matches iOS system color for secondary text
        Label time = new Label("now");
        time.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e8e93;");

        // Small fixed gap between the arrow and the sender name
        Region gap = new Region();
        gap.setMinWidth(10);
        statusBar.getChildren().addAll(backArrow, gap, senderLabel, sp, time);
        phone.getChildren().add(statusBar);

        VBox bubbleArea = new VBox(10);
        bubbleArea.setPadding(new Insets(10, 16, 20, 16));
        bubbleArea.setAlignment(Pos.CENTER_LEFT);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("SMS_MESSAGE")) {
                // Resolve any LINK element so it can be rendered inside the same chat bubble
                String linkValue = parts.stream()
                        .filter(e -> e.getElementType().equals("LINK"))
                        .map(ScenarioElement::getValue)
                        .findFirst().orElse(null);

                // Dark gray bubble with asymmetric radius: sharper bottom-left corner indicates an incoming message
                VBox bubble = new VBox(6);
                bubble.setMaxWidth(380);
                bubble.setPadding(new Insets(10, 14, 10, 14));
                bubble.setStyle("-fx-background-color: #2c2c2e; -fx-background-radius: 18 18 18 4;");

                Label msg = new Label(el.getValue());
                msg.setWrapText(true);
                msg.setMaxWidth(360);
                msg.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-line-spacing: 4px;");
                bubble.getChildren().add(msg);

                if (linkValue != null) {
                    // Blue underlined URL rendered inside the bubble, as it would appear in a real SMS thread
                    Label linkLabel = new Label(linkValue);
                    linkLabel.setWrapText(true);
                    linkLabel.setMaxWidth(360);
                    linkLabel.setStyle(
                            "-fx-font-size: 15px;" +
                                    "-fx-text-fill: #60a5fa;" +
                                    "-fx-underline: true;"
                    );
                    bubble.getChildren().add(linkLabel);
                }

                bubbleArea.getChildren().add(bubble);
            }
        }
        phone.getChildren().add(bubbleArea);
        return phone;
    }

    /**
     * Builds a fake browser window widget for baiting and web-based attack scenarios.
     *
     * <p>All visible text is driven from seed data elements:
     * {@code BROWSER_TITLE} sets the tab title,
     * {@code BROWSER_URL} sets the address bar,
     * {@code BROWSER_SITE_NAME} sets the page header,
     * {@code BROWSER_BADGE} sets the hero badge text,
     * {@code BROWSER_BODY} sets the main page content, and
     * {@code BROWSER_BUTTON} sets the fake CTA button label.</p>
     *
     * @param parts elements containing all browser content
     * @return browser-style JavaFX card
     */
    public VBox buildBrowserWidget(List<ScenarioElement> parts) {

        // Resolve all data-driven values from seed elements before building the UI
        String tabTitle  = parts.stream().filter(e -> e.getElementType().equals("BROWSER_TITLE"))
                .map(ScenarioElement::getValue).findFirst().orElse("Secure Page");
        String urlText   = parts.stream().filter(e -> e.getElementType().equals("BROWSER_URL"))
                .map(ScenarioElement::getValue).findFirst().orElse("");
        String siteName  = parts.stream().filter(e -> e.getElementType().equals("BROWSER_SITE_NAME"))
                .map(ScenarioElement::getValue).findFirst().orElse("");
        String badgeText = parts.stream().filter(e -> e.getElementType().equals("BROWSER_BADGE"))
                .map(ScenarioElement::getValue).findFirst().orElse("");
        String btnLabel  = parts.stream().filter(e -> e.getElementType().equals("BROWSER_BUTTON"))
                .map(ScenarioElement::getValue).findFirst().orElse("Download");

        // White card with a rounded border and drop shadow to look like a floating browser window
        VBox browser = new VBox();
        browser.setMaxWidth(Double.MAX_VALUE);
        browser.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #c0c0c0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 16, 0, 0, 4);"
        );

        // Title bar: traffic lights on the left, active tab on the right of them
        HBox titleBar = new HBox(8);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        // No bottom padding; the tab sits flush against the toolbar below
        titleBar.setPadding(new Insets(10, 16, 0, 16));
        titleBar.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 10 10 0 0;"
        );
        // macOS traffic light dots: close (red), minimize (yellow), full-screen (green)
        for (String color : new String[]{"#ff5f57", "#febc2e", "#28c840"}) {
            Region dot = new Region();
            dot.setPrefSize(12, 12);
            dot.setMinSize(12, 12);
            dot.setMaxSize(12, 12);
            dot.setStyle("-fx-background-color:" + color + "; -fx-background-radius: 6;");
            titleBar.getChildren().add(dot);
        }
        // Small fixed gap between the traffic lights and the active tab
        Region titleSpacer = new Region();
        titleSpacer.setPrefWidth(12);
        titleBar.getChildren().add(titleSpacer);

        // Active tab: white pill with rounded top corners, sitting above the toolbar
        HBox tab = new HBox(6);
        tab.setAlignment(Pos.CENTER_LEFT);
        tab.setPadding(new Insets(6, 14, 0, 14));
        // White stands out from the gray bar, creating the visual of an active foreground tab
        tab.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 6 6 0 0;");
        Label tabIcon = new Label("🔒");
        tabIcon.setStyle("-fx-font-size: 11px;");
        Label tabTitleLabel = new Label(tabTitle);
        tabTitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        tab.getChildren().addAll(tabIcon, tabTitleLabel);
        titleBar.getChildren().add(tab);
        // Expanding region fills remaining space to the right of the tab with the gray bar color
        Region titleFill = new Region();
        HBox.setHgrow(titleFill, Priority.ALWAYS);
        titleBar.getChildren().add(titleFill);
        browser.getChildren().add(titleBar);

        // Toolbar: back/forward/reload buttons followed by the address bar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(8, 14, 8, 14));
        toolbar.setStyle("-fx-background-color: #e8e8e8;");
        // Non-interactive navigation labels; purely decorative
        for (String sym : new String[]{"←", "→", "↻"}) {
            Label btn = new Label(sym);
            btn.setStyle("-fx-font-size: 15px; -fx-text-fill: #666666; -fx-padding: 2 6 2 6;");
            toolbar.getChildren().add(btn);
        }
        // Pill-shaped address bar grows to fill available width
        HBox addressBar = new HBox(6);
        addressBar.setAlignment(Pos.CENTER_LEFT);
        addressBar.setPadding(new Insets(5, 10, 5, 10));
        addressBar.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #bbbbbb;" +
                        // large radius gives the rounded appearance
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;"
        );
        HBox.setHgrow(addressBar, Priority.ALWAYS);
        // Green lock icon signals HTTPS
        Label lockIcon = new Label("🔒");
        lockIcon.setStyle("-fx-font-size: 11px; -fx-text-fill: #4caf50;");
        Label urlLabel = new Label(urlText);
        urlLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
        addressBar.getChildren().addAll(lockIcon, urlLabel);
        toolbar.getChildren().add(addressBar);
        // Extensions menu dots; non-interactive
        Label extDots = new Label("⋮");
        extDots.setStyle("-fx-font-size: 18px; -fx-text-fill: #666666; -fx-padding: 0 4 0 4;");
        toolbar.getChildren().add(extDots);
        browser.getChildren().add(toolbar);

        // Page content area; one VBox per BROWSER_BODY element
        for (ScenarioElement el : parts) {
            if (!el.getElementType().equals("BROWSER_BODY")) continue;

            // White padded page body, rounded only at the bottom to close the browser card
            VBox page = new VBox(18);
            page.setPadding(new Insets(32, 40, 36, 40));
            page.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 0 0 10 10;");

            // Site header: blue nav bar shown only when BROWSER_SITE_NAME is provided
            if (!siteName.isBlank()) {
                HBox siteHeader = new HBox();
                siteHeader.setAlignment(Pos.CENTER_LEFT);
                siteHeader.setPadding(new Insets(10, 16, 10, 16));
                // Branded blue background mimicking real website header designs
                siteHeader.setStyle("-fx-background-color: #1a56db; -fx-background-radius: 6;");
                Label siteNameLabel = new Label(siteName);
                siteNameLabel.setStyle(
                        "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;"
                );
                // Spacer pushes the nav links to the right edge
                Region headerSpacer = new Region();
                HBox.setHgrow(headerSpacer, Priority.ALWAYS);
                // Static nav links; non-interactive
                Label navLinks = new Label("Home   Deals   Support   Login");
                navLinks.setStyle("-fx-font-size: 13px; -fx-text-fill: #c7d9ff;");
                siteHeader.getChildren().addAll(siteNameLabel, headerSpacer, navLinks);
                page.getChildren().add(siteHeader);
            }

            // Hero badge: amber pill shown only when BROWSER_BADGE is provided
            if (!badgeText.isBlank()) {
                // Outer HBox prevents the pill from stretching to fill the full page width
                HBox badgeRow = new HBox();
                badgeRow.setAlignment(Pos.CENTER_LEFT);
                HBox heroBadge = new HBox(10);
                heroBadge.setAlignment(Pos.CENTER_LEFT);
                heroBadge.setPadding(new Insets(6, 16, 6, 16));
                // Warm amber background with a matching border
                heroBadge.setStyle(
                        "-fx-background-color: #fef3c7;" +
                                "-fx-border-color: #f59e0b;" +
                                // border-radius: 20 gives the full pill shape
                                "-fx-border-radius: 20;" +
                                "-fx-background-radius: 20;"
                );
                Label badge = new Label(badgeText);
                badge.setStyle(
                        // Dark amber text on the light amber background for sufficient contrast
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #92400e;"
                );
                heroBadge.getChildren().add(badge);
                badgeRow.getChildren().add(heroBadge);
                page.getChildren().add(badgeRow);
            }

            // Body text: parsed line by line; lines starting with "File:" become download chips
            VBox bodySection = new VBox(10);
            StringBuilder bodyAccum = new StringBuilder();

            for (String raw : el.getValue().split("\n")) {
                String line = raw.strip();
                if (line.toLowerCase().startsWith("file:")) {
                    // Render any accumulated body text as a label before inserting the file chip,
                    // so text above the 'File:' line appears above it in the layout
                    if (bodyAccum.length() > 0) {
                        Label bl = new Label(bodyAccum.toString().strip());
                        bl.setWrapText(true);
                        bl.setMaxWidth(Double.MAX_VALUE);
                        bl.setStyle("-fx-font-size: 16px; -fx-text-fill: #1f2937; -fx-line-spacing: 6px;");
                        bodySection.getChildren().add(bl);
                        bodyAccum.setLength(0);
                    }
                    // File download chip: gray card with icon, filename, and static subtitle
                    // USE_PREF_SIZE prevents the chip from growing to fill the page width
                    String fileName = line.substring(5).strip();
                    HBox fileChip = new HBox(10);
                    fileChip.setAlignment(Pos.CENTER_LEFT);
                    fileChip.setPadding(new Insets(10, 16, 10, 16));
                    fileChip.setMaxWidth(Region.USE_PREF_SIZE);
                    fileChip.setStyle(
                            "-fx-background-color: #f3f4f6;" +
                                    "-fx-border-color: #d1d5db;" +
                                    "-fx-border-radius: 8;" +
                                    "-fx-background-radius: 8;"
                    );
                    Label fileIcon = new Label("📦");
                    fileIcon.setStyle("-fx-font-size: 20px;");
                    VBox fileInfo = new VBox(2);
                    Label fileNameLabel = new Label(fileName);
                    fileNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");
                    // Static subtitle; always "Windows Installer Package" for .exe baiting scenarios
                    Label fileType = new Label("Windows Installer Package");
                    fileType.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
                    fileInfo.getChildren().addAll(fileNameLabel, fileType);
                    fileChip.getChildren().addAll(fileIcon, fileInfo);
                    bodySection.getChildren().add(fileChip);
                } else {
                    // Accumulate regular text lines for batch rendering as a single Label
                    if (bodyAccum.length() > 0 || !line.isEmpty()) {
                        if (bodyAccum.length() > 0) bodyAccum.append("\n");
                        bodyAccum.append(line);
                    }
                }
            }
            // Render any remaining accumulated text after the loop
            if (bodyAccum.length() > 0) {
                Label bl = new Label(bodyAccum.toString().strip());
                bl.setWrapText(true);
                bl.setMaxWidth(Double.MAX_VALUE);
                bl.setStyle("-fx-font-size: 16px; -fx-text-fill: #1f2937; -fx-line-spacing: 6px;");
                bodySection.getChildren().add(bl);
            }
            page.getChildren().add(bodySection);

            // CTA button: green, disabled, centered at a fixed width to avoid spanning the full page
            HBox buttonRow = new HBox();
            buttonRow.setAlignment(Pos.CENTER);
            Button fakeDownload = new Button("⬇   " + btnLabel);
            fakeDownload.setDisable(true);
            fakeDownload.setPrefWidth(320);
            fakeDownload.setPrefHeight(44);
            fakeDownload.setStyle(
                    "-fx-background-color: #16a34a;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 6;" +
                            // opacity: 1 overrides JavaFX's default dimming of disabled buttons
                            "-fx-opacity: 1;"
            );
            buttonRow.getChildren().add(fakeDownload);

            // Trust badges: centered below the button
            HBox trustRow = new HBox(20);
            trustRow.setAlignment(Pos.CENTER);
            trustRow.setPadding(new Insets(4, 0, 0, 0));
            for (String badge : new String[]{"✔  Virus-free guaranteed", "✔  No credit card required", "✔  Students only"}) {
                Label b = new Label(badge);
                // Muted gray; subordinate to the button above
                b.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
                trustRow.getChildren().add(b);
            }

            page.getChildren().addAll(buttonRow, trustRow);
            browser.getChildren().add(page);
        }

        return browser;
    }

    /**
     * Builds a fake security popup window widget.
     *
     * @param parts elements containing popup title and body text
     * @return popup-style JavaFX card
     */
    public VBox buildPopupWidget(List<ScenarioElement> parts) {
        // Light gray dialog body with a thin border and a heavier shadow than other widgets,
        // imitating the raised appearance of a Windows modal dialog
        VBox dialog = new VBox();
        dialog.setMaxWidth(680);
        dialog.setStyle(
                "-fx-background-color: #f0f0f0;" +
                        "-fx-border-color: #888888;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        // offset y: 5 gives a more pronounced bottom shadow, typical of floating dialogs
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 18, 0, 3, 5);"
        );

        // Red title bar mimicking a Windows security/error dialog chrome
        HBox titleBar = new HBox(8);
        titleBar.setPadding(new Insets(6, 10, 6, 10));
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: #c0392b; -fx-background-radius: 3 3 0 0;");
        Label icon = new Label("🛡");
        icon.setStyle("-fx-font-size: 14px;");
        Label winTitle = new Label("Windows Security Alert");
        winTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        // Spacer pushes the window control buttons to the right
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        titleBar.getChildren().addAll(icon, winTitle, sp);
        // Fake minimize/maximise/close buttons; non-interactive
        for (String sym : new String[]{"—", "□", "✕"}) {
            Label btn = new Label(sym);
            btn.setStyle("-fx-font-size: 12px; -fx-text-fill: #ffffff; -fx-padding: 2 7 2 7;");
            titleBar.getChildren().add(btn);
        }
        dialog.getChildren().add(titleBar);

        // Dialog body: large warning icon on the left, title and message text on the right
        HBox body = new HBox(16);
        body.setPadding(new Insets(20));
        body.setAlignment(Pos.TOP_LEFT);
        // Large amber warning triangle
        Label warningIcon = new Label("⚠");
        warningIcon.setStyle("-fx-font-size: 48px; -fx-text-fill: #e67e22;");
        VBox textArea = new VBox(10);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("POPUP_TITLE")) {
                // Bold red alert title to match the Windows security dialog pattern
                Label t = new Label(el.getValue().strip());
                t.setWrapText(true);
                t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #b00020;");
                textArea.getChildren().add(t);
            } else if (el.getElementType().equals("POPUP_BODY")) {
                // Strip each line to remove SQL formatting indentation before rendering
                String[] rawLines = el.getValue().split("\n");
                StringBuilder cleaned = new StringBuilder();
                for (String line : rawLines) {
                    if (cleaned.length() > 0) cleaned.append("\n");
                    cleaned.append(line.strip());
                }
                // Near-black body text on the light gray background for readability
                Label b = new Label(cleaned.toString());
                b.setWrapText(true);
                b.setStyle("-fx-font-size: 16px; -fx-text-fill: #1a1a1a; -fx-line-spacing: 5px;");
                textArea.getChildren().add(b);
            }
        }

        body.getChildren().addAll(warningIcon, textArea);
        dialog.getChildren().add(body);

        // Footer bar with a fake OK button; disabled and non-interactive
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(8, 12, 10, 12));
        // Top border creates a dividing line between the body and the footer, as in a real dialog
        buttonRow.setStyle("-fx-border-color: #cccccc transparent transparent transparent; -fx-background-color: #f0f0f0;");
        Label okBtn = new Label("  OK  ");
        // Styled as a depressed Windows button; light gray with a thin border
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

        // Avatar row: circular initial badge + sender name + timestamp
        HBox avatarRow = new HBox(10);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        // Fixed-size square with a circular background; the initial letter acts as the avatar
        VBox avatarStack = new VBox();
        avatarStack.setPrefSize(36, 36);
        avatarStack.setMinSize(36, 36);
        avatarStack.setMaxSize(36, 36);
        avatarStack.setAlignment(Pos.CENTER);
        // Discord-like indigo/purple avatar color
        avatarStack.setStyle("-fx-background-color: #5865f2; -fx-background-radius: 18;");
        // First character of the sender name, uppercased, as the avatar initial
        Label avatarInitial = new Label(senderName.substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        avatarStack.getChildren().add(avatarInitial);

        Label nameLabel = new Label(senderName);
        // Light near-white text for the sender name against the app's dark background
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");
        Label timeLabel = new Label("just now");
        // Muted blue-gray timestamp, subordinate to the sender name
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        avatarRow.getChildren().addAll(avatarStack, nameLabel, timeLabel);
        wrapper.getChildren().add(avatarRow);

        for (ScenarioElement el : parts) {
            if (el.getElementType().equals("CHAT_MESSAGE")) {
                // Resolve any LINK element to render inside the bubble
                String linkValue = parts.stream()
                        .filter(e -> e.getElementType().equals("LINK"))
                        .map(ScenarioElement::getValue)
                        .findFirst().orElse(null);

                // Indent the bubble to sit below and to the right of the avatar
                HBox bubbleRow = new HBox();
                bubbleRow.setPadding(new Insets(0, 0, 0, 46));
                // Dark bubble with asymmetric radius: sharp top-left corner aligns with the avatar above
                VBox bubble = new VBox(6);
                bubble.setPadding(new Insets(10, 14, 10, 14));
                bubble.setMaxWidth(440);
                bubble.setStyle(
                        "-fx-background-color: #2d3748;" +
                                // top-left corner is square (4px) to visually connect to the sender row
                                "-fx-background-radius: 4 18 18 18;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 4, 0, 0, 1);"
                );
                Label msg = new Label(el.getValue());
                msg.setWrapText(true);
                msg.setMaxWidth(420);
                // Soft off-white text on the dark bubble background
                msg.setStyle("-fx-font-size: 17px; -fx-text-fill: #e2e8f0; -fx-line-spacing: 4px;");
                bubble.getChildren().add(msg);

                if (linkValue != null) {
                    // Blue underlined URL rendered inside the bubble on its own line
                    Label linkLabel = new Label(linkValue);
                    linkLabel.setWrapText(true);
                    linkLabel.setMaxWidth(420);
                    linkLabel.setStyle(
                            "-fx-font-size: 15px;" +
                                    "-fx-text-fill: #60a5fa;" +
                                    "-fx-underline: true;"
                    );
                    bubble.getChildren().add(linkLabel);
                }

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
     * Shared helper for building colored accent boxes.
     */
    private VBox buildAccentBox(ScenarioElement el, String bg, String stripeColor,
                                String iconChar, String iconColor, String textColor) {
        // Outer HBox holds the colored stripe and the content side by side
        HBox outer = new HBox(0);
        outer.setStyle(
                "-fx-background-color: " + bg + ";" +
                        // Border uses the accent color on all sides to create a colored outline
                        "-fx-border-color: " + stripeColor + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // Narrow vertical stripe on the left edge; the primary visual accent
        Region stripe = new Region();
        stripe.setPrefWidth(5);
        stripe.setMinWidth(5);
        // Only the left two corners are rounded to blend with the outer container corners
        stripe.setStyle("-fx-background-color: " + stripeColor + "; -fx-background-radius: 8 0 0 8;");

        // Content area grows to fill remaining width after the stripe
        VBox content = new VBox(8);
        content.setPadding(new Insets(14, 18, 14, 14));
        HBox.setHgrow(content, Priority.ALWAYS);

        // Heading row: icon + label, only rendered when a label is provided in the element
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

        // Strip SQL indentation from each body line before rendering
        String[] bodyLines = el.getValue().split("\n");
        StringBuilder cleanedBody = new StringBuilder();
        for (String line : bodyLines) {
            if (cleanedBody.length() > 0) cleanedBody.append("\n");
            cleanedBody.append(line.strip());
        }
        Label body = new Label(cleanedBody.toString());
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setStyle("-fx-font-size: 16px; -fx-text-fill: " + textColor + "; -fx-line-spacing: 4px;");
        content.getChildren().add(body);
        outer.getChildren().addAll(stripe, content);

        // Wrapped in a VBox so the caller receives a VBox regardless of internal layout
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
        // 2-3 options: horizontal row; buttons expand to fill available width
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
            // 4+ options: vertical stack; horizontal layout becomes cramped with longer option text
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
     * Builds a styled scenario option button with hover state handling.
     *
     * <p>Normal and hover styles are defined as inline strings and swapped via
     * mouse event handlers. This is necessary because JavaFX CSS pseudo-classes
     * such as {@code :hover} are not available without an external stylesheet.</p>
     *
     * @param text label text to display on the button
     * @return styled option button without an action handler attached
     */
    private Button makeOptionButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(48);
        // Dark navy base, left-aligned text with horizontal padding, blue border on hover
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
                // Slightly lighter background and a blue accent border
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
     * @param accentColor color used for the border and icon
     * @param bgColor card background color
     * @param resultIcon result symbol, usually {@code ✓} or {@code ✕}
     * @param tagText short outcome label
     * @param title result heading
     * @param bodyText explanatory result text
     * @return styled result card
     */
    public VBox buildResultCard(String accentColor, String bgColor,
                                String resultIcon, String tagText,
                                String title, String bodyText) {
        // Dark tinted card with a colored border matching the outcome (green/amber/red)
        VBox card = new VBox(16);
        card.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        // padding defined here rather than via VBox spacing so the border-radius clips correctly
                        "-fx-padding: 28 32 28 32;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 14, 0, 0, 3);"
        );

        // Badge row: circular icon + outcome tag + title heading
        HBox badgeRow = new HBox(14);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        // Circular icon badge: the result symbol (✓ or ✕) in a translucent tinted circle
        Label iconLabel = new Label(resultIcon);
        iconLabel.setMinSize(44, 44);
        iconLabel.setPrefSize(44, 44);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        // "22" hex suffix on the accent color produces ~14% opacity for the background tint
                        "-fx-background-color: " + accentColor + "22;" +
                        "-fx-background-radius: 22;"
        );

        // Tag and title stacked vertically; tag is small and uppercase-styled, title is large
        VBox badgeText = new VBox(3);
        Label tagLabel = new Label(tagText);
        tagLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        "-fx-letter-spacing: 1;"
        );
        Label titleLbl = new Label(title);
        // Large white heading makes the outcome more readable
        titleLbl.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #f1f5f9;"
        );
        badgeText.getChildren().addAll(tagLabel, titleLbl);
        badgeRow.getChildren().addAll(iconLabel, badgeText);
        card.getChildren().add(badgeRow);

        // Subtle horizontal rule using the accent color at ~27% opacity ("44" hex) as a divider
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: " + accentColor + "44;");
        card.getChildren().add(divider);

        // Feedback body: muted text, slightly larger than normal body text for readability
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
     * @param accentColor button background color
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
        // Dark green tinted panel that appears inline within the content area
        VBox toast = new VBox(10);
        toast.setStyle(
                "-fx-background-color: #0f2a1e;" +
                        "-fx-border-color: #22c55e;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 18 14 18;"
        );

        // Header row: lightbulb icon + "Observation" label in a brighter green than the body text
        HBox toastHeader = new HBox(10);
        toastHeader.setAlignment(Pos.CENTER_LEFT);
        Label toastIcon = new Label("💡");
        toastIcon.setStyle("-fx-font-size: 18px;");
        Label toastTitle = new Label("Observation");
        // Brighter green for the title to create a hierarchy against the softer body text below
        toastTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #4ade80;");
        toastHeader.getChildren().addAll(toastIcon, toastTitle);

        // Feedback message in a slightly muted green; readable but not competing with the title
        Label toastBody = new Label(feedbackText);
        toastBody.setWrapText(true);
        toastBody.setMaxWidth(Double.MAX_VALUE);
        toastBody.setStyle("-fx-font-size: 15px; -fx-text-fill: #86efac; -fx-line-spacing: 3px;");

        // The Continue button's action is wired by ScenarioController after this method returns,
        // because the controller owns the scene graph reference needed to remove the toast on dismiss
        Button continueBtn = new Button("Continue →");
        continueBtn.setStyle(
                // Solid green button, distinct from the toast background
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
     * <p>The form intentionally looks like a routine account registration prompt.
     * Submitting the form represents falling for the trap, while choosing the secondary
     * skip link represents the safer action.</p>
     *
     * @return fake registration form widget
     */
    public VBox buildRegistrationFormWidget() {
        // White card styled identically to the email widget
        VBox card = new VBox();
        card.setMaxWidth(620);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);"
        );

        // Chrome bar: same macOS-style title bar as the email widget
        HBox chrome = new HBox(8);
        chrome.setAlignment(Pos.CENTER_LEFT);
        chrome.setPadding(new Insets(10, 16, 10, 16));
        chrome.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: transparent transparent #d0d0d0 transparent;"
        );
        // Traffic light dots matching the email widget chrome
        for (String color : new String[]{"#ff5f57", "#febc2e", "#28c840"}) {
            Region dot = new Region();
            dot.setPrefSize(12, 12);
            dot.setMinSize(12, 12);
            dot.setMaxSize(12, 12);
            dot.setStyle("-fx-background-color:" + color + "; -fx-background-radius: 6;");
            chrome.getChildren().add(dot);
        }
        Region chromeSpacer = new Region();
        HBox.setHgrow(chromeSpacer, Priority.ALWAYS);
        chrome.getChildren().add(chromeSpacer);
        // Lock icon + portal name
        Label chromeLabel = new Label("🔒  Training Results Portal");
        chromeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555; -fx-font-weight: bold;");
        chrome.getChildren().add(chromeLabel);
        card.getChildren().add(chrome);

        // Form body with padding
        VBox body = new VBox(16);
        body.setPadding(new Insets(28, 32, 28, 32));

        Label intro = new Label("Create a free account to save your progress and view your detailed results.");
        intro.setWrapText(true);
        intro.setMaxWidth(Double.MAX_VALUE);
        intro.setStyle("-fx-font-size: 15px; -fx-text-fill: #444444; -fx-line-spacing: 4px;");
        body.getChildren().add(intro);
        // Separator visually separates the intro from the fields, like a real form
        body.getChildren().add(new Separator());

        // Input fields with paired hidden error labels
        TextField nameField     = new TextField();
        TextField emailField    = new TextField();
        PasswordField passField = new PasswordField();

        Label nameError  = makeErrorLabel("Full name is required.");
        Label emailError = makeErrorLabel("Please enter a valid email address.");
        Label passError  = makeErrorLabel("Password must be at least 8 characters.");

        // Each field is wrapped with its error label in a tight VBox(4) so the error appears directly below
        body.getChildren().add(new VBox(4, makeFormRow("Full Name", nameField), nameError));
        body.getChildren().add(new VBox(4, makeFormRow("Email address", emailField), emailError));
        body.getChildren().add(new VBox(4, makeFormRow("Password", passField), passError));

        // Boilerplate terms text, muted and small
        Label terms = new Label("By creating an account you agree to our Terms of Service and Privacy Policy.");
        terms.setWrapText(true);
        terms.setMaxWidth(Double.MAX_VALUE);
        terms.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999;");
        body.getChildren().add(terms);

        // Submit button: always visible; validation errors appear on click rather than inline
        Button submitBtn = new Button("Create Account & View Results");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setPrefHeight(44);
        // Blue CTA matches standard web form conventions
        submitBtn.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        // Simple email pattern - sufficient to validate format without being overly strict
        java.util.regex.Pattern emailPattern =
                java.util.regex.Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

        submitBtn.setOnAction(e -> {
            boolean nameOk  = !nameField.getText().trim().isEmpty();
            boolean emailOk = emailPattern.matcher(emailField.getText().trim()).matches();
            boolean passOk  = passField.getText().length() >= 8;

            setError(nameError,  !nameOk);
            setError(emailError, !emailOk);
            setError(passError,  !passOk);

            // Only fire the submit callback when all three fields pass validation
            if (nameOk && emailOk && passOk) {
                onBonusSubmit.run();
            }
        });

        // Clear each field's error as soon as the user fixes the input
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

        // The "Skip" action is deliberately styled as a low-prominence secondary link
        Label skipLink = new Label("Skip for now");
        skipLink.setMaxWidth(Double.MAX_VALUE);
        skipLink.setAlignment(Pos.CENTER);
        skipLink.setStyle(
                "-fx-font-size: 13px;" +
                        // Muted gray underline link, much less visible than the blue submit button above
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
                // Slightly larger than widget body text and in the app's standard slate color,
                // so it blends naturally into the scenario screen without competing with the widgets
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #cbd5e1;" +
                        "-fx-line-spacing: 6px;"
        );
        return label;
    }

    /**
     * Builds a labeled input row for the bonus registration form.
     *
     * <p>The label occupies a fixed minimum width so all input fields
     * in the form align vertically.</p>
     *
     * @param labelText text shown to the left of the input field
     * @param field the input control ({@link TextField} or {@link PasswordField})
     * @return horizontal row containing the label and field
     */
    private HBox makeFormRow(String labelText, javafx.scene.control.TextInputControl field) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        // Fixed minimum width keeps all field labels the same width, aligning the inputs in a column
        Label lbl = new Label(labelText);
        lbl.setMinWidth(130);
        lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");

        // Field grows to fill remaining width after the label
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-font-size: 14px;" +
                        // Light gray background with a border matching standard web input styling
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
