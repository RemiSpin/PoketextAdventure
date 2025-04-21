package Overworld.Routes;

import Overworld.Route;
import Overworld.Town;
import Overworld.Towns.Pewter;

public class Route2North extends Route {
    private Pewter pewterCity;

    public Route2North(Town viridianForest) {
        super("Route 2 North",
                "The northern section of Route 2, leading from Viridian Forest toward Pewter City.",
                "R2N.png",
                viridianForest,
                null); // Will be set to Pewter City
    }

    @Override
    public String getInitialEntryMessage() {
        return "You emerge from the dense Viridian Forest onto the northern section of Route 2. " +
                "The path opens up before you, with the rugged silhouette of Mt. Moon visible in the distance. " +
                "The air feels fresher here, and you can see Pewter City just ahead.";
    }

    public Pewter getPewterCity() {
        if (pewterCity == null) {
            pewterCity = new Pewter();
            // Set destination2 to Pewter City
            super.destination2 = pewterCity;
            // Set Route2North in Pewter City for the return trip
            pewterCity.setRoute2North(this);
        }
        return pewterCity;
    }
}
