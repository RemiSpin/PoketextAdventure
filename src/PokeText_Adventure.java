//import PokemonLogic.Pokemon;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//
//public class PokeText_Adventure {
//    public static void main(String[] args) throws IOException {
//        // Specify the local file path to the image
//        String imagePath = "GIFs/ani_bw_001.gif";
//
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Pokemon Image Display");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//            try {
//                // Load the GIF image into a BufferedImage
//                BufferedImage originalImage = ImageIO.read(new File(imagePath));
//
//                // Get the first frame (index 0)
//                Image firstFrame = originalImage.getSubimage(0, 0, originalImage.getWidth(), originalImage.getHeight());
//
//                ImageIcon icon = new ImageIcon(firstFrame);
//
//                JLabel label = new JLabel(icon);
//                label.setPreferredSize(new Dimension(400, 400));
//
//                frame.getContentPane().add(label, BorderLayout.CENTER);
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//}


import PlayerRelated.Player;
import PokemonLogic.Exceptions;
import PokemonLogic.Pokemon;
import Overworld.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class PokeText_Adventure extends Application {

    private TextArea textArea = new TextArea();
    private TextField inputField = new TextField();
    private Button sendButton = new Button("Send");
    private final PrintStream standardOut = System.out;

    public static void main(String[] args) {
            launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        textArea.setEditable(false);

        Font customFont = Font.loadFont(getClass().getResourceAsStream("WindowThings/RBYGSC.ttf"), 16);

        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));

        textArea.setFont(customFont);
        sendButton.setFont(customFont);
        inputField.setFont(customFont);

        sendButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");

        textArea.setWrapText(true);

        BorderPane root = new BorderPane();
        root.setCenter(textArea);

        HBox inputBox = new HBox();
        inputBox.setAlignment(Pos.CENTER);
        inputBox.getChildren().addAll(inputField, sendButton);

        HBox.setHgrow(inputField, Priority.ALWAYS);

        root.setBottom(inputBox);

        Scene scene = new Scene(root, 700, 700);

        primaryStage.setTitle("PokeText");
        primaryStage.setScene(scene);
        primaryStage.show();

        Pokemon pokemon = null;

        if (getParameters().getRaw().size() == 2) {
            // Parse command line arguments
            String pokemonName = getParameters().getRaw().get(0);
            int pokemonLevel = Integer.parseInt(getParameters().getRaw().get(1));
            // Create a new Pokemon instance
            try {
                pokemon = new Pokemon(pokemonName, pokemonLevel);
            } catch (Exceptions.PokemonLevelException | Exceptions.PokemonNameException e) {
                throw new RuntimeException(e);
            }
        }

        Pallet pallet = new Pallet();
        Player player = new Player();
        Player.setName();
        System.out.println(player.getName());
        player.addPokemonToParty(pokemon);
        System.out.println(pokemon);
        pallet.enter(player);

        openSpriteWindow(pokemon);
    }

    private void openSpriteWindow(Pokemon pokemon) {
        Stage spriteStage = new Stage();
        spriteStage.setTitle("Pokemon Sprite");

        InputStream stream = getClass().getResourceAsStream(pokemon.getSpritePath());
        Image spriteImage = new Image(stream);

        ImageView imageView = new ImageView(spriteImage);

        VBox vBox = new VBox(imageView);
        vBox.setAlignment(Pos.CENTER);

        Scene spriteScene = new Scene(vBox, 300, 300);
        spriteStage.setScene(spriteScene);

        //Blocks interaction with the main window
        spriteStage.initModality(Modality.APPLICATION_MODAL);

        spriteStage.show();
    }

    public static class TextAreaOutputStream extends OutputStream {
        private TextArea textArea;

        public TextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.appendText(String.valueOf((char) b));
        }
    }
}
