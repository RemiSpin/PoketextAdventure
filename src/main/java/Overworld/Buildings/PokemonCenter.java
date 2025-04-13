package Overworld.Buildings;
import PlayerRelated.Player;
import PokemonLogic.Pokemon;

@SuppressWarnings({"FieldMayBeFinal", "static-access"})

public class PokemonCenter {
    private String name;
    private String description;

    public PokemonCenter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void healPokemon(Player player) {
        // Get the player's party and heal each Pokemon
        for (Pokemon pokemon : player.getParty()) {
            // Restore HP to maximum
            pokemon.setRemainingHealth(pokemon.getHp());

            // Clear status conditions
            pokemon.setStatusCondition(Pokemon.StatusCondition.none);
        }

        WindowThings.mainWindow.appendToOutput("Your Pokémon have been restored to full health!");
        WindowThings.mainWindow.appendToOutput("We hope to see you again!");
    }

    public void shop(Player player) {
        System.out.println(player.getName() + " shops at the " + getName() + "'s PokeMart.");
        // Add shopping logic
    }
}