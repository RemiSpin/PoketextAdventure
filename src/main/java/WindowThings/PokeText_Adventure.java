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

public class PokeText_Adventure extends Application {
    public static void main(String[] args) {
        Application.launch(mainWindow.class, args);
    }
    public static Player player = new Player();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pokemon bulbasaur = new Pokemon("Bulbasaur", 14);
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
                // The column names should be replaced with the actual column names in your party_pokemon table
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

// i have
// the following
// unimplemented animation:
// // Create the entry animation
// TranslateTransition entryAnimation = new TranslateTransition(Duration.millis(1800),
//         controlsBox);entryAnimation.setFromY(scene.getHeight());entryAnimation.setToY(0);entryAnimation.setInterpolator(Interpolator.EASE_IN);

// // Create the stopping animation
// TranslateTransition stopAnimation = new TranslateTransition(Duration.millis(1800),
//         controlsBox);stopAnimation.setToY(0);stopAnimation.setInterpolator(Interpolator.EASE_OUT);

// // Ensure starting position
// controlsBox.setTranslateY(scene.getHeight());

// I want
// it to
// mimic the
// following animations
// exactly when
// they appear
// on screen:if(pokemon==player.getCurrentPokemon())
// {
//     pokemonButton.setStyle(pokemonButtonStyle);
//     pokemonButton.setOnAction(event -> {
//         TranslateTransition closeAnim = new TranslateTransition(Duration.millis(1800), switchBox);
//         closeAnim.setToY(scene.getHeight());
//         closeAnim.setInterpolator(Interpolator.EASE_IN);
//         closeAnim.setOnFinished(evt -> {
//             controlsBox.getChildren().clear();
//             controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
//         });
//         closeAnim.play();
//         Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(1400), ae -> {
//             closeAnim.stop();
//             controlsBox.getChildren().clear();
//             controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
//         }));
//         timeline1.play();
//     });
// }else
// {
//     pokemonButton.setStyle(pokemonButtonStyle);
//     pokemonButton.setOnAction(event -> {
//         TranslateTransition closeAnim = new TranslateTransition(Duration.millis(1800), switchBox);
//         closeAnim.setToY(scene.getHeight());
//         closeAnim.setInterpolator(Interpolator.EASE_IN);
//         closeAnim.setOnFinished(evt -> {
//             player.setCurrentPokemon(pokemon);
//             updateBattleUI();
//             controlsBox.getChildren().clear();
//             controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
//         });
//         closeAnim.play();
//         Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(400), ae -> {
//             closeAnim.stop();
//             player.setCurrentPokemon(pokemon);
//             playerPokemonNickname.setText(pokemon.getNickname());
//             Image newSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
//             playerPokemonView.setImage(newSprite);
//             updateBattleUI();
//             controlsBox.getChildren().clear();
//             controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
//         }));
//         timeline1.play();
//     });
// }switchBox.getChildren().add(pokemonButton);}controlsBox.getChildren().clear();controlsBox.getChildren().add(switchBox);

// TranslateTransition tt = new TranslateTransition(Duration.millis(1800),
//         switchBox);tt.setToY(-20);tt.setInterpolator(Interpolator.EASE_OUT);tt.jumpTo(Duration.millis(1400));tt.play();
// });