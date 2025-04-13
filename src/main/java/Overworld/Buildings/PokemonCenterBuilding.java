package Overworld.Buildings;

import Overworld.Town;
import PlayerRelated.Player;

public class PokemonCenterBuilding extends PokemonCenter implements Town {
    private final String imageFile = "center.png"; // Image file for the Pokemon Center
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
