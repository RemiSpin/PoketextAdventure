import java.util.*;

public class Player {
    private final String name;
    private int money;
    private final List<Pokemon> party;
    private final List<Pokemon> pc;
    private final Map<Item, Integer> inventory;
    private final List<String> badges;
    private final Map<PokemonSpecies, Boolean> pokedex;

    public Player(String name) {
        this.name = name;
        this.money = 1000;
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

    public List<Pokemon> getParty() {
        return Collections.unmodifiableList(party);
    }

    public List<Pokemon> getPC() {
        return Collections.unmodifiableList(pc);
    }

    public Map<Item, Integer> getInventory() {
        return Collections.unmodifiableMap(inventory);
    }

    public List<String> getBadges() {
        return Collections.unmodifiableList(badges);
    }

    public Map<PokemonSpecies, Boolean> getPokedex() {
        return Collections.unmodifiableMap(pokedex);
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public void subtractMoney(int amount) {
        if (money - amount >= 0) {
            money -= amount;
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    public void addToInventory(Item item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    public void removeFromInventory(Item item, int quantity) {
        if (inventory.containsKey(item)) {
            int currentQuantity = inventory.get(item);
            if (currentQuantity <= quantity) {
                inventory.remove(item);
            } else {
                inventory.put(item, currentQuantity - quantity);
            }
        } else {
            System.out.println("Item not found in inventory.");
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
            pc.add(pokemon);
        }
    }

    public void removePokemonFromParty(Pokemon pokemon) {
        if (party.contains(pokemon)) {
            party.remove(pokemon);
            pc.add(pokemon);
        } else {
            System.out.println("The specified Pokémon is not in your party.");
        }
    }
}