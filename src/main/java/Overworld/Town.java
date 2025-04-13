package Overworld;

import Overworld.Buildings.PokemonCenter;
import PlayerRelated.Player;

public interface Town {
    String getName();

    String getDescription();

    String getImageFile();

    // Add a version with the fromPokemonCenter parameter
    default void enter(Player player, boolean fromPokemonCenter) {
        if (!fromPokemonCenter) {
            enter(player);
        }
    }

    // Keep the original for backward compatibility
    void enter(Player player);

    PokemonCenter getPokemonCenter();

    String getInitialEntryMessage();
}