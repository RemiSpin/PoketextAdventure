package Overworld.Buildings;

import Overworld.Town;
import Overworld.Towns.Pallet;
import PlayerRelated.Player;
import WindowThings.PokeText_Adventure;
import WindowThings.StarterSelectionWindow;

/**
 * Represents Professor Oak's Laboratory in Pallet Town.
 * This is where new trainers can get their starter Pokémon.
 */
public class OaksLab implements Town {
    private final String name;
    private final String description;
    private final String imageFile = "OakLab.png";

    public OaksLab(String name, String description) {
        this.name = name;
        this.description = description;
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
        WindowThings.mainWindow.appendToOutput(player.getName() + " enters " + getName() + ".");

        // Check if this is the player's first visit
        if (!player.hasVisitedTown(getName())) {
            // Mark as visited to prevent showing the starter selection again
            player.addVisitedTown(getName());

            // Show the first-time entry message with Gary
            WindowThings.mainWindow.appendToOutput(
                    "Professor Oak: Ah, " + player.getName() + "! There you are! I've been waiting for you!", "blue");

            WindowThings.mainWindow.appendToOutput(
                    "A boy with spiky hair turns around and gives you a dismissive look.");

            WindowThings.mainWindow.appendToOutput(
                    "Gary: Hmph! Took you long enough, " + player.getName() + "! I'm ready to get my Pokémon!",
                    "green");

            WindowThings.mainWindow.appendToOutput(
                    "Professor Oak: Now, now, Gary. Be patient. You'll both get to choose your partner.", "blue");

            WindowThings.mainWindow.appendToOutput(
                    "Professor Oak: It's time for you to choose!", "blue");

            // Only show starter selection if player doesn't already have Pokemon
            if (player.getParty().isEmpty()) {
                // Use JavaFX Platform.runLater to ensure UI updates properly
                javafx.application.Platform.runLater(() -> {
                    StarterSelectionWindow starterWindow = new StarterSelectionWindow(PokeText_Adventure.player);
                    starterWindow.show();
                });
            } else {
                WindowThings.mainWindow.appendToOutput(
                        "Professor Oak: I see you already have a Pokémon with you! Take good care of it!", "blue");
            }
        } else {
            // Regular entry message for subsequent visits
            WindowThings.mainWindow.appendToOutput(
                    "The lab is filled with high-tech equipment and books. Professor Oak is busy working on his research.");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return null;
    }

    @Override
    public String getInitialEntryMessage() {
        return "You step into Professor Oak's Laboratory. The air hums with the sound of advanced research equipment. "
                +
                "Shelves line the walls, filled with books and research papers about Pokémon. " +
                "This is where new trainers begin their journey by receiving their first Pokémon.";
    }

    /**
     * Returns to Pallet Town.
     * 
     * @return The parent town (Pallet)
     */
    public Pallet getParentTown() {
        return new Pallet();
    }
}
