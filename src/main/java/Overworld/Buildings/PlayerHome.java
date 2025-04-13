package Overworld.Buildings;

import Overworld.Town;
import Overworld.Towns.Pallet;
import PlayerRelated.Player;
import PokemonLogic.Pokemon;

@SuppressWarnings("static-access")

public class PlayerHome extends PokemonCenter implements Town {
    private final String imageFile = "PlayerHouse.png";

    public PlayerHome(String name, String description) {
        super(name, description);
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
        } else {
            WindowThings.mainWindow.appendToOutput("Welcome home!");
        }
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return this;
    }

    @Override
    public String getInitialEntryMessage() {
        return "Sunlight streams through the window, warming your familiar bedroom. Your game console sits quietly, posters hang on the walls everything looks the same as always, yet... today feels different. A wave of excitement rushes over you as you remember: you've finally turned ten!\n"
                + //
                "\n" + //
                "Here in Pallet Town, that birthday marks a huge milestone. It's the traditional age when young people like you are finally ready to receive their very first Pokémon partner from Professor Oak and begin their own incredible journey across the Kanto region.\n"
                + //
                "\n" + //
                "Professor Oak, the famous Pokémon expert whose lab is just a short walk away, is expecting you. He has your starter Pokémon waiting the companion who will join you on countless adventures! Your very own Pokémon story starts today. Time to shake off the sleep, head downstairs, and step out towards the lab!";
    }

    @Override
    public void healPokemon(Player player) {
        for (Pokemon pokemon : player.getParty()) {
            pokemon.setRemainingHealth(pokemon.getHp());
            pokemon.setStatusCondition(Pokemon.StatusCondition.none);
        }
        WindowThings.mainWindow
                .appendToOutput("You took a nice rest at home!");
    }
    public Pallet getParentTown() {
        return new Pallet();
    }
}