package Overworld.Routes;

import Overworld.Route;
import Overworld.Town;

public class Route22 extends Route {

    public Route22(Town viridianCity) {
        super("Route 22",
              "A rugged path leading from Viridian City toward Indigo Plateau.",
              "R22.png",
              viridianCity,
              null); // The Indigo Plateau will be connected here when implemented
    }

    @Override
    public String getInitialEntryMessage() {
        return "A winding path stretches before you, marking the beginning of the journey to the Indigo Plateau. " +
               "The terrain is rugged, with tall grass swaying in the breeze, hiding wild Pokémon eager for battle. " +
               "In the distance, you can see the silhouette of mountains where the Pokémon League awaits challengers " +
               "who prove their worth.";
    }
}
