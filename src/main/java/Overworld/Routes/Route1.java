package Overworld.Routes;

import Overworld.Route;
import Overworld.Town;

public class Route1 extends Route {

    public Route1(Town palletTown, Town viridianCity) {
        super("Route 1",
                "A peaceful path connecting Pallet Town and Viridian City.",
                "R1.png",
                palletTown,
                viridianCity);
    }

    @Override
    public String getInitialEntryMessage() {
        return "This simple path cuts north through fields of tall, swaying grass that rustle with unseen movement. The salty air of Pallet Town gives way to the earthy scent of the open route and the sounds of common wild Pokémon often found hiding just off the trail.";
    }
}