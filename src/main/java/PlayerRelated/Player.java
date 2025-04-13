package PlayerRelated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import PokemonLogic.Pokemon;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("FieldMayBeFinal")

public class Player {
    private static String name = "Red";
    private int money;
    private final List<Pokemon> party;
    private final List<Pokemon> pc;
    private final List<String> badges;
    private Pokemon currentPokemon;
    private String currentTownName;
    private static Font pokemonFont;
    private static Font pokemonFontSmall;

    static {
        try {
            pokemonFont = Font.loadFont(Player.class.getResourceAsStream("/RBYGSC.ttf"), 18);
            pokemonFontSmall = Font.loadFont(Player.class.getResourceAsStream("/RBYGSC.ttf"), 11);
        } catch (Exception e) {
            System.out.println("Could not load Pokémon font: " + e.getMessage());
            pokemonFont = Font.font("Arial", FontWeight.BOLD, 18);
            pokemonFontSmall = Font.font("Arial", 11);
        }
    }

    public Player() {
        this.money = 1000;
        this.party = new ArrayList<>();
        this.pc = new ArrayList<>();
        this.badges = new ArrayList<>();
    }

    // Constructor that takes a name parameter
    public Player(String playerName) {
        this.money = 0; // Initial money will be set later
        this.party = new ArrayList<>();
        this.pc = new ArrayList<>();
        this.badges = new ArrayList<>();
        name = playerName; // Set the static name field
    }

    public static String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public List<Pokemon> getParty() {
        return Collections.unmodifiableList(party);
    }

    public List<Pokemon> getPC() {
        return Collections.unmodifiableList(pc);
    }

    public List<String> getBadges() {
        return Collections.unmodifiableList(badges);
    }

    public void addMoney(int amount) {
        money += amount;
    }

    // Method to set money
    public void setMoney(int amount) {
        this.money = amount;
    }

    public static void setName() {
        // Create custom name input dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Player Name");
        dialogStage.setResizable(false);

        // Main container with monochrome background
        BorderPane dialogPane = new BorderPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");
        dialogPane.setPadding(new Insets(20));

        // Title label with monochrome styling
        Label titleLabel = new Label("Enter Your Name");
        titleLabel.setFont(pokemonFont);
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(10, 0, 10, 0));
        titleLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Text field for name input
        TextField nameField = new TextField("Red");
        nameField.setFont(pokemonFontSmall);
        nameField.setPrefWidth(200);
        nameField.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");

        HBox inputBox = new HBox(10);
        inputBox.getChildren().add(nameField);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(15, 0, 15, 0));

        // Create the buttons with monochrome styling
        Button okButton = createStyledButton("OK", "#404040");
        Button cancelButton = createStyledButton("Cancel", "#606060");

        // Set button actions
        okButton.setOnAction(e -> {
            name = nameField.getText().trim();
            if (name.isEmpty())
                name = "Red";
            dialogStage.close();
        });

        cancelButton.setOnAction(e -> {
            name = "Red"; // Default name
            dialogStage.close();
        });

        // Button container
        HBox buttonBox = new HBox(15);
        buttonBox.getChildren().addAll(okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Main layout
        VBox mainBox = new VBox(15);
        mainBox.getChildren().addAll(titleLabel, inputBox, buttonBox);
        dialogPane.setCenter(mainBox);

        // Set up the scene
        Scene dialogScene = new Scene(dialogPane, 350, 200);

        // Add enter key handler to text field
        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                okButton.fire();
            }
        });

        dialogStage.setScene(dialogScene);

        // Show dialog and wait for it to close
        dialogStage.showAndWait();
    }

    private static Button createStyledButton(String text, String baseColor) {
        Button button = new Button(text);
        button.setFont(pokemonFontSmall);

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
        button.setOnMouseReleased(e -> button.setStyle(buttonStyle));

        return button;
    }

    public void subtractMoney(int amount) {
        if (money - amount >= 0) {
            money -= amount;
        } else {
            displayMessage("Insufficient funds.");
        }
    }

    private void displayMessage(String message) {
        Platform.runLater(() -> {
            // Create custom dialog stage
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Message");
            dialogStage.setResizable(false);

            // Main container with monochrome background
            BorderPane dialogPane = new BorderPane();
            dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");
            dialogPane.setPadding(new Insets(20));

            // Message text with monochrome style box
            Label messageLabel = new Label(message);
            messageLabel.setFont(pokemonFontSmall);
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(15));
            messageLabel.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #000000; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 2;");

            dialogPane.setCenter(messageLabel);

            // Create a styled OK button
            Button okButton = createStyledButton("OK", "#303030"); // Dark gray
            okButton.setOnAction(e -> dialogStage.close());

            // Button container
            HBox buttonBox = new HBox(okButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            dialogPane.setBottom(buttonBox);

            // Set up the scene
            Scene dialogScene = new Scene(dialogPane, 300, 150);

            // Add enter key handler to the scene
            dialogScene.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    okButton.fire();
                }
            });

            dialogStage.setScene(dialogScene);

            // Show dialog and wait for it to close
            dialogStage.showAndWait();
        });
    }

    public void earnBadge(String badgeName) {
        badges.add(badgeName);
        displayMessage("You earned the " + badgeName + "!");
    }

    public void addPokemonToParty(Pokemon pokemon) {
        if (party.size() < 6) {
            party.add(pokemon);
        } else {
            pc.add(pokemon);
        }

        // Schedule the dialog to show after the current animation/layout cycle is done
        Platform.runLater(() -> {
            showNicknameDialog(pokemon);
        });
    }

    private void showNicknameDialog(Pokemon pokemon) {
        // Create custom nickname dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Nickname for " + pokemon.getName());
        dialogStage.setResizable(false);

        // Main container with monochrome background
        BorderPane dialogPane = new BorderPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");
        dialogPane.setPadding(new Insets(20));

        // Title label with monochrome styling
        Label titleLabel = new Label("Enter a nickname for your newly caught " + pokemon.getName() + "!");
        titleLabel.setFont(pokemonFontSmall);
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(10));
        titleLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Text field for nickname input
        TextField nicknameField = new TextField(pokemon.getName());
        nicknameField.setFont(pokemonFontSmall);
        nicknameField.setPrefWidth(200);
        nicknameField.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");

        HBox inputBox = new HBox(15);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().add(nicknameField);
        inputBox.setPadding(new Insets(15, 0, 15, 0));

        // Create the buttons with monochrome styling
        Button okButton = createStyledButton("OK", "#404040");
        Button cancelButton = createStyledButton("Cancel", "#606060");

        // Set button actions
        okButton.setOnAction(e -> {
            String nickname = nicknameField.getText().trim();
            if (!nickname.isEmpty()) {
                pokemon.setNickname(nickname);
            }
            dialogStage.close();
        });

        cancelButton.setOnAction(e -> {
            // Keep default name (pokemon's name)
            dialogStage.close();
        });

        // Button container
        HBox buttonBox = new HBox(15);
        buttonBox.getChildren().addAll(okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Main layout
        VBox mainBox = new VBox(15);
        mainBox.getChildren().addAll(titleLabel, inputBox, buttonBox);
        dialogPane.setCenter(mainBox);

        // Set up the scene
        Scene dialogScene = new Scene(dialogPane, 400, 250);

        // Add enter key handler to text field
        nicknameField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                okButton.fire();
            }
        });

        dialogStage.setScene(dialogScene);

        // Show dialog and wait for it to close
        dialogStage.showAndWait();
    }

    // Method to directly add a Pokemon to the party (for loading from save)
    public void addToParty(Pokemon pokemon) {
        if (party.size() < 6) {
            party.add(pokemon);
        } else {
            pc.add(pokemon); // If party is full, add to PC
            displayMessage(pokemon.getNickname() + " was added to PC.");
        }
    }

    // Method to directly add a Pokemon to the PC (for loading from save)
    public void addToPC(Pokemon pokemon) {
        pc.add(pokemon);
    }

    // Method to add a badge (for loading from save)
    public void addBadge(String badgeName) {
        if (!badges.contains(badgeName)) {
            badges.add(badgeName);
        }
    }

    public void switchPokemonOutsideBattle() {
        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                for (int i = 0; i < party.size(); i++) {
                    System.out.println((i + 1) + ". " + party.get(i).getNickname());
                }

                System.out.println("Choose a Pokemon to switch (1-" + party.size() + "), or press 0 to go back:");
                int choice = scanner.nextInt();

                // Check if the user wants to go back
                if (choice == 0) {
                    System.out.println("Going back to the previous menu.");
                    return;
                }

                // Validate the input
                if (choice < 1 || choice > party.size()) {
                    System.out.println("Invalid choice. Please choose a number between 1 and " + party.size()
                            + ", or press 0 to go back.");
                    continue;
                }

                // Adjust the choice to be zero-based
                choice--;

                // Swap the chosen Pokemon with the first Pokemon in the party
                Pokemon temp = party.get(0);
                party.set(0, party.get(choice));
                party.set(choice, temp);

                System.out.println(party.get(0).getNickname() + " is in front.");
                break;
            }
        }
    }

    public void removePokemonFromParty(Pokemon pokemon) {
        if (party.contains(pokemon)) {
            party.remove(pokemon);
            pc.add(pokemon);
            displayMessage(pokemon.getNickname() + " was sent to the PC.");
        } else {
            displayMessage("The specified Pokémon is not in your party.");
        }
    }

    public boolean hasUsablePokemon() {
        for (Pokemon pokemon : party) {
            if (pokemon.getRemainingHealth() > 0) {
                return true;
            }
        }
        return false;
    }

    public Pokemon getCurrentPokemon() {
        return currentPokemon;
    }

    public void setCurrentPokemon(Pokemon pokemon) {
        this.currentPokemon = pokemon;
    }

    public Pokemon getFirstPokemon() {
        if (party != null && !party.isEmpty()) {
            return party.get(0);
        }
        return null; // or throw an exception if no Pokemon in party
    }

    private Set<String> visitedTowns = new HashSet<>();

    public boolean hasVisitedTown(String townName) {
        return visitedTowns.contains(townName);
    }

    public void addVisitedTown(String townName) {
        visitedTowns.add(townName);
    }

    public Set<String> getVisitedTowns() {
        return new HashSet<>(visitedTowns);
    }

    public void setCurrentTownName(String townName) {
        this.currentTownName = townName;
    }

    public String getCurrentTownName() {
        return currentTownName;
    }

    // Method to withdraw a Pokemon from PC to party
    public boolean withdrawPokemonFromPC(int pcIndex) {
        if (pcIndex < 0 || pcIndex >= pc.size()) {
            return false;
        }

        if (party.size() >= 6) {
            return false;
        }

        Pokemon pokemon = pc.get(pcIndex);
        pc.remove(pcIndex);
        party.add(pokemon);
        return true;
    }

    // Method to deposit a Pokemon from party to PC
    public boolean depositPokemonToPC(int partyIndex) {
        if (partyIndex < 0 || partyIndex >= party.size()) {
            return false;
        }

        // Don't allow depositing the last Pokemon
        if (party.size() <= 1) {
            return false;
        }

        // If this is the current Pokemon, reset currentPokemon
        Pokemon pokemon = party.get(partyIndex);
        if (pokemon == currentPokemon) {
            // Set current Pokemon to the first one that's not being deposited
            for (Pokemon p : party) {
                if (p != pokemon) {
                    currentPokemon = p;
                    break;
                }
            }
        }

        party.remove(partyIndex);
        pc.add(pokemon);
        return true;
    }
}