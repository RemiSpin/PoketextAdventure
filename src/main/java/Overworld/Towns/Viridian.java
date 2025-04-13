package Overworld.Towns;

import Overworld.Buildings.PokemonCenter;
import Overworld.Buildings.PokemonCenterBuilding;
import Overworld.Town;
import PlayerRelated.Player;

@SuppressWarnings({"FieldMayBeFinal", "unused", "static-access"})

public class Viridian implements Town {
    private String name;
    private String description;
    private boolean firstTimeEntry = true;
    private PokemonCenter pokemonCenter;
    private String imageFile = "Viridian.png";

    public Viridian() {
        this.name = "Viridian City";
        this.description = "The Eternally Green Paradise.";
        this.pokemonCenter = new PokemonCenterBuilding("Viridian Pokemon Center",
                "A place to rest and heal your Pokemon.",
                this);
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
        enter(player, false);
    }

    @Override
    public void enter(Player player, boolean fromPokemonCenter) {
        if (fromPokemonCenter) {
            return;
        }

        if (!player.hasVisitedTown(getName())) {
            WindowThings.mainWindow.appendToOutput(getInitialEntryMessage());
            player.addVisitedTown(getName());
            firstTimeEntry = false;
        } else {
            WindowThings.mainWindow.appendToOutput(player.getName() + " enters " + getName() + ".");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return pokemonCenter;
    }

    @Override
    public String getInitialEntryMessage() {
        return "This bustling city serves as a gateway, sitting right where the paths fork and the deep Viridian Forest begins. You can feel the city's energy buzz around you, yet the fresh scent of trees and the sound of rustling leaves drift in from the nearby woods. The formidable Pokémon Gym stands as a challenge for skilled trainers, while excited whispers of the far-off Pokémon League inspire the journeys of many who pass through.";
    }
}