package Overworld;

import PlayerRelated.Player;

@SuppressWarnings({"FieldMayBeFinal", "static-access"})

public class Pallet implements Towns {
    private String name;
    private String description;
    private boolean firstTimeEntry = true;
    private PokemonCenter playerHome;

    public Pallet() {
        this.name = "Pallet Town";
        this.description = "The small town where you grew up.";

        this.playerHome = new PokemonCenter(Player.getName() + "'s Home", "Your cozy home.");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void enter(Player player) {
        if (firstTimeEntry) {
            System.out.println(getInitialEntryMessage());
            firstTimeEntry = false;
        } else {
            System.out.println(player.getName() + " enters " + getName() + ".");
        }
        // Add enter Town logic
    }

    @Override
    public PokemonCenter getPokemonCenter() {
        return playerHome;
    }

    @Override
    public String getInitialEntryMessage() {
        return "Nestled within the scenic embrace of Pallet Town, where the rustling leaves harmonize with the gentle " +
                "murmur of streams, a budding adventurer prepares to venture forth, enraptured by the serene beauty " +
                "that has quietly nurtured dreams and now whispers promises of untold wonders just beyond the horizon.";
    }
}