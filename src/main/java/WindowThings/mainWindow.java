package WindowThings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import PlayerRelated.Player;
import PlayerRelated.SaveGame;
import PokemonLogic.Pokemon;
import PokemonLogic.PokemonInfo;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@SuppressWarnings({"FieldMayBeFinal", "unused"})

public class mainWindow extends Application {
    private TextArea textArea = new TextArea();
    private TextField inputField = new TextField();

    @Override
    public void start(Stage primaryStage) throws IOException {
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
            System.out.println("- " + Player.getName() + " : Shows your trainer information");
            System.out.println("- Help : Shows this help message");
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
                String requestedNickname = input.substring(8); // Remove "pokemon " prefix
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

    public static class TextAreaOutputStream extends OutputStream {
        private TextArea textArea;

        public TextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            textArea.appendText(String.valueOf((char) b));
        }
    }
}