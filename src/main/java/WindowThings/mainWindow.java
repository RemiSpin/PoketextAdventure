package WindowThings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import Overworld.Town;
import PlayerRelated.Player;
import PlayerRelated.SaveGame;
import PokemonLogic.Pokemon;
import PokemonLogic.PokemonInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@SuppressWarnings({ "FieldMayBeFinal", "unused" })

public class mainWindow extends Application {
    private static TextArea textArea = new TextArea();
    private TextField inputField = new TextField();
    private static Stage primaryStage;

    // Getter for the primary stage
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void appendToOutput(String message) {
        Platform.runLater(() -> {
            textArea.appendText("> " + message + "\n");
            // Auto-scroll to bottom
            textArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        mainWindow.primaryStage = primaryStage;
        Player player = PokeText_Adventure.player;
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 700, 700);

        textArea.setEditable(false);
        Font customFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 16);

        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));

        textArea.setFont(customFont);
        inputField.setFont(customFont);
        textArea.setWrapText(true);

        root.setCenter(textArea);

        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().addAll(inputField);

        HBox.setHgrow(inputField, Priority.ALWAYS);
        root.setBottom(inputBox);

        primaryStage.setTitle("PokeText");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Display welcome message in the main text area BEFORE starting the game
        appendToOutput("Welcome to PokeText Adventure!");
        appendToOutput("");

        // Add a personalized welcome once the player name is set
        appendToOutput("Type 'help' to see a list of available commands.");

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
                System.out.println("Available commands:");
                System.out.println("- Save : Saves your current game progress");
                System.out.println("- Pokemon : Shows a list of Pokemon in your party");
                System.out.println("- Pokemon (nickname) : Shows detailed stats for a specific Pokemon");
                System.out.println("- Area : Shows information about your current location");
                System.out.println("- " + Player.getName() + " : Shows your trainer information");
                System.out.println("- Help : Shows this help message");
            }
            case "area" -> {
                if (exploreWindow.playerCurrentTown != null) {
                    Town currentTown = exploreWindow.playerCurrentTown;
                    System.out.println("Current location: " + currentTown.getName());
                    System.out.println(currentTown.getDescription());

                    // Also show Pokemon Center information if available
                    if (currentTown.getPokemonCenter() != null) {
                        System.out.println("\nFacilities:");
                        System.out.println("- " + currentTown.getPokemonCenter().getName() + ": " +
                                currentTown.getPokemonCenter().getDescription());
                    }
                } else {
                    System.out.println("You are not currently in any town.");
                }
            }
            case "pokemon" -> {
                System.out.println("Your Pokemon:");
                for (Pokemon pokemon : PokeText_Adventure.player.getParty()) {
                    System.out.println("- " + pokemon.getNickname());
                }
                System.out.println("\nIf you wish to see the stats of a pokemon, say: \nPokemon (Nickname)!");
            }
            default -> {
                if (input.equals(Player.getName().toLowerCase())) {
                    System.out.println("Trainer Information:");
                    System.out.println("Name: " + Player.getName());
                    System.out.println("Money: $" + PokeText_Adventure.player.getMoney());
                    System.out.println("Badges: " + PokeText_Adventure.player.getBadges());

                    int partySize = PokeText_Adventure.player.getParty().size();
                    int pcSize = PokeText_Adventure.player.getPC().size();
                    System.out.println("Total Pokemon: " + (partySize + pcSize));
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
                        System.out.println("Could not find a Pokemon with nickname: " + requestedNickname);
                    }
                } else {
                    System.out.println("Command doesn't exist! Type 'help' to see a list of available commands.");
                }
            }
        }
    }

    // Modify the TextAreaOutputStream
    public static class TextAreaOutputStream extends OutputStream {
        private TextArea textArea;
        private StringBuilder buffer = new StringBuilder();

        public TextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            char ch = (char) b;
            buffer.append(ch);

            // When we get a newline, append the complete line to the text area
            if (ch == '\n') {
                final String text = buffer.toString();
                Platform.runLater(() -> {
                    // Add the ">" prefix to the beginning of the line
                    textArea.appendText(text.startsWith("> ") ? text : "> " + text);
                    // Auto-scroll to bottom
                    textArea.setScrollTop(Double.MAX_VALUE);
                });
                buffer.setLength(0);
            }
        }

        @Override
        public void flush() throws IOException {
            if (buffer.length() > 0) {
                final String text = buffer.toString();
                Platform.runLater(() -> {
                    // Add the ">" prefix to the beginning of the line
                    textArea.appendText(text.startsWith("> ") ? text : "> " + text);
                    textArea.setScrollTop(Double.MAX_VALUE);
                });
                buffer.setLength(0);
            }
        }
    }
}