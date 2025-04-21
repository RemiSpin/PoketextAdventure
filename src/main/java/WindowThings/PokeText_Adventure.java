package WindowThings;

import java.io.File;
import java.io.IOException;

import Overworld.Buildings.PlayerHome;
import Overworld.Town;
import Overworld.Towns.Pallet;
import PlayerRelated.LoadGame;
import PlayerRelated.Player;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

@SuppressWarnings("unused")

public class PokeText_Adventure extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Check if a save file exists
        File saveFile = new File("savegame.db");
        if (saveFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Load Game");
            alert.setHeaderText("Save game found");
            alert.setContentText("Would you like to load your saved game?");

            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

            if (result == ButtonType.OK) {
                LoadGame loadGame = new LoadGame();
                Player loadedPlayer = loadGame.loadGame();
                if (loadedPlayer != null) {
                    player = loadedPlayer;

                    // Create the correct town based on saved location
                    Town startingTown;
                    String savedTownName = loadedPlayer.getCurrentTownName();

                    if (savedTownName != null) {
                        if (savedTownName.equals(Player.getName() + "'s Home")) {
                            // If saved in player's home
                            Pallet pallet = new Pallet();
                            startingTown = (PlayerHome) pallet.getPokemonCenter();
                        } else {
                            // Default to Pallet Town for now
                            startingTown = new Pallet();
                        }
                    } else {
                        // Fallback if no town name was saved
                        startingTown = new Pallet();
                    }

                    exploreWindow explorationWindow = new exploreWindow(startingTown);
                    explorationWindow.show();
                    return;
                }
            }
        }

        Player.setName();

        // Create Pallet Town first to access Player's Home
        Pallet palletTown = new Pallet();
        // Get the PlayerHome instance from Pallet Town
        PlayerHome playerHome = (PlayerHome) palletTown.getPokemonCenter();

        // Set the current town name in the player object
        player.setCurrentTownName(Player.getName() + "'s Home");

        // Start the game in Player's Home
        exploreWindow explorationWindow = new exploreWindow(playerHome);
        explorationWindow.show();
    }

    public static void main(String[] args) {
        Application.launch(mainWindow.class, args);
    }

    public static Player player = new Player();
}