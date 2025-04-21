package Overworld.Buildings;

import Overworld.Town;
import PlayerRelated.Player;

public class PokemonCenterBuilding extends PokemonCenter implements Town {
    private final String imageFile = "Center.png"; // Image file for the Pokemon Center
    private Town parentTown; // Store reference to the parent town

    public PokemonCenterBuilding(String name, String description, Town parentTown) {
        super(name, description);
        this.parentTown = parentTown;
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

            // Check if this is Viridian City's Pokémon Center and player doesn't have the
            // parcel yet
            if (getName().equals("Viridian Pokemon Center") && !player.hasOaksParcel()
                    && !player.hasDeliveredOaksParcel()) {
                WindowThings.mainWindow
                        .appendToOutput("The shopkeeper at the counter notices you're from Pallet Town.");
                WindowThings.mainWindow.appendToOutput("Vendor: Oh! You're " + Player.getName() + " from Pallet Town?");
                WindowThings.mainWindow.appendToOutput(
                        "Vendor: Professor Oak placed an order for a custom Poké Ball. It just arrived.");
                WindowThings.mainWindow
                        .appendToOutput("Vendor: Could you take this parcel to him? He's been waiting for it.");
                WindowThings.mainWindow.appendToOutput("You received OAK'S PARCEL!");
                player.setHasOaksParcel(true);
            }
        } else {
            WindowThings.mainWindow.appendToOutput("Welcome to the Pokémon Center!");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return this;
    }

    @Override
    public String getInitialEntryMessage() {
        return "Welcome to the Pokémon Center! We restore your tired Pokémon to full health.";
    }

    // Method to get parent town for return functionality
    public Town getParentTown() {
        return parentTown;
    }
}
