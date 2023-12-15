package BattleLogic;

import PlayerRelated.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Battle extends Application {
    private Player player;
    private Trainer opponent;

    public Battle(Player player, Trainer opponent) {
        this.player = player;
        this.opponent = opponent;
    }

@Override
public void start(Stage primaryStage) {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 500, 500);

    // Load the background image
    Image backgroundImage = new Image(getClass().getResourceAsStream("/WindowThings/Assets/BattleBG.png"));

    // Create the BackgroundImage object
    BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    // Set the background of the BorderPane
    root.setBackground(new Background(background));

    // Load the images
    Image playerPokemonImage = new Image(getClass().getResourceAsStream("/WindowThings/" + player.getParty().get(0).getSpritePath()));
    Image opponentPokemonImage = new Image(getClass().getResourceAsStream("/WindowThings/" + opponent.getPokemonList().get(0).getSpritePath()));

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

    // Add the ImageViews, health bars, and health labels to the root node
    root.getChildren().addAll(playerPokemonView, opponentPokemonView, playerHealthBarBackground, playerHealthBarForeground, opponentHealthBarBackground, opponentHealthBarForeground, playerHealthLabel, opponentHealthLabel);

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
    playerHealthBarBackground.setLayoutY(playerPokemonView.getLayoutY() + playerPokemonView.getFitHeight() + 75);
    playerHealthBarForeground.setLayoutX(playerPokemonView.getLayoutX() - 20);
    playerHealthBarForeground.setLayoutY(playerPokemonView.getLayoutY() + playerPokemonView.getFitHeight() + 75);
    opponentHealthBarBackground.setLayoutX(opponentPokemonView.getLayoutX() - 25);
    opponentHealthBarBackground.setLayoutY(opponentPokemonView.getLayoutY() + opponentPokemonView.getFitHeight() + 75);
    opponentHealthBarForeground.setLayoutX(opponentPokemonView.getLayoutX() - 25);
    opponentHealthBarForeground.setLayoutY(opponentPokemonView.getLayoutY() + opponentPokemonView.getFitHeight() + 75);

    // Set the positions of the health labels
    playerHealthLabel.setLayoutY(playerHealthBarBackground.getLayoutY() + playerHealthBarBackground.getHeight() + 5);
    opponentHealthLabel.setLayoutY(opponentHealthBarBackground.getLayoutY() + opponentHealthBarBackground.getHeight() + 5);

    // Flip the opponent's Pokemon image horizontally so it faces the player's Pokemon
    playerPokemonView.setScaleX(-1);

    primaryStage.setTitle("Pokemon Battle");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false); // Make the window unresizable
    primaryStage.show();

    // TODO: Implement battle logic
}

    // TODO: Add methods for battle logic
}