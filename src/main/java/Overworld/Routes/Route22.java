package Overworld.Routes;

import java.io.IOException;

import BattleLogic.Battle;
import BattleLogic.Trainer;
import BattleLogic.trainerPokemon;
import Overworld.Route;
import Overworld.Town;
import PlayerRelated.Player;
import WindowThings.mainWindow;

public class Route22 extends Route {
    // Flag to track if the rival battle has already occurred
    private static boolean rivalBattleOccurred = false;

    public Route22(Town viridianCity) {
        super("Route 22",
                "A rugged path leading from Viridian City toward Indigo Plateau.",
                "R22.png",
                viridianCity,
                null); // The Indigo Plateau will be connected here when implemented
    }

    @Override
    public String getInitialEntryMessage() {
        return "A winding path stretches before you, marking the beginning of the journey to the Indigo Plateau. " +
                "The terrain is rugged, with tall grass swaying in the breeze, hiding wild Pokémon eager for battle. " +
                "In the distance, you can see the silhouette of mountains where the Pokémon League awaits challengers "
                +
                "who prove their worth.";
    }

    @Override
    public void enter(Player player) {
        super.enter(player);

        // Check if this is the first time entering Route 22
        if (!rivalBattleOccurred) {
            // Slight delay to allow the player to read the entry message first
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> startRivalBattle(player));
                        }
                    },
                    1500);
        }
    }

    private void startRivalBattle(Player player) {
        // Check if player has any usable Pokémon before starting trainer battle
        if (!player.hasUsablePokemon()) {
            mainWindow.appendToOutput("You have no Pokémon that can battle! All your Pokémon have fainted!");
            // Show Gary's dialogue but don't start battle
            mainWindow.appendToOutput("As you explore Route 22, a familiar voice calls out.");
            mainWindow.appendToOutput("Gary: Hey! " + Player.getName() + "!", "green");
            mainWindow.appendToOutput(
                    "Gary: What? All your Pokémon are fainted? How pathetic! Come back when you're actually prepared for a battle!",
                    "green");
            return;
        }

        try {
            // Get the player's starter choice to determine rival's Pokemon
            String playerStarter = player.getChosenStarter();
            if (playerStarter == null || playerStarter.isEmpty()) {
                // If for some reason we don't have the starter info, default to Bulbasaur
                playerStarter = "Bulbasaur";
            }

            // Gary's Pokemon is based on type advantage against player's starter
            trainerPokemon rivalStarter;
            trainerPokemon pidgey = new trainerPokemon("Pidgey", 9, "Tackle", "Sand Attack");

            if (playerStarter.equals("Bulbasaur")) {
                // Fire beats Grass
                rivalStarter = new trainerPokemon("Charmander", 9, "Scratch", "Growl");
            } else if (playerStarter.equals("Charmander")) {
                // Water beats Fire
                rivalStarter = new trainerPokemon("Squirtle", 9, "Tackle", "Tail Whip");
            } else {
                // Grass beats Water
                rivalStarter = new trainerPokemon("Bulbasaur", 9, "Tackle", "Growl");
            }

            // Show encounter dialogue
            mainWindow.appendToOutput("As you explore Route 22, a familiar voice calls out.");
            mainWindow.appendToOutput("Gary: Hey! " + Player.getName() + "!", "green");
            mainWindow.appendToOutput("You turn to see Gary approaching with a confident smirk.");
            mainWindow.appendToOutput(
                    "Gary: You're heading to the Pokémon League? Forget it! You probably don't even have any badges!",
                    "green");
            mainWindow.appendToOutput(
                    "Gary: The guard won't let you through without them, and frankly, you'd never get them anyway!",
                    "green");
            mainWindow.appendToOutput("Gary's eyes narrow as he readies a Poké Ball.");
            mainWindow.appendToOutput("Gary: I'll show you just how outclassed you really are. Go!", "green");

            // Create post-battle dialogue
            String postBattleDialogue = "";

            // Create trainer with rival's Pokemon
            Trainer rival = new Trainer("Gary", 144, pidgey, rivalStarter);

            // Start the battle with field background (Route 22)
            Battle battleWindow = new Battle(player, rival, postBattleDialogue, "field");

            // Mark the battle as occurred
            rivalBattleOccurred = true;
        } catch (IOException e) {
            // Handle the IOException from trainerPokemon constructor
            System.err.println("Error creating rival battle: " + e.getMessage());
            mainWindow.appendToOutput("There was an issue with your rival. They'll challenge you another time.");
            e.printStackTrace();
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("Unexpected error in rival battle: " + e.getMessage());
            mainWindow.appendToOutput("Your rival appears to be busy. They'll challenge you later.");
            e.printStackTrace();
        }
    }

    // Reset method for testing purposes or new game
    public static void resetRivalBattleFlag() {
        rivalBattleOccurred = false;
    }

    // Getter to check if rival battle has occurred (for save/load)
    public static boolean hasRivalBattleOccurred() {
        return rivalBattleOccurred;
    }

    // Setter for rival battle state (for loading saved games)
    public static void setRivalBattleOccurred(boolean occurred) {
        rivalBattleOccurred = occurred;
    }
}
