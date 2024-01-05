package PlayerRelated;

import PokemonLogic.Pokemon;
import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Player {
    private static String name = "Red";
    private int money;
    private final List<Pokemon> party;
    private final List<Pokemon> pc;
    private final List<String> badges;
    private Pokemon currentPokemon;

    public Player() {
        this.money = 1000;
        this.party = new ArrayList<>();
        this.pc = new ArrayList<>();
        this.badges = new ArrayList<>();
    }

    public static String getName() {
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

    public List<String> getBadges() {
        return Collections.unmodifiableList(badges);
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public static void setName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Player Name");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");

        java.util.Optional<String> result = dialog.showAndWait();

        result.ifPresent(newName -> {
            name = newName;
        });
    }
    public void subtractMoney(int amount) {
        if (money - amount >= 0) {
            money -= amount;
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    public void earnBadge(String badgeName) {
        badges.add(badgeName);
    }

    public void addPokemonToParty(Pokemon pokemon) {
        if (party.size() < 6) {
            party.add(pokemon);
        } else {
            pc.add(pokemon);
        }
        // Remove when PC is implemented
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nickname for " + pokemon.getName());
        dialog.setHeaderText("Enter a nickname for your newly caught " + pokemon.getName() + "!:");
        dialog.setContentText("Nickname:");

        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(nickname -> {
            pokemon.setNickname(nickname);
        });
    }

    public void removePokemonFromParty(Pokemon pokemon) {
        if (party.contains(pokemon)) {
            party.remove(pokemon);
            pc.add(pokemon);
        } else {
            System.out.println("The specified Pokémon is not in your party.");
        }
    }

    public boolean hasUsablePokemon() {
        for (Pokemon pokemon : party) {
            if (pokemon.getRemainingHealth() > 0) {
                return true;
            }
        }
        return false;
    }

    public Pokemon getCurrentPokemon() {
        return currentPokemon;
    }

    public void setCurrentPokemon(Pokemon pokemon) {
        this.currentPokemon = pokemon;
    }
}