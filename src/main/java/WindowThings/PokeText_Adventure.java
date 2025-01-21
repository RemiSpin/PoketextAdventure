package WindowThings;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import BattleLogic.Battle;
import BattleLogic.Trainer;
import BattleLogic.trainerPokemon;
import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import PokemonLogic.PokemonInfo;
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
        System.out.println(Player.getName());
        player.addPokemonToParty(bulbasaur);
        player.addPokemonToParty(pidgey);
        player.addPokemonToParty(rattata);
        player.addPokemonToParty(pikachu);
        player.addPokemonToParty(mankey);
        player.addPokemonToParty(caterpie);

    //    while (bulbasaur.getLevel() != 16){
    //        bulbasaur.gainExperience();
    //    }

        PokemonInfo bDetailsWindow = new PokemonInfo(bulbasaur);
        PokemonInfo pidDetailsWindow = new PokemonInfo(pidgey);
        PokemonInfo rDetailsWindow = new PokemonInfo(rattata);
        PokemonInfo pikDetailsWindow = new PokemonInfo(pikachu);
        PokemonInfo mDetailsWindow = new PokemonInfo(mankey);
        PokemonInfo cDetailsWindow = new PokemonInfo(caterpie);

        // Create a new Stage for the Battle window
        Stage battleStage = new Stage();
        Battle battleWindow = new Battle(player, new Trainer("Gary", 500, new trainerPokemon("Charmander", 5, "Scratch", "Growl")));
        battleWindow.start(battleStage);
        
        try {
            // Establish a connection
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "savegame.db";
            Connection conn = DriverManager.getConnection(url);

            // Create a Statement
            Statement stmt = conn.createStatement();

            // Execute a query
            ResultSet rs = stmt.executeQuery("SELECT * FROM party_pokemon");

            // Iterate over the ResultSet
            while (rs.next()) {
                // Retrieve the data
                String pokemon_id = rs.getString("pokemon_id");
                String name = rs.getString("name");
                String nickname = rs.getString("nickname");
                int level = rs.getInt("level");

                // Display the data
                System.out.println("Pokemon ID: " + pokemon_id);
                System.out.println("Name: " + name);
                System.out.println("Nickname: " + nickname);
                System.out.println("Level: " + level);
            }

            // Close the connection
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}