package Overworld.Routes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import BattleLogic.Battle;
import BattleLogic.Trainer;
import BattleLogic.trainerPokemon;
import Overworld.Route;
import Overworld.Town;
import PlayerRelated.Player;
import WindowThings.mainWindow;

public class ViridianForest extends Route {
    private Route2North route2North;

    // List to track all trainers in the forest
    private List<Trainer> forestTrainers;

    // Track which trainers have been defeated
    private List<Boolean> defeatedTrainers;

    // Current trainer index
    private int currentTrainerIndex = 0;

    public ViridianForest(Town route2South, Town route2North) {
        super("Viridian Forest",
                "A dense forest full of Bug-type Pokémon and winding paths.",
                "ViridianForest.png",
                route2South,
                route2North);

        // Increase encounter chance in the forest
        setEncounterChance(40);

        // Initialize trainers
        initializeTrainers();
    }

    @Override
    public String getInitialEntryMessage() {
        return "You enter the sprawling maze of trees that is Viridian Forest. Dappled sunlight filters through the " +
                "dense canopy, and the air is filled with the sounds of rustling leaves and buzzing insects. " +
                "The paths wind between the massive tree trunks, and you can glimpse movement in the undergrowth all around. "
                +
                "Bug catchers and other trainers often come here to train and catch Bug-type Pokémon.";
    }

    // Add a method to get/create Route 2 North
    public Route2North getRoute2North() {
        if (route2North == null) {
            route2North = new Route2North(this);
            // Set destination2 to Route 2 North
            super.destination2 = route2North;
        }
        return route2North;
    }

    // Initialize all trainers in Viridian Forest
    private void initializeTrainers() {
        forestTrainers = new ArrayList<>();
        defeatedTrainers = new ArrayList<>();

        try {
            trainerPokemon weedle1 = new trainerPokemon("Weedle", 7, "Poison Sting", "String Shot");
            trainerPokemon kakuna = new trainerPokemon("Kakuna", 7, "Harden");
            trainerPokemon weedle2 = new trainerPokemon("Weedle", 7, "Poison Sting", "String Shot");
            forestTrainers.add(new Trainer("Bug Catcher Doug", 84, weedle1, kakuna, weedle2));

            trainerPokemon weedle3 = new trainerPokemon("Weedle", 6, "Poison Sting", "String Shot");
            trainerPokemon caterpie1 = new trainerPokemon("Caterpie", 6, "Tackle", "String Shot");
            forestTrainers.add(new Trainer("Bug Catcher Rick", 74, weedle3, caterpie1));

            trainerPokemon caterpie2 = new trainerPokemon("Caterpie", 7, "Tackle", "String Shot");
            trainerPokemon caterpie3 = new trainerPokemon("Caterpie", 8, "Tackle", "String Shot");
            forestTrainers.add(new Trainer("Bug Catcher Anthony", 96, caterpie2, caterpie3));

            trainerPokemon metapod1 = new trainerPokemon("Metapod", 7, "Harden");
            trainerPokemon caterpie4 = new trainerPokemon("Caterpie", 7, "Tackle", "String Shot");
            trainerPokemon metapod2 = new trainerPokemon("Metapod", 7, "Harden");
            forestTrainers.add(new Trainer("Bug Catcher Charlie", 84, metapod1, caterpie4, metapod2));

            trainerPokemon weedle4 = new trainerPokemon("Weedle", 9, "Poison Sting", "String Shot");
            forestTrainers.add(new Trainer("Bug Catcher Sammy", 108, weedle4));

            // Initialize all trainers as undefeated
            for (int i = 0; i < forestTrainers.size(); i++) {
                defeatedTrainers.add(false);
            }

        } catch (IOException e) {
            System.err.println("Error creating forest trainers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get the next undefeated trainer
    public Trainer getNextTrainer() {
        if (currentTrainerIndex >= forestTrainers.size()) {
            return null;
        }
        return forestTrainers.get(currentTrainerIndex);
    }

    // Mark the current trainer as defeated and move to the next one
    public void defeatCurrentTrainer() {
        if (currentTrainerIndex < defeatedTrainers.size()) {
            defeatedTrainers.set(currentTrainerIndex, true);
            currentTrainerIndex++;
        }
    }

    // Check if all trainers have been defeated
    public boolean areAllTrainersDefeated() {
        for (Boolean defeated : defeatedTrainers) {
            if (!defeated) {
                return false;
            }
        }
        return true;
    }

    // Get the current trainer's index
    public int getCurrentTrainerIndex() {
        return currentTrainerIndex;
    }

    // Get total number of trainers
    public int getTotalTrainers() {
        return forestTrainers.size();
    }

    // Reset all trainers (for testing or new game)
    public void resetTrainers() {
        for (int i = 0; i < defeatedTrainers.size(); i++) {
            defeatedTrainers.set(i, false);
        }
        currentTrainerIndex = 0;
    }

    // Start a battle with the current trainer
    public void startTrainerBattle(Player player) {
        // Check if player has any usable Pokémon before starting trainer battle
        if (!player.hasUsablePokemon()) {
            mainWindow.appendToOutput("You don't think it's a great idea.");
            return;
        }

        Trainer currentTrainer = getNextTrainer();
        if (currentTrainer != null) {
            try {
                // Show pre-battle dialogue based on which trainer it is
                String trainerName = currentTrainer.getName();
                mainWindow.appendToOutput(trainerName + " wants to battle!");

                // Start the battle with forest background
                Battle battleWindow = new Battle(player, currentTrainer, "", "forest");

                // The trainer will be marked as defeated in the Battle class
                defeatCurrentTrainer();

            } catch (Exception e) {
                System.err.println("Error starting trainer battle: " + e.getMessage());
                mainWindow.appendToOutput("There was a problem with the trainer battle. Please try again.");
                e.printStackTrace();
            }
        } else {
            mainWindow.appendToOutput("There are no more trainers to battle in this area!");
        }
    }

    // Getter for the defeated trainers list (for save/load)
    public List<Boolean> getDefeatedTrainers() {
        return defeatedTrainers;
    }

    // Setter for trainer defeated states (for loading saved games)
    public void setDefeatedTrainers(List<Boolean> defeatedStates) {
        if (defeatedStates == null || defeatedStates.isEmpty()) {
            return;
        }

        // Copy the states to our list
        for (int i = 0; i < Math.min(defeatedTrainers.size(), defeatedStates.size()); i++) {
            defeatedTrainers.set(i, defeatedStates.get(i));
        }

        // Recalculate the current trainer index based on defeated states
        currentTrainerIndex = 0;
        for (Boolean defeated : defeatedTrainers) {
            if (defeated) {
                currentTrainerIndex++;
            } else {
                break;
            }
        }
    }

    // Method to set the current trainer index directly (for loading saved games)
    public void setCurrentTrainerIndex(int index) {
        this.currentTrainerIndex = Math.min(index, forestTrainers.size());
    }
}
