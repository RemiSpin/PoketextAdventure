package WindowThings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import PlayerRelated.Player;
import PokemonLogic.Pokemon;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@SuppressWarnings({ "unused", "FieldMayBeFinal" })

public class PokemonInfo {
    private static List<PokemonInfo> activeWindows = new ArrayList<>();

    public static List<PokemonInfo> getActiveWindows() {
        return new ArrayList<>(activeWindows);
    }

    private Pokemon pokemon;
    private Stage stage;

    public PokemonInfo(Pokemon pokemon) {
        this.pokemon = pokemon;
        createAndShowWindow();
    }

    private void createAndShowWindow() {
        try {
            this.stage = new Stage();
            GridPane root = new GridPane();
            stage.setTitle(pokemon.getNickname() + "'s Stats");
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(10, 10, 10, 10));

            // Prevent manual closing
            stage.setOnCloseRequest(e -> {
                e.consume();
                System.out.println("Please use the window controls to close this window.");
            });

            // Register with main window
            WindowThings.mainWindow.registerWindow(stage);

            Image backgroundImage = new Image(getClass().getResourceAsStream("/infoBG.png"));
            BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, 100, false, false, false, true);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            root.setBackground(new Background(background));

            // Load the custom font
            Font font = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 16);

            // Load the sprite of the Pokemon
            Image pokemonSprite = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
            ImageView pokemonSpriteView = new ImageView(pokemonSprite);

            GridPane.setHalignment(pokemonSpriteView, HPos.CENTER);

            // Create the Labels
            Label trainerNameLabel = new Label("Trainer: " + Player.getName());
            GridPane.setHalignment(trainerNameLabel, HPos.CENTER);
            Label pokemonNumberLabel = new Label("Pokedex: " + pokemon.getNumber());
            GridPane.setHalignment(pokemonNumberLabel, HPos.CENTER);
            Label nameLabel = new Label("Name: " + pokemon.getNickname());
            GridPane.setHalignment(nameLabel, HPos.CENTER);
            Label speciesNameLabel = new Label("Species Name: " + pokemon.getName());
            GridPane.setHalignment(speciesNameLabel, HPos.CENTER);
            Label type1Label = new Label("Type 1: " + pokemon.getType1());
            GridPane.setHalignment(type1Label, HPos.CENTER);
            Label type2Label = new Label("Type 2: " + pokemon.getType2());
            GridPane.setHalignment(type2Label, HPos.CENTER);
            Label levelLabel = new Label("Level: " + pokemon.getLevel());
            GridPane.setHalignment(levelLabel, HPos.CENTER);
            Label statusLabel = new Label("Status: " + pokemon.getStatusCondition());
            GridPane.setHalignment(statusLabel, HPos.CENTER);
            Label hpLabel = new Label("HP: " + pokemon.getRemainingHealth() + " / " + pokemon.getHp());
            GridPane.setHalignment(hpLabel, HPos.CENTER);
            Label attackLabel = new Label("Attack: " + pokemon.getAttack());
            GridPane.setHalignment(attackLabel, HPos.CENTER);
            Label defenseLabel = new Label("Defense: " + pokemon.getDefense());
            GridPane.setHalignment(defenseLabel, HPos.CENTER);
            Label specialAttackLabel = new Label("Special Attack: " + pokemon.getSpecialAttack());
            GridPane.setHalignment(specialAttackLabel, HPos.CENTER);
            Label specialDefenseLabel = new Label("Special Defense: " + pokemon.getSpecialDefense());
            GridPane.setHalignment(specialDefenseLabel, HPos.CENTER);
            Label speedLabel = new Label("Speed: " + pokemon.getSpeed());
            GridPane.setHalignment(speedLabel, HPos.CENTER);
            Label movesLabel = new Label("Moves: " + pokemon.getMoves());
            GridPane.setHalignment(movesLabel, HPos.CENTER);
            Label experienceLabel = new Label(
                    "Experience: " + pokemon.getExperience() + " / " + pokemon.getLevelTreshhold());
            GridPane.setHalignment(experienceLabel, HPos.CENTER);

            // Set the font for the Labels
            nameLabel.setFont(font);
            speciesNameLabel.setFont(font);
            type1Label.setFont(font);
            type2Label.setFont(font);
            levelLabel.setFont(font);
            statusLabel.setFont(font);
            hpLabel.setFont(font);
            attackLabel.setFont(font);
            defenseLabel.setFont(font);
            specialAttackLabel.setFont(font);
            specialDefenseLabel.setFont(font);
            speedLabel.setFont(font);
            movesLabel.setFont(font);
            experienceLabel.setFont(font);
            trainerNameLabel.setFont(font);
            pokemonNumberLabel.setFont(font);

            // Add the ImageView and Labels to the root node
            root.addRow(0, pokemonSpriteView);
            root.addRow(1, trainerNameLabel);
            root.addRow(2, pokemonNumberLabel);
            root.addRow(3, nameLabel);
            root.addRow(4, speciesNameLabel);
            root.addRow(5, new Label());
            root.addRow(6, type1Label);
            root.addRow(7, type2Label);
            root.addRow(8, new Label());
            root.addRow(9, levelLabel);
            root.addRow(10, experienceLabel);
            root.addRow(11, new Label());
            root.addRow(12, statusLabel);
            root.addRow(13, new Label());
            root.addRow(14, hpLabel);
            root.addRow(15, attackLabel);
            root.addRow(16, defenseLabel);
            root.addRow(17, specialAttackLabel);
            root.addRow(18, specialDefenseLabel);
            root.addRow(19, speedLabel);
            root.addRow(20, new Label());
            root.addRow(21, movesLabel);

            // Add a close button to the window
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> {
                activeWindows.remove(this);
                WindowThings.mainWindow.unregisterWindow(stage);
                stage.close();
            });

            // Add the close button to a new row in the GridPane
            GridPane.setHalignment(closeButton, HPos.CENTER);
            root.addRow(22, closeButton);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
            activeWindows.add(PokemonInfo.this);
        } catch (Exception e) {
            Logger.getLogger(PokemonInfo.class.getName()).log(Level.SEVERE,
                    "Failed to initialize Pokemon info window", e);

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Display Pokemon Info");
                alert.setContentText("An error occurred while trying to show Pokemon information: "
                        + e.getMessage());
                alert.showAndWait();

                // Clean up and close window
                activeWindows.remove(this);
                if (stage != null) {
                    stage.close();
                }
            });
        }
    }
}