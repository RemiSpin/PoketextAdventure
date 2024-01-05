package PokemonLogic;

import PlayerRelated.Player;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PokemonInfo extends Application {
    private Pokemon pokemon;

    public PokemonInfo(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        primaryStage.setTitle(pokemon.getNickname() + "'s Stats");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 10, 10, 10));

        Image backgroundImage = new Image(getClass().getResourceAsStream("/WindowThings/Assets/infoBG.png"));
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, 100, false, false, false, true);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(background));

        // Load the custom font
        Font font = Font.loadFont(getClass().getResourceAsStream("/WindowThings/Assets/rbygsc.ttf"), 14);

        // Load the sprite of the Pokemon
        Image pokemonSprite = new Image(getClass().getResourceAsStream("/WindowThings/" + pokemon.getSpritePath()));
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
        Label movesLabel = new Label("Moves: " + pokemon.getMoves().toString());
        GridPane.setHalignment(movesLabel, HPos.CENTER);
        Label experienceLabel = new Label("Experience: " + pokemon.getExperience() + " / " + pokemon.getLevelTreshhold());
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

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}