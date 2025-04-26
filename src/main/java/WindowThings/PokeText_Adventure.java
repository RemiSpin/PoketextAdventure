package WindowThings;

import java.io.File;
import java.io.IOException;

import Overworld.Buildings.PlayerHome;
import Overworld.Buildings.PokemonCenter;
import Overworld.Town;
import Overworld.Towns.Pallet;
import Overworld.Towns.Pewter;
import Overworld.Towns.Viridian;
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
                        // Resolve the town by its name
                        startingTown = resolveTownByName(savedTownName);

                        if (startingTown != null) {
                            exploreWindow explorationWindow = new exploreWindow(startingTown);
                            explorationWindow.show();
                            return;
                        }
                    }

                    // Fallback to Pallet Town if town resolution failed
                    startingTown = new Pallet();
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

    /**
     * Resolves a town object based on its name
     * 
     * @param townName The name of the town to resolve
     * @return The resolved town object, or null if not found
     */
    private Town resolveTownByName(String townName) {
        // Check for major towns
        if (townName.equals("Pallet Town")) {
            return new Pallet();
        } else if (townName.equals("Viridian City")) {
            return new Viridian();
        } else if (townName.equals("Pewter City")) {
            return new Pewter();
        }
        // Check for buildings in towns
        else if (townName.equals(Player.getName() + "'s Home")) {
            Pallet pallet = new Pallet();
            return (PlayerHome) pallet.getPokemonCenter();
        } else if (townName.equals("Professor Oak's Laboratory")) {
            Pallet pallet = new Pallet();
            return pallet.getOaksLab();
        } else if (townName.equals("Viridian Pokemon Center")) {
            Viridian viridian = new Viridian();
            PokemonCenter center = viridian.getPokemonCenter();
            if (center instanceof Town) {
                return (Town) center;
            }
        } else if (townName.equals("Pewter Pokemon Center")) {
            Pewter pewter = new Pewter();
            PokemonCenter center = pewter.getPokemonCenter();
            if (center instanceof Town) {
                return (Town) center;
            }
        } else if (townName.equals("Pewter Gym")) {
            Pewter pewter = new Pewter();
            return pewter.getPewterGym();
        }
        // Check for routes - we'll recreate them through their parent towns
        else if (townName.startsWith("Route ")) {
            if (townName.equals("Route 1")) {
                Pallet pallet = new Pallet();
                return pallet.getRoute1();
            } else if (townName.equals("Route 22")) {
                Viridian viridian = new Viridian();
                return viridian.getRoute22();
            } else if (townName.equals("Route 2 South")) {
                Viridian viridian = new Viridian();
                return viridian.getRoute2South();
            } else if (townName.equals("Route 2 North")) {
                Pewter pewter = new Pewter();
                return pewter.getRoute2North();
            }
        }

        // Default to null if town not found
        return null;
    }

    public static void main(String[] args) {
        Application.launch(mainWindow.class, args);
    }

    public static Player player = new Player();
}