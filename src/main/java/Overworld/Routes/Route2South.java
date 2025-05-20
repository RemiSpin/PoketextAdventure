package Overworld.Routes;

import Overworld.Route;
import Overworld.Town;

public class Route2South extends Route {
    private ViridianForest viridianForest;

    public Route2South(Town viridianCity) {
        super("Route 2 South",
                "A path connecting Viridian City to Viridian Forest.",
                "R2S.png",
                viridianCity,
                null);
    }

    @Override
    public String getInitialEntryMessage() {
        return "The path narrows as you head north from Viridian City. Tall grass grows thick on either side, " +
                "and you can see the dense Viridian Forest looming ahead. The air becomes cooler and filled with " +
                "the rustling sounds of insects and small Pokémon scurrying through the underbrush.";
    }

    // Add a method to get/create Viridian Forest
    public ViridianForest getViridianForest() {
        if (viridianForest == null) {
            viridianForest = new ViridianForest(this, null);
            super.destination2 = viridianForest;
        }
        return viridianForest;
    }
}
