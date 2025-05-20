package Overworld.Towns;

import Overworld.Buildings.PewterGym;
import Overworld.Buildings.PokemonCenter;
import Overworld.Buildings.PokemonCenterBuilding;
import Overworld.Route;
import Overworld.Town;
import PlayerRelated.Player;
import WindowThings.mainWindow;

@SuppressWarnings({ "FieldMayBeFinal", "unused", "static-access" })

public class Pewter implements Town {
    private String name;
    private String description;
    private boolean firstTimeEntry = true;
    private PokemonCenter pokemonCenter;
    private PewterGym pewterGym;
    private String imageFile = "Pewter.png";
    private Route route2North;

    public Pewter() {
        this.name = "Pewter City";
        this.description = "A stone gray city that sits at the base of Mt. Moon.";
        this.pokemonCenter = new PokemonCenterBuilding("Pewter Pokemon Center",
                "A place to rest and heal your Pokemon.",
                this);
        this.pewterGym = new PewterGym(this);
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
            mainWindow.appendToOutput(getInitialEntryMessage());
            player.addVisitedTown(getName());
            firstTimeEntry = false;
        } else {
            mainWindow.appendToOutput(player.getName() + " enters " + getName() + ".");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return pokemonCenter;
    }

    @Override
    public String getInitialEntryMessage() {
        return "You arrive at Pewter City, the City of Stone. The buildings are made of solid gray rock that seems to rise "
                +
                "from the earth itself. The air has a cool, mineral quality to it, with Mt. Moon looming in the distance. At the north end "
                +
                "of town stands the imposing Pewter Gym, home to Rock-type Pokémon and the first official challenge on your journey to the Pokémon League.";
    }

    public Route getRoute2North() {
        return route2North;
    }

    public void setRoute2North(Route route) {
        this.route2North = route;
    }

    public PewterGym getPewterGym() {
        return pewterGym;
    }
}
