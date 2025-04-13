package BattleLogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import PlayerRelated.Player;
import PokemonLogic.IPokemon;
import PokemonLogic.Pokemon;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings({ "FieldMayBeFinal", "OverridableMethodCallInConstructor", "incomplete-switch", "unused" })
public class Battle extends Application {

    private Player player;
    private Trainer opponent;
    private Rectangle playerHealthBarForeground;
    private Rectangle opponentHealthBarForeground;
    private Rectangle playerHealthBarBackground;
    private Rectangle opponentHealthBarBackground;
    private Label playerHealthLabel;
    private Label opponentHealthLabel;
    private Pokemon wildPokemon;
    private boolean isWildBattle;
    private static final Logger logger = LoggerFactory.getLogger(Battle.class);
    private ImageView opponentPokemonView;
    private ImageView playerPokemonView;
    private Pane root;
    private HBox controlsBox;
    private Scene scene;
    private Label playerPokemonNickname;
    private Label opponentPokemonNickname;
    private Label playerPokemonLevel;
    private Label opponentPokemonLevel;

    // Add these fields to store original button event handlers
    private javafx.event.EventHandler<javafx.event.ActionEvent> fightButtonHandler;
    private javafx.event.EventHandler<javafx.event.ActionEvent> catchButtonHandler;
    private javafx.event.EventHandler<javafx.event.ActionEvent> switchButtonHandler;
    private javafx.event.EventHandler<javafx.event.ActionEvent> runButtonHandler;

    // Add status effect labels
    private Label playerStatusLabel;
    private Label opponentStatusLabel;

    public Battle(Player player, Trainer opponent) {
        this.player = player;
        this.opponent = opponent;
        player.setCurrentPokemon(player.getParty().get(0));
        opponent.setCurrentPokemon(opponent.getPokemonList().get(0));

        Stage battleStage = new Stage();
        try {
            start(battleStage);
        } catch (Exception e) {
            // Add proper error logging instead of silently catching
            System.err.println("Error starting trainer battle: " + e.getMessage());
        }
    }

    public Battle(Pokemon playerPokemon, Pokemon wildPokemon, Player player, boolean isWildBattle) {
        this.player = player;
        this.wildPokemon = wildPokemon;
        this.isWildBattle = isWildBattle;
        player.setCurrentPokemon(playerPokemon);

        Stage battleStage = new Stage();
        try {
            start(battleStage);
        } catch (Exception e) {
            // Improve error reporting
            System.err.println("Error starting wild battle: " + e.getMessage());
        }
    }

    private void aiTurn() {
        // Check for status conditions that prevent attacking
        IPokemon aiPokemon = opponent.getCurrentPokemon();
        PokemonLogic.Pokemon.StatusCondition status = ((trainerPokemon) aiPokemon).getStatusCondition();

        if (status != PokemonLogic.Pokemon.StatusCondition.none) {
            // Sleep and freeze prevent attacking
            if (status == PokemonLogic.Pokemon.StatusCondition.SLP
                    || status == PokemonLogic.Pokemon.StatusCondition.FRZ) {
                String statusName = (status == PokemonLogic.Pokemon.StatusCondition.SLP) ? "asleep" : "frozen";
                WindowThings.mainWindow
                        .appendToOutput(aiPokemon.getNickname() + " is " + statusName + " and can't move!");

                // Check for recovery (20% chance each turn)
                if (Math.random() < 0.2) {
                    ((trainerPokemon) aiPokemon).setStatusCondition(PokemonLogic.Pokemon.StatusCondition.none);
                    WindowThings.mainWindow.appendToOutput(aiPokemon.getNickname() +
                            (status == PokemonLogic.Pokemon.StatusCondition.SLP ? " woke up!" : " thawed out!"));
                    updateStatusLabels();
                }

                // Process end-of-turn status effects then end turn
                processStatusEffects(player.getCurrentPokemon());
                processStatusEffects(aiPokemon);
                return;
            }

            // Paralysis has 25% chance to prevent action
            if (status == PokemonLogic.Pokemon.StatusCondition.PAR && Math.random() < 0.25) {
                WindowThings.mainWindow.appendToOutput(aiPokemon.getNickname() + " is fully paralyzed and can't move!");

                // Process end-of-turn status effects then end turn
                processStatusEffects(player.getCurrentPokemon());
                processStatusEffects(aiPokemon);
                return;
            }
        }

        trainerPokemon trainerPkmn = opponent.getCurrentPokemon();
        Pokemon playerPokemon = player.getCurrentPokemon();

        // Get AI pokemon's moves
        List<Move> availableMoves = trainerPkmn.getMoves();
        Move bestMove = null;
        int maxDamage = 0;

        // Find move that deals most damage
        for (Move move : availableMoves) {
            int potentialDamage = calculateDamage(move, trainerPkmn, playerPokemon);

            // Consider type effectiveness
            double typeMultiplier = calculateTypeEffectivenessMultiplier(move, playerPokemon);
            potentialDamage = (int) (potentialDamage * typeMultiplier);

            if (potentialDamage > maxDamage) {
                maxDamage = potentialDamage;
                bestMove = move;
            }
        }

        // Use the selected move
        if (bestMove != null) {
            System.out.println(trainerPkmn.getName() + " used " + bestMove.getName() + "!");

            // Check move accuracy
            Random random = new Random();
            int accuracyCheck = random.nextInt(100) + 1; // 1-100

            if (accuracyCheck > bestMove.getAccuracy()) {
                // Move missed!
                WindowThings.mainWindow.appendToOutput(trainerPkmn.getName() + "'s attack missed!");
                return; // Skip damage calculation
            }

            int damage = calculateDamage(bestMove, trainerPkmn, playerPokemon);

            // Display type effectiveness message
            double typeMultiplier = calculateTypeEffectivenessMultiplier(bestMove, playerPokemon);
            displayTypeEffectivenessMessage(typeMultiplier);

            playerPokemon.setRemainingHealth(playerPokemon.getRemainingHealth() - damage);

            // Update player Pokemon health display
            updatePokemonUI(playerPokemon, playerHealthBarForeground, playerHealthLabel);

            // Apply status effect if this move has one
            tryApplyStatusEffect(bestMove, player.getCurrentPokemon());

            // Process end-of-turn status effects
            processStatusEffects(trainerPkmn);
            processStatusEffects(playerPokemon);

            // Check if player Pokemon fainted
            if (playerPokemon.getRemainingHealth() <= 0) {
                // Play fainting animation
                animatePokemonFainting(playerPokemonView, playerPokemon.getNickname(), () -> {
                    // Check if player has more usable Pokemon
                    if (!player.hasUsablePokemon()) {
                        System.out.println("You have no more usable Pokemon!");
                        System.out.println("You blacked out!");

                        // Close battle window
                        Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                            Stage stage = (Stage) playerHealthBarForeground.getScene().getWindow();
                            Platform.runLater(() -> stage.close());
                        }));
                        exitDelay.play();
                    } else {
                        // Force player to switch Pokemon
                        System.out.println("Choose your next Pokemon!");
                        // Automatically trigger the switch menu
                        Platform.runLater(() -> {
                            animateBattleButtonsSlideOut();
                            prepareForPokemonSwitch();
                        });
                    }
                });
            }
        }
    }

    private void applyPlayerAction(String action) {
        // Check for status conditions that prevent attacking
        PokemonLogic.Pokemon.StatusCondition status = player.getCurrentPokemon().getStatusCondition();
        if (status != PokemonLogic.Pokemon.StatusCondition.none) {
            // Sleep and freeze prevent attacking
            if (status == PokemonLogic.Pokemon.StatusCondition.SLP
                    || status == PokemonLogic.Pokemon.StatusCondition.FRZ) {
                String statusName = (status == PokemonLogic.Pokemon.StatusCondition.SLP) ? "asleep" : "frozen";
                WindowThings.mainWindow.appendToOutput(
                        player.getCurrentPokemon().getNickname() + " is " + statusName + " and can't move!");

                // Check for recovery (20% chance each turn)
                if (Math.random() < 0.2) {
                    player.getCurrentPokemon().setStatusCondition(PokemonLogic.Pokemon.StatusCondition.none);
                    WindowThings.mainWindow.appendToOutput(player.getCurrentPokemon().getNickname() +
                            (status == PokemonLogic.Pokemon.StatusCondition.SLP ? " woke up!" : " thawed out!"));
                    updateStatusLabels();
                } else {
                    // Skip to opponent's turn
                    if (isWildBattle) {
                        wildPokemonTurn();
                    } else {
                        aiTurn();
                    }
                    return;
                }
            }

            // Paralysis has 25% chance to prevent action
            if (status == PokemonLogic.Pokemon.StatusCondition.PAR && Math.random() < 0.25) {
                WindowThings.mainWindow.appendToOutput(player.getCurrentPokemon().getNickname() +
                        " is fully paralyzed and can't move!");

                // Skip to opponent's turn
                if (isWildBattle) {
                    wildPokemonTurn();
                } else {
                    aiTurn();
                }
                return;
            }
        }

        // Find the move with the given name in the player's Pokemon's moves
        Move move = player.getCurrentPokemon().getMovesList().stream()
                .filter(m -> m.getName().equals(action))
                .findFirst()
                .orElse(null);

        // If the move was found, apply it
        if (move != null) {
            // Check move accuracy first
            Random random = new Random();
            int accuracyCheck = random.nextInt(100) + 1; // 1-100

            if (accuracyCheck > move.getAccuracy()) {
                // Move missed!
                WindowThings.mainWindow.appendToOutput(player.getCurrentPokemon().getNickname() + "'s attack missed!");

                // Skip damage calculation and go straight to opponent's turn
                if (isWildBattle) {
                    wildPokemonTurn();
                } else {
                    aiTurn();
                }
                return;
            }

            if (isWildBattle) {
                // Wild battle - apply damage to wild Pokemon
                int damage = calculateDamage(move, player.getCurrentPokemon(), wildPokemon);

                // Display type effectiveness message
                double typeMultiplier = calculateTypeEffectivenessMultiplier(move, wildPokemon);
                displayTypeEffectivenessMessage(typeMultiplier);

                wildPokemon.setRemainingHealth(wildPokemon.getRemainingHealth() - damage);

                // Update wild Pokemon health bar
                updatePokemonUI(wildPokemon, opponentHealthBarForeground, opponentHealthLabel);

                // Apply status effect if this move has one
                tryApplyStatusEffect(move, wildPokemon);

                // Check if wild Pokemon fainted
                if (wildPokemon.getRemainingHealth() <= 0) {
                    // Play fainting animation
                    animatePokemonFainting(opponentPokemonView, "Wild " + wildPokemon.getName(), () -> {
                        // Award experience to player's Pokemon
                        try {
                            player.getCurrentPokemon().gainExperience(wildPokemon);
                        } catch (IOException e) {
                            logger.error("Error awarding experience: {}", e.getMessage());
                        }

                        // Close battle window with a small delay
                        Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                            Stage stage = (Stage) opponentHealthBarForeground.getScene().getWindow();
                            Platform.runLater(() -> stage.close());
                        }));
                        exitDelay.play();
                    });
                    return;
                }

                // Only process the player's status effects after their turn
                processStatusEffects(player.getCurrentPokemon());

                if (wildPokemon.getRemainingHealth() > 0) {
                    wildPokemonTurn();
                }
            } else {
                // Trainer battle - apply damage to trainer's Pokemon
                int damage = calculateDamage(move, player.getCurrentPokemon(), opponent.getPokemonList().get(0));

                trainerPokemon aiPokemon = opponent.getCurrentPokemon();
                aiPokemon.setRemainingHealth(aiPokemon.getRemainingHealth() - damage);

                // ADD THIS: Display type effectiveness message
                double typeMultiplier = calculateTypeEffectivenessMultiplier(move, aiPokemon);
                displayTypeEffectivenessMessage(typeMultiplier);

                // Apply status effect if this move has one
                tryApplyStatusEffect(move, opponent.getCurrentPokemon());

                // Check if the current opponent Pokemon fainted
                if (aiPokemon.getRemainingHealth() <= 0) {
                    // Play fainting animation
                    animatePokemonFainting(opponentPokemonView, aiPokemon.getName(), () -> {
                        // Award experience to player's Pokemon immediately when a trainer's Pokemon
                        // faints
                        try {
                            player.getCurrentPokemon().gainExperience(aiPokemon);
                        } catch (IOException e) {
                            logger.error("Error awarding experience: {}", e.getMessage());
                        }

                        // Check if this was the trainer's last Pokemon
                        if (!opponent.hasUsablePokemon()) {
                            System.out.println("You won the battle!");
                            int prizeMoney = opponent.getRewardMoney();
                            player.addMoney(prizeMoney);
                            System.out.println("You got $" + prizeMoney + " for winning!");

                            // Close battle window with a small delay
                            Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                                Stage stage = (Stage) opponentHealthBarForeground.getScene().getWindow();
                                Platform.runLater(() -> stage.close());
                            }));
                            exitDelay.play();
                        } else {
                            // Trainer sends out next Pokemon
                            opponent.switchToNextPokemon();
                            System.out.println(
                                    opponent.getName() + " sends out " + opponent.getCurrentPokemon().getName() + "!");

                            // Update nickname and level labels for the opponent
                            if (opponentPokemonNickname != null) {
                                opponentPokemonNickname.setText(opponent.getCurrentPokemon().getName());
                            }
                            if (opponentPokemonLevel != null) {
                                opponentPokemonLevel.setText("Lv. " + opponent.getCurrentPokemon().getLevel());
                            }

                            // Update the opponent's health display first (before the animation)
                            updatePokemonUI(opponent.getCurrentPokemon(), opponentHealthBarForeground,
                                    opponentHealthLabel);
                            updateStatusLabels(); // Add this line

                            // Load the new Pokemon sprite
                            Image newOpponentSprite = new Image(
                                    getClass().getResourceAsStream("/" + opponent.getCurrentPokemon().getSpritePath()));

                            // Create color adjust for the white flash effect (similar to player animation)
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setBrightness(2.0); // Start with white brightness
                            opponentPokemonView.setEffect(colorAdjust);

                            // Position the new Pokemon off-screen (to the right side)
                            opponentPokemonView.setOpacity(1.0); // Make fully visible
                            opponentPokemonView.setTranslateX(scene.getWidth()); // Start off-screen right
                            opponentPokemonView.setTranslateY(0); // Reset Y position
                            opponentPokemonView.setImage(newOpponentSprite); // Set new sprite

                            // Create slide-in animation
                            TranslateTransition slideInAnim = new TranslateTransition(Duration.millis(800),
                                    opponentPokemonView);
                            slideInAnim.setFromX(scene.getWidth() / 2); // Start from right side
                            slideInAnim.setToX(0); // Move to original position
                            slideInAnim.setInterpolator(Interpolator.EASE_OUT);

                            // Create brightness fade animation
                            Timeline brightnessAnim = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 2.0)),
                                    new KeyFrame(Duration.millis(800),
                                            new KeyValue(colorAdjust.brightnessProperty(), 0.0)));

                            // Combine both animations
                            ParallelTransition newPokemonEntrance = new ParallelTransition(slideInAnim, brightnessAnim);

                            // When animation completes, remove the effect
                            newPokemonEntrance.setOnFinished(evt -> {
                                opponentPokemonView.setEffect(null);
                            });

                            // Play the entrance animation
                            newPokemonEntrance.play();
                        }
                    });
                    return;
                }

                // Only process player's status effects here
                processStatusEffects(player.getCurrentPokemon());

                if (opponent.getCurrentPokemon().getRemainingHealth() > 0) {
                    aiTurn();
                }
            }
        }
    }

    /**
     * Universal damage calculation method that works with both Pokemon types.
     * 
     * @param move     The move being used
     * @param attacker The attacking Pokemon
     * @param defender The defending Pokemon
     * @return The calculated damage
     */
    private int calculateDamage(Move move, IPokemon attacker, IPokemon defender) {
        // Early return for status moves - they should do no damage at all
        if (move.getCategory().equalsIgnoreCase("status")) {
            return 0; // Status moves do 0 damage
        }

        // Step 1: Calculate base damage
        int levelFactor = (2 * attacker.getLevel()) / 5 + 2;

        // Check move category to determine which stats to use
        double attackDefenseRatio;
        if (move.getCategory().equalsIgnoreCase("physical")) {
            attackDefenseRatio = (double) attacker.getAttack() / defender.getDefense();
        } else if (move.getCategory().equalsIgnoreCase("special")) {
            attackDefenseRatio = (double) attacker.getSpecialAttack() / defender.getSpecialDefense();
        } else {
            // Other categories (should not reach here after the early return for status)
            attackDefenseRatio = 0;
        }

        double baseDamage = (levelFactor * move.getPower() * attackDefenseRatio) / 50;

        // Step 2: Add 2 to the base damage
        baseDamage += 2;

        // Step 3: Apply type multiplier
        double typeMultiplier = calculateTypeEffectivenessMultiplier(move, defender);

        // Step 4: Apply random variance (0.85 to 1.00)
        Random random = new Random();
        double randomFactor = 0.85 + (random.nextDouble() * 0.15);

        double finalDamage = baseDamage * typeMultiplier * randomFactor;

        // Step 5: Check for critical hit - only for moves with actual power
        // (Ensures status moves that might slip through don't crit)
        if (move.getPower() > 0 && random.nextInt(16) == 0) { // 1/16 chance
            System.out.println("Critical hit!");
            finalDamage *= 2;
        }

        // Step 6: Floor the final damage to at least 1
        return Math.max(1, (int) Math.floor(finalDamage));
    }

    /**
     * Calculate type effectiveness for any IPokemon implementation.
     */
    private double calculateTypeEffectivenessMultiplier(Move move, IPokemon pokemon) {
        double multiplier = 1.0;

        // Check if the move is super effective against the Pokemon's type1
        if (move.getSuperEffective().contains(pokemon.getType1())) {
            multiplier *= 2;
        }

        // Check if the move is super effective against the Pokemon's type2
        if (pokemon.getType2() != null && !pokemon.getType2().isEmpty() &&
                move.getSuperEffective().contains(pokemon.getType2())) {
            multiplier *= 2;
        }

        // Check if the move is not very effective against the Pokemon's type1
        if (move.getNotVeryEffective().contains(pokemon.getType1())) {
            multiplier /= 2;
        }

        // Check if the move is not very effective against the Pokemon's type2
        if (pokemon.getType2() != null && !pokemon.getType2().isEmpty() &&
                move.getNotVeryEffective().contains(pokemon.getType2())) {
            multiplier /= 2;
        }

        return multiplier;
    }

    /**
     * Displays an appropriate message based on type effectiveness
     * 
     * @param typeMultiplier The type effectiveness multiplier
     */
    private void displayTypeEffectivenessMessage(double typeMultiplier) {
        if (typeMultiplier > 1.9) { // For double super effective (4x)
            WindowThings.mainWindow.appendToOutput("It's super effective!!");
        } else if (typeMultiplier > 1.0) { // For super effective (2x)
            WindowThings.mainWindow.appendToOutput("It's super effective!");
        } else if (typeMultiplier < 0.6) { // For double not very effective (0.25x)
            WindowThings.mainWindow.appendToOutput("It's not very effective...");
        } else if (typeMultiplier < 1.0) { // For not very effective (0.5x)
            WindowThings.mainWindow.appendToOutput("It's not very effective.");
        }
        // No message for normal effectiveness (1.0x)
    }

    // Type colors
    private String getTypeColor(String type) {
        return switch (type.toLowerCase()) {
            case "normal" -> "#A8A878";
            case "fire" -> "#F08030";
            case "water" -> "#6890F0";
            case "electric" -> "#F8D030";
            case "grass" -> "#78C850";
            case "ice" -> "#98D8D8";
            case "fighting" -> "#C03028";
            case "poison" -> "#A040A0";
            case "ground" -> "#E0C068";
            case "flying" -> "#A890F0";
            case "psychic" -> "#F85888";
            case "bug" -> "#A8B820";
            case "rock" -> "#B8A038";
            case "ghost" -> "#705898";
            case "dragon" -> "#7038F8";
            case "dark" -> "#705848";
            case "steel" -> "#B8B8D0";
            default -> "#68A090";
        };
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            root = new Pane(); // Initialize root as class member
            scene = new Scene(root, 500, 500);

            // Load the custom font
            Font font = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 11);

            // Load the background image
            Image backgroundImage = new Image(getClass().getResourceAsStream("/BattleBG.png"));

            // Create the BackgroundImage object
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

            // Set the background of the BorderPane
            root.setBackground(new Background(background));

            // Load the images - different for wild battles vs trainer battles
            Image playerPokemonImage = new Image(
                    getClass().getResourceAsStream("/" + player.getCurrentPokemon().getSpritePath()));
            Image opponentPokemonImage;

            // Set up Pokemon images and labels based on battle type
            if (isWildBattle) {
                opponentPokemonImage = new Image(getClass().getResourceAsStream("/" + wildPokemon.getSpritePath()));
            } else {
                opponentPokemonImage = new Image(
                        getClass().getResourceAsStream("/" + opponent.getCurrentPokemon().getSpritePath()));
            }

            // Initialize the ImageView objects - THIS IS THE KEY FIX
            playerPokemonView = new ImageView(playerPokemonImage);
            opponentPokemonView = new ImageView(opponentPokemonImage);

            // Change these from local variables to instance variables
            playerPokemonNickname = new Label(player.getCurrentPokemon().getNickname());

            if (isWildBattle) {
                opponentPokemonNickname = new Label(wildPokemon.getName());
            } else {
                opponentPokemonNickname = new Label(opponent.getCurrentPokemon().getName());
            }

            // Create the level labels
            playerPokemonLevel = new Label("Lv. " + player.getCurrentPokemon().getLevel());

            if (isWildBattle) {
                opponentPokemonLevel = new Label("Lv. " + wildPokemon.getLevel());
            } else {
                opponentPokemonLevel = new Label("Lv. " + opponent.getCurrentPokemon().getLevel());
            }

            // Add status effect labels
            playerStatusLabel = new Label("");
            opponentStatusLabel = new Label("");
            playerStatusLabel.setFont(font);
            opponentStatusLabel.setFont(font);

            // Apply styling to make the status labels prettier with pill-shaped background
            String statusLabelStyle = "-fx-padding: 1 5 1 5; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 9px; -fx-text-fill: white;";
            playerStatusLabel.setStyle(statusLabelStyle);
            opponentStatusLabel.setStyle(statusLabelStyle);

            // Create the health bars using rectangles
            playerHealthBarBackground = new Rectangle(100, 10);
            playerHealthBarBackground.setFill(Color.DARKGREY);
            playerHealthBarForeground = new Rectangle(
                    player.getCurrentPokemon().getRemainingHealth() / (double) player.getCurrentPokemon().getHp() * 100,
                    10);
            playerHealthBarBackground.setStroke(Color.BLACK); // Default outline color
            playerHealthBarBackground.setStrokeWidth(2);
            playerHealthBarBackground.setArcWidth(10);
            playerHealthBarBackground.setArcHeight(10);
            playerHealthBarForeground.setFill(Color.LIGHTGREEN);
            playerHealthBarForeground.setArcWidth(10);
            playerHealthBarForeground.setArcHeight(10);

            opponentHealthBarBackground = new Rectangle(100, 10);
            opponentHealthBarBackground.setFill(Color.DARKGREY);
            opponentHealthBarBackground.setStroke(Color.BLACK); // Default outline color
            opponentHealthBarBackground.setStrokeWidth(2);
            opponentHealthBarBackground.setArcWidth(10);
            opponentHealthBarBackground.setArcHeight(10);

            if (isWildBattle) {
                opponentHealthBarForeground = new Rectangle(
                        wildPokemon.getRemainingHealth() / (double) wildPokemon.getHp() * 100, 10);
            } else {
                opponentHealthBarForeground = new Rectangle(
                        opponent.getCurrentPokemon().getRemainingHealth()
                                / (double) opponent.getCurrentPokemon().getHp()
                                * 100,
                        10);
            }

            opponentHealthBarForeground.setFill(Color.LIGHTGREEN);

            // Set the positions of the status labels next to health text instead of
            // nickname
            // We'll keep these but hide them since we're using health bar outlines instead
            playerStatusLabel.setVisible(false);
            opponentStatusLabel.setVisible(false);

            // Create the health labels
            playerHealthLabel = new Label(
                    player.getCurrentPokemon().getRemainingHealth() + "/" + player.getCurrentPokemon().getHp());

            if (isWildBattle) {
                opponentHealthLabel = new Label(
                        wildPokemon.getRemainingHealth() + "/" + wildPokemon.getHp());
            } else {
                opponentHealthLabel = new Label(
                        opponent.getCurrentPokemon().getRemainingHealth() + "/" + opponent.getCurrentPokemon().getHp());
            }

            // Set the font for labels
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
            Rectangle playerXPBarForeground = new Rectangle(
                    player.getCurrentPokemon().getExperience() / (double) player.getCurrentPokemon().getLevelTreshhold()
                            * 100,
                    5);
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
            root.getChildren().addAll(playerPokemonView, opponentPokemonView, playerHealthBarBackground,
                    playerHealthBarForeground, opponentHealthBarBackground, opponentHealthBarForeground,
                    playerHealthLabel,
                    opponentHealthLabel, playerXPBarBackground, playerXPBarForeground, opponentXPBarBackground,
                    playerPokemonNickname, opponentPokemonNickname, playerPokemonLevel, opponentPokemonLevel,
                    playerStatusLabel, opponentStatusLabel);

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
            playerHealthBarBackground
                    .setLayoutY(playerPokemonView.getLayoutY() + playerPokemonView.getFitHeight() + 100);
            playerHealthBarForeground.setLayoutX(playerPokemonView.getLayoutX() - 20);
            playerHealthBarForeground
                    .setLayoutY(playerPokemonView.getLayoutY() + playerPokemonView.getFitHeight() + 100);
            opponentHealthBarBackground.setLayoutX(opponentPokemonView.getLayoutX() - 25);
            opponentHealthBarBackground
                    .setLayoutY(opponentPokemonView.getLayoutY() + opponentPokemonView.getFitHeight() + 100);
            opponentHealthBarForeground.setLayoutX(opponentPokemonView.getLayoutX() - 25);
            opponentHealthBarForeground
                    .setLayoutY(opponentPokemonView.getLayoutY() + opponentPokemonView.getFitHeight() + 100);
            playerHealthBarBackground.setArcWidth(5);
            playerHealthBarBackground.setArcHeight(5);
            playerHealthBarForeground.setArcWidth(5);
            playerHealthBarForeground.setArcHeight(5);

            opponentHealthBarBackground.setArcWidth(5);
            opponentHealthBarBackground.setArcHeight(5);
            opponentHealthBarForeground.setArcWidth(5);
            opponentHealthBarForeground.setArcHeight(5);

            // Set the positions of the nickname labels
            playerPokemonNickname.setLayoutX(playerHealthBarBackground.getLayoutX());
            playerPokemonNickname
                    .setLayoutY(playerHealthBarBackground.getLayoutY() - playerPokemonNickname.getHeight() - 15);
            opponentPokemonNickname.setLayoutX(opponentHealthBarBackground.getLayoutX());
            opponentPokemonNickname
                    .setLayoutY(opponentHealthBarBackground.getLayoutY() - opponentPokemonNickname.getHeight() - 15);

            // Set the positions of the health labels
            playerHealthLabel.setLayoutX(playerHealthBarBackground.getLayoutX());
            playerHealthLabel
                    .setLayoutY(playerHealthBarBackground.getLayoutY() + playerHealthBarBackground.getHeight());
            opponentHealthLabel.setLayoutX(opponentHealthBarBackground.getLayoutX());
            opponentHealthLabel
                    .setLayoutY(opponentHealthBarBackground.getLayoutY() + opponentHealthBarBackground.getHeight());

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

            // Position the status labels next to health text instead of nickname
            playerStatusLabel.setLayoutX(playerHealthLabel.getLayoutX() + 60); // Position after health text
            playerStatusLabel.setLayoutY(playerHealthLabel.getLayoutY());
            opponentStatusLabel.setLayoutX(opponentHealthLabel.getLayoutX() + 60); // Position after health text
            opponentStatusLabel.setLayoutY(opponentHealthLabel.getLayoutY());

            // Update status display initially
            updateStatusLabels();

            // Flip the opponent's Pokemon image horizontally so it faces the player's
            // Pokemon
            playerPokemonView.setScaleX(-1);

            primaryStage.setTitle("Pokemon Battle");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            // Create battle controls
            controlsBox = new HBox(20);
            controlsBox.setLayoutY(scene.getHeight() - 120);
            controlsBox.setPrefWidth(scene.getWidth());
            controlsBox.setPrefWidth(scene.getWidth());
            controlsBox.setAlignment(Pos.CENTER);
            controlsBox.setSpacing(20);

            // Create and style buttons
            Button fightButton = new Button("Fight");
            Button catchButton = new Button("Catch");
            Button switchButton = new Button("Switch");
            Button runButton = new Button("Run");

            // Increase font size
            Font largeFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 18);
            fightButton.setFont(largeFont);
            catchButton.setFont(largeFont);
            switchButton.setFont(largeFont);
            runButton.setFont(largeFont);

            // Apply improved styling to match exploreWindow buttons
            String baseButtonStyle = "-fx-background-color: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-padding: 10 15 10 15; " +
                    "-fx-text-fill: black; " +
                    "-fx-border-color: #000000; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-width: 2px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);";

            String hoverButtonStyle = "-fx-background-color: #f0f0f0; " +
                    "-fx-background-radius: 5; " +
                    "-fx-padding: 10 15 10 15; " +
                    "-fx-text-fill: black; " +
                    "-fx-border-color: #000000; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-width: 2px; " +
                    "-fx-scale-x: 1.03; " +
                    "-fx-scale-y: 1.03; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);";

            String pressedButtonStyle = "-fx-background-color: #e0e0e0; " +
                    "-fx-background-radius: 5; " +
                    "-fx-padding: 10 15 10 15; " +
                    "-fx-text-fill: black; " +
                    "-fx-border-color: #000000; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-width: 2px; " +
                    "-fx-scale-x: 0.98; " +
                    "-fx-scale-y: 0.98; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);";

            // Apply the same styling to all main battle buttons
            for (Button button : new Button[] { fightButton, catchButton, switchButton, runButton }) {
                button.setStyle(baseButtonStyle);
                button.setOnMouseEntered(e -> button.setStyle(hoverButtonStyle));
                button.setOnMouseExited(e -> button.setStyle(baseButtonStyle));
                button.setOnMousePressed(e -> button.setStyle(pressedButtonStyle));
                button.setOnMouseReleased(e -> button.setStyle(hoverButtonStyle));
            }

            // Fight button animation
            TranslateTransition fightButtonAnim = new TranslateTransition(Duration.millis(500), fightButton);
            fightButtonAnim.setFromY(100);
            fightButtonAnim.setToY(0);
            fightButtonAnim.setInterpolator(Interpolator.EASE_OUT);

            // Catch button animation
            TranslateTransition catchButtonAnim = new TranslateTransition(Duration.millis(525), catchButton);
            catchButtonAnim.setFromY(100);
            catchButtonAnim.setToY(0);
            catchButtonAnim.setInterpolator(Interpolator.EASE_OUT);

            // Switch button animation
            TranslateTransition switchButtonAnim = new TranslateTransition(Duration.millis(550), switchButton);
            switchButtonAnim.setFromY(100);
            switchButtonAnim.setToY(0);
            switchButtonAnim.setInterpolator(Interpolator.EASE_OUT);

            // Run button animation
            TranslateTransition runButtonAnim = new TranslateTransition(Duration.millis(600), runButton);
            runButtonAnim.setFromY(100);
            runButtonAnim.setToY(0);
            runButtonAnim.setInterpolator(Interpolator.EASE_OUT);

            // Play animations
            fightButtonAnim.play();
            catchButtonAnim.play();
            switchButtonAnim.play();
            runButtonAnim.play();

            // Fight button disappearing animation
            TranslateTransition fightButtonHide = new TranslateTransition(Duration.millis(500), fightButton);
            fightButtonHide.setFromY(0);
            fightButtonHide.setToY(100);
            fightButtonHide.setInterpolator(Interpolator.EASE_IN);

            // Catch button disappearing animation
            TranslateTransition catchButtonHide = new TranslateTransition(Duration.millis(550), catchButton);
            catchButtonHide.setFromY(0);
            catchButtonHide.setToY(100);
            catchButtonHide.setInterpolator(Interpolator.EASE_IN);

            // Switch button disappearing animation
            TranslateTransition switchButtonHide = new TranslateTransition(Duration.millis(600), switchButton);
            switchButtonHide.setFromY(0);
            switchButtonHide.setToY(100);
            switchButtonHide.setInterpolator(Interpolator.EASE_IN);

            // Run button disappearing animation
            TranslateTransition runButtonHide = new TranslateTransition(Duration.millis(700), runButton);
            runButtonHide.setFromY(0);
            runButtonHide.setToY(100);
            runButtonHide.setInterpolator(Interpolator.EASE_IN);

            VBox.setVgrow(controlsBox, Priority.NEVER);
            root.getChildren().add(controlsBox);

            // Configure HBox for buttons - removed background styling
            controlsBox.setSpacing(10);
            controlsBox.setAlignment(Pos.CENTER);
            controlsBox.setPadding(new Insets(70, 10, 10, 10));

            // Use full width
            controlsBox.setPrefWidth(500);
            controlsBox.setMaxWidth(Double.MAX_VALUE);
            controlsBox.setMinHeight(40);

            // Initialize controlsBox with default buttons
            controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

            // Make buttons grow to fill space
            HBox.setHgrow(fightButton, Priority.ALWAYS);
            HBox.setHgrow(catchButton, Priority.ALWAYS);
            HBox.setHgrow(switchButton, Priority.ALWAYS);
            HBox.setHgrow(runButton, Priority.ALWAYS);

            // Store the original event handlers before setting them
            fightButtonHandler = e -> {
                fightButtonHide.play();
                catchButtonHide.play();
                switchButtonHide.play();
                runButtonHide.play();

                runButtonHide.setOnFinished(finishedEvent -> {
                    controlsBox.getChildren().clear();

                    if (player.getCurrentPokemon() != null) {
                        List<Move> moves = player.getCurrentPokemon().getMovesList();
                        if (moves != null && !moves.isEmpty()) {
                            controlsBox.getChildren().clear();

                            // Create container for rows of buttons
                            VBox moveContainer = new VBox(20); // Add spacing between rows
                            moveContainer.setAlignment(Pos.CENTER);
                            moveContainer.setPadding(new Insets(0, 10, 0, 10));

                            HBox topRow = new HBox(10); // Top row of moves
                            HBox bottomRow = new HBox(10); // Bottom row of moves
                            topRow.setAlignment(Pos.CENTER);
                            bottomRow.setAlignment(Pos.CENTER);

                            // Create VBox to hold both rows with 10px spacing
                            VBox moveRows = new VBox(10);
                            moveRows.setAlignment(Pos.CENTER);

                            // Create lists to store move buttons by row for reverse animation
                            List<Button> topRowButtons = new ArrayList<>();
                            List<Button> bottomRowButtons = new ArrayList<>();

                            // Add move buttons with animation
                            for (int i = 0; i < moves.size(); i++) {
                                Move move = moves.get(i);
                                Button moveButton = new Button(move.getName());
                                moveButton.setFont(font);

                                String baseColor = getTypeColor(move.getType());
                                // Create darker and lighter variants for gradient
                                Color typeColor = Color.web(baseColor);
                                Color darker = typeColor.darker();
                                Color lighter = typeColor.brighter();

                                // Keep the existing color-based styling but improve the button appearance
                                String moveButtonStyle = String.format(
                                        "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                                                "-fx-text-fill: black; " +
                                                "-fx-border-color: derive(%s, -20%%); " +
                                                "-fx-border-radius: 5; " +
                                                "-fx-background-radius: 5; " +
                                                "-fx-min-width: 150px; " +
                                                "-fx-min-height: 40px; " +
                                                "-fx-border-width: 2px; " +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 1);",
                                        lighter.toString().replace("0x", "#"),
                                        darker.toString().replace("0x", "#"),
                                        baseColor);

                                String hoverStyle = String.format(
                                        "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                                                "-fx-text-fill: black; " +
                                                "-fx-border-color: derive(%s, -20%%); " +
                                                "-fx-border-radius: 5; " +
                                                "-fx-background-radius: 5; " +
                                                "-fx-min-width: 150px; " +
                                                "-fx-min-height: 40px; " +
                                                "-fx-border-width: 2px; " +
                                                "-fx-scale-x: 1.03; " +
                                                "-fx-scale-y: 1.03; " +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 6, 0, 0, 2);",
                                        lighter.toString().replace("0x", "#"),
                                        darker.toString().replace("0x", "#"),
                                        baseColor);

                                String pressedStyle = String.format(
                                        "-fx-background-color: linear-gradient(to top, %s, %s); " +
                                                "-fx-text-fill: black; " +
                                                "-fx-border-color: derive(%s, -20%%); " +
                                                "-fx-border-radius: 5; " +
                                                "-fx-background-radius: 5; " +
                                                "-fx-min-width: 150px; " +
                                                "-fx-min-height: 40px; " +
                                                "-fx-border-width: 2px; " +
                                                "-fx-scale-x: 0.98; " +
                                                "-fx-scale-y: 0.98; " +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);",
                                        lighter.toString().replace("0x", "#"),
                                        darker.toString().replace("0x", "#"),
                                        baseColor);

                                moveButton.setStyle(moveButtonStyle);
                                moveButton.setOnMouseEntered(ev -> moveButton.setStyle(hoverStyle));
                                moveButton.setOnMouseExited(ev -> moveButton.setStyle(moveButtonStyle));
                                moveButton.setOnMousePressed(ev -> moveButton.setStyle(pressedStyle));
                                moveButton.setOnMouseReleased(ev -> moveButton.setStyle(hoverStyle));

                                // Initial position: below the visible window
                                moveButton.setTranslateY(scene.getHeight() - 300);

                                // Set the action for each move button
                                moveButton.setOnAction(ev -> {
                                    System.out.println(
                                            player.getCurrentPokemon().getNickname() + " used " + move.getName() + "!");
                                    applyPlayerAction(move.getName());

                                    // Only update UI if battle is still ongoing
                                    updateBattleUI();
                                });

                                // Assign buttons to rows and store them in the corresponding lists
                                if (i < 2) {
                                    topRow.getChildren().add(moveButton);
                                    topRowButtons.add(moveButton);

                                    // Animation for top row
                                    TranslateTransition moveButtonAnim = new TranslateTransition(Duration.millis(600),
                                            moveButton);
                                    moveButtonAnim.setFromY(scene.getHeight() - 400);
                                    moveButtonAnim.setToY(-120);
                                    moveButtonAnim.setInterpolator(Interpolator.EASE_OUT);
                                    moveButtonAnim.play();
                                } else {
                                    bottomRow.getChildren().add(moveButton);
                                    bottomRowButtons.add(moveButton);

                                    // Animation for bottom row
                                    TranslateTransition moveButtonAnim = new TranslateTransition(Duration.millis(700),
                                            moveButton);
                                    moveButtonAnim.setFromY(scene.getHeight() - 400);
                                    moveButtonAnim.setToY(-120);
                                    moveButtonAnim.setInterpolator(Interpolator.EASE_OUT);
                                    moveButtonAnim.play();
                                }
                            }

                            // Add both rows to the VBox
                            moveRows.getChildren().addAll(topRow, bottomRow);

                            // Create the "Back" button with improved styling to match exploreWindow
                            // Made font smaller to fit within the tiny button size
                            Button backButton = new Button("◄");
                            backButton.setFont(Font.font("Arial", 12)); // Decreased font size from 16 to 12
                            backButton.setStyle("-fx-background-color: #cc0000; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 2 4 2 4; " + // Reverted to original smaller padding
                                    "-fx-background-radius: 5; " +
                                    "-fx-border-color: #800000; " +
                                    "-fx-border-radius: 5; " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);");

                            backButton.setOnMouseEntered(ev -> backButton.setStyle("-fx-background-color: #ff0000; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 2 4 2 4; " + // Reverted to original smaller padding
                                    "-fx-background-radius: 5; " +
                                    "-fx-border-color: #800000; " +
                                    "-fx-border-radius: 5; " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-scale-x: 1.03; " +
                                    "-fx-scale-y: 1.03; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);"));

                            backButton.setOnMouseExited(ev -> backButton.setStyle("-fx-background-color: #cc0000; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 2 4 2 4; " + // Reverted to original smaller padding
                                    "-fx-background-radius: 5; " +
                                    "-fx-border-color: #800000; " +
                                    "-fx-border-radius: 5; " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);"));

                            backButton.setOnMousePressed(ev -> backButton.setStyle("-fx-background-color: #990000; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 2 4 2 4; " + // Reverted to original smaller padding
                                    "-fx-background-radius: 5; " +
                                    "-fx-border-color: #800000; " +
                                    "-fx-border-radius: 5; " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-scale-x: 0.98; " +
                                    "-fx-scale-y: 0.98; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);"));

                            backButton.setPrefWidth(25); // Reverted to original smaller width
                            backButton.setPrefHeight(25); // Reverted to original smaller height
                            backButton.setTranslateY(scene.getHeight() + 100);

                            // Handle back button click
                            backButton.setOnAction(backEvent -> {
                                // Reverse animation for top row buttons
                                for (Button moveButton : topRowButtons) {
                                    TranslateTransition reverseAnim = new TranslateTransition(Duration.millis(600),
                                            moveButton);
                                    reverseAnim.setFromY(-120); // Current position
                                    reverseAnim.setToY(scene.getHeight() - 400); // Move out of view
                                    reverseAnim.setInterpolator(Interpolator.EASE_IN);
                                    reverseAnim.play();
                                }

                                // Reverse animation for bottom row buttons
                                for (Button moveButton : bottomRowButtons) {
                                    TranslateTransition reverseAnim = new TranslateTransition(Duration.millis(700),
                                            moveButton);
                                    reverseAnim.setFromY(-120); // Current position
                                    reverseAnim.setToY(scene.getHeight() - 400); // Move out of view
                                    reverseAnim.setInterpolator(Interpolator.EASE_IN);
                                    reverseAnim.play();
                                }

                                // Reverse animation for the back button
                                TranslateTransition backButtonReverseAnim = new TranslateTransition(
                                        Duration.millis(500),
                                        backButton);
                                backButtonReverseAnim.setFromY(-120); // Current position
                                backButtonReverseAnim.setToY(scene.getHeight() - 400); // Move out of view
                                backButtonReverseAnim.setInterpolator(Interpolator.EASE_IN);
                                backButtonReverseAnim.play();

                                // Delay clearing controlsBox until animations finish
                                new Timeline(new KeyFrame(Duration.millis(500), ae -> {
                                    controlsBox.getChildren().clear();

                                    // Reset button positions
                                    fightButton.setTranslateY(scene.getHeight());
                                    catchButton.setTranslateY(scene.getHeight());
                                    switchButton.setTranslateY(scene.getHeight());
                                    runButton.setTranslateY(scene.getHeight());

                                    // Add all buttons back
                                    controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

                                    // Play animations
                                    fightButtonAnim.play();
                                    catchButtonAnim.play();
                                    switchButtonAnim.play();
                                    runButtonAnim.play();
                                })).play();
                            });

                            // Animate back button
                            TranslateTransition backButtonAnim = new TranslateTransition(Duration.millis(500),
                                    backButton);
                            backButtonAnim.setFromY(scene.getHeight() - 400);
                            backButtonAnim.setToY(-120);
                            backButtonAnim.setInterpolator(Interpolator.EASE_OUT);
                            backButtonAnim.play();

                            // Add "Back" button to the top of moveContainer
                            moveContainer.getChildren().addAll(backButton, moveRows);
                            controlsBox.getChildren().add(moveContainer);
                        }
                    }
                });
            };

            // Apply the stored handler
            fightButton.setOnAction(fightButtonHandler);

            // Store and apply switch button handler
            switchButtonHandler = e -> {
                fightButtonHide.play();
                catchButtonHide.play();
                switchButtonHide.play();
                runButtonHide.play();

                runButtonHide.setOnFinished(finishedEvent -> {
                    controlsBox.getChildren().clear();
                    HBox switchBox = new HBox(5);
                    switchBox.setAlignment(Pos.CENTER);
                    switchBox.setPadding(new Insets(5));
                    switchBox.setTranslateY(scene.getHeight());

                    String pokemonButtonStyle = "-fx-border-color: black; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 2; " +
                            "-fx-background-radius: 2; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-color: linear-gradient(to bottom, #B30000 50%, #EBEBEB 50%); " +
                            "-fx-min-width: 65px; " +
                            "-fx-min-height: 65px; " +
                            "-fx-focus-color: transparent; " +
                            "-fx-faint-focus-color: transparent;";

                    // Style for already active Pokémon
                    String activeButtonStyle = pokemonButtonStyle +
                            "-fx-border-color: gold; " +
                            "-fx-border-width: 2px; " +
                            "-fx-effect: dropshadow(three-pass-box, gold, 5, 0.7, 0, 0);";

                    // Style for fainted Pokémon
                    String faintedButtonStyle = pokemonButtonStyle +
                            "-fx-opacity: 0.6; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 1);";

                    for (int i = 0; i < player.getParty().size(); i++) {
                        Pokemon pokemon = player.getParty().get(i);
                        Button pokemonButton = new Button();
                        Image pokemonSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
                        ImageView spriteView = new ImageView(pokemonSprite);
                        spriteView.setFitWidth(55);

                        spriteView.setFitHeight(55);
                        spriteView.setPreserveRatio(true);
                        pokemonButton.setGraphic(spriteView);

                        // Add more details to tooltip
                        String tooltipText = pokemon.getNickname() + " (Lv." + pokemon.getLevel() + ")";
                        if (pokemon.getRemainingHealth() <= 0) {
                            tooltipText += " - Fainted";
                        } else {
                            tooltipText += " - HP: " + pokemon.getRemainingHealth() + "/" + pokemon.getHp();
                        }
                        pokemonButton.setTooltip(new Tooltip(tooltipText));

                        // Apply appropriate style based on Pokémon state
                        if (player.getCurrentPokemon() == pokemon) {
                            pokemonButton.setStyle(activeButtonStyle);
                        } else if (pokemon.getRemainingHealth() <= 0) {
                            pokemonButton.setStyle(faintedButtonStyle);
                            // Add a visual indicator for fainted Pokémon (gray overlay)
                            ColorAdjust grayscale = new ColorAdjust();
                            grayscale.setSaturation(-1.0); // Full desaturation
                            spriteView.setEffect(grayscale);
                        } else {
                            pokemonButton.setStyle(pokemonButtonStyle);
                        }

                        pokemonButton.setOnAction(event -> {
                            // If clicked on current Pokémon, close the switch menu and return to main
                            // battle controls
                            if (player.getCurrentPokemon() == pokemon) {
                                System.out.println(pokemon.getNickname() + " is already out!");

                                // Add a brief highlight effect before closing
                                ColorAdjust highlight = new ColorAdjust();
                                highlight.setBrightness(0.3);
                                pokemonButton.setEffect(highlight);

                                // Animate the highlight fading
                                Timeline highlightFade = new Timeline(
                                        new KeyFrame(Duration.ZERO, new KeyValue(highlight.brightnessProperty(), 0.3)),
                                        new KeyFrame(Duration.millis(300),
                                                new KeyValue(highlight.brightnessProperty(), 0.0)));
                                highlightFade.play();

                                // Close the switch menu
                                TranslateTransition closeAnim = new TranslateTransition(Duration.millis(800),
                                        switchBox);
                                closeAnim.setToY(scene.getHeight());
                                closeAnim.setInterpolator(Interpolator.EASE_IN);
                                closeAnim.setOnFinished(evt -> {
                                    controlsBox.getChildren().clear();

                                    // Reset button positions
                                    fightButton.setTranslateY(scene.getHeight());
                                    catchButton.setTranslateY(scene.getHeight());
                                    switchButton.setTranslateY(scene.getHeight());
                                    runButton.setTranslateY(scene.getHeight());

                                    // Add all buttons back
                                    controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

                                    // Play animations
                                    fightButtonAnim.play();
                                    catchButtonAnim.play();
                                    switchButtonAnim.play();
                                    runButtonAnim.play();
                                });
                                closeAnim.play();
                                return;
                            }

                            // Skip if the Pokemon is fainted
                            if (pokemon.getRemainingHealth() <= 0) {
                                System.out.println(pokemon.getNickname() + " has fainted and cannot battle!");

                                // Add visual feedback for trying to select a fainted Pokémon
                                ColorAdjust errorEffect = new ColorAdjust();
                                errorEffect.setSaturation(-0.5);
                                errorEffect.setBrightness(-0.2);
                                pokemonButton.setEffect(errorEffect);

                                // Shake animation to indicate error
                                TranslateTransition shakeLeft = new TranslateTransition(Duration.millis(80),
                                        pokemonButton);
                                shakeLeft.setByX(-5);
                                TranslateTransition shakeRight = new TranslateTransition(Duration.millis(80),
                                        pokemonButton);
                                shakeRight.setByX(5);
                                TranslateTransition shakeCenter = new TranslateTransition(Duration.millis(80),
                                        pokemonButton);
                                shakeCenter.setByX(0);

                                Timeline resetEffect = new Timeline(
                                        new KeyFrame(Duration.millis(400), evt -> pokemonButton.setEffect(null)));

                                shakeLeft.setOnFinished(evt -> shakeRight.play());
                                shakeRight.setOnFinished(evt -> shakeCenter.play());
                                shakeCenter.setOnFinished(evt -> resetEffect.play());
                                shakeLeft.play();
                                return;
                            }

                            // Store current Pokemon for reference
                            Pokemon previousPokemon = player.getCurrentPokemon();

                            // Apply color adjustment for switch-out animation
                            ColorAdjust colorAdjust = new ColorAdjust();
                            playerPokemonView.setEffect(colorAdjust);

                            // Reverse color fade (turn to white while moving out)
                            Timeline switchOutFade = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 0.0)),
                                    new KeyFrame(Duration.millis(800),
                                            new KeyValue(colorAdjust.brightnessProperty(), 2.0)));

                            // Switch-out animation for current Pokémon
                            TranslateTransition switchOutAnim = new TranslateTransition(Duration.millis(800),
                                    playerPokemonView);
                            switchOutAnim.setToX(-scene.getWidth() / 2);
                            switchOutAnim.setInterpolator(Interpolator.EASE_IN);

                            switchOutAnim.setOnFinished(evt -> {
                                // Store the final X position of the outgoing Pokémon
                                double switchOutEndX = playerPokemonView.getTranslateX();

                                // Update player's current Pokemon before UI updates
                                player.setCurrentPokemon(pokemon);

                                // Update all UI elements together - before showing the new sprite
                                // This ensures the health bar and name change together
                                playerPokemonNickname.setText(pokemon.getNickname());
                                playerPokemonLevel.setText("Lv. " + pokemon.getLevel());
                                updatePokemonUI(pokemon, playerHealthBarForeground, playerHealthLabel);
                                updateStatusLabels(); // Add this line

                                // Update XP bar
                                double playerXPPercent = (double) pokemon.getExperience() / pokemon.getLevelTreshhold()
                                        * 100;
                                playerXPBarForeground.setWidth(playerXPPercent);

                                // Now update the sprite after all other UI elements have been updated
                                Image newSprite = new Image(
                                        getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
                                playerPokemonView.setImage(newSprite);

                                // Set the new Pokémon's initial position to the final position of the outgoing
                                // Pokémon
                                playerPokemonView.setTranslateX(switchOutEndX);
                                colorAdjust.setBrightness(2.0); // Ensure the new Pokémon starts fully white

                                // Switch-in color fade animation (from bright white to normal)
                                Timeline switchInFade = new Timeline(
                                        new KeyFrame(Duration.ZERO,
                                                new KeyValue(colorAdjust.brightnessProperty(), 2.0)),
                                        new KeyFrame(Duration.millis(800),
                                                new KeyValue(colorAdjust.brightnessProperty(), 0.0)));

                                // Switch-in animation for new Pokémon
                                TranslateTransition switchInAnim = new TranslateTransition(Duration.millis(800),
                                        playerPokemonView);
                                switchInAnim.setToX(0); // Move to the correct position
                                switchInAnim.setInterpolator(Interpolator.EASE_OUT);

                                // When the switch-in animation completes
                                switchInAnim.setOnFinished(ev -> {
                                    playerPokemonView.setEffect(null);
                                    System.out.println(previousPokemon.getNickname() + " was withdrawn!");
                                    System.out.println("Go! " + pokemon.getNickname() + "!");

                                    // Now the opponent gets to attack after the switch
                                    // Delay the opponent's attack slightly for better visual flow
                                    Timeline attackDelay = new Timeline(
                                            new KeyFrame(Duration.millis(500), attackEvent -> {
                                                if (isWildBattle) {
                                                    wildPokemonTurn();
                                                } else {
                                                    aiTurn();
                                                }

                                                // Update the UI after opponent's attack
                                                updateBattleUI();

                                                // Check if player's Pokemon fainted from the opponent's attack
                                                if (player.getCurrentPokemon().getRemainingHealth() <= 0) {
                                                    System.out.println(
                                                            player.getCurrentPokemon().getNickname() + " fainted!");

                                                    // Check if player has more usable Pokemon
                                                    if (!player.hasUsablePokemon()) {
                                                        System.out.println("You have no more usable Pokemon!");
                                                        System.out.println("You blacked out!");

                                                        // Close battle window
                                                        Stage stage = (Stage) playerHealthBarForeground.getScene()
                                                                .getWindow();
                                                        Platform.runLater(stage::close);
                                                    } else {
                                                        // Force player to switch Pokemon
                                                        System.out.println("Choose your next Pokemon!");
                                                    }
                                                }
                                            }));
                                    attackDelay.play();
                                });

                                // Play switch-in animations
                                switchInAnim.play();
                                switchInFade.play();
                            });

                            // Play switch-out animations
                            switchOutAnim.play();
                            switchOutFade.play();

                            TranslateTransition closeAnim = new TranslateTransition(Duration.millis(1800), switchBox);
                            closeAnim.setToY(scene.getHeight());
                            closeAnim.setInterpolator(Interpolator.EASE_IN);
                            closeAnim.setOnFinished(evt -> {
                                controlsBox.getChildren().clear();

                                // Set initial state for buttons (likely off-screen position)
                                fightButton.setTranslateY(scene.getHeight());
                                catchButton.setTranslateY(scene.getHeight());
                                switchButton.setTranslateY(scene.getHeight());
                                runButton.setTranslateY(scene.getHeight());

                                // Add buttons first
                                controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

                                // Then play animations
                                fightButtonAnim.play();
                                catchButtonAnim.play();
                                switchButtonAnim.play();
                                runButtonAnim.play();
                            });
                            closeAnim.play();
                        });

                        switchBox.getChildren().add(pokemonButton);
                    }

                    // Place switchBox directly in controlsBox (no VBox container)
                    controlsBox.getChildren().clear();
                    controlsBox.getChildren().add(switchBox);

                    // Restore original animation behavior
                    TranslateTransition tt = new TranslateTransition(Duration.millis(1800), switchBox);
                    tt.setToY(-20);
                    tt.setInterpolator(Interpolator.EASE_OUT);
                    tt.jumpTo(Duration.millis(1400)); // Jump to near the end of the animation for that snappy effect
                    tt.play();
                });
            };
            switchButton.setOnAction(switchButtonHandler);

            // Different button behaviors for wild vs trainer battles
            if (isWildBattle) {
                // Wild battle-specific Catch button behavior
                catchButtonHandler = e -> {
                    fightButtonHide.play();
                    catchButtonHide.play();
                    switchButtonHide.play();
                    runButtonHide.play();

                    runButtonHide.setOnFinished(finishedEvent -> {
                        // Determine catch probability (75% chance)
                        boolean catchSuccess = Math.random() < 0.75;

                        // Start the catch animation sequence
                        animateCatchAttempt(catchSuccess, wildPokemon, opponentPokemonView, () -> {
                            if (catchSuccess) {
                                System.out.println("You caught " + wildPokemon.getName() + "!");
                                player.addPokemonToParty(wildPokemon);

                                // Close the battle window on the FX thread after a short delay
                                Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1500), exitEvent -> {
                                    Stage stage = (Stage) controlsBox.getScene().getWindow();
                                    Platform.runLater(stage::close);
                                }));
                                exitDelay.play();
                            } else {
                                System.out.println(wildPokemon.getName() + " broke free!");

                                // Wild Pokémon's turn after a delay
                                Timeline turnDelay = new Timeline(new KeyFrame(Duration.millis(500), turnEvent -> {
                                    wildPokemonTurn();

                                    // Reset controls after wild Pokémon's turn
                                    controlsBox.getChildren().clear();
                                    fightButton.setTranslateY(scene.getHeight());
                                    catchButton.setTranslateY(scene.getHeight());
                                    switchButton.setTranslateY(scene.getHeight());
                                    runButton.setTranslateY(scene.getHeight());

                                    controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

                                    fightButtonAnim.play();
                                    catchButtonAnim.play();
                                    switchButtonAnim.play();
                                    runButtonAnim.play();
                                }));
                                turnDelay.play();
                            }
                        });
                    });
                };
                catchButton.setOnAction(catchButtonHandler);

                // Wild battle-specific Run button behavior
                runButtonHandler = e -> {
                    fightButtonHide.play();
                    catchButtonHide.play();
                    switchButtonHide.play();
                    runButtonHide.play();

                    runButtonHide.setOnFinished(finishedEvent -> {
                        // Check if player's Pokémon is faster
                        boolean playerFaster = player.getCurrentPokemon().getSpeed() > wildPokemon.getSpeed();

                        // 75% chance to run if player's Pokémon is faster, 0% otherwise
                        boolean canRun = playerFaster && Math.random() < 0.75;

                        if (canRun) {
                            System.out.println("Got away safely!");

                            // Close the battle window
                            Stage stage = (Stage) controlsBox.getScene().getWindow();
                            Platform.runLater(() -> stage.close());
                        } else {
                            System.out.println("Can't escape!");

                            // Wild Pokémon's turn
                            wildPokemonTurn();

                            // Reset controls after wild Pokémon's turn
                            controlsBox.getChildren().clear();
                            fightButton.setTranslateY(scene.getHeight());
                            catchButton.setTranslateY(scene.getHeight());
                            switchButton.setTranslateY(scene.getHeight());
                            runButton.setTranslateY(scene.getHeight());

                            controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

                            fightButtonAnim.play();
                            catchButtonAnim.play();
                            switchButtonAnim.play();
                            runButtonAnim.play();
                        }
                    });
                };
                runButton.setOnAction(runButtonHandler);
            } else {
                // Trainer battle catch button
                catchButtonHandler = e -> {
                    System.out.println("You can't catch a trainer's Pokemon! You thief!");
                };
                catchButton.setOnAction(catchButtonHandler);

                // Trainer battle run button
                runButtonHandler = e -> {
                    System.out.println("Can't run from a trainer battle!");
                };
                runButton.setOnAction(runButtonHandler);
            }
        } catch (Exception e) {
            System.err.println("Critical error in Battle.start(): " + e.getMessage());
        }
    }

    /**
     * Animates the entire catching sequence.
     * 
     * @param catchSuccess  Whether the catch will ultimately be successful
     * @param targetPokemon The Pokemon being caught
     * @param targetView    The ImageView of the target Pokemon
     * @param onComplete    Callback to execute when animation completes
     */
    private void animateCatchAttempt(boolean catchSuccess, IPokemon targetPokemon, ImageView targetView,
            Runnable onComplete) {
        // Number of shakes based on success (3 for success, 0-2 for failure)
        int numShakes = catchSuccess ? 3 : new Random().nextInt(3);

        // Create pokeball image view
        Image pokeballImage = new Image(getClass().getResourceAsStream("/Pokeball.png"));
        ImageView pokeball = new ImageView(pokeballImage);
        pokeball.setFitWidth(30);
        pokeball.setFitHeight(30);
        pokeball.setPreserveRatio(true);

        // Starting position (off-screen left, at player level)
        pokeball.setTranslateX(-50);
        pokeball.setTranslateY(targetView.getLayoutY() + 60);

        // Add pokeball to scene
        root.getChildren().add(pokeball);

        // Calculate target position
        double targetX = targetView.getLayoutX() + targetView.getFitWidth() / 2;
        double targetY = targetView.getLayoutY() + targetView.getFitHeight() / 2;

        // 1. Throw animation (arc path)
        animatePokeballThrow(pokeball, targetX, targetY, () -> {
            // 2. Capture animation (Pokemon glows white and disappears)
            animatePokemonCapture(targetView, () -> {
                // 3. Drop animation (Pokeball falls to ground)
                animatePokeballDrop(pokeball, targetY + 40, () -> {
                    // 4. Shake animation
                    animatePokeballShake(pokeball, numShakes, () -> {
                        // 5. Finish animation (either release Pokemon or complete catch)
                        if (catchSuccess) {
                            // Success! Just leave the pokeball there
                            onComplete.run();
                        } else {
                            // Failure - Pokemon breaks out
                            animateBreakout(pokeball, targetView, onComplete);
                        }
                    });
                });
            });
        });
    }

    /**
     * Animates throwing the Pokeball in an arc toward the target
     */
    private void animatePokeballThrow(ImageView pokeball, double targetX, double targetY, Runnable onComplete) {
        // Duration of throw animation
        Duration throwDuration = Duration.millis(800);

        // Create path for arc trajectory
        Path path = new Path();
        path.getElements().add(new MoveTo(pokeball.getTranslateX(), pokeball.getTranslateY()));

        // Control point for arc (above the target)
        double controlX = (pokeball.getTranslateX() + targetX) / 2;
        double controlY = targetY - 100; // Control point above the path

        path.getElements().add(new QuadCurveTo(controlX, controlY, targetX, targetY));

        // Create path transition
        PathTransition pathTransition = new PathTransition(throwDuration, path, pokeball);
        pathTransition.setInterpolator(Interpolator.EASE_OUT);

        // Add rotation to the pokeball while it's flying
        RotateTransition rotateTransition = new RotateTransition(throwDuration, pokeball);
        rotateTransition.setByAngle(720); // Two full spins
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        // Play animations in parallel
        ParallelTransition throwAnimation = new ParallelTransition(pathTransition, rotateTransition);
        throwAnimation.setOnFinished(event -> onComplete.run());
        throwAnimation.play();
    }

    /**
     * Animates the Pokemon being captured (glow and disappear)
     */
    private void animatePokemonCapture(ImageView pokemonView, Runnable onComplete) {
        // Store the original opacity
        double originalOpacity = pokemonView.getOpacity();

        // Create a white glow effect
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        pokemonView.setEffect(colorAdjust);

        // Animate brightness to create glow effect
        Timeline glowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 0)),
                new KeyFrame(Duration.millis(500), new KeyValue(colorAdjust.brightnessProperty(), 1)));

        // Fade out the Pokemon
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pokemonView);
        fadeOut.setFromValue(originalOpacity);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.millis(400));

        // Play animations sequentially
        SequentialTransition captureAnimation = new SequentialTransition(glowAnimation, fadeOut);
        captureAnimation.setOnFinished(event -> onComplete.run());
        captureAnimation.play();
    }

    /**
     * Animates the Pokeball dropping to the ground
     */
    private void animatePokeballDrop(ImageView pokeball, double groundY, Runnable onComplete) {
        TranslateTransition dropAnimation = new TranslateTransition(Duration.millis(300), pokeball);
        dropAnimation.setToY(groundY);
        dropAnimation.setInterpolator(Interpolator.EASE_IN);
        dropAnimation.setOnFinished(event -> onComplete.run());
        dropAnimation.play();
    }

    /**
     * Animates the Pokeball shaking
     */
    private void animatePokeballShake(ImageView pokeball, int numShakes, Runnable onComplete) {
        SequentialTransition shakeSequence = new SequentialTransition();

        // Add a small delay before first shake
        PauseTransition initialPause = new PauseTransition(Duration.millis(300));
        shakeSequence.getChildren().add(initialPause);

        for (int i = 0; i < numShakes; i++) {
            // Tilt left
            RotateTransition tiltLeft = new RotateTransition(Duration.millis(150), pokeball);
            tiltLeft.setFromAngle(0);
            tiltLeft.setToAngle(-20);
            tiltLeft.setInterpolator(Interpolator.EASE_OUT);

            // Tilt right
            RotateTransition tiltRight = new RotateTransition(Duration.millis(300), pokeball);
            tiltRight.setFromAngle(-20);
            tiltRight.setToAngle(20);
            tiltRight.setInterpolator(Interpolator.EASE_BOTH);

            // Tilt center
            RotateTransition tiltCenter = new RotateTransition(Duration.millis(150), pokeball);
            tiltCenter.setFromAngle(20);
            tiltCenter.setToAngle(0);
            tiltCenter.setInterpolator(Interpolator.EASE_IN;

            // Add pause between shakes
            PauseTransition pauseBetweenShakes = new PauseTransition(Duration.millis(300));

            // Add sound effect for each shake (if available)
            shakeSequence.getChildren().addAll(tiltLeft, tiltRight, tiltCenter, pauseBetweenShakes);
        }

        // Set completion callback
        shakeSequence.setOnFinished(event -> onComplete.run());
        shakeSequence.play();
    }

    /**
     * Animates the Pokemon breaking out of the Pokeball
     */
    private void animateBreakout(ImageView pokeball, ImageView pokemonView, Runnable onComplete) {
        // Remove the pokeball with a small "explosion" effect
        FadeTransition pokeballFade = new FadeTransition(Duration.millis(200), pokeball);
        pokeballFade.setFromValue(1.0);
        pokeballFade.setToValue(0.0);

        ScaleTransition pokeballBurst = new ScaleTransition(Duration.millis(200), pokeball);
        pokeballBurst.setFromX(1.0);
        pokeballBurst.setFromY(1.0);
        pokeballBurst.setToX(1.5);
        pokeballBurst.setToY(1.5);

        // Bring the Pokemon back
        FadeTransition pokemonReturn = new FadeTransition(Duration.millis(300), pokemonView);
        pokemonReturn.setFromValue(0.0);
        pokemonReturn.setToValue(1.0);
        pokemonReturn.setDelay(Duration.millis(100));

        // Reset the Pokemon's effect
        pokemonReturn.setOnFinished(event -> {
            pokemonView.setEffect(null);

            // Remove the pokeball from the scene
            Timeline cleanupTimeline = new Timeline(new KeyFrame(
                    Duration.ZERO,
                    removeEvent -> root.getChildren().remove(pokeball)));
            cleanupTimeline.play();

            onComplete.run();
        });

        // Play animations in sequence
        ParallelTransition burstAnimation = new ParallelTransition(pokeballFade, pokeballBurst);
        SequentialTransition breakoutSequence = new SequentialTransition(burstAnimation, pokemonReturn);
        breakoutSequence.play();
    }

    // Add a new method for wild Pokemon attacks
    private void wildPokemonTurn() {
        // Check for status conditions that prevent attacking
        PokemonLogic.Pokemon.StatusCondition status = wildPokemon.getStatusCondition();

        if (status != PokemonLogic.Pokemon.StatusCondition.none) {
            // Sleep and freeze prevent attacking
            if (status == PokemonLogic.Pokemon.StatusCondition.SLP
                    || status == PokemonLogic.Pokemon.StatusCondition.FRZ) {
                String statusName = (status == PokemonLogic.Pokemon.StatusCondition.SLP) ? "asleep" : "frozen";
                WindowThings.mainWindow
                        .appendToOutput("Wild " + wildPokemon.getName() + " is " + statusName + " and can't move!");

                // Check for recovery (20% chance each turn)
                if (Math.random() < 0.2) {
                    wildPokemon.setStatusCondition(PokemonLogic.Pokemon.StatusCondition.none);
                    WindowThings.mainWindow.appendToOutput("Wild " + wildPokemon.getName() +
                            (status == PokemonLogic.Pokemon.StatusCondition.SLP ? " woke up!" : " thawed out!"));
                    updateStatusLabels();
                }

                // Process end-of-turn status effects then end turn
                processStatusEffects(player.getCurrentPokemon());
                processStatusEffects(wildPokemon);
                return;
            }

            // Paralysis has 25% chance to prevent action
            if (status == PokemonLogic.Pokemon.StatusCondition.PAR && Math.random() < 0.25) {
                WindowThings.mainWindow
                        .appendToOutput("Wild " + wildPokemon.getName() + " is fully paralyzed and can't move!");

                // Process end-of-turn status effects then end turn
                processStatusEffects(player.getCurrentPokemon());
                processStatusEffects(wildPokemon);
                return;
            }
        }

        // Choose a random move from the wild Pokemon's moveset
        List<Move> availableMoves = wildPokemon.getMovesList();
        if (availableMoves.isEmpty())
            return;

        Move randomMove = availableMoves.get(new Random().nextInt(availableMoves.size()));
        System.out.println("Wild " + wildPokemon.getName() + " used " + randomMove.getName() + "!");

        // Check move accuracy
        Random random = new Random();
        int accuracyCheck = random.nextInt(100) + 1; // 1-100

        if (accuracyCheck > randomMove.getAccuracy()) {
            // Move missed!
            WindowThings.mainWindow.appendToOutput("Wild " + wildPokemon.getName() + "'s attack missed!");
            return; // Skip damage calculation
        }

        // Calculate and apply damage to player's Pokemon
        int damage = calculateDamage(randomMove, wildPokemon, player.getCurrentPokemon());

        // Add this line to display the effectiveness message
        double typeMultiplier = calculateTypeEffectivenessMultiplier(randomMove, player.getCurrentPokemon());
        displayTypeEffectivenessMessage(typeMultiplier);

        player.getCurrentPokemon().setRemainingHealth(player.getCurrentPokemon().getRemainingHealth() - damage);

        // Update player Pokemon health display
        updatePokemonUI(player.getCurrentPokemon(), playerHealthBarForeground, playerHealthLabel);

        // Apply status effect from move
        tryApplyStatusEffect(randomMove, player.getCurrentPokemon());

        // Process end-of-turn status effects
        processStatusEffects(wildPokemon);
        processStatusEffects(player.getCurrentPokemon());

        // Check if player's Pokemon fainted
        if (player.getCurrentPokemon().getRemainingHealth() <= 0) {
            // Play fainting animation
            animatePokemonFainting(playerPokemonView, player.getCurrentPokemon().getNickname(), () -> {
                // Check if player has more usable Pokemon
                if (!player.hasUsablePokemon()) {
                    System.out.println("You have no more usable Pokemon!");
                    System.out.println("You blacked out!");

                    // Close battle window
                    Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                        Stage stage = (Stage) playerHealthBarForeground.getScene().getWindow();
                        Platform.runLater(() -> stage.close());
                    }));
                    exitDelay.play();
                } else {
                    // Force player to switch Pokemon
                    System.out.println("Choose your next Pokemon!");
                    // Automatically trigger the switch menu
                    Platform.runLater(() -> {
                        animateBattleButtonsSlideOut();
                        prepareForPokemonSwitch();
                    });
                }
            });
        }
    }

    /**
     * Updates all battle UI elements to reflect current game state.
     * This consolidates all UI updates in one place for better maintainability.
     */
    private void updateBattleUI() {
        // Update player Pokemon UI
        updatePokemonUI(
                player.getCurrentPokemon(),
                playerHealthBarForeground,
                playerHealthLabel);

        // Update opponent Pokemon UI
        if (isWildBattle) {
            updatePokemonUI(
                    wildPokemon,
                    opponentHealthBarForeground,
                    opponentHealthLabel);
        } else if (opponent != null && opponent.getCurrentPokemon() != null) {
            updatePokemonUI(
                    opponent.getCurrentPokemon(),
                    opponentHealthBarForeground,
                    opponentHealthLabel);
        }

        // Update status labels
        updateStatusLabels();
    }

    /**
     * Helper method to update UI elements for a specific Pokemon.
     * This reduces code duplication when updating player/opponent UI.
     * 
     * @param pokemon     The Pokemon whose UI needs updating
     * @param healthBar   The health bar rectangle to update
     * @param healthLabel The health label to update
     */
    private void updatePokemonUI(IPokemon pokemon, Rectangle healthBar, Label healthLabel) {
        double healthPercent = (double) pokemon.getRemainingHealth() / pokemon.getHp() * 100;
        healthBar.setWidth(healthPercent);
        healthLabel.setText(pokemon.getRemainingHealth() + "/" + pokemon.getHp());
        // Note: We don't set the health bar color here anymore
        // That's now handled by updateStatusLabels() which considers both health and
        // status
    }

    /**
     * Animates a Pokemon fainting with a fading effect
     * 
     * @param pokemonView The ImageView of the fainting Pokemon
     * @param pokemonName The name of the Pokemon for display purpose
     * @param onComplete  Callback to execute when animation completes
     */
    private void animatePokemonFainting(ImageView pokemonView, String pokemonName, Runnable onComplete) {
        System.out.println(pokemonName + " fainted!");

        // Apply a subtle shake effect first
        TranslateTransition shakeLeft = new TranslateTransition(Duration.millis(100), pokemonView);
        shakeLeft.setByX(-5);
        TranslateTransition shakeRight = new TranslateTransition(Duration.millis(100), pokemonView);
        shakeRight.setByX(10);
        TranslateTransition shakeCenter = new TranslateTransition(Duration.millis(100), pokemonView);
        shakeCenter.setByX(-5);

        // Create sink animation - Pokemon falls downward slightly
        TranslateTransition fallAnimation = new TranslateTransition(Duration.millis(800), pokemonView);
        fallAnimation.setByY(30); // Fall down by 30 pixels
        fallAnimation.setInterpolator(Interpolator.EASE_IN);

        // Create fade animation - Pokemon fades out
        FadeTransition fadeAnimation = new FadeTransition(Duration.millis(800), pokemonView);
        fadeAnimation.setFromValue(1.0);
        fadeAnimation.setToValue(0.0);
        fadeAnimation.setInterpolator(Interpolator.EASE_IN);

        // Create the complete animation sequence
        SequentialTransition shakeSequence = new SequentialTransition(
                shakeLeft, shakeRight, shakeCenter);

        // After shaking, start falling and fading
        ParallelTransition fallAndFade = new ParallelTransition(
                fallAnimation, fadeAnimation);

        // Combine all animations
        SequentialTransition faintingAnimation = new SequentialTransition(
                shakeSequence, fallAndFade);

        // After animation completes
        faintingAnimation.setOnFinished(event -> {
            // Don't reset yet if we're waiting for user to send out a new Pokemon
            if (onComplete != null) {
                onComplete.run();
            }
        });

        faintingAnimation.play();
    }

    /**
     * Animates battle buttons sliding out of the screen.
     */
    private void animateBattleButtonsSlideOut() {
        // Create a list to store all animations
        List<TranslateTransition> animations = new ArrayList<>();

        // Animate each battle button to slide out of the screen - similar to the back
        // button animation
        for (Node button : controlsBox.getChildren()) {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), button);
            slideOut.setFromY(0); // Current position
            slideOut.setToY(scene.getHeight()); // Move down and out of view
            slideOut.setInterpolator(Interpolator.EASE_IN);
            animations.add(slideOut);
            slideOut.play();
        }

        // Set up callback to prepare for Pokemon switch after all animations complete
        if (!animations.isEmpty()) {
            animations.get(animations.size() - 1).setOnFinished(e -> {
                // Clear the controlsBox after animations are done
                controlsBox.getChildren().clear();
                // Prepare the Pokemon switch interface
                prepareForPokemonSwitch();
            });
        }
    }

    /**
     * Prepares the interface for switching Pokemon.
     */
    private void prepareForPokemonSwitch() {
        // Create a container for the Pokemon switch buttons
        HBox switchBox = new HBox(5);
        switchBox.setAlignment(Pos.CENTER);
        switchBox.setPadding(new Insets(5));
        switchBox.setTranslateY(scene.getHeight()); // Start below the visible area

        // Define button styles
        String pokemonButtonStyle = "-fx-border-color: black; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 2; " +
                "-fx-background-radius: 2; " +
                "-fx-cursor: hand; " +
                "-fx-background-color: linear-gradient(to bottom, #B30000 50%, #EBEBEB 50%); " +
                "-fx-min-width: 65px; " +
                "-fx-min-height: 65px; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;";

        // Style for fainted Pokémon
        String faintedButtonStyle = pokemonButtonStyle +
                "-fx-opacity: 0.6; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);";

        // Create buttons for each Pokemon in the player's party
        for (int i = 0; i < player.getParty().size(); i++) {
            Pokemon pokemon = player.getParty().get(i);

            // Skip the already fainted Pokemon (the one that triggered this menu)
            if (pokemon == player.getCurrentPokemon()) {
                continue;
            }

            Button pokemonButton = new Button();

            // Load and set the Pokemon sprite
            Image pokemonSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
            ImageView spriteView = new ImageView(pokemonSprite);
            spriteView.setFitWidth(55);
            spriteView.setFitHeight(55);
            spriteView.setPreserveRatio(true);
            pokemonButton.setGraphic(spriteView);

            // Add tooltip with Pokemon details
            String tooltipText = pokemon.getNickname() + " (Lv." + pokemon.getLevel() + ")";
            if (pokemon.getRemainingHealth() <= 0) {
                tooltipText += " - Fainted";
            } else {
                tooltipText += " - HP: " + pokemon.getRemainingHealth() + "/" + pokemon.getHp();
            }
            pokemonButton.setTooltip(new Tooltip(tooltipText));

            // Apply appropriate style based on Pokemon's state
            if (pokemon.getRemainingHealth() <= 0) {
                pokemonButton.setStyle(faintedButtonStyle);
                // Add grayscale effect for fainted Pokemon
                ColorAdjust grayscale = new ColorAdjust();
                grayscale.setSaturation(-1.0); // Full desaturation
                spriteView.setEffect(grayscale);
            } else {
                pokemonButton.setStyle(pokemonButtonStyle);
            }

            // Set the action for each button
            pokemonButton.setOnAction(event -> {
                // Skip if the Pokemon is fainted
                if (pokemon.getRemainingHealth() <= 0) {
                    System.out.println(pokemon.getNickname() + " has fainted and cannot battle!");

                    // Add visual feedback for trying to select a fainted Pokemon
                    ColorAdjust errorEffect = new ColorAdjust();
                    errorEffect.setSaturation(-0.5);
                    errorEffect.setBrightness(-0.2);
                    pokemonButton.setEffect(errorEffect);

                    // Shake animation to indicate error
                    TranslateTransition shakeLeft = new TranslateTransition(Duration.millis(80), pokemonButton);
                    shakeLeft.setByX(-5);
                    TranslateTransition shakeRight = new TranslateTransition(Duration.millis(80), pokemonButton);
                    shakeRight.setByX(5);
                    TranslateTransition shakeCenter = new TranslateTransition(Duration.millis(80), pokemonButton);
                    shakeCenter.setByX(0);

                    Timeline resetEffect = new Timeline(
                            new KeyFrame(Duration.millis(400), evt -> {
                                if (pokemon.getRemainingHealth() <= 0) {
                                    // Reset to grayscale for fainted Pokemon
                                    ColorAdjust grayscale = new ColorAdjust();
                                    grayscale.setSaturation(-1.0);
                                    spriteView.setEffect(grayscale);
                                } else {
                                    pokemonButton.setEffect(null);
                                }
                            }));

                    shakeLeft.setOnFinished(evt -> shakeRight.play());
                    shakeRight.setOnFinished(evt -> shakeCenter.play());
                    shakeCenter.setOnFinished(evt -> resetEffect.play());
                    shakeLeft.play();
                    return;
                }

                // Switch to the selected Pokemon
                Pokemon previousPokemon = player.getCurrentPokemon();
                player.setCurrentPokemon(pokemon);

                // Remove the switch menu with animation
                TranslateTransition switchBoxOut = new TranslateTransition(Duration.millis(500), switchBox);
                switchBoxOut.setToY(scene.getHeight());
                switchBoxOut.setInterpolator(Interpolator.EASE_IN);

                // Create color adjust for white flash effect
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(2.0); // Start with white brightness

                // Create animations for the new Pokemon
                ParallelTransition menuOut = new ParallelTransition(switchBoxOut);
                menuOut.setOnFinished(evt -> {
                    // Remove menu elements
                    controlsBox.getChildren().clear();

                    // Load new Pokemon sprite
                    Image newSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
                    playerPokemonView.setImage(newSprite);
                    playerPokemonView.setEffect(colorAdjust);
                    playerPokemonView.setOpacity(1.0);
                    playerPokemonView.setTranslateX(-scene.getWidth() / 2); // Start off-screen left
                    playerPokemonView.setTranslateY(0); // Reset Y position

                    // Update UI information for the new Pokemon
                    playerPokemonNickname.setText(pokemon.getNickname());
                    playerPokemonLevel.setText("Lv. " + pokemon.getLevel());
                    updatePokemonUI(pokemon, playerHealthBarForeground, playerHealthLabel);
                    updateStatusLabels(); // Add this line

                    // Create slide-in animation
                    TranslateTransition slideInAnim = new TranslateTransition(Duration.millis(800), playerPokemonView);
                    slideInAnim.setFromX(-scene.getWidth() / 2); // Start from left side
                    slideInAnim.setToX(0); // Move to original position
                    slideInAnim.setInterpolator(Interpolator.EASE_OUT);

                    // Create brightness fade animation
                    Timeline brightnessAnim = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 2.0)),
                            new KeyFrame(Duration.millis(800), new KeyValue(colorAdjust.brightnessProperty(), 0.0)));

                    // Combine both animations
                    ParallelTransition newPokemonEntrance = new ParallelTransition(slideInAnim, brightnessAnim);

                    // When animation completes
                    newPokemonEntrance.setOnFinished(entranceEvt -> {
                        playerPokemonView.setEffect(null);
                        System.out.println("Go! " + pokemon.getNickname() + "!");

                        // Reset and show the battle buttons
                        Button fightButton = new Button("Fight");
                        Button catchButton = new Button("Catch");
                        Button switchButton = new Button("Switch");
                        Button runButton = new Button("Run");

                        // Apply the same styling as in the start method
                        Font largeFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 18);
                        String baseButtonStyle = "-fx-background-color: white; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10 15 10 15; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-width: 2px; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);";

                        String hoverButtonStyle = "-fx-background-color: #f0f0f0; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10 15 10 15; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-width: 2px; " +
                                "-fx-scale-x: 1.03; " +
                                "-fx-scale-y: 1.03; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);";

                        String pressedButtonStyle = "-fx-background-color: #e0e0e0; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10 15 10 15; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-width: 2px; " +
                                "-fx-scale-x: 0.98; " +
                                "-fx-scale-y: 0.98; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);";

                        Button[] battleButtons = { fightButton, catchButton, switchButton, runButton };
                        for (Button button : battleButtons) {
                            button.setFont(largeFont);
                            button.setStyle(baseButtonStyle);
                            // Add hover/pressed styles
                            button.setOnMouseEntered(e -> button.setStyle(hoverButtonStyle));
                            button.setOnMouseExited(e -> button.setStyle(baseButtonStyle));
                            button.setOnMousePressed(e -> button.setStyle(pressedButtonStyle));
                            button.setOnMouseReleased(e -> button.setStyle(hoverButtonStyle));
                        }

                        // Create slide-out animations for the new buttons
                        TranslateTransition fightButtonHide = new TranslateTransition(Duration.millis(500),
                                fightButton);
                        fightButtonHide.setFromY(0);
                        fightButtonHide.setToY(100);
                        fightButtonHide.setInterpolator(Interpolator.EASE_IN);

                        TranslateTransition catchButtonHide = new TranslateTransition(Duration.millis(550),
                                catchButton);
                        catchButtonHide.setFromY(0);
                        catchButtonHide.setToY(100);
                        catchButtonHide.setInterpolator(Interpolator.EASE_IN);

                        TranslateTransition switchButtonHide = new TranslateTransition(Duration.millis(600),
                                switchButton);
                        switchButtonHide.setFromY(0);
                        switchButtonHide.setToY(100);
                        switchButtonHide.setInterpolator(Interpolator.EASE_IN);

                        TranslateTransition runButtonHide = new TranslateTransition(Duration.millis(700), runButton);
                        runButtonHide.setFromY(0);
                        runButtonHide.setToY(100);
                        runButtonHide.setInterpolator(Interpolator.EASE_IN);

                        // Set button positions
                        for (Button button : battleButtons) {
                            button.setTranslateY(scene.getHeight());
                        }

                        // Create modified event handlers that incorporate the slide-out animations
                        fightButton.setOnAction(e -> {
                            fightButtonHide.play();
                            catchButtonHide.play();
                            switchButtonHide.play();
                            runButtonHide.play();

                            runButtonHide.setOnFinished(finishedEvent -> {
                                // Execute the original fight button handler logic
                                fightButtonHandler.handle(e);
                            });
                        });

                        catchButton.setOnAction(e -> {
                            fightButtonHide.play();
                            catchButtonHide.play();
                            switchButtonHide.play();
                            runButtonHide.play();

                            runButtonHide.setOnFinished(finishedEvent -> {
                                // Execute the original catch button handler logic
                                catchButtonHandler.handle(e);
                            });
                        });

                        switchButton.setOnAction(e -> {
                            fightButtonHide.play();
                            catchButtonHide.play();
                            switchButtonHide.play();
                            runButtonHide.play();

                            runButtonHide.setOnFinished(finishedEvent -> {
                                // Execute the original switch button handler logic
                                switchButtonHandler.handle(e);
                            });
                        });

                        runButton.setOnAction(e -> {
                            fightButtonHide.play();
                            catchButtonHide.play();
                            switchButtonHide.play();
                            runButtonHide.play();

                            runButtonHide.setOnFinished(finishedEvent -> {
                                // Execute the original run button handler logic
                                runButtonHandler.handle(e);
                            });
                        });

                        // Add buttons to controlsBox
                        controlsBox.getChildren().addAll(fightButton, catchButton, switchButton, runButton);

                        // Make buttons grow to fill space
                        HBox.setHgrow(fightButton, Priority.ALWAYS);
                        HBox.setHgrow(catchButton, Priority.ALWAYS);
                        HBox.setHgrow(switchButton, Priority.ALWAYS);
                        HBox.setHgrow(runButton, Priority.ALWAYS);

                        // Animate buttons in
                        TranslateTransition fightAnim = new TranslateTransition(Duration.millis(500), fightButton);
                        fightAnim.setFromY(scene.getHeight());
                        fightAnim.setToY(0);
                        fightAnim.setInterpolator(Interpolator.EASE_OUT);

                        TranslateTransition catchAnim = new TranslateTransition(Duration.millis(525), catchButton);
                        catchAnim.setFromY(scene.getHeight());
                        catchAnim.setToY(0);
                        catchAnim.setInterpolator(Interpolator.EASE_OUT);

                        TranslateTransition switchAnim = new TranslateTransition(Duration.millis(550), switchButton);
                        switchAnim.setFromY(scene.getHeight());
                        switchAnim.setToY(0);
                        switchAnim.setInterpolator(Interpolator.EASE_OUT);

                        TranslateTransition runAnim = new TranslateTransition(Duration.millis(600), runButton);
                        runAnim.setFromY(scene.getHeight());
                        runAnim.setToY(0);
                        runAnim.setInterpolator(Interpolator.EASE_OUT);

                        // Play animations
                        fightAnim.play();
                        catchAnim.play();
                        switchAnim.play();
                        runAnim.play();
                    });

                    newPokemonEntrance.play();
                });

                menuOut.play();
            });

            switchBox.getChildren().add(pokemonButton);
        }

        // If there are no non-fainted Pokemon to switch to
        if (switchBox.getChildren().isEmpty()) {
            System.out.println("No usable Pokemon left!");
            System.out.println("You blacked out!");

            // Close battle window
            Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                Stage stage = (Stage) playerHealthBarForeground.getScene().getWindow();
                Platform.runLater(() -> stage.close());
            }));
            exitDelay.play();
            return;
        }

        // Add the switch box to the controls
        controlsBox.getChildren().clear();
        controlsBox.getChildren().add(switchBox);

        // Animate the switch box sliding up
        TranslateTransition switchBoxAnim = new TranslateTransition(Duration.millis(800), switchBox);
        switchBoxAnim.setToY(-20); // Slide up to position
        switchBoxAnim.setInterpolator(Interpolator.EASE_OUT);
        switchBoxAnim.play();
    }

    /**
     * Try to apply status effects from moves based on their effects and chances
     */
    private void tryApplyStatusEffect(Move move, IPokemon targetPokemon) {
        String effect = move.getEffect();
        if (effect == null || effect.isEmpty()) {
            return;
        }

        // Parse status effect and chance from the effect string
        if (effect.startsWith("burnChance(")) {
            int chance = parseChance(effect, "burnChance");
            if (Math.random() * 100 < chance) {
                applyStatus(targetPokemon, PokemonLogic.Pokemon.StatusCondition.BRN);
                WindowThings.mainWindow.appendToOutput(targetPokemon.getNickname() + " was burned!");
            }
        } else if (effect.startsWith("poisonChance(")) {
            int chance = parseChance(effect, "poisonChance");
            if (Math.random() * 100 < chance) {
                applyStatus(targetPokemon, PokemonLogic.Pokemon.StatusCondition.PSN);
                WindowThings.mainWindow.appendToOutput(targetPokemon.getNickname() + " was poisoned!");
            }
        } else if (effect.startsWith("paralyzeChance(")) {
            int chance = parseChance(effect, "paralyzeChance");
            if (Math.random() * 100 < chance) {
                applyStatus(targetPokemon, PokemonLogic.Pokemon.StatusCondition.PAR);
                WindowThings.mainWindow.appendToOutput(targetPokemon.getNickname() + " was paralyzed!");
            }
        } else if (effect.startsWith("sleepChance(")) {
            int chance = parseChance(effect, "sleepChance");
            if (Math.random() * 100 < chance) {
                applyStatus(targetPokemon, PokemonLogic.Pokemon.StatusCondition.SLP);
                WindowThings.mainWindow.appendToOutput(targetPokemon.getNickname() + " fell asleep!");
            }
        }

        // Update status labels
        updateStatusLabels();
    }

    /**
     * Helper method to parse the chance percentage from effect strings like
     * "burnChance(10)"
     */
    private int parseChance(String effect, String effectName) {
        try {
            int startIndex = effectName.length() + 1; // +1 for the opening parenthesis
            int endIndex = effect.indexOf(')');
            if (endIndex > startIndex) {
                String chanceStr = effect.substring(startIndex, endIndex);
                return Integer.parseInt(chanceStr);
            }
        } catch (NumberFormatException e) {
            logger.error("Error parsing chance from effect string: {}", effect, e);
        }
        return 0;
    }

    /**
     * Apply status condition to Pokemon, handling both Pokemon and trainerPokemon
     * types
     */
    private void applyStatus(IPokemon pokemon, PokemonLogic.Pokemon.StatusCondition status) {
        // Skip if Pokemon already has a status condition
        PokemonLogic.Pokemon.StatusCondition currentStatus = null;

        if (pokemon instanceof Pokemon) {
            currentStatus = ((Pokemon) pokemon).getStatusCondition();
        } else if (pokemon instanceof trainerPokemon) {
            currentStatus = ((trainerPokemon) pokemon).getStatusCondition();
        }

        if (currentStatus != null && currentStatus != PokemonLogic.Pokemon.StatusCondition.none) {
            WindowThings.mainWindow.appendToOutput("It doesn't affect " + pokemon.getNickname() + "!");
            return;
        }

        // Apply the status
        if (pokemon instanceof Pokemon) {
            ((Pokemon) pokemon).setStatusCondition(status);
        } else if (pokemon instanceof trainerPokemon) {
            ((trainerPokemon) pokemon).setStatusCondition(status);
        }
    }

    /**
     * Process end-of-turn status effects (damage, etc.)
     */
    private void processStatusEffects(IPokemon pokemon) {
        // Skip if fainted
        if (pokemon.getRemainingHealth() <= 0)
            return;

        // Get status condition
        PokemonLogic.Pokemon.StatusCondition status = null;
        if (pokemon instanceof Pokemon) {
            status = ((Pokemon) pokemon).getStatusCondition();
        } else if (pokemon instanceof trainerPokemon) {
            status = ((trainerPokemon) pokemon).getStatusCondition();
        }

        if (status == null || status == PokemonLogic.Pokemon.StatusCondition.none) {
            return;
        }

        // Apply status effects
        switch (status) {
            case PSN -> {
                // Poison damage (1/8 of max HP)
                int poisonDamage = Math.max(1, pokemon.getHp() / 8);
                pokemon.setRemainingHealth(pokemon.getRemainingHealth() - poisonDamage);
                WindowThings.mainWindow.appendToOutput(pokemon.getNickname() + " was hurt by poison!");
            }

            case BRN -> {
                // Burn damage (1/16 of max HP)
                int burnDamage = Math.max(1, pokemon.getHp() / 16);
                pokemon.setRemainingHealth(pokemon.getRemainingHealth() - burnDamage);
                WindowThings.mainWindow.appendToOutput(pokemon.getNickname() + " was hurt by its burn!");
            }
        }

        // Update UI after damage
        if (pokemon == player.getCurrentPokemon()) {
            updatePokemonUI(pokemon, playerHealthBarForeground, playerHealthLabel);
        } else if (isWildBattle && pokemon == wildPokemon || !isWildBattle && pokemon == opponent.getCurrentPokemon()) {
            updatePokemonUI(pokemon, opponentHealthBarForeground, opponentHealthLabel);
        }

        // Check if Pokemon fainted due to status effect
        if (pokemon.getRemainingHealth() <= 0) {
            if (pokemon == player.getCurrentPokemon()) {
                animatePokemonFainting(playerPokemonView, pokemon.getNickname(), () -> {
                    if (!player.hasUsablePokemon()) {
                        System.out.println("You have no more usable Pokemon!");
                        System.out.println("You blacked out!");

                        // Close battle window
                        Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                            Stage stage = (Stage) playerHealthBarForeground.getScene().getWindow();
                            Platform.runLater(() -> stage.close());
                        }));
                        exitDelay.play();
                    } else {
                        // Force player to switch Pokemon
                        System.out.println("Choose your next Pokemon!");
                        Platform.runLater(() -> {
                            animateBattleButtonsSlideOut();
                            prepareForPokemonSwitch();
                        });
                    }
                });
            } else if (isWildBattle && pokemon == wildPokemon) {
                animatePokemonFainting(opponentPokemonView, "Wild " + pokemon.getName(), () -> {
                    try {
                        player.getCurrentPokemon().gainExperience(pokemon);
                    } catch (IOException e) {
                        logger.error("Error awarding experience: {}", e.getMessage());
                    }

                    Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                        Stage stage = (Stage) opponentHealthBarForeground.getScene().getWindow();
                        Platform.runLater(() -> stage.close());
                    }));
                    exitDelay.play();
                });
            } else if (!isWildBattle && pokemon == opponent.getCurrentPokemon()) {
                animatePokemonFainting(opponentPokemonView, pokemon.getNickname(), () -> {
                    try {
                        player.getCurrentPokemon().gainExperience(pokemon);
                    } catch (IOException e) {
                        logger.error("Error awarding experience: {}", e.getMessage());
                    }

                    if (!opponent.hasUsablePokemon()) {
                        System.out.println("You won the battle!");
                        int prizeMoney = opponent.getRewardMoney();
                        player.addMoney(prizeMoney);
                        System.out.println("You got $" + prizeMoney + " for winning!");

                        Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1000), exitEvent -> {
                            Stage stage = (Stage) opponentHealthBarForeground.getScene().getWindow();
                            Platform.runLater(() -> stage.close());
                        }));
                        exitDelay.play();
                    } else {
                        opponent.switchToNextPokemon();
                        // ...existing code for switching opponent Pokemon
                    }
                });
            }
        }
    }

    /**
     * Updates status labels in UI
     */
    private void updateStatusLabels() {
        // Update player health bar color based on status and health percentage
        PokemonLogic.Pokemon.StatusCondition playerStatus = player.getCurrentPokemon().getStatusCondition();
        double playerHealthPercent = (double) player.getCurrentPokemon().getRemainingHealth()
                / player.getCurrentPokemon().getHp() * 100;

        // Set color based on status condition
        playerHealthBarForeground.setFill(getHealthBarColor(playerStatus, playerHealthPercent));

        // Reset outline to default - we don't need special outline now
        playerHealthBarBackground.setStroke(Color.BLACK);
        playerHealthBarBackground.setStrokeWidth(2);

        // Update opponent health bar color
        PokemonLogic.Pokemon.StatusCondition opponentStatus;
        double opponentHealthPercent;

        if (isWildBattle) {
            opponentStatus = wildPokemon.getStatusCondition();
            opponentHealthPercent = (double) wildPokemon.getRemainingHealth() / wildPokemon.getHp() * 100;
        } else {
            opponentStatus = ((trainerPokemon) opponent.getCurrentPokemon()).getStatusCondition();
            opponentHealthPercent = (double) opponent.getCurrentPokemon().getRemainingHealth() /
                    opponent.getCurrentPokemon().getHp() * 100;
        }

        // Set opponent health bar color based on status and health
        opponentHealthBarForeground.setFill(getHealthBarColor(opponentStatus, opponentHealthPercent));

        // Reset outline to default
        opponentHealthBarBackground.setStroke(Color.BLACK);
        opponentHealthBarBackground.setStrokeWidth(2);
    }

    /**
     * Get appropriate health bar color based on status condition and health
     * percentage
     */
    @SuppressWarnings("incomplete-switch")
    private Color getHealthBarColor(PokemonLogic.Pokemon.StatusCondition status, double healthPercent) {
        // First check for status conditions
        if (status != null && status != PokemonLogic.Pokemon.StatusCondition.none) {
            switch (status) {
                case BRN -> {
                    return Color.ORANGERED;
                }
                case PAR -> {
                    return Color.GOLD;
                }
                case PSN -> {
                    return Color.PURPLE;
                }
                case SLP -> {
                    return Color.DARKGRAY;
                }
                case FRZ -> {
                    return Color.DEEPSKYBLUE;
                }
            }
        }

        // No status effect, use standard health-based colors
        if (healthPercent > 50) {
            return Color.LIGHTGREEN;
        } else if (healthPercent > 25) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    /**
     * Convert status condition to display text
     */
    private String getStatusDisplayText(PokemonLogic.Pokemon.StatusCondition status) {
        return switch (status) {
            case BRN -> "BRN";
            case PAR -> "PAR";
            case PSN -> "PSN";
            case SLP -> "SLP";
            case FRZ -> "FRZ";
            default -> "";
        };
    }

    /**
     * Get hex color string for status effect background
     */
    private String getStatusColorHex(PokemonLogic.Pokemon.StatusCondition status) {
        return switch (status) {
            case BRN -> "#FF4500"; // OrangeRed
            case PAR -> "#FFD700"; // Gold
            case PSN -> "#800080"; // Purple
            case SLP -> "#696969"; // DarkGray
            case FRZ -> "#00BFFF"; // DeepSkyBlue
            default -> "transparent";
        };
    }

    /**
     * Get color for status effect
     */
    private Color getStatusColor(PokemonLogic.Pokemon.StatusCondition status) {
        return switch (status) {
            case BRN -> Color.ORANGERED;
            case PAR -> Color.GOLD;
            case PSN -> Color.PURPLE;
            case SLP -> Color.DARKGRAY;
            case FRZ -> Color.DEEPSKYBLUE;
            default -> Color.BLACK;
        };
    }
}