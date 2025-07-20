package Overworld;

import java.util.Random;

import BattleLogic.Battle;
import Overworld.Buildings.PokemonCenter;
import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.application.Platform;

@SuppressWarnings({ "FieldMayBeFinal", "unused", "static-access" })

public class Route implements Town {
    private String name;
    private String description;
    private String imageFile;
    private Town destination1;
    protected Town destination2;
    private Random random = new Random();
    private int encounterChance = 30;

    public Route(String name, String description, String imageFile,
            Town destination1, Town destination2) {
        this.name = name;
        this.description = description;
        this.imageFile = imageFile;
        this.destination1 = destination1;
        this.destination2 = destination2;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageFile() {
        return imageFile;
    }

    @Override
    public void enter(Player player) {
        if (!player.hasVisitedTown(getName())) {
            WindowThings.mainWindow.appendToOutput(getInitialEntryMessage());
            player.addVisitedTown(getName());
        } else {
            WindowThings.mainWindow.appendToOutput(player.getName() + " travels along " + getName() + ".");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return null;
    }

    @Override
    public String getInitialEntryMessage() {
        return null;
    }

    public Town getDestination1() {
        return destination1;
    }

    public Town getDestination2() {
        return destination2;
    }

    public void setEncounterChance(int chance) {
        this.encounterChance = Math.max(0, Math.min(100, chance));
    }

    private void triggerWildPokemonEncounter(Player player) {
        // Check if player has any usable Pokémon before triggering encounter
        if (!player.hasUsablePokemon()) {
            WindowThings.mainWindow.appendToOutput("You don't think it's a great idea.");
            return;
        }

        // Get a random Pokemon from the encounter pool
        Pokemon wildPokemon = EncounterPool.getRandomEncounter(name);

        if (wildPokemon == null) {
            return;
        }

        WindowThings.mainWindow.appendToOutput("A wild " + wildPokemon.getName() + " (Lv. " +
                wildPokemon.getLevel() + ") appeared!");

        // Start battle in a separate thread to not block UI
        Platform.runLater(() -> {
            try {
                // Use the constructor for wild battles with location-specific background
                // Default "field" for most routes, special cases handled by name
                String battleLocation = "field";

                // Check for special locations
                if (name.contains("Viridian Forest")) {
                    battleLocation = "forest";
                }

                Battle battle = new Battle(player.getCurrentPokemon(), wildPokemon, player, true, null, battleLocation);
            } catch (Exception e) {
                System.out.println("Error starting battle: " + e.getMessage());
            }
        });
    }
}