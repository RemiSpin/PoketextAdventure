package WindowThings;

import BattleLogic.Battle;
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class mainWindow extends Application {

    private TextArea textArea = new TextArea();
    private TextField inputField = new TextField();
    private Button sendButton = new Button("Send");

    // Define the application states
    public enum AppState {
        BATTLE
    }

    // Add a variable for the current state
    private AppState currentState = AppState.BATTLE; // Set the initial state to BATTLE

    @Override
    public void start(Stage primaryStage) throws IOException {
        BorderPane root = new BorderPane(); // Segments the window
        Scene scene = new Scene(root, 700, 700);

        textArea.setEditable(false);

        Font customFont = Font.loadFont(getClass().getResourceAsStream("Assets/RBYGSC.ttf"), 16);

        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));

        textArea.setFont(customFont);
        sendButton.setFont(customFont);
        inputField.setFont(customFont);

        sendButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        textArea.setWrapText(true);

        root.setCenter(textArea);

        HBox inputBox = new HBox(); // Makes input and button stick horizontally
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().addAll(inputField, sendButton);

        HBox.setHgrow(inputField, Priority.ALWAYS); // Makes the Hbox expand

        root.setBottom(inputBox); // Puts the bottom things

        primaryStage.setTitle("PokeText");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Call the start method of PokeText_Adventure
        PokeText_Adventure pokeTextAdventure = new PokeText_Adventure();
        pokeTextAdventure.start(primaryStage);

        sendButton.setOnAction(e -> {
            String input = inputField.getText();
            inputField.clear();

            // Process the input as a command
            switch (currentState) {
                case BATTLE:
                    // Call the startBattle method with the user's input
                    battle.startBattle(input);
                    break;
            }
        });
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