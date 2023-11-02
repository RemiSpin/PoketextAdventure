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
////        // Specify the local file path to the image
////        String imagePath = "GIFs/ani_bw_001.gif";
////
////        SwingUtilities.invokeLater(() -> {
////            JFrame frame = new JFrame("Pokemon Image Display");
////            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////
////            try {
////                // Load the GIF image into a BufferedImage
////                BufferedImage originalImage = ImageIO.read(new File(imagePath));
////
////                // Get the first frame (index 0)
////                Image firstFrame = originalImage.getSubimage(0, 0, originalImage.getWidth(), originalImage.getHeight());
////
////                ImageIcon icon = new ImageIcon(firstFrame);
////
////                JLabel label = new JLabel(icon);
////                label.setPreferredSize(new Dimension(400, 400));
////
////                frame.getContentPane().add(label, BorderLayout.CENTER);
////                frame.pack();
////                frame.setLocationRelativeTo(null);
////                frame.setVisible(true);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
//        Pokemon pokemon = new Pokemon("Charmander", 15);
//        System.out.println(pokemon);
//    }
//}

import PokemonLogic.Pokemon;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class PokeText_Adventure extends Application {
    private TextArea textArea = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        textArea.setEditable(false);

        // Font font = Font.loadFont(getClass().getResourceAsStream("src/WindowThings/RBYGSC.ttf"), 14);
        // textArea.setFont(font);

        // Load the font
        Font font = Font.loadFont(new FileInputStream("C:\\Users\\Darian\\IdeaProjects\\PokeText Adventure\\src\\WindowThings\\RBYGSC.ttf"), 14);
        textArea.setFont(font);

        // Create a new scene and add the TextArea to it
        Scene scene = new Scene(textArea, 400, 500);

        // Set the stage's title
        primaryStage.setTitle("PokeText");

        // Set the scene for the stage
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();

        // Redirect System.out to the TextArea
        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));

        Pokemon pokemon = new Pokemon("Charmander", 15);
        System.out.println(pokemon);

        // Set line spacing programmatically
        setLineSpacing(textArea, 4);
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

    // Programmatically set line spacing for a TextArea
    private void setLineSpacing(TextArea textArea, double spacing) {
        for (Node node : textArea.lookupAll(".text")) {
            if (node instanceof Text) {
                Text textNode = (Text) node;
                textNode.setLineSpacing(spacing);
            }
        }
    }
}
