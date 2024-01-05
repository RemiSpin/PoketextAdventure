package WindowThings;

import BattleLogic.*;
import Overworld.*;
import PlayerRelated.*;
import PokemonLogic.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class PokeText_Adventure extends Application {
    public static void main(String[] args) {
        Application.launch(mainWindow.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pokemon bulbasaur = new Pokemon("Bulbasaur", 7);
        Pokemon pidgey = new Pokemon("Pidgey", 3);
        Pokemon rattata = new Pokemon("Rattata", 2);
        Pokemon pikachu = new Pokemon("Pikachu", 4);
        Pokemon mankey = new Pokemon("Mankey", 3);
        Pokemon caterpie = new Pokemon("Caterpie", 4);
        Player player = new Player();
        Player.setName();
        System.out.println(Player.getName());
        player.addPokemonToParty(bulbasaur);
        player.addPokemonToParty(pidgey);
        player.addPokemonToParty(rattata);
        player.addPokemonToParty(pikachu);
        player.addPokemonToParty(mankey);
        player.addPokemonToParty(caterpie);

        // Create a new Stage for each PokemonInfo window
        Stage bDetailsStage = new Stage();
        PokemonInfo bDetailsWindow = new PokemonInfo(bulbasaur);
        bDetailsWindow.start(bDetailsStage);

        Stage pidDetailsStage = new Stage();
        PokemonInfo pidDetailsWindow = new PokemonInfo(pidgey);
        pidDetailsWindow.start(pidDetailsStage);

        Stage rDetailsStage = new Stage();
        PokemonInfo rDetailsWindow = new PokemonInfo(rattata);
        rDetailsWindow.start(rDetailsStage);

        Stage pikDetailsStage = new Stage();
        PokemonInfo pikDetailsWindow = new PokemonInfo(pikachu);
        pikDetailsWindow.start(pikDetailsStage);

        Stage mDetailsStage = new Stage();
        PokemonInfo mDetailsWindow = new PokemonInfo(mankey);
        mDetailsWindow.start(mDetailsStage);

        Stage cDetailsStage = new Stage();
        PokemonInfo cDetailsWindow = new PokemonInfo(caterpie);
        cDetailsWindow.start(cDetailsStage);

        // Create a new Stage for the Battle window
        Stage battleStage = new Stage();
        Battle battleWindow = new Battle(player, new Trainer("Gary", 500, new trainerPokemon("Charmander", 5, "Scratch", "Growl")));
        battleWindow.start(battleStage);
    }
}
