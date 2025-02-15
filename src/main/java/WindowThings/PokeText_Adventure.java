package WindowThings;

import java.io.IOException;

import BattleLogic.Battle;
import BattleLogic.Trainer;
import BattleLogic.trainerPokemon;
import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.application.Application;
import javafx.stage.Stage;

@SuppressWarnings("unused")


public class PokeText_Adventure extends Application {
    public static void main(String[] args) {
        Application.launch(mainWindow.class, args);
    }
    public static Player player = new Player();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pokemon bulbasaur = new Pokemon("Bulbasaur", 5);
        Pokemon pidgey = new Pokemon("Pidgey", 3);
        Pokemon rattata = new Pokemon("Rattata", 2);
        Pokemon pikachu = new Pokemon("Pikachu", 4);
        Pokemon mankey = new Pokemon("Mankey", 3);
        Pokemon caterpie = new Pokemon("Caterpie", 4);
        Player.setName();
        player.addPokemonToParty(bulbasaur);
        player.addPokemonToParty(pidgey);
        player.addPokemonToParty(rattata);
        player.addPokemonToParty(pikachu);
        player.addPokemonToParty(mankey);
        player.addPokemonToParty(caterpie);

    //    PokemonInfo biDetailsWindow = new PokemonInfo(bulbasaur);
    //    while (bulbasaur.getLevel() != 16){ bulbasaur.gainExperience(); }
    //    PokemonInfo bDetailsWindow = new PokemonInfo(bulbasaur);

        Battle battleWindow = new Battle(player, new Trainer("Gary", 500, new trainerPokemon("Charmander", 5, "Scratch", "Growl")));
    }
}