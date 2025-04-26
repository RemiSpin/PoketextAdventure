package WindowThings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import Overworld.Town;
import PlayerRelated.Player;
import PlayerRelated.PokedexWindow;
import PlayerRelated.SaveGame;
import PokemonLogic.Pokemon;
import PokemonLogic.PokemonInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

@SuppressWarnings({ "FieldMayBeFinal", "unused" })

public class mainWindow extends Application {
    private static TextFlow textFlow = new TextFlow();
    private static ScrollPane scrollPane = new ScrollPane(textFlow);
    private TextField inputField = new TextField();
    private static Stage primaryStage;
    private static Font pokemonFont;

    // Add a static list to track all secondary windows
    private static final List<Stage> secondaryWindows = new ArrayList<>();

    static {
        try {
            pokemonFont = Font.loadFont(mainWindow.class.getResourceAsStream("/RBYGSC.ttf"), 16);
        } catch (Exception e) {
            System.out.println("Could not load Pokemon font: " + e.getMessage());
            pokemonFont = Font.font("Arial", 16);
        }
    }

    // Method to register a secondary window
    public static void registerWindow(Stage window) {
        secondaryWindows.add(window);
    }

    // Method to unregister a secondary window
    public static void unregisterWindow(Stage window) {
        secondaryWindows.remove(window);
    }

    // Method to check if there are any open secondary windows
    public static boolean hasSecondaryWindows() {
        return !secondaryWindows.isEmpty();
    }

    // Method to check if there are any secondary windows EXCEPT exploreWindow
    public static boolean hasSecondaryWindowsExceptExplore() {
        for (Stage window : secondaryWindows) {
            if (!exploreWindow.isExploreWindow(window)) {
                return true;
            }
        }
        return false;
    }

    // Method to close all secondary windows
    public static void closeAllSecondaryWindows() {
        // Create a copy to avoid concurrent modification
        List<Stage> windowsToClose = new ArrayList<>(secondaryWindows);
        for (Stage window : windowsToClose) {
            window.close();
        }
        secondaryWindows.clear();
    }

    // Getter for the primary stage
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // Original method for backward compatibility
    public static void appendToOutput(String message) {
        Platform.runLater(() -> {
            Text prefix = new Text("> ");
            prefix.setFill(Color.BLACK);
            prefix.setFont(pokemonFont);

            Text text = new Text(message + "\n");
            text.setFill(Color.BLACK);
            text.setFont(pokemonFont);

            textFlow.getChildren().addAll(prefix, text);

            // Improved auto-scroll to ensure we reach the bottom
            scrollToBottom();
        });
    }

    // New method with color support using string color names
    public static void appendToOutput(String message, String colorName) {
        Platform.runLater(() -> {
            Text prefix = new Text("> ");
            prefix.setFill(Color.BLACK);
            prefix.setFont(pokemonFont);

            Text text = new Text(message + "\n");

            // Convert string color name to Color
            Color textColor = Color.BLACK; // Default color
            try {
                // Try to parse as a named color
                textColor = Color.valueOf(colorName.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If that fails, try to interpret as a web color
                try {
                    textColor = Color.web(colorName);
                } catch (IllegalArgumentException ex) {
                    // If all fails, use default black
                    System.err.println("Invalid color: " + colorName + ". Using default black.");
                }
            }

            text.setFill(textColor);
            text.setFont(pokemonFont);

            textFlow.getChildren().addAll(prefix, text);

            // Improved auto-scroll to ensure we reach the bottom
            scrollToBottom();
        });
    }

    // Helper method to ensure scrolling to the bottom works properly
    private static void scrollToBottom() {
        // Use Platform.runLater to ensure this happens after layout
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        mainWindow.primaryStage = primaryStage;
        Player player = PokeText_Adventure.player;
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 700, 700);

        // Configure TextFlow with padding but no borders
        textFlow.setStyle("-fx-background-color: white;");
        textFlow.setPrefWidth(660);
        textFlow.setLineSpacing(3);
        textFlow.setPadding(new javafx.geometry.Insets(15)); // Keep padding inside text area

        // Use TextFlow directly without the container border
        scrollPane.setContent(textFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(650);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        scrollPane.setPadding(new javafx.geometry.Insets(15, 15, 15, 15)); // Keep margin around ScrollPane

        // Setup custom output stream
        System.setOut(new PrintStream(new TextFlowOutputStream()));

        // Style the input field - simplified without borders
        inputField.setFont(pokemonFont);
        inputField.setPadding(new javafx.geometry.Insets(5));

        root.setCenter(scrollPane);

        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new javafx.geometry.Insets(10, 15, 15, 15)); // Keep padding for input area
        inputBox.getChildren().addAll(inputField);

        HBox.setHgrow(inputField, Priority.ALWAYS);
        root.setBottom(inputBox);

        // Keep subtle background color for visual separation
        root.setStyle("-fx-background-color: #f5f5f5;");

        primaryStage.setTitle("PokeText");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add close request handler to the primary stage
        primaryStage.setOnCloseRequest(event -> {
            if (hasSecondaryWindowsExceptExplore()) {
                // If there are secondary windows (except exploreWindow), prevent closing
                event.consume();
                appendToOutput("Please close all windows except the exploration window before exiting the game.",
                        "red");
            } else {
                // Create a custom styled dialog instead of standard alert
                Stage dialogStage = new Stage();
                dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                dialogStage.setTitle("Confirm Exit");
                dialogStage.setResizable(true); // Allow resizing

                // Main container with monochrome background
                javafx.scene.layout.BorderPane dialogPane = new javafx.scene.layout.BorderPane();
                dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");
                dialogPane.setPadding(new javafx.geometry.Insets(20));

                // Title label with monochrome styling
                javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Exit");
                // Try to use the Pokémon font if available
                try {
                    javafx.scene.text.Font pokemonFontLarge = javafx.scene.text.Font.loadFont(
                            getClass().getResourceAsStream("/RBYGSC.ttf"), 16);
                    titleLabel.setFont(pokemonFontLarge);
                } catch (Exception e) {
                    titleLabel.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
                }

                titleLabel.setTextFill(javafx.scene.paint.Color.BLACK);
                titleLabel.setAlignment(javafx.geometry.Pos.CENTER);
                titleLabel.setMaxWidth(300);
                titleLabel.setPrefWidth(300);
                titleLabel.setWrapText(true); // Ensure wrapping
                titleLabel.setPadding(new javafx.geometry.Insets(10));
                titleLabel.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-width: 2;");

                // Use TextFlow instead of Label for better text wrapping
                javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow();
                javafx.scene.text.Text contentText = new javafx.scene.text.Text(
                        "Are you sure you want to exit? Make sure you saved!");

                // Style the text
                try {
                    javafx.scene.text.Font pokemonFontSmall = javafx.scene.text.Font.loadFont(
                            getClass().getResourceAsStream("/RBYGSC.ttf"), 12);
                    contentText.setFont(pokemonFontSmall);
                } catch (Exception e) {
                    contentText.setFont(javafx.scene.text.Font.font("Arial", 12));
                }

                textFlow.getChildren().add(contentText);
                textFlow.setMaxWidth(300);
                textFlow.setPrefWidth(300);
                textFlow.setLineSpacing(5);
                textFlow.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 15; " +
                        "-fx-border-width: 1;");

                // Create buttons with custom styling
                javafx.scene.control.Button yesButton = createStyledButton("Yes", "#404040");
                javafx.scene.control.Button noButton = createStyledButton("No", "#606060");

                // Button container
                javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
                buttonBox.getChildren().addAll(yesButton, noButton);
                buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
                buttonBox.setPadding(new javafx.geometry.Insets(15, 0, 0, 0));

                // Add elements to the layout
                javafx.scene.layout.VBox contentBox = new javafx.scene.layout.VBox(15);
                contentBox.getChildren().addAll(titleLabel, textFlow, buttonBox);
                dialogPane.setCenter(contentBox);

                // Create scene - slightly taller to accommodate wrapped text
                javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogPane, 350, 220);
                dialogStage.setScene(dialogScene);

                // Set button actions
                yesButton.setOnAction(e -> {
                    // User confirmed, close all secondary windows and exit
                    closeAllSecondaryWindows();
                    dialogStage.close();
                });

                noButton.setOnAction(e -> {
                    // User cancelled, prevent window from closing
                    event.consume();
                    dialogStage.close();
                });

                // Show dialog and wait for it to close
                dialogStage.showAndWait();
            }
        });

        // Display welcome message in the main text area
        appendToOutput("Welcome to PokeText Adventure!", "red");
        appendToOutput("Type 'help' to see a list of available commands.", "blue");

        // Now start the game logic
        PokeText_Adventure pokeTextAdventure = new PokeText_Adventure();
        pokeTextAdventure.start(primaryStage);

        inputField.setOnAction(e -> processInput());
    }

    private void processInput() {
        String input = inputField.getText().trim().toLowerCase();
        inputField.clear();

        switch (input) {
            case "save" -> {
                SaveGame saveGame = new SaveGame(PokeText_Adventure.player);
                saveGame.saveGame();
            }
            case "help" -> {
                appendToOutput("Available commands:", "blue");
                appendToOutput("Save : Saves your current game progress");
                appendToOutput("Pokemon : Shows a list of Pokemon in your party");
                appendToOutput("Pokemon (nickname) : Shows detailed stats for a specific Pokemon");
                appendToOutput("Lead : Change which Pokemon appears first in your party");
                appendToOutput("Area : Shows information about your current location");
                appendToOutput("Pokedex : Opens the Pokedex to track caught Pokemon");
                appendToOutput(Player.getName() + " : Shows your trainer information");
                appendToOutput("Help : Shows this help message");
            }
            case "pokedex" -> {
                // Open the Pokedex window
                PokedexWindow pokedexWindow = new PokedexWindow();
                pokedexWindow.show();
            }
            case "area" -> {
                if (exploreWindow.playerCurrentTown != null) {
                    Town currentTown = exploreWindow.playerCurrentTown;
                    appendToOutput("Current location: " + currentTown.getName(), "blue");
                    appendToOutput(currentTown.getDescription());

                    // Also show Pokemon Center information if available
                    if (currentTown.getPokemonCenter() != null) {
                        appendToOutput("\nFacilities:", "blue");
                        appendToOutput("- " + currentTown.getPokemonCenter().getName() + ": " +
                                currentTown.getPokemonCenter().getDescription());
                    }
                } else {
                    appendToOutput("You are not currently in any town.", "red");
                }
            }
            case "pokemon" -> {
                appendToOutput("Your Pokemon:", "blue");
                for (Pokemon pokemon : PokeText_Adventure.player.getParty()) {
                    appendToOutput("- " + pokemon.getNickname());
                }
                appendToOutput("\nIf you wish to see the stats of a pokemon, say: \nPokemon (Nickname)!", "green");
            }
            case "lead", "switchlead" -> {
                // Get the player's current party
                List<Pokemon> party = PokeText_Adventure.player.getParty();

                if (party.size() <= 1) {
                    appendToOutput("You need at least two Pokemon to change your lead Pokemon!", "red");
                    return;
                }

                // Show current party order
                appendToOutput("Your current party order:", "blue");
                for (int i = 0; i < party.size(); i++) {
                    Pokemon pokemon = party.get(i);
                    String status = (pokemon.getRemainingHealth() <= 0) ? " (Fainted)" : "";
                    appendToOutput((i + 1) + ". " + pokemon.getNickname() + " (Lv." +
                            pokemon.getLevel() + " " + pokemon.getName() + ")" + status);
                }

                appendToOutput("\nEnter the number of the Pokemon you want as your lead:", "green");

                // Store the current event handler to restore it later
                javafx.event.EventHandler<javafx.event.ActionEvent> originalHandler = inputField.getOnAction();

                // Set a new temporary handler for this specific input
                inputField.setOnAction(e -> {
                    String selection = inputField.getText().trim();
                    inputField.clear();

                    try {
                        int choice = Integer.parseInt(selection);
                        if (choice < 1 || choice > party.size()) {
                            appendToOutput("Invalid selection! Please enter a number between 1 and " + party.size(),
                                    "red");
                        } else {
                            // Check if selected Pokemon is fainted
                            Pokemon selectedPokemon = party.get(choice - 1);
                            if (selectedPokemon.getRemainingHealth() <= 0) {
                                appendToOutput("Cannot set " + selectedPokemon.getNickname() +
                                        " as your lead Pokemon because it has fainted!", "red");
                            }
                            // If not selecting the current lead (1) and not fainted
                            else if (choice != 1) {
                                // Create a modifiable copy of the party list
                                List<Pokemon> modifiableParty = new ArrayList<>(party);

                                // Store the Pokemon we want to swap
                                Pokemon firstPokemon = modifiableParty.get(0);
                                Pokemon selectedPkm = modifiableParty.get(choice - 1);

                                // Update the player's party with the modified order
                                // Use the appropriate Player method to set the party
                                PokeText_Adventure.player.swapPokemonPosition(0, choice - 1);

                                // Update current Pokemon if needed
                                if (PokeText_Adventure.player.getCurrentPokemon() == firstPokemon) {
                                    PokeText_Adventure.player.setCurrentPokemon(selectedPkm);
                                } else if (PokeText_Adventure.player.getCurrentPokemon() == selectedPkm) {
                                    PokeText_Adventure.player.setCurrentPokemon(firstPokemon);
                                }

                                appendToOutput(selectedPkm.getNickname() + " is now your lead Pokemon!", "green");

                                // Show updated party order
                                appendToOutput("Updated party order:", "blue");
                                List<Pokemon> updatedParty = PokeText_Adventure.player.getParty();
                                for (int i = 0; i < updatedParty.size(); i++) {
                                    Pokemon pokemon = updatedParty.get(i);
                                    String status = (pokemon.getRemainingHealth() <= 0) ? " (Fainted)" : "";
                                    appendToOutput((i + 1) + ". " + pokemon.getNickname() + " (Lv." +
                                            pokemon.getLevel() + " " + pokemon.getName() + ")" + status);
                                }
                            } else {
                                appendToOutput(selectedPokemon.getNickname() + " is already your lead Pokemon!",
                                        "green");
                            }
                        }
                    } catch (NumberFormatException ex) {
                        appendToOutput("Please enter a valid number!", "red");
                    } finally {
                        // Always restore the original event handler
                        inputField.setOnAction(originalHandler);
                    }
                });
            }
            default -> {
                if (input.equals(Player.getName().toLowerCase())) {
                    appendToOutput("Trainer Information:", "blue");
                    appendToOutput("Name: " + Player.getName());
                    appendToOutput("Money: $" + PokeText_Adventure.player.getMoney());
                    appendToOutput("Badges: " + PokeText_Adventure.player.getBadges());

                    int partySize = PokeText_Adventure.player.getParty().size();
                    int pcSize = PokeText_Adventure.player.getPC().size();
                    appendToOutput("Total Pokemon: " + (partySize + pcSize));
                    // add playtime

                } else if (input.startsWith("pokemon ")) {
                    String requestedNickname = input.substring(8);
                    boolean found = false;

                    for (Pokemon pokemon : PokeText_Adventure.player.getParty()) {
                        if (pokemon.getNickname().toLowerCase().equals(requestedNickname)) {
                            PokemonInfo pokemonInfoWindow = new PokemonInfo(pokemon);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        appendToOutput("Could not find a Pokemon with nickname: " + requestedNickname, "red");
                    }
                } else {
                    appendToOutput("Command doesn't exist! Type 'help' to see a list of available commands.", "red");
                }
            }
        }
    }

    // Custom OutputStream that writes to our TextFlow
    public static class TextFlowOutputStream extends OutputStream {
        private StringBuilder buffer = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            char ch = (char) b;
            buffer.append(ch);

            // When we get a newline, append the complete line to the text flow
            if (ch == '\n') {
                final String text = buffer.toString();
                Platform.runLater(() -> {
                    Text prefix = new Text(text.startsWith("> ") ? "" : "> ");
                    prefix.setFill(Color.BLACK);
                    prefix.setFont(pokemonFont);

                    Text content = new Text(text.startsWith("> ") ? text : text);
                    content.setFill(Color.BLACK);
                    content.setFont(pokemonFont);

                    textFlow.getChildren().addAll(prefix, content);

                    // Use the improved scrolling method
                    scrollToBottom();
                });
                buffer.setLength(0);
            }
        }

        @Override
        public void flush() throws IOException {
            if (buffer.length() > 0) {
                final String text = buffer.toString();
                Platform.runLater(() -> {
                    Text prefix = new Text(text.startsWith("> ") ? "" : "> ");
                    prefix.setFill(Color.BLACK);
                    prefix.setFont(pokemonFont);

                    Text content = new Text(text.startsWith("> ") ? text : text);
                    content.setFill(Color.BLACK);
                    content.setFont(pokemonFont);

                    textFlow.getChildren().addAll(prefix, content);

                    // Use the improved scrolling method
                    scrollToBottom();
                });
                buffer.setLength(0);
            }
        }
    }

    // Helper method to create styled buttons
    private javafx.scene.control.Button createStyledButton(String text, String baseColor) {
        javafx.scene.control.Button button = new javafx.scene.control.Button(text);

        // Try to use the Pokémon font if available - with smaller font size
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