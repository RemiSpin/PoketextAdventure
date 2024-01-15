package BattleLogic;

import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class Battle extends Application {
//    private static Battle instance;

    private Player player;
    private Trainer opponent;

    public Battle(Player player, Trainer opponent) {
        this.player = player;
        this.opponent = opponent;
    }

//    public static Battle getInstance(Player player, Trainer opponent) {
//        if (instance == null) {
//            instance = new Battle(player, opponent);
//        }
//        return instance;
//    }
        private void aiTurn() {
            // Create a new thread for the AI's turn
            new Thread(() -> {
                // Decide the AI's action
                String action = decideAction();

                // Apply the AI's action
                applyAction(action);
            }).start();
        }

        private String decideAction() {
            // Get the AI's current Pokemon and the player's current Pokemon
            trainerPokemon aiPokemon = opponent.getPokemonList().get(0);
            Pokemon playerPokemon = player.getParty().get(0);

            // Store the best move and its power
            Move bestMove = null;
            int bestMovePower = -1;

            // Check each of the AI's moves
            for (Move move : aiPokemon.getMoves()) {
                // If the move is super effective, choose that move
                if (move.getSuperEffective().contains(playerPokemon.getType1()) ||
                        (playerPokemon.getType2() != null && move.getSuperEffective().contains(playerPokemon.getType2()))) {
                    return move.getName();
                }

                // If the move has higher power than the current best move, update the best move
                if (move.getPower() > bestMovePower) {
                    bestMove = move;
                    bestMovePower = move.getPower();
                }
            }

            // If no super effective move was found, return the move with the highest power
            return bestMove.getName();
        }

    private void applyAction(String action) {
        // Find the move with the given name in the AI's Pokemon's moves
        Move move = opponent.getPokemonList().get(0).getMoves().stream()
                .filter(m -> m.getName().equals(action))
                .findFirst()
                .orElse(null);

        // If the move was found, apply it
        if (move != null) {
            // Calculate the damage this move would deal to the player's Pokemon
            int damage = calculateDamage(move, opponent.getPokemonList().get(0), player.getParty().get(0));

            // Reduce the player's Pokemon's health by this amount
            Pokemon playerPokemon = player.getParty().get(0);
            playerPokemon.setRemainingHealth(playerPokemon.getRemainingHealth() - damage);
        }
    }

    private void applyPlayerAction(String action) {
        // Find the move with the given name in the player's Pokemon's moves
        Move move = player.getParty().get(0).getMovesList().stream()
                .filter(m -> m.getName().equals(action))
                .findFirst()
                .orElse(null);

        // If the move was found, apply it
        if (move != null) {
            // Calculate the damage this move would deal to the AI's Pokemon
            int damage = calculateDamage(move, player.getParty().get(0), opponent.getPokemonList().get(0));

            // Reduce the AI's Pokemon's health by this amount
            trainerPokemon aiPokemon = opponent.getPokemonList().get(0);
            aiPokemon.setRemainingHealth(aiPokemon.getRemainingHealth() - damage);
        }
    }

    private int calculateDamage(Move move, Pokemon attacker, trainerPokemon defender) {
        // Calculate the base damage
        int baseDamage = (((2 * attacker.getLevel()) / 5 + 2) * move.getPower() * (attacker.getAttack() / defender.getDefense()));

        // Calculate the type effectiveness multiplier
        double typeMultiplier = calculateTypeEffectivenessMultiplier(move, defender);

        // Calculate the final damage
        int finalDamage = (int) (baseDamage * typeMultiplier);

        // 1/16 chance to deal double damage (critical hit)
        Random rand = new Random();
        if (rand.nextInt(16) == 0) {
            finalDamage *= 2;
        }

        return finalDamage;
    }

    private int calculateDamage(Move move, trainerPokemon attacker, Pokemon defender) {
        // Calculate the base damage
        int baseDamage = (((2 * attacker.getLevel()) / 5 + 2) * move.getPower() * (attacker.getAttack() / defender.getDefense()));

        // Calculate the type effectiveness multiplier
        double typeMultiplier = calculateTypeEffectivenessMultiplier(move, defender);

        // Calculate the final damage
        int finalDamage = (int) (baseDamage * typeMultiplier);

        // 1/16 chance to deal double damage (critical hit)
        Random rand = new Random();
        if (rand.nextInt(16) == 0) {
            finalDamage *= 2;
        }

        return finalDamage;
    }

    private double calculateTypeEffectivenessMultiplier(Move move, Pokemon pokemon) {
        double multiplier = 1.0;

        // Check if the move is super effective against the Pokemon's type1
        if (move.getSuperEffective().contains(pokemon.getType1())) {
            multiplier *= 2;
        }

        // Check if the move is super effective against the Pokemon's type2
        if (pokemon.getType2() != null && move.getSuperEffective().contains(pokemon.getType2())) {
            multiplier *= 2;
        }

        // Check if the move is not very effective against the Pokemon's type1
        if (move.getNotVeryEffective().contains(pokemon.getType1())) {
            multiplier /= 2;
        }

        // Check if the move is not very effective against the Pokemon's type2
        if (pokemon.getType2() != null && move.getNotVeryEffective().contains(pokemon.getType2())) {
            multiplier /= 2;
        }

        return multiplier;
    }

    private double calculateTypeEffectivenessMultiplier(Move move, trainerPokemon pokemon) {
        double multiplier = 1.0;

        // Check if the move is super effective against the Pokemon's type1
        if (move.getSuperEffective().contains(pokemon.getType1())) {
            multiplier *= 2;
        }

        // Check if the move is super effective against the Pokemon's type2
        if (pokemon.getType2() != null && move.getSuperEffective().contains(pokemon.getType2())) {
            multiplier *= 2;
        }

        // Check if the move is not very effective against the Pokemon's type1
        if (move.getNotVeryEffective().contains(pokemon.getType1())) {
            multiplier /= 2;
        }

        // Check if the move is not very effective against the Pokemon's type2
        if (pokemon.getType2() != null && move.getNotVeryEffective().contains(pokemon.getType2())) {
            multiplier /= 2;
        }

        return multiplier;
    }

//    public void startBattle(String input) {
//
//        // AI's turn: start the AI's turn
//        aiTurn();
//
//        // Parse the user's input to an integer
//        int choice;
//        try {
//            choice = Integer.parseInt(input);
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid input. Please enter a number.");
//            return;
//        }
//
//        switch (choice) {
//            case 1:
//                // Player's turn: get the player's action and apply it
//                // For now, let's assume the player always uses their first move
//                applyPlayerAction(player.getParty().get(0).getMovesList().get(0).getName());
//                break;
//            case 2:
//                // Use an item
//                // useItem();
//                break;
//            case 3:
//                // Switch Pokemon
//                player.switchPokemon();
//                break;
//            case 4:
//                // Run from the battle
//                System.out.println("You ran away!");
//                return;
//            default:
//                System.out.println("Invalid choice. Please choose again.");
//                break;
//        }
//
//        // If the AI has no more usable Pokemon, end the battle
//        if (!opponent.hasUsablePokemon()) {
//            System.out.println("Player wins!");
//            return;
//        }
//
//        // If the player has no more usable Pokemon, end the battle
//        if (!player.hasUsablePokemon()) {
//            System.out.println("AI wins!");
//            return;
//        }
//    }

    @Override
    public void start(Stage primaryStage) {
    Pane root = new Pane();
    Scene scene = new Scene(root, 500, 500);

    // Load the custom font
    Font font = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 11);

    // Load the background image
    Image backgroundImage = new Image(getClass().getResourceAsStream("/BattleBG.png"));

    // Create the BackgroundImage object
    BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    // Set the background of the BorderPane
    root.setBackground(new Background(background));

    // Load the images
    Image playerPokemonImage = new Image(getClass().getResourceAsStream("/" + player.getParty().get(0).getSpritePath()));
    Image opponentPokemonImage = new Image(getClass().getResourceAsStream("/" + opponent.getPokemonList().get(0).getSpritePath()));

    // Create the nickname labels
    Label playerPokemonNickname = new Label(player.getParty().get(0).getNickname());
    Label opponentPokemonNickname = new Label(opponent.getPokemonList().get(0).getName());

    // Create the level labels
    Label playerPokemonLevel = new Label("Lv. " + player.getParty().get(0).getLevel());
    Label opponentPokemonLevel = new Label("Lv. " + opponent.getPokemonList().get(0).getLevel());

    // Create the ImageViews
    ImageView playerPokemonView = new ImageView(playerPokemonImage);
    ImageView opponentPokemonView = new ImageView(opponentPokemonImage);

    // Create the health bars using rectangles
    Rectangle playerHealthBarBackground = new Rectangle(100, 10);
    playerHealthBarBackground.setFill(Color.DARKGREY);
    playerHealthBarBackground.setStroke(Color.BLACK);
    playerHealthBarBackground.setStrokeWidth(2);
    playerHealthBarBackground.setArcWidth(10);
    playerHealthBarBackground.setArcHeight(10);
    Rectangle playerHealthBarForeground = new Rectangle(player.getParty().get(0).getRemainingHealth() / (double) player.getParty().get(0).getHp() * 100, 10);
    playerHealthBarForeground.setFill(Color.LIGHTGREEN);
    playerHealthBarForeground.setArcWidth(10);
    playerHealthBarForeground.setArcHeight(10);
    Rectangle opponentHealthBarBackground = new Rectangle(100, 10);
    opponentHealthBarBackground.setFill(Color.DARKGREY);
    opponentHealthBarBackground.setStroke(Color.BLACK);
    opponentHealthBarBackground.setStrokeWidth(2);
    opponentHealthBarBackground.setArcWidth(10);
    opponentHealthBarBackground.setArcHeight(10);
    Rectangle opponentHealthBarForeground = new Rectangle(opponent.getPokemonList().get(0).getRemainingHealth() / (double) opponent.getPokemonList().get(0).getHp() * 100, 10);
    opponentHealthBarForeground.setFill(Color.LIGHTGREEN);
    opponentHealthBarForeground.setArcWidth(10);
    opponentHealthBarForeground.setArcHeight(10);

    // Create the health labels
    Label playerHealthLabel = new Label(player.getParty().get(0).getRemainingHealth() + "/" + player.getParty().get(0).getHp());
    Label opponentHealthLabel = new Label(opponent.getPokemonList().get(0).getRemainingHealth() + "/" + opponent.getPokemonList().get(0).getHp());

    // Set the font
    playerHealthLabel.setFont(font);
    opponentHealthLabel.setFont(font);
    playerPokemonNickname.setFont(font);
    opponentPokemonNickname.setFont(font);
    playerPokemonLevel.setFont(font);
    opponentPokemonLevel.setFont(font);

    // Set the color
    playerHealthLabel.setTextFill(Color.rgb(10, 10, 10));
    opponentHealthLabel.setTextFill(Color.rgb(10, 10, 10));
    playerPokemonNickname.setTextFill(Color.rgb(10, 10, 10));
    opponentPokemonNickname.setTextFill(Color.rgb(10, 10, 10));
    playerPokemonLevel.setTextFill(Color.rgb(10, 10, 10));
    opponentPokemonLevel.setTextFill(Color.rgb(10, 10, 10));

    // Create the XP bars using rectangles
    Rectangle playerXPBarBackground = new Rectangle(100, 5);
    playerXPBarBackground.setFill(Color.DARKGREY);
    playerXPBarBackground.setStroke(Color.BLACK);
    playerXPBarBackground.setStrokeWidth(1);
    playerXPBarBackground.setArcWidth(5);
    playerXPBarBackground.setArcHeight(5);
    Rectangle playerXPBarForeground = new Rectangle(player.getParty().get(0).getExperience() / (double) player.getParty().get(0).getLevelTreshhold() * 100, 5);
    playerXPBarForeground.setFill(Color.LIGHTBLUE);
    playerXPBarForeground.setArcWidth(5);
    playerXPBarForeground.setArcHeight(5);
    Rectangle opponentXPBarBackground = new Rectangle(100, 5);
    opponentXPBarBackground.setFill(Color.DARKGREY);
    opponentXPBarBackground.setStroke(Color.BLACK);
    opponentXPBarBackground.setStrokeWidth(1);
    opponentXPBarBackground.setArcWidth(5);
    opponentXPBarBackground.setArcHeight(5);

    // Add to the root node
    root.getChildren().addAll(playerPokemonView, opponentPokemonView, playerHealthBarBackground, playerHealthBarForeground, opponentHealthBarBackground, opponentHealthBarForeground, playerHealthLabel, opponentHealthLabel, playerXPBarBackground, playerXPBarForeground, opponentXPBarBackground, playerPokemonNickname, opponentPokemonNickname, playerPokemonLevel, opponentPokemonLevel);

    // Set the size of the ImageViews
    playerPokemonView.setFitWidth(60);
    playerPokemonView.setPreserveRatio(true);
    opponentPokemonView.setFitWidth(60);
    opponentPokemonView.setPreserveRatio(true);

    // Set the positions of the ImageViews
    playerPokemonView.setLayoutX(scene.getWidth() / 3 - playerPokemonView.getFitWidth() / 2 - 30);
    playerPokemonView.setLayoutY(scene.getHeight() / 3 - playerPokemonView.getFitHeight() / 2);
    opponentPokemonView.setLayoutX(2 * scene.getWidth() / 3 - opponentPokemonView.getFitWidth() / 2 + 30);
    opponentPokemonView.setLayoutY(scene.getHeight() / 3 - opponentPokemonView.getFitHeight() / 2);

    // Set the positions of the health bars
    playerHealthBarBackground.setLayoutX(playerPokemonView.getLayoutX() - 20);
    playerHealthBarBackground.setLayoutY(playerPokemonView.getLayoutY() + playerPokemonView.getFitHeight() + 100);
    playerHealthBarForeground.setLayoutX(playerPokemonView.getLayoutX() - 20);
    playerHealthBarForeground.setLayoutY(playerPokemonView.getLayoutY() + playerPokemonView.getFitHeight() + 100);
    opponentHealthBarBackground.setLayoutX(opponentPokemonView.getLayoutX() - 25);
    opponentHealthBarBackground.setLayoutY(opponentPokemonView.getLayoutY() + opponentPokemonView.getFitHeight() + 100);
    opponentHealthBarForeground.setLayoutX(opponentPokemonView.getLayoutX() - 25);
    opponentHealthBarForeground.setLayoutY(opponentPokemonView.getLayoutY() + opponentPokemonView.getFitHeight() + 100);

    // Set the positions of the nickname labels
    playerPokemonNickname.setLayoutX(playerHealthBarBackground.getLayoutX());
    playerPokemonNickname.setLayoutY(playerHealthBarBackground.getLayoutY() - playerPokemonNickname.getHeight() - 15);
    opponentPokemonNickname.setLayoutX(opponentHealthBarBackground.getLayoutX());
    opponentPokemonNickname.setLayoutY(opponentHealthBarBackground.getLayoutY() - opponentPokemonNickname.getHeight() - 15);

    // Set the positions of the health labels
    playerHealthLabel.setLayoutX(playerHealthBarBackground.getLayoutX());
    playerHealthLabel.setLayoutY(playerHealthBarBackground.getLayoutY() + playerHealthBarBackground.getHeight());
    opponentHealthLabel.setLayoutX(opponentHealthBarBackground.getLayoutX());
    opponentHealthLabel.setLayoutY(opponentHealthBarBackground.getLayoutY() + opponentHealthBarBackground.getHeight());

    // Set the positions of the XP bars to be under the health labels
    playerXPBarBackground.setLayoutX(playerHealthBarBackground.getLayoutX());
    playerXPBarBackground.setLayoutY(playerHealthLabel.getLayoutY() + playerHealthLabel.getHeight() + 30);
    playerXPBarForeground.setLayoutX(playerHealthBarBackground.getLayoutX());
    playerXPBarForeground.setLayoutY(playerHealthLabel.getLayoutY() + playerHealthLabel.getHeight() + 30);
    opponentXPBarBackground.setLayoutX(opponentHealthBarBackground.getLayoutX());
    opponentXPBarBackground.setLayoutY(opponentHealthLabel.getLayoutY() + opponentHealthLabel.getHeight() + 30);

    // Set the positions of the level labels
    playerPokemonLevel.setLayoutX(playerXPBarBackground.getLayoutX());
    playerPokemonLevel.setLayoutY(playerXPBarBackground.getLayoutY() + playerXPBarBackground.getHeight());
    opponentPokemonLevel.setLayoutX(opponentXPBarBackground.getLayoutX());
    opponentPokemonLevel.setLayoutY(opponentXPBarBackground.getLayoutY() + opponentXPBarBackground.getHeight());

    // Flip the opponent's Pokemon image horizontally so it faces the player's Pokemon
    playerPokemonView.setScaleX(-1);

        primaryStage.setTitle("Pokemon Battle");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Make the window unresizable
        primaryStage.show();

}
}