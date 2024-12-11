package PlayerRelated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import PokemonLogic.Pokemon;
import javafx.scene.control.TextInputDialog;


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

        name = result.map(n -> n.trim().isEmpty() ? "Red" : n).orElse("Red");
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

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nickname for " + pokemon.getName());
        dialog.setHeaderText("Enter a nickname for your newly caught " + pokemon.getName() + "!:");
        dialog.setContentText("Nickname:");

        java.util.Optional<String> result = dialog.showAndWait();
        String nickname = result.map(n -> n.trim().isEmpty() ? pokemon.getName() : n).orElse(pokemon.getName());
        pokemon.setNickname(nickname);
    }

    public void switchPokemon() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            for (int i = 0; i < party.size(); i++) {
                System.out.println((i + 1) + ". " + party.get(i).getNickname());
            }

            System.out.println("Choose a Pokemon to switch into battle (1-" + party.size() + "), or press 0 to go back:");
            int choice = scanner.nextInt();

            // Check if the user wants to go back
            if (choice == 0) {
                System.out.println("Going back to the previous menu.");
                return;
            }

            // Validate the input
            if (choice < 1 || choice > party.size()) {
                System.out.println("Invalid choice. Please choose a number between 1 and " + party.size() + ", or press 0 to go back.");
                continue;
            }

            // Adjust the choice to be zero-based
            choice--;

            // Check if the chosen Pokemon is usable
            if (!party.get(choice).isUsable()) {
                System.out.println("The chosen Pokemon is fainted. Please choose a different Pokemon.");
                continue;
            }

            // Swap the chosen Pokemon with the first Pokemon in the party
            Pokemon temp = party.get(0);
            party.set(0, party.get(choice));
            party.set(choice, temp);

            System.out.println("Go " + party.get(0).getNickname() + "!");
            break;
        }
    }

    public void switchPokemonOutsideBattle() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            for (int i = 0; i < party.size(); i++) {
                System.out.println((i + 1) + ". " + party.get(i).getNickname());
            }

            System.out.println("Choose a Pokemon to switch (1-" + party.size() + "), or press 0 to go back:");
            int choice = scanner.nextInt();

            // Check if the user wants to go back
            if (choice == 0) {
                System.out.println("Going back to the previous menu.");
                return;
            }

            // Validate the input
            if (choice < 1 || choice > party.size()) {
                System.out.println("Invalid choice. Please choose a number between 1 and " + party.size() + ", or press 0 to go back.");
                continue;
            }

            // Adjust the choice to be zero-based
            choice--;

            // Swap the chosen Pokemon with the first Pokemon in the party
            Pokemon temp = party.get(0);
            party.set(0, party.get(choice));
            party.set(choice, temp);

            System.out.println(party.get(0).getNickname() + " is in front.");
            break;
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