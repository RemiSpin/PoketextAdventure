package Overworld.Towns;

import Overworld.Buildings.PlayerHome;
import Overworld.Buildings.PokemonCenter;
import Overworld.Route;
import Overworld.Routes.Route1;
import Overworld.Town;
import PlayerRelated.Player;

@SuppressWarnings({ "FieldMayBeFinal", "static-access", "unused" })

public class Pallet implements Town {
    private String name;
    private String description;
    private boolean firstTimeEntry = true;
    private PokemonCenter playerHome;
    private String imageFile = "Pallet.png";
    private Route route1;

    public Pallet() {
        this.name = "Pallet Town";
        this.description = "The small town where you grew up.";
        this.playerHome = new PlayerHome(Player.getName() + "'s Home", "Your cozy home.");
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
            firstTimeEntry = false;
        } else {
            WindowThings.mainWindow.appendToOutput(player.getName() + " enters " + getName() + ".");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return playerHome;
    }

    @Override
    public String getInitialEntryMessage() {
        return "In this small, peaceful town nestled beside the vast ocean, the air carries the fresh scent of sea salt mixed with the smell of tall grass from the nearby route. The gentle sound of waves provides a calm backdrop to the humble houses and the prominent Pokémon Lab of Professor Oak. It's a quiet place, filled with the hopeful energy of new beginnings and the promise of adventures waiting just beyond its borders.";
    }

    public Route getRoute1() {
        if (route1 == null) {
            // Create Viridian City
            Viridian viridian = new Viridian();
            // Create Route 1 connecting to Viridian
            route1 = new Route1(this, viridian);
        }
        return route1;
    }
}