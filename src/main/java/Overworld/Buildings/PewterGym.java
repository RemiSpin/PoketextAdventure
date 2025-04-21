package Overworld.Buildings;

import Overworld.Town;
import PlayerRelated.Player;
import WindowThings.mainWindow;

public class PewterGym implements Town {
    private final String name = "Pewter Gym";
    private final String description = "A gym specializing in Rock-type Pokémon. Its leader is Brock.";
    private final String imageFile = "PewterGym.png";
    private Town parentTown;

    public PewterGym(Town parentTown) {
        this.parentTown = parentTown;
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
            mainWindow.appendToOutput(getInitialEntryMessage());
            player.addVisitedTown(getName());
        } else {
            mainWindow.appendToOutput("You enter the " + getName() + ".");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return null; // Gym doesn't have a Pokemon Center
    }

    @Override
    public String getInitialEntryMessage() {
        return "You enter the Pewter City Gym. The interior is adorned with large boulders and rock formations, reflecting Brock's specialty in Rock-type Pokémon. A trainer is practicing with their Pokémon, preparing for challengers.";
    }

    // Method to get parent town for return functionality
    public Town getParentTown() {
        return parentTown;
    }
}
