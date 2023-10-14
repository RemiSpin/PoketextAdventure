import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private int money;
    private List<Pokemon> party; // Maximum of 6 Pokémon in the party
    private List<Pokemon> pc;    // Storage for additional caught Pokémon
    private Map<Item, Integer> inventory;  // Items and their quantities
    private List<String> badges;  // List of earned gym badges
    private Map<PokemonSpecies, Boolean> pokedex;  // Pokémon species and their captured status

    public Player(String name) {
        this.name = name;
        this.money = 1000;  // Initial money
        this.party = new ArrayList<>();
        this.pc = new ArrayList<>();
        this.inventory = new HashMap<>();
        this.badges = new ArrayList<>();
        this.pokedex = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public void subtractMoney(int amount) {
        money -= amount;
    }

    public List<Pokemon> getParty() {
        return party;
    }

    public List<Pokemon> getPC() {
        return pc;
    }

    public Map<Item, Integer> getInventory() {
        return inventory;
    }

    public List<String> getBadges() {
        return badges;
    }

    public Map<PokemonSpecies, Boolean> getPokedex() {
        return pokedex;
    }

    public void addToInventory(Item item, int quantity) {
        if (inventory.containsKey(item)) {
            inventory.put(item, inventory.get(item) + quantity);
        } else {
            inventory.put(item, quantity);
        }
    }

    public void removeFromInventory(Item item, int quantity) {
        if (inventory.containsKey(item)) {
            int currentQuantity = inventory.get(item);
            if (currentQuantity <= quantity) {
                inventory.remove(item);
            } else {
                inventory.put(item, currentQuantity - quantity);
            }
        }
    }

    public void earnBadge(String badgeName) {
        badges.add(badgeName);
    }

    public void addToPokedex(PokemonSpecies species) {
        if (!pokedex.containsKey(species)) {
            pokedex.put(species, true);
        }
    }

    public boolean hasCapturedInPokedex(PokemonSpecies species) {
        return pokedex.getOrDefault(species, false);
    }

    public void addPokemonToParty(Pokemon pokemon) {
        if (party.size() < 6) {
            party.add(pokemon);
        } else {
            // Party is full; add to PC instead
            pc.add(pokemon);
        }
    }

    public void removePokemonFromParty(Pokemon pokemon) {
        party.remove(pokemon);
    }
}
