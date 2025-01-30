package BattleLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings({ "FieldMayBeFinal", "OverridableMethodCallInConstructor" })

public class Battle extends Application {

    private Player player;
    private Trainer opponent;
    private Rectangle playerHealthBarForeground;
    private Rectangle opponentHealthBarForeground;
    private Label playerHealthLabel;
    private Label opponentHealthLabel;

    public Battle(Player player, Trainer opponent) {
        this.player = player;
        this.opponent = opponent;
        player.setCurrentPokemon(player.getParty().get(0));
        opponent.setCurrentPokemon(opponent.getPokemonList().get(0));

        Stage battleStage = new Stage();
        try {
            start(battleStage);
        } catch (Exception e) {
        }
    }

    private void aiTurn() {
        trainerPokemon aiPokemon = opponent.getCurrentPokemon();
        Pokemon playerPokemon = player.getCurrentPokemon();

        // Get AI pokemon's moves
        List<Move> availableMoves = aiPokemon.getMoves();
        Move bestMove = null;
        int maxDamage = 0;

        // Find move that deals most damage
        for (Move move : availableMoves) {
            int potentialDamage = calculateDamage(move, aiPokemon, playerPokemon);

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
            System.out.println(aiPokemon.getName() + " used " + bestMove.getName() + "!");
            int damage = calculateDamage(bestMove, aiPokemon, playerPokemon);
            playerPokemon.setRemainingHealth(playerPokemon.getRemainingHealth() - damage);

            // Check if player Pokemon fainted
            if (playerPokemon.getRemainingHealth() <= 0) {
                System.out.println(playerPokemon.getNickname() + " fainted!");
                if (!player.hasUsablePokemon()) {
                    System.out.println("You have no more usable Pokemon!");
                    System.out.println("You blacked out!");
                    // Handle battle end here
                } else {
                    // switchButton.setOnAction
                }
            }
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
        // Step 1: Calculate base damage
        int levelFactor = (2 * attacker.getLevel()) / 5 + 2;
        double attackDefenseRatio = (double) attacker.getAttack() / defender.getDefense();
        double baseDamage = (levelFactor * move.getPower() * attackDefenseRatio) / 50;

        // Step 2: Add 2 to the base damage
        baseDamage += 2;

        // Step 3: Apply type multiplier
        double typeMultiplier = calculateTypeEffectivenessMultiplier(move, defender);

        // Step 4: Apply random variance (0.85 to 1.00)
        Random random = new Random();
        double randomFactor = 0.85 + (random.nextDouble() * 0.15);

        double finalDamage = baseDamage * typeMultiplier * randomFactor;

        // Step 5: Check for critical hit
        if (random.nextInt(16) == 0) { // 1/16 chance
            System.out.println("Critical hit!");
            finalDamage *= 2;
        }

        // Step 6: Floor the final damage to at least 1
        finalDamage = Math.max(Math.floor(finalDamage), 1);

        return (int) finalDamage;
    }

    private int calculateDamage(Move move, trainerPokemon attacker, Pokemon defender) {
        int levelFactor = (2 * attacker.getLevel()) / 5 + 2;
        double attackDefenseRatio = (double) attacker.getAttack() / defender.getDefense();
        double baseDamage = (levelFactor * move.getPower() * attackDefenseRatio) / 50;

        baseDamage += 2;

        double typeMultiplier = calculateTypeEffectivenessMultiplier(move, defender);

        int finalDamage = (int) (baseDamage * typeMultiplier);

        Random crit = new Random();
        if (crit.nextInt(16) == 0) {
            System.out.println("Critical hit!");
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
        Pane root = new Pane();
        Scene scene = new Scene(root, 500, 500);

        // Load the custom font
        Font font = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 11);

        // Load the background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/BattleBG.png"));

        // Create the BackgroundImage object
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        // Set the background of the BorderPane
        root.setBackground(new Background(background));

        // Load the images
        Image playerPokemonImage = new Image(
                getClass().getResourceAsStream("/" + player.getParty().get(0).getSpritePath()));
        Image opponentPokemonImage = new Image(
                getClass().getResourceAsStream("/" + opponent.getPokemonList().get(0).getSpritePath()));

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
        playerHealthBarForeground = new Rectangle(
                player.getParty().get(0).getRemainingHealth() / (double) player.getParty().get(0).getHp() * 100, 10);
        playerHealthBarBackground.setStrokeWidth(2);
        playerHealthBarBackground.setArcWidth(10);
        playerHealthBarBackground.setArcHeight(10);
        playerHealthBarForeground = new Rectangle(
                player.getParty().get(0).getRemainingHealth() / (double) player.getParty().get(0).getHp() * 100, 10);
        playerHealthBarForeground.setFill(Color.LIGHTGREEN);
        playerHealthBarForeground.setArcWidth(10);
        playerHealthBarForeground.setArcHeight(10);
        Rectangle opponentHealthBarBackground = new Rectangle(100, 10);
        opponentHealthBarBackground.setFill(Color.DARKGREY);
        opponentHealthBarForeground = new Rectangle(opponent.getPokemonList().get(0).getRemainingHealth()
                / (double) opponent.getPokemonList().get(0).getHp() * 100, 10);
        opponentHealthBarBackground.setStrokeWidth(2);
        opponentHealthBarBackground.setArcWidth(10);
        opponentHealthBarBackground.setArcHeight(10);
        opponentHealthBarForeground = new Rectangle(opponent.getPokemonList().get(0).getRemainingHealth()
                / (double) opponent.getPokemonList().get(0).getHp() * 100, 10);
        opponentHealthBarForeground.setFill(Color.LIGHTGREEN);
        playerHealthLabel = new Label(
                player.getParty().get(0).getRemainingHealth() + "/" + player.getParty().get(0).getHp());
        opponentHealthLabel = new Label(
                opponent.getPokemonList().get(0).getRemainingHealth() + "/" + opponent.getPokemonList().get(0).getHp());

        // Create the health labels
        playerHealthLabel = new Label(
                player.getParty().get(0).getRemainingHealth() + "/" + player.getParty().get(0).getHp());
        opponentHealthLabel = new Label(
                opponent.getPokemonList().get(0).getRemainingHealth() + "/" + opponent.getPokemonList().get(0).getHp());

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
        Rectangle playerXPBarForeground = new Rectangle(
                player.getParty().get(0).getExperience() / (double) player.getParty().get(0).getLevelTreshhold() * 100,
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
                playerHealthBarForeground, opponentHealthBarBackground, opponentHealthBarForeground, playerHealthLabel,
                opponentHealthLabel, playerXPBarBackground, playerXPBarForeground, opponentXPBarBackground,
                playerPokemonNickname, opponentPokemonNickname, playerPokemonLevel, opponentPokemonLevel);

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
        playerHealthLabel.setLayoutY(playerHealthBarBackground.getLayoutY() + playerHealthBarBackground.getHeight());
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

        // Flip the opponent's Pokemon image horizontally so it faces the player's
        // Pokemon
        playerPokemonView.setScaleX(-1);

        primaryStage.setTitle("Pokemon Battle");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Create battle controls
        HBox controlsBox = new HBox(20);
        controlsBox.setLayoutY(scene.getHeight() - 120);
        controlsBox.setPrefWidth(scene.getWidth());
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setSpacing(20);

        // Create and style buttons
        Button fightButton = new Button("Fight");
        Button switchButton = new Button("Switch");
        Button runButton = new Button("Run");

        // Increase font size
        Font largeFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 18);
        fightButton.setFont(largeFont);
        switchButton.setFont(largeFont);
        runButton.setFont(largeFont);

        // Style buttons to span width and have consistent height
        String buttonStyle = "-fx-text-fill: black; " +
                "-fx-border-color: black; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 2; " +
                "-fx-background-radius: 2; " +
                "-fx-min-width: 50px; " +
                "-fx-min-height: 20px; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent; " +
                "-fx-background-insets: 0;";

        fightButton.setStyle(buttonStyle);
        switchButton.setStyle(buttonStyle);
        runButton.setStyle(buttonStyle);

        // Fight button animation
        TranslateTransition fightButtonAnim = new TranslateTransition(Duration.millis(500), fightButton);
        fightButtonAnim.setFromY(100);
        fightButtonAnim.setToY(0);
        fightButtonAnim.setInterpolator(Interpolator.EASE_OUT);

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
        switchButtonAnim.play();
        runButtonAnim.play();

        // Fight button disappearing animation
        TranslateTransition fightButtonHide = new TranslateTransition(Duration.millis(500), fightButton);
        fightButtonHide.setFromY(0);
        fightButtonHide.setToY(100);
        fightButtonHide.setInterpolator(Interpolator.EASE_IN);

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

        // Configure HBox for buttons
        controlsBox.setSpacing(10);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(70, 10, 10, 10));

        // Use full width
        controlsBox.setPrefWidth(500);
        controlsBox.setMaxWidth(Double.MAX_VALUE);
        controlsBox.setMinHeight(40);

        // Initialize controlsBox with default buttons
        controlsBox.getChildren().addAll(fightButton, switchButton, runButton);

        // Make buttons grow to fill space
        HBox.setHgrow(fightButton, Priority.ALWAYS);
        HBox.setHgrow(switchButton, Priority.ALWAYS);
        HBox.setHgrow(runButton, Priority.ALWAYS);

        // Fight
        fightButton.setOnAction(e -> {
            fightButtonHide.play();
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

                            String moveButtonStyle = String.format(
                                    "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                                            "-fx-text-fill: black; " +
                                            "-fx-border-color: derive(%s, -20%%); " +
                                            "-fx-border-radius: 5; " +
                                            "-fx-background-radius: 5; " +
                                            "-fx-min-width: 150px; " +
                                            "-fx-min-height: 40px; " +
                                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2); " +
                                            "-fx-cursor: hand;",
                                    lighter.toString().replace("0x", "#"),
                                    darker.toString().replace("0x", "#"),
                                    baseColor);

                            String hoverStyle = String.format(
                                    "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                                            "-fx-scale-x: 1.05; " +
                                            "-fx-scale-y: 1.05; " +
                                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 0, 3);",
                                    lighter.brighter().toString().replace("0x", "#"),
                                    darker.toString().replace("0x", "#"));

                            String pressedStyle = String.format(
                                    "-fx-background-color: linear-gradient(to top, %s, %s); " +
                                            "-fx-scale-x: 0.98; " +
                                            "-fx-scale-y: 0.98; " +
                                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 1);",
                                    lighter.toString().replace("0x", "#"),
                                    darker.darker().toString().replace("0x", "#"));

                            moveButton.setStyle(moveButtonStyle);
                            moveButton.setOnMouseEntered(ev -> moveButton.setStyle(moveButtonStyle + hoverStyle));
                            moveButton.setOnMouseExited(ev -> moveButton.setStyle(moveButtonStyle));
                            moveButton.setOnMousePressed(ev -> moveButton.setStyle(moveButtonStyle + pressedStyle));
                            moveButton.setOnMouseReleased(ev -> moveButton.setStyle(moveButtonStyle));

                            // Initial position: below the visible window
                            moveButton.setTranslateY(scene.getHeight() - 300);

                            // Set the action for each move button
                            moveButton.setOnAction(ev -> {
                                System.out.println(
                                        player.getCurrentPokemon().getNickname() + " used " + move.getName() + "!");
                                applyPlayerAction(move.getName());

                                // Check if opponent fainted
                                if (!opponent.hasUsablePokemon()) {
                                    System.out.println("You won the battle!");
                                    int prizeMoney = opponent.getRewardMoney();
                                    player.addMoney(prizeMoney);
                                    System.out.println("You got $" + prizeMoney + " for winning!");
                                    Stage stage = (Stage) controlsBox.getScene().getWindow();
                                    stage.close();
                                    return;
                                }

                                aiTurn();
                                // Update player Pokemon health based on first party Pokemon
                                Pokemon playerPokemon = player.getCurrentPokemon();
                                double playerHealthPercent = (double) playerPokemon.getRemainingHealth()
                                        / playerPokemon.getHp() * 100;
                                playerHealthBarForeground.setWidth(playerHealthPercent);
                                playerHealthLabel
                                        .setText(playerPokemon.getRemainingHealth() + "/" + playerPokemon.getHp());

                                // Update player Pokemon XP bar and level
                                double playerXPPercent = (double) playerPokemon.getExperience()
                                        / playerPokemon.getLevelTreshhold() * 100;
                                playerXPBarForeground.setWidth(playerXPPercent);
                                playerPokemonLevel.setText("Lv. " + playerPokemon.getLevel());

                                // Update opponent Pokemon health based on first Pokemon in their list
                                trainerPokemon opponentPokemon = opponent.getCurrentPokemon();
                                double oppHealthPercent = (double) opponentPokemon.getRemainingHealth()
                                        / opponentPokemon.getHp() * 100;
                                opponentHealthBarForeground.setWidth(oppHealthPercent);
                                opponentHealthLabel
                                        .setText(opponentPokemon.getRemainingHealth() + "/" + opponentPokemon.getHp());

                                // Update opponent Pokemon level
                                opponentPokemonLevel.setText("Lv. " + opponentPokemon.getLevel());
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

                        // Create the "Back" button
                        Button backButton = new Button("◄");
                        backButton.setFont(Font.font("Arial", 14));
                        backButton.setStyle("-fx-background-color: #cc0000; " +
                                "-fx-text-fill: white; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 2 4 2 4;");
                        backButton.setPrefWidth(25);
                        backButton.setPrefHeight(25);
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
                            TranslateTransition backButtonReverseAnim = new TranslateTransition(Duration.millis(500),
                                    backButton);
                            backButtonReverseAnim.setFromY(-120); // Current position
                            backButtonReverseAnim.setToY(scene.getHeight() - 400); // Move out of view
                            backButtonReverseAnim.setInterpolator(Interpolator.EASE_IN);
                            backButtonReverseAnim.play();

                            // Delay clearing controlsBox until animations finish
                            new Timeline(new KeyFrame(Duration.millis(500), ae -> {
                                controlsBox.getChildren().clear();

                                fightButtonAnim.play();
                                switchButtonAnim.play();
                                runButtonAnim.play();
                                controlsBox.getChildren().addAll(fightButton, switchButton, runButton);

                            })).play();
                        });

                        // Animate back button
                        TranslateTransition backButtonAnim = new TranslateTransition(Duration.millis(500), backButton);
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
        });

        // Switch
        switchButton.setOnAction(e -> {
            fightButtonHide.play();
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

                for (int i = 0; i < player.getParty().size(); i++) {
                    Pokemon pokemon = player.getParty().get(i);
                    Button pokemonButton = new Button();
                    Image pokemonSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
                    ImageView spriteView = new ImageView(pokemonSprite);
                    spriteView.setFitWidth(55);

                    spriteView.setFitHeight(55);
                    spriteView.setPreserveRatio(true);
                    pokemonButton.setGraphic(spriteView);
                    pokemonButton.setTooltip(new Tooltip(pokemon.getNickname()));
                    pokemonButton.setStyle(pokemonButtonStyle);

                    pokemonButton.setOnAction(event -> {
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

                            // Update to the new Pokémon sprite
                            player.setCurrentPokemon(pokemon);
                            playerPokemonNickname.setText(pokemon.getNickname());
                            Image newSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
                            playerPokemonView.setImage(newSprite);

                            // Set the new Pokémon's initial position to the final position of the outgoing
                            // Pokémon
                            playerPokemonView.setTranslateX(switchOutEndX);
                            colorAdjust.setBrightness(2.0); // Ensure the new Pokémon starts fully white

                            // Switch-in color fade animation (from bright white to normal)
                            Timeline switchInFade = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 2.0)),
                                    new KeyFrame(Duration.millis(800),
                                            new KeyValue(colorAdjust.brightnessProperty(), 0.0)));

                            // Switch-in animation for new Pokémon
                            TranslateTransition switchInAnim = new TranslateTransition(Duration.millis(800),
                                    playerPokemonView);
                            switchInAnim.setToX(0); // Move to the correct position
                            switchInAnim.setInterpolator(Interpolator.EASE_OUT);

                            switchInAnim.setOnFinished(ev -> playerPokemonView.setEffect(null));

                            // Play switch-in animations
                            switchInAnim.play();
                            switchInFade.play();

                            // Update health bar and labels
                            Pokemon playerPokemon = player.getCurrentPokemon();
                            double playerHealthPercent = (double) playerPokemon.getRemainingHealth()
                                    / playerPokemon.getHp()
                                    * 100;
                            playerHealthBarForeground.setWidth(playerHealthPercent);
                            playerHealthLabel.setText(playerPokemon.getRemainingHealth() + "/" + playerPokemon.getHp());

                            double playerXPPercent = (double) playerPokemon.getExperience()
                                    / playerPokemon.getLevelTreshhold() * 100;
                            playerXPBarForeground.setWidth(playerXPPercent);
                            playerPokemonLevel.setText("Lv. " + playerPokemon.getLevel());

                            trainerPokemon opponentPokemon = opponent.getCurrentPokemon();
                            double oppHealthPercent = (double) opponentPokemon.getRemainingHealth()
                                    / opponentPokemon.getHp() * 100;
                            opponentHealthBarForeground.setWidth(oppHealthPercent);
                            opponentHealthLabel
                                    .setText(opponentPokemon.getRemainingHealth() + "/" + opponentPokemon.getHp());

                            opponentPokemonLevel.setText("Lv. " + opponentPokemon.getLevel());
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
                            switchButton.setTranslateY(scene.getHeight());
                            runButton.setTranslateY(scene.getHeight());

                            // Add buttons first
                            controlsBox.getChildren().addAll(fightButton, switchButton, runButton);

                            // Then play animations
                            fightButtonAnim.play();
                            switchButtonAnim.play();
                            runButtonAnim.play();
                        });
                        closeAnim.play();
                    });

                    switchBox.getChildren().add(pokemonButton);
                }

                controlsBox.getChildren().clear();
                controlsBox.getChildren().add(switchBox);

                TranslateTransition tt = new TranslateTransition(Duration.millis(1800), switchBox);
                tt.setToY(-20);
                tt.setInterpolator(Interpolator.EASE_OUT);
                tt.jumpTo(Duration.millis(1400));
                tt.play();
            });
        });
    }
}