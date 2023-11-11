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

//import PokemonLogic.Pokemon;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.TextArea;
//import javafx.scene.text.Font;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PrintStream;
//
//public class PokeText_Adventure extends Application {
//    private TextArea textArea = new TextArea();
//    private PrintStream standardOut = System.out; // Store the original standard output
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws IOException {
//        textArea.setEditable(false);
//
//        // Create a new scene
//        Scene scene = new Scene(textArea, 700, 700);
//
//        // Creating a custom font
//        Font customFont = Font.loadFont(getClass().getResourceAsStream("WindowThings/RBYGSC.ttf"), 16);
//
//        // Set the stage's title
//        primaryStage.setTitle("PokeText");
//
//        // Set Font
//        textArea.setFont(customFont);
//
//        // Set the scene for the stage
//        primaryStage.setScene(scene);
//
//        // Show the stage
//        primaryStage.show();
//
//        // Redirect System.out to the custom TextAreaOutputStream
//        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));
//
//        Pokemon pokemon = new Pokemon("Bulbasaur", 15);
//        pokemon.gainExperience();pokemon.gainExperience();pokemon.gainExperience();pokemon.gainExperience();pokemon.gainExperience();
//
//        System.out.println(pokemon);
//    }
//
//    public static class TextAreaOutputStream extends OutputStream {
//        private TextArea textArea;
//
//        public TextAreaOutputStream(TextArea textArea) {
//            this.textArea = textArea;
//        }
//
//        @Override
//        public void write(int b) throws IOException {
//            textArea.appendText(String.valueOf((char) b));
//        }
//    }
//}

import BattleLogic.Move;
import BattleLogic.moveFactory;
import PokemonLogic.Pokemon;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class PokeText_Adventure {
    public static void main(String[] args) {
        // Load available moves from moves.json
        List<Move> moves = moveFactory.createMovesFromJson();

        // Create an instance of the moveFactory to load Pokémon move sets
        moveFactory factory = new moveFactory();
        factory.loadPokemonLearnsets();

        try {
            // Create a Pokémon (e.g., Bulbasaur) and pass the moves list to it
            Pokemon pokemon = new Pokemon("Bulbasaur", 5);
            pokemon.setMoveFactory(factory);

            // Simulate gaining experience (this should trigger learning moves at certain levels)
            for (int i = 0; i < 23; i++) {
                pokemon.gainExperience();
            }

            // Print the Pokémon's information to check its moveset
            pokemon.displayMoveset();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}