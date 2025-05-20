package Overworld;

import Overworld.Buildings.PokemonCenter;
import PlayerRelated.Player;

public interface Town {
    String getName();

    String getDescription();

    String getImageFile();

    default void enter(Player player, boolean fromPokemonCenter) {
        if (!fromPokemonCenter) {
            enter(player);
        }
    }

    void enter(Player player);

    PokemonCenter getPokemonCenter();

    String getInitialEntryMessage();
}