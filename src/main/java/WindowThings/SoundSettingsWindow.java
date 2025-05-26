package WindowThings;

import Utils.MusicManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Sound Settings Window for managing music volume and mute controls
 */
public class SoundSettingsWindow {
    private Stage stage;
    private Slider volumeSlider;
    private Button muteButton;
    private javafx.animation.Timeline updateTimeline;

    public SoundSettingsWindow() {
        createWindow();
    }

    private void createWindow() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Sound Settings");
        stage.setResizable(false);

        // Register this window
        mainWindow.registerWindow(stage);

        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f8f8, #e8e8e8);");

        // Title
        Label titleLabel = createStyledLabel("Sound Settings", 18, true);
        titleLabel.setStyle("-fx-background-color: white; " +
                "-fx-padding: 10; " +
                "-fx-border-color: #333333; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");

        // Volume section
        VBox volumeSection = new VBox(15);
        volumeSection.setAlignment(Pos.CENTER);

        Label volumeTitleLabel = createStyledLabel("Volume Control:", 14, false);

        // Volume slider
        HBox volumeContainer = new HBox(10);
        volumeContainer.setAlignment(Pos.CENTER);

        volumeSlider = new Slider(0, 1, MusicManager.getInstance().getVolume());
        volumeSlider.setPrefWidth(200);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setMinorTickCount(1);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setSnapToTicks(false);

        // Style the slider
        volumeSlider.setStyle("-fx-control-inner-background: #e0e0e0; " +
                "-fx-accent: #4a90e2;");

        volumeContainer.getChildren().add(volumeSlider);

        volumeSection.getChildren().addAll(volumeTitleLabel, volumeContainer);

        // Mute section
        VBox muteSection = new VBox(10);
        muteSection.setAlignment(Pos.CENTER);

        muteButton = createStyledButton("Mute");
        muteButton.setPrefWidth(140);
        muteButton.setPrefHeight(40);

        muteSection.getChildren().addAll(muteButton);

        // Add all sections to main container
        mainContainer.getChildren().addAll(
                titleLabel,
                volumeSection,
                muteSection);

        // Set up event handlers
        setupEventHandlers();

        // Create scene
        Scene scene = new Scene(mainContainer, 320, 280);
        stage.setScene(scene);

        // Clean up when window closes
        stage.setOnCloseRequest(e -> {
            if (updateTimeline != null) {
                updateTimeline.stop();
            }
            mainWindow.unregisterWindow(stage);
        });

        // Start updating the display
        startUpdateTimer();

        // Initial update
        updateDisplay();
    }

    private void setupEventHandlers() {
        // Volume slider handler
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            float volume = newVal.floatValue();
            MusicManager.getInstance().setVolume(volume);
        });

        // Mute button handler
        muteButton.setOnAction(e -> {
            MusicManager.getInstance().toggleMute();
            updateMuteButton();
        });
    }

    private void startUpdateTimer() {
        updateTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        e -> updateDisplay()));
        updateTimeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        updateTimeline.play();
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            // Update volume slider
            MusicManager musicManager = MusicManager.getInstance();
            float currentVolume = musicManager.getVolume();
            if (Math.abs(volumeSlider.getValue() - currentVolume) > 0.01) {
                volumeSlider.setValue(currentVolume);
            }

            // Update mute button
            updateMuteButton();
        });
    }

    private void updateMuteButton() {
        // Use consistent styling but change text based on mute state
        if (MusicManager.getInstance().isMuted()) {
            muteButton.setText("Unmute");
        } else {
            muteButton.setText("Mute");
        }
    }

    private Label createStyledLabel(String text, int fontSize, boolean bold) {
        Label label = new Label(text);

        // Try to use the Pokémon font if available
        try {
            Font pokemonFont = Font.loadFont(
                    getClass().getResourceAsStream("/RBYGSC.ttf"), fontSize);
            label.setFont(pokemonFont);
        } catch (Exception e) {
            if (bold) {
                label.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize));
            } else {
                label.setFont(Font.font("Arial", fontSize));
            }
        }

        label.setTextFill(Color.BLACK);
        return label;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);

        // Try to use the Pokémon font if available
        try {
            Font pokemonFont = Font.loadFont(
                    getClass().getResourceAsStream("/RBYGSC.ttf"), 12);
            button.setFont(pokemonFont);
        } catch (Exception e) {
            button.setFont(Font.font("Arial", 12));
        }

        // Use consistent styling with other game windows
        String buttonStyle = "-fx-background-color: white; " +
                "-fx-text-fill: black; " +
                "-fx-border-color: #000000; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 8 15 8 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);";

        String hoverStyle = "-fx-background-color: #f0f0f0; " +
                "-fx-scale-x: 1.03; " +
                "-fx-scale-y: 1.03; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);";

        String pressedStyle = "-fx-background-color: #e0e0e0; " +
                "-fx-scale-x: 0.98; " +
                "-fx-scale-y: 0.98; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);";

        button.setStyle(buttonStyle);
        button.setOnMouseEntered(e -> button.setStyle(buttonStyle + hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(buttonStyle));
        button.setOnMousePressed(e -> button.setStyle(buttonStyle + pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(buttonStyle + hoverStyle));

        return button;
    }

    public void show() {
        stage.show();
    }

    public void close() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        stage.close();
    }
}
