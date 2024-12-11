package BattleLogic;

import java.util.List;
import java.util.Random;

import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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
        // Calculate the base damage
        int baseDamage = (((2 * attacker.getLevel()) / 5 + 2) * move.getPower()
                * (attacker.getAttack() / defender.getDefense()));

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
        int baseDamage = (((2 * attacker.getLevel()) / 5 + 2) * move.getPower()
                * (attacker.getAttack() / defender.getDefense()));

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
        Rectangle playerHealthBarForeground = new Rectangle(
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
        Rectangle opponentHealthBarForeground = new Rectangle(opponent.getPokemonList().get(0).getRemainingHealth()
                / (double) opponent.getPokemonList().get(0).getHp() * 100, 10);
        opponentHealthBarForeground.setFill(Color.LIGHTGREEN);
        playerHealthLabel = new Label(
                player.getParty().get(0).getRemainingHealth() + "/" + player.getParty().get(0).getHp());
        opponentHealthLabel = new Label(
                opponent.getPokemonList().get(0).getRemainingHealth() + "/" + opponent.getPokemonList().get(0).getHp());

        // Create the health labels
        Label playerHealthLabel = new Label(
                player.getParty().get(0).getRemainingHealth() + "/" + player.getParty().get(0).getHp());
        Label opponentHealthLabel = new Label(
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
        primaryStage.setResizable(false); // Make the window unresizable
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

        // Create the entry animation
        TranslateTransition entryAnimation = new TranslateTransition(Duration.millis(1800), controlsBox);
        entryAnimation.setFromY(scene.getHeight());
        entryAnimation.setToY(0);
        entryAnimation.setInterpolator(Interpolator.EASE_IN);

        // Create the stopping animation
        TranslateTransition stopAnimation = new TranslateTransition(Duration.millis(1800), controlsBox);
        stopAnimation.setToY(0);
        stopAnimation.setInterpolator(Interpolator.EASE_OUT);

        // Ensure starting position
        controlsBox.setTranslateY(scene.getHeight());

        // Make buttons grow to fill space
        HBox.setHgrow(fightButton, Priority.ALWAYS);
        HBox.setHgrow(switchButton, Priority.ALWAYS);
        HBox.setHgrow(runButton, Priority.ALWAYS);

        // Update the fight button handler
        fightButton.setOnAction(e -> {
            if (player.getCurrentPokemon() != null) {
                List<Move> moves = player.getCurrentPokemon().getMovesList();
                if (moves != null && !moves.isEmpty()) {
                    controlsBox.getChildren().clear();

                    // Create vertical container
                    VBox moveContainer = new VBox(10);
                    moveContainer.setAlignment(Pos.CENTER);
                    moveContainer.setTranslateY(-70); // Move up by 50 pixels
                    moveContainer.setPadding(new Insets(20, 10, 20, 10));

                    HBox topRow = new HBox(20);
                    HBox bottomRow = new HBox(20);
                    topRow.setAlignment(Pos.CENTER);
                    bottomRow.setAlignment(Pos.CENTER);

                    // Add move buttons to rows
                    for (int i = 0; i < moves.size(); i++) {
                        Move move = moves.get(i);
                        Button moveButton = new Button(move.getName());
                        moveButton.setFont(font);
                        moveButton.setStyle(buttonStyle);
                        moveButton.setPrefWidth(150);
                        moveButton.setPrefHeight(40);

                        moveButton.setOnAction(ev -> {
                            // Player's turn
                            System.out.println(
                                    player.getCurrentPokemon().getNickname() + " used " + move.getName() + "!");
                            applyPlayerAction(move.getName());

                            // Check if opponent fainted
                            if (opponent.getCurrentPokemon().getRemainingHealth() <= 0) {
                                System.out.println(opponent.getCurrentPokemon().getName() + " fainted!");
                                if (!opponent.hasUsablePokemon()) {
                                    System.out.println("You won the battle!");
                                    return;
                                }
                            }
                            // AI's turn
                            aiTurn();
                            updateBattleUI();

                            // Return to main battle controls
                            controlsBox.getChildren().clear();
                            controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
                        });

                        // Add to appropriate row
                        if (i < 2) {
                            topRow.getChildren().add(moveButton);
                        } else {
                            bottomRow.getChildren().add(moveButton);
                        }
                    }

                    moveContainer.getChildren().addAll(topRow, bottomRow);
                    controlsBox.getChildren().add(moveContainer);
                }
            }
        });

        // Update the switch button
        switchButton.setOnAction(e -> {
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

                if (pokemon == player.getCurrentPokemon()) {
                    pokemonButton.setStyle(pokemonButtonStyle);
                    pokemonButton.setOnAction(event -> {
                        TranslateTransition closeAnim = new TranslateTransition(Duration.millis(1800), switchBox);
                        closeAnim.setToY(scene.getHeight());
                        closeAnim.setInterpolator(Interpolator.EASE_IN);
                        closeAnim.setOnFinished(evt -> {
                            controlsBox.getChildren().clear();
                            controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
                        });
                        closeAnim.play();
                        Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(1400), ae -> {
                            closeAnim.stop();
                            controlsBox.getChildren().clear();
                            controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
                        }));
                        timeline1.play();
                    });
                } else {
                    pokemonButton.setStyle(pokemonButtonStyle);
                    pokemonButton.setOnAction(event -> {
                        TranslateTransition closeAnim = new TranslateTransition(Duration.millis(1800), switchBox);
                        closeAnim.setToY(scene.getHeight());
                        closeAnim.setInterpolator(Interpolator.EASE_IN);
                        closeAnim.setOnFinished(evt -> {
                            player.setCurrentPokemon(pokemon);
                            updateBattleUI();
                            controlsBox.getChildren().clear();
                            controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
                        });
                        closeAnim.play();
                        Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(400), ae -> {
                            closeAnim.stop();
                            player.setCurrentPokemon(pokemon);
                            playerPokemonNickname.setText(pokemon.getNickname());
                            Image newSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
                            playerPokemonView.setImage(newSprite);
                            updateBattleUI();
                            controlsBox.getChildren().clear();
                            controlsBox.getChildren().addAll(fightButton, switchButton, runButton);
                        }));
                        timeline1.play();
                    });
                }
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
    }

    private void updateBattleUI() {
        // Update health bars and labels based on current Pokemon states
        playerHealthBarForeground.setWidth(player.getCurrentPokemon().getRemainingHealth() /
                (double) player.getCurrentPokemon().getHp() * 100);
        opponentHealthBarForeground.setWidth(opponent.getCurrentPokemon().getRemainingHealth() /
                (double) opponent.getCurrentPokemon().getHp() * 100);

        playerHealthLabel.setText(player.getCurrentPokemon().getRemainingHealth() +
                "/" + player.getCurrentPokemon().getHp());
        opponentHealthLabel.setText(opponent.getCurrentPokemon().getRemainingHealth() +
                "/" + opponent.getCurrentPokemon().getHp());
    }
}


