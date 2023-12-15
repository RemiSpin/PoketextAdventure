package WindowThings;

import BattleLogic.*;
import Overworld.*;
import PlayerRelated.*;
import PokemonLogic.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class PokeText_Adventure extends Application {
    public static void main(String[] args) {
        Application.launch(mainWindow.class, args);
    }

@Override
public void start(Stage primaryStage) throws IOException {
    Pokemon pokemon = new Pokemon("Bulbasaur", 5);

    Player player = new Player();
    Player.setName();
    System.out.println(player.getName());
    player.addPokemonToParty(pokemon);
    System.out.println(pokemon);
    Battle battleWindow = new Battle(player, new Trainer("Gary", 500, new trainerPokemon("Charmander", 5, "Scratch")));
    battleWindow.start(new Stage());
}

//    private void openSpriteWindow(Pokemon pokemon) {
//        Stage spriteStage = new Stage();
//        spriteStage.setTitle("Pokemon Sprite");
//
//        InputStream stream = getClass().getResourceAsStream(pokemon.getSpritePath());
//        Image spriteImage = new Image(stream);
//
//        ImageView imageView = new ImageView(spriteImage);
//
//        VBox vBox = new VBox(imageView);
//        vBox.setAlignment(Pos.CENTER);
//
//        Scene spriteScene = new Scene(vBox, 300, 300);
//        spriteStage.setScene(spriteScene);
//
//        spriteStage.show();
//    }
}
