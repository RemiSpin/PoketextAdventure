package WindowThings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import PlayerRelated.Player;
import PlayerRelated.SaveGame;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class mainWindow extends Application {
    private TextArea textArea = new TextArea();
    private TextField inputField = new TextField();
    private Button sendButton = new Button("Send");

//    // Define the application states
//    public enum AppState {
//        BATTLE
//    }
//
//    // Add a variable for the current state
//    private AppState currentState = AppState.BATTLE; // Set the initial state to BATTLE

    @Override
    public void start(Stage primaryStage) throws IOException {
        Player player = PokeText_Adventure.player;
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 700, 700);

        textArea.setEditable(false);
        Font customFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 16);

        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));

        textArea.setFont(customFont);
        sendButton.setFont(customFont);
        inputField.setFont(customFont);

        sendButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        textArea.setWrapText(true);

        root.setCenter(textArea);

        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().addAll(inputField, sendButton);

        HBox.setHgrow(inputField, Priority.ALWAYS);
        root.setBottom(inputBox);

        primaryStage.setTitle("PokeText");
        primaryStage.setScene(scene);
        primaryStage.show();

        PokeText_Adventure pokeTextAdventure = new PokeText_Adventure();
        pokeTextAdventure.start(primaryStage);

        // Handle input when Send button is clicked
        sendButton.setOnAction(e -> processInput());
        
        // Handle input when Enter is pressed
        inputField.setOnAction(e -> processInput());
    }

    private void processInput() {
        String input = inputField.getText().trim().toLowerCase();
        inputField.clear();

        if (input.equals("save")) {
            SaveGame saveGame = new SaveGame(PokeText_Adventure.player);
            saveGame.saveGame();
        }
        // Add other command handling here
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