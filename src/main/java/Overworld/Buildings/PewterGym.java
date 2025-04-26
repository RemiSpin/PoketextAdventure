package Overworld.Buildings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import BattleLogic.Battle;
import BattleLogic.Trainer;
import BattleLogic.trainerPokemon;
import Overworld.Town;
import PlayerRelated.Player;
import WindowThings.mainWindow;
import javafx.stage.Stage;

public class PewterGym implements Town {
    private final String name = "Pewter Gym";
    private final String description = "A gym specializing in Rock-type Pokémon. Its leader is Brock.";
    private final String imageFile = "PewterGym.png";
    private Town parentTown;

    // List to track all trainers in the gym
    private List<Trainer> gymTrainers;

    // Track which trainers have been defeated
    private List<Boolean> defeatedTrainers;

    // Current trainer index
    private int currentTrainerIndex = 0;

    public PewterGym(Town parentTown) {
        this.parentTown = parentTown;

        // Initialize trainers
        initializeTrainers();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageFile() {
        return imageFile;
    }

    @Override
    public void enter(Player player) {
        if (!player.hasVisitedTown(getName())) {
            mainWindow.appendToOutput(getInitialEntryMessage());
            player.addVisitedTown(getName());
        } else {
            mainWindow.appendToOutput("You enter the " + getName() + ".");

            // Check if player has already earned the Boulder Badge
            if (player.getBadges().contains("Boulder Badge")) {
                mainWindow.appendToOutput(
                        "Brock: You've already proven your strength here. Keep up the good work on your journey!");
            }
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return null; // Gym doesn't have a Pokemon Center
    }

    @Override
    public String getInitialEntryMessage() {
        return "You enter the Pewter City Gym. The interior is adorned with large boulders and rock formations, reflecting Brock's specialty in Rock-type Pokémon. A trainer is practicing with their Pokémon, preparing for challengers.";
    }

    // Method to get parent town for return functionality
    public Town getParentTown() {
        return parentTown;
    }

    // Initialize all trainers in Pewter Gym
    private void initializeTrainers() {
        gymTrainers = new ArrayList<>();
        defeatedTrainers = new ArrayList<>();

        try {
            // Camper Lian with Geodude and Sandshrew
            trainerPokemon geodude1 = new trainerPokemon("Geodude", 10, "Tackle", "Defense Curl");
            trainerPokemon sandshrew = new trainerPokemon("Sandshrew", 11, "Scratch", "Defense Curl", "Sand-Attack");
            gymTrainers.add(new Trainer("Camper Lian", 220, geodude1, sandshrew));

            // Gym Leader Brock with Geodude and Onix
            trainerPokemon geodude2 = new trainerPokemon("Geodude", 12, "Tackle", "Defense Curl");
            trainerPokemon onix = new trainerPokemon("Onix", 14, "Tackle", "Bind", "Rock Tomb");
            gymTrainers.add(new Trainer("Gym Leader Brock", 1400, geodude2, onix));

            // Initialize all trainers as undefeated
            for (int i = 0; i < gymTrainers.size(); i++) {
                defeatedTrainers.add(false);
            }
        } catch (IOException e) {
            System.err.println("Error creating gym trainers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get the next undefeated trainer
    public Trainer getNextTrainer() {
        if (currentTrainerIndex >= gymTrainers.size()) {
            return null;
        }
        return gymTrainers.get(currentTrainerIndex);
    }

    // Mark the current trainer as defeated and move to the next one
    public void defeatCurrentTrainer() {
        if (currentTrainerIndex < defeatedTrainers.size()) {
            defeatedTrainers.set(currentTrainerIndex, true);
            currentTrainerIndex++;

            // If the defeated trainer was Brock (the last trainer), award the Boulder Badge
            if (currentTrainerIndex == gymTrainers.size()) {
                Player player = WindowThings.PokeText_Adventure.player;
                if (!player.getBadges().contains("Boulder Badge")) {
                    player.earnBadge("Boulder Badge");
                    mainWindow.appendToOutput("Brock: You're tough! Take this - the Boulder Badge!", "brown");
                    mainWindow.appendToOutput(
                            "Brock: That was a great battle, young trainer! You have a bright future ahead of you!",
                            "brown");
                }
            }
        }
    }

    // Check if all trainers have been defeated
    public boolean areAllTrainersDefeated() {
        for (Boolean defeated : defeatedTrainers) {
            if (!defeated) {
                return false;
            }
        }
        return true;
    }

    // Check if we're facing the leader (Brock)
    public boolean isNextTrainerLeader() {
        return currentTrainerIndex == 1 && !defeatedTrainers.get(currentTrainerIndex);
    }

    // Reset all trainers (for testing or new game)
    public void resetTrainers() {
        for (int i = 0; i < defeatedTrainers.size(); i++) {
            defeatedTrainers.set(i, false);
        }
        currentTrainerIndex = 0;
    }

    // Start a battle with the current trainer
    public void startTrainerBattle(Player player) {
        Trainer currentTrainer = getNextTrainer();
        if (currentTrainer != null) {
            try {
                // Show pre-battle dialogue based on which trainer it is
                String trainerName = currentTrainer.getName();

                // If this is Brock, show a confirmation dialog
                if (isNextTrainerLeader()) {
                    boolean confirmBattle = showLeaderConfirmation();
                    if (!confirmBattle) {
                        return;
                    }

                    mainWindow.appendToOutput(
                            "Brock: I'm Brock! I'm Pewter's Gym Leader! I believe in rock hard defense and determination!",
                            "brown");
                    mainWindow.appendToOutput("Brock: That's why my Pokémon are all the Rock-type!", "brown");
                } else {
                    mainWindow.appendToOutput(trainerName + " wants to battle!");
                }

                // Create a final reference to store the current trainer index for the
                // post-battle check
                final int trainerIndexBeforeBattle = currentTrainerIndex;
                final String trainerNameBeforeBattle = currentTrainer.getName();

                // Start the battle
                Battle battleWindow = new Battle(player, currentTrainer, "");

                // Use Battle's post-battle dialogue callback to mark trainer as defeated after
                // win
                battleWindow.setPostBattleAction(() -> {
                    // Only mark as defeated if the player won (will have gotten prize money)
                    if (trainerIndexBeforeBattle == currentTrainerIndex) {
                        // Only update if we haven't already updated (prevents double badges)
                        defeatCurrentTrainer(); // This will award badge if it was Brock
                        mainWindow.appendToOutput("You defeated " + trainerNameBeforeBattle + "!");
                    }
                });

            } catch (Exception e) {
                System.err.println("Error starting trainer battle: " + e.getMessage());
                mainWindow.appendToOutput("There was a problem with the trainer battle. Please try again.");
                e.printStackTrace();
            }
        } else {
            mainWindow.appendToOutput("There are no more trainers to battle in this gym!");
        }
    }

    // Add a public method that can be called by the Battle class when the battle is
    // won
    public void handleTrainerDefeat(String trainerName) {
        // Make sure we're defeating the correct trainer
        Trainer currentTrainer = getNextTrainer();
        if (currentTrainer != null && currentTrainer.getName().equals(trainerName)) {
            defeatCurrentTrainer();
        }
    }

    // Show confirmation dialog before battling the leader
    private boolean showLeaderConfirmation() {
        // Create a custom styled dialog instead of standard alert
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Gym Leader Challenge");
        dialogStage.setResizable(true); // Make the window resizable

        // Main container with monochrome background
        javafx.scene.layout.BorderPane dialogPane = new javafx.scene.layout.BorderPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");
        dialogPane.setPadding(new javafx.geometry.Insets(20));

        // Title label with monochrome styling
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Challenge Gym Leader Brock?");

        // Try to use the Pokémon font if available
        try {
            javafx.scene.text.Font pokemonFontLarge = javafx.scene.text.Font.loadFont(
                    getClass().getResourceAsStream("/RBYGSC.ttf"), 16);
            javafx.scene.text.Font pokemonFontSmall = javafx.scene.text.Font.loadFont(
                    getClass().getResourceAsStream("/RBYGSC.ttf"), 12);
            titleLabel.setFont(pokemonFontLarge);
        } catch (Exception e) {
            titleLabel.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        }

        titleLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(javafx.geometry.Pos.CENTER);
        titleLabel.setMaxWidth(380); // Explicit width setting
        titleLabel.setPrefWidth(380); // Prefer this width
        titleLabel.setPadding(new javafx.geometry.Insets(10));
        titleLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Create a text flow container for better wrapping
        javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow();
        javafx.scene.text.Text contentText = new javafx.scene.text.Text(
                "You are about to challenge the Gym Leader. Make sure your Pokémon are ready for this tough battle!");

        // Style the text
        try {
            javafx.scene.text.Font pokemonFontSmall = javafx.scene.text.Font.loadFont(
                    getClass().getResourceAsStream("/RBYGSC.ttf"), 12);
            contentText.setFont(pokemonFontSmall);
        } catch (Exception e) {
            contentText.setFont(javafx.scene.text.Font.font("Arial", 12));
        }

        textFlow.getChildren().add(contentText);
        textFlow.setMaxWidth(380); // Set explicit width
        textFlow.setPrefWidth(380); // Prefer this width
        textFlow.setLineSpacing(5); // Add some spacing between lines
        textFlow.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 15; " +
                "-fx-border-width: 1;");

        // Create buttons with custom styling
        javafx.scene.control.Button okButton = createStyledButton("Challenge", "#404040");
        javafx.scene.control.Button cancelButton = createStyledButton("Cancel", "#606060");

        // Button container
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.getChildren().addAll(okButton, cancelButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setPadding(new javafx.geometry.Insets(15, 0, 0, 0));

        // Add elements to the layout
        javafx.scene.layout.VBox contentBox = new javafx.scene.layout.VBox(15);
        contentBox.getChildren().addAll(titleLabel, textFlow, buttonBox);
        dialogPane.setCenter(contentBox);

        // Create scene - make it wider to ensure text fits
        javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogPane, 450, 270);
        dialogStage.setScene(dialogScene);

        // Set result flag
        final boolean[] result = { false };

        // Set button actions
        okButton.setOnAction(e -> {
            result[0] = true;
            dialogStage.close();
        });

        cancelButton.setOnAction(e -> {
            result[0] = false;
            dialogStage.close();
        });

        // Show dialog and wait for it to close
        dialogStage.showAndWait();

        return result[0];
    }

    // Helper method to create styled buttons
    private javafx.scene.control.Button createStyledButton(String text, String baseColor) {
        javafx.scene.control.Button button = new javafx.scene.control.Button(text);

        // Try to use the Pokémon font if available
        try {
            javafx.scene.text.Font pokemonFont = javafx.scene.text.Font.loadFont(
                    getClass().getResourceAsStream("/RBYGSC.ttf"), 12); // Reduced from 14
            button.setFont(pokemonFont);
        } catch (Exception e) {
            button.setFont(javafx.scene.text.Font.font("Arial", 12)); // Reduced from 14
        }

        // White background with black text styling
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
}
