package Overworld;
import PlayerRelated.Player;

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
        System.out.println(player.getName() + "'s Pokemon are healed at the " + getName() + ".");
        // Add healing logic
    }

    public void shop(Player player) {
        System.out.println(player.getName() + " shops at the " + getName() + "'s PokeMart.");
        // Add shopping logic
    }
}