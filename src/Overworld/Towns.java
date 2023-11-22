package Overworld;


import PlayerRelated.Player;

public interface Towns {
    String getName();
    String getDescription();
    void enter(Player player);
    PokemonCenter getPokemonCenter();
    String getInitialEntryMessage();
}