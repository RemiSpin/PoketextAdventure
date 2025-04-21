package WindowThings;

import java.util.HashMap;
import java.util.Map;

import BattleLogic.Battle;
import BattleLogic.Trainer;
import BattleLogic.trainerPokemon;
import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StarterSelectionWindow {
    private Stage stage;
    private Player player;
    private static Font pokemonFont;
    private static Font pokemonFontSmall;
    private static final Map<String, Image> imageCache = new HashMap<>();

    // Pokemon data
    private final String[] starterNames = { "Bulbasaur", "Charmander", "Squirtle" };
    private final String[] starterTypes = { "Grass/Poison", "Fire", "Water" };
    // Improved descriptions that are more engaging while still concise
    private final String[] starterDescriptions = {
            "While it is young, it uses the nutrients that are stored in the seeds on its back in order to grow.",
            "It has a preference for hot things. When it rains, steam is said to spout from the tip of its tail.",
            "The shell is soft when it is born. It soon becomes so resilient, prodding fingers will bounce off it."
    };
    // Pokemon numbers for sprite filenames
    private final String[] pokeNumbers = { "001", "004", "007" };

    static {
        try {
            pokemonFont = Font.loadFont(StarterSelectionWindow.class.getResourceAsStream("/RBYGSC.ttf"), 18);
            pokemonFontSmall = Font.loadFont(StarterSelectionWindow.class.getResourceAsStream("/RBYGSC.ttf"), 14);
        } catch (Exception e) {
            System.out.println("Could not load Pokemon font: " + e.getMessage());
            pokemonFont = Font.font("Arial", FontWeight.BOLD, 18);
            pokemonFontSmall = Font.font("Arial", 14);
        }
    }

    public StarterSelectionWindow(Player player) {
        this.player = player;
        setupStage();
    }

    private void setupStage() {
        stage = new Stage();
        stage.setTitle("Choose Your Starter Pokemon");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        // Prevent manual closing
        stage.setOnCloseRequest(event -> {
            event.consume();
            WindowThings.mainWindow.appendToOutput("You must choose a starter Pokemon to continue!");
        });

        // Register with main window
        mainWindow.registerWindow(stage);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");

        // Title area
        Label titleLabel = new Label("Choose Your First Pokemon!");
        titleLabel.setFont(pokemonFont);
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setPadding(new Insets(15));
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Professor Oak's message
        Label messageLabel = new Label("Professor Oak: Here, take one of these rare Pokemon!");
        messageLabel.setFont(pokemonFontSmall);
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(10));

        VBox topSection = new VBox(10, titleLabel, messageLabel);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(10));
        mainLayout.setTop(topSection);

        // Pokemon choices section
        HBox pokemonChoices = new HBox(15); // Reduced horizontal spacing
        pokemonChoices.setAlignment(Pos.CENTER);
        pokemonChoices.setPadding(new Insets(10)); // Reduced padding

        for (int i = 0; i < 3; i++) {
            final int index = i;
            VBox pokemonCard = createPokemonCard(starterNames[i], starterTypes[i], i + 1,
                    e -> selectPokemon(index));
            pokemonChoices.getChildren().add(pokemonCard);
        }

        mainLayout.setCenter(pokemonChoices);

        // Increase window size to show descriptions better
        Scene scene = new Scene(mainLayout, 950, 580); // Slightly taller window
        stage.setScene(scene);
    }

    private VBox createPokemonCard(String name, String type, int dexNum,
            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(5); // Reduced vertical spacing between elements
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setMinWidth(260); // Slightly wider cards
        card.setMinHeight(340); // Set minimum height
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: #000000; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 15;");

        // Pokemon name
        Label nameLabel = new Label(name);
        nameLabel.setFont(pokemonFont);
        nameLabel.setPadding(new Insets(3, 0, 0, 0)); // Minimal top padding

        // Pokemon image with correct path format
        ImageView pokemonImage = new ImageView();
        try {
            // Using the correct GIF path with leading zeros for Pokemon number
            String imagePath = "/GIFs/ani_bw_" + pokeNumbers[dexNum - 1] + ".gif";
            Image image = getOrLoadImage(imagePath);
            if (image != null) {
                pokemonImage.setImage(image);
                pokemonImage.setFitHeight(85); // Slightly smaller
                pokemonImage.setFitWidth(85); // Slightly smaller
                pokemonImage.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.out.println("Error loading Pokemon image: " + e.getMessage());
        }

        // Type label
        Label typeLabel = new Label("Type: " + type);
        typeLabel.setFont(pokemonFontSmall);
        typeLabel.setPadding(new Insets(0)); // No padding

        // Description label with improved display
        Label descLabel = new Label(starterDescriptions[dexNum - 1]);
        descLabel.setFont(pokemonFontSmall);
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setPrefHeight(100); // Much taller description area
        descLabel.setPrefWidth(230); // Slightly wider
        descLabel.setPadding(new Insets(5));
        VBox.setMargin(descLabel, new Insets(10, 0, 10, 0)); // Add vertical margin

        // Choose button
        Button chooseButton = new Button("I Choose You!");
        chooseButton.setFont(pokemonFontSmall);
        chooseButton.setOnAction(action);
        chooseButton.setStyle("-fx-background-color: white; " +
                "-fx-text-fill: black; " +
                "-fx-border-color: #000000; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 8 15 8 15;");

        String hoverStyle = "-fx-background-color: #f0f0f0; " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05;";

        chooseButton.setOnMouseEntered(e -> {
            String currentStyle = chooseButton.getStyle();
            chooseButton.setStyle(currentStyle + hoverStyle);
        });

        chooseButton.setOnMouseExited(e -> {
            chooseButton.setStyle("-fx-background-color: white; " +
                    "-fx-text-fill: black; " +
                    "-fx-border-color: #000000; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-padding: 8 15 8 15;");
        });

        // Add spacer
        Region spacer = new Region();
        spacer.setPrefHeight(5); // Small space at bottom

        card.getChildren().addAll(nameLabel, pokemonImage, typeLabel, descLabel, chooseButton, spacer);
        return card;
    }

    private Image getOrLoadImage(String path) {
        if (!imageCache.containsKey(path)) {
            try {
                imageCache.put(path, new Image(getClass().getResourceAsStream(path)));
            } catch (Exception e) {
                System.out.println("Error caching image " + path + ": " + e.getMessage());
                return null;
            }
        }
        return imageCache.get(path);
    }

    private void selectPokemon(int index) {
        // Create the selected starter Pokemon
        Pokemon starter = null;
        try {
            String starterName;
            // Create Pokemon based on selection using the proper constructor
            switch (index) {
                case 0: // Bulbasaur
                    starterName = "Bulbasaur";
                    starter = new Pokemon(starterName, 5);
                    break;
                case 1: // Charmander
                    starterName = "Charmander";
                    starter = new Pokemon(starterName, 5);
                    break;
                case 2: // Squirtle
                    starterName = "Squirtle";
                    starter = new Pokemon(starterName, 5);
                    break;
                default:
                    starterName = "Bulbasaur";
                    starter = new Pokemon(starterName, 5);
                    break;
            }

            // Save the chosen starter in the player object
            player.setChosenStarter(starterName);

            // Create a final reference to starterName for use in the lambda
            final String finalStarterName = starterName;
            final Pokemon finalStarter = starter;

            // Add to player's party with a callback that will run after nickname is set
            player.addPokemonToParty(starter, () -> {
                // This code will run after the nickname dialog is closed

                // Set as current Pokemon
                player.setCurrentPokemon(finalStarter);

                // Output message
                WindowThings.mainWindow.appendToOutput(
                        "Professor Oak: So, you've chosen " + finalStarter.getName() + "! That's an excellent choice!");

                // Start the rival battle after the nickname has been set
                startRivalBattle(finalStarterName);
            });

            // Close the window properly
            mainWindow.unregisterWindow(stage);
            stage.close();

        } catch (Exception e) {
            System.out.println("Error creating starter Pokemon: " + e.getMessage());
            WindowThings.mainWindow.appendToOutput("There was a problem selecting your starter. Please try again.");
            e.printStackTrace();
        }
    }

    // Extracted method to start the rival battle
    private void startRivalBattle(String starterName) {
        try {
            // Gary chooses the Pokemon with type advantage
            trainerPokemon rivalStarter;

            if (starterName.equals("Bulbasaur")) {
                // Fire beats Grass
                rivalStarter = new trainerPokemon("Charmander", 5, "Scratch", "Growl");
            } else if (starterName.equals("Charmander")) {
                // Water beats Fire
                rivalStarter = new trainerPokemon("Squirtle", 5, "Tackle", "Tail Whip");
            } else {
                // Grass beats Water
                rivalStarter = new trainerPokemon("Bulbasaur", 5, "Tackle", "Growl");
            }

            // Show Gary choosing his Pokemon and challenging the player
            WindowThings.mainWindow.appendToOutput("Gary: Hah! I'll take this one then!");
            WindowThings.mainWindow.appendToOutput("Gary picks " + rivalStarter.getName() + "!");
            WindowThings.mainWindow.appendToOutput(
                    "Professor Oak: Take good care of your Pokemon, both of you.");
            WindowThings.mainWindow.appendToOutput(
                    "Gary turns to you with a smirk on his face.");
            WindowThings.mainWindow.appendToOutput(
                    "Gary: Let's see which one of us picked the better Pokemon! Let's go!");

            // Create post-battle dialogue based on win condition
            String postBattleDialogue = "Gary: What? Unbelievable! I picked the wrong Pokemon!\n" +
                    "Gary glares at you.\n" +
                    "Gary: I'm going to make my Pokemon stronger! When I do, I'll challenge you again!\n" +
                    "Gary storms out of the lab.\n" +
                    "Professor Oak approaches you with a smile.\n" +
                    "Professor Oak: Well done! That was an impressive first battle!\n" +
                    "Professor Oak: Here, I have something for you that will help on your journey.\n" +
                    "Professor Oak hands you 5 Poke Balls.\n" +
                    "Professor Oak: And this is my invention, the Pokedex!\n" +
                    "Professor Oak: It automatically records data on Pokemon you've seen or caught.\n" +
                    "Professor Oak hands you a Pokedex.\n" +
                    "Professor Oak: To make a complete guide on all the Pokemon in the world...\n" +
                    "That was my dream! But I'm too old to do it now.\n" +
                    "So, I want you to fulfill my dream for me!\n" +
                    "Now get moving! Your legend is about to unfold!";

            // Create the trainer battle with Gary, including post-battle dialogue
            Battle battleWindow = new Battle(player, new Trainer("Gary", 80, rivalStarter), postBattleDialogue);

        } catch (Exception e) {
            System.out.println("Error starting rival battle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void show() {
        stage.showAndWait();
    }
}
