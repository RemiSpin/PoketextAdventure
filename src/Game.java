import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player player;
    private List<Pokemon> availablePokemon;
    private Random random = new Random();
    private Scanner scanner = new Scanner(System.in);

    public Game() {
        availablePokemon = new ArrayList<>();
        initializeAvailablePokemon();
        setupGame();
    }

    private void initializeAvailablePokemon() {
        try (BufferedReader reader = new BufferedReader(new FileReader("pokemon.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header row
                }

                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String name = parts[1].trim();
                    int baseHP = Integer.parseInt(parts[4].trim());
                    int attack = Integer.parseInt(parts[5].trim());
                    int defense = Integer.parseInt(parts[6].trim());
                    int spAtk = Integer.parseInt(parts[7].trim());
                    int spDef = Integer.parseInt(parts[8].trim());
                    int speed = Integer.parseInt(parts[9].trim());
                    String type1 = parts[2].trim();
                    String type2 = parts[3].trim();

                    availablePokemon.add(new Pokemon(name, type1, type2, baseHP, attack, defense, spAtk, spDef, speed));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGame() {
        System.out.println("Hello there! Welcome to the world of Pokémon! Myself… I study Pokémon as a profession.");
        System.out.println("My name is Oak! People call me the Pokémon Prof!");
        System.out.println("This world is inhabited by creatures called Pokémon!");
        System.out.println("For some people, Pokémon are pets. Others use them for fights. Myself… I study Pokémon as a profession.");
        System.out.println("First, what is your name?");
        String playerName = scanner.nextLine();
        player = new Player(playerName);
        System.out.println("Right! So your name is " + playerName + "!");
        System.out.println("This is my grandson. He's been your rival since you were a baby.");
        System.out.println("…Erm, what is his name again?");
        String rivalName = scanner.nextLine();
        System.out.println("That's right! I remember now! His name is " + rivalName + "!");
        System.out.println(playerName + "! Your very own Pokémon legend is about to unfold! A world of dreams and adventures with Pokémon awaits!");
        System.out.println("Let's go!");

        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Capture a random Pokémon");
            System.out.println("2. View your Pokémon");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    captureRandomPokemon();
                    break;
                case 2:
                    player.displayPokemon();
                    break;
                case 3:
                    exitGame();
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void captureRandomPokemon() {
        if (!availablePokemon.isEmpty()) {
            int randomIndex = random.nextInt(availablePokemon.size());
            Pokemon randomPokemon = availablePokemon.remove(randomIndex);
            player.addPokemon(randomPokemon);

            System.out.println("\nYou captured a " + randomPokemon.getName() + "!");
        } else {
            System.out.println("No more Pokémon available to capture.");
        }
    }

    private void exitGame() {
        scanner.close();
        System.exit(0);
    }

    public static void main(String[] args) {
        new Game();
    }
}
