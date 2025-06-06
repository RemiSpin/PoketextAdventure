package WindowThings;

import java.util.List;

import PokemonLogic.Pokemon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

@SuppressWarnings({ "FieldMayBeFinal", "unused" })

public class PCWindow {
    private Stage stage;
    private ListView<String> partyListView;
    private ListView<String> pcListView;
    private Label detailsLabel;
    private Font pokemonFont;
    private Font pokemonFontSmall;
    private Pokemon selectedPokemon;
    private ImageView pokemonPreviewImage;

    public PCWindow() {
        // Create the stage
        stage = new Stage();
        stage.setTitle("PC Storage System");

        // Load the custom font
        try {
            pokemonFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 18);
            pokemonFontSmall = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 11);
        } catch (Exception e) {
            System.out.println("Could not load Pokémon font: " + e.getMessage());
            pokemonFont = Font.font("Arial", FontWeight.BOLD, 18);
            pokemonFontSmall = Font.font("Arial", 11);
        }

        // Make the PC window dependent on the main window
        Stage mainStage = mainWindow.getPrimaryStage();
        if (mainStage != null) {
            stage.initOwner(mainStage);
        }

        // Prevent manual closing
        stage.setOnCloseRequest(event -> {
            event.consume();
            System.out.println("Use the Close button to exit the PC Storage System.");
        });

        // Register with main window
        WindowThings.mainWindow.registerWindow(stage);

        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");

        // Create the title with styled panel
        Label titleLabel = new Label("Pokémon Storage System");
        titleLabel.setFont(pokemonFont);
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(10, 0, 10, 0));
        titleLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.4));
        titleLabel.setEffect(dropShadow);

        VBox topContainer = new VBox(10);
        topContainer.getChildren().add(titleLabel);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(0, 0, 20, 0));
        mainLayout.setTop(topContainer);

        // Create the party list view
        partyListView = new ListView<>();
        partyListView.setPrefHeight(150);
        partyListView.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Custom cell factory for party list
        partyListView.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setFont(pokemonFontSmall);
                    setStyle("-fx-padding: 5; -fx-background-radius: 5;");
                    // Highlight when selected
                    selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            setStyle("-fx-background-color: #303030; " +
                                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-padding: 5; -fx-background-radius: 5;");
                        } else {
                            setStyle("-fx-background-color: transparent; " +
                                    "-fx-text-fill: black; -fx-font-weight: normal; " +
                                    "-fx-padding: 5; -fx-background-radius: 5;");
                        }
                    });
                }
            }
        });

        VBox partyBox = new VBox(5);
        Label partyLabel = new Label("YOUR PARTY:");
        partyLabel.setFont(pokemonFontSmall);
        partyLabel.setTextFill(Color.BLACK);
        partyLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 2 5 2 5; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");
        partyBox.getChildren().addAll(partyLabel, partyListView);

        // Create the PC list view
        pcListView = new ListView<>();
        pcListView.setPrefHeight(150);
        pcListView.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Custom cell factory for PC list
        pcListView.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setFont(pokemonFontSmall);
                    setStyle("-fx-padding: 5; -fx-background-radius: 5;");
                    // Highlight when selected
                    selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        if (isNowSelected) {
                            setStyle("-fx-background-color: #303030; " +
                                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-padding: 5; -fx-background-radius: 5;");
                        } else {
                            setStyle("-fx-background-color: transparent; " +
                                    "-fx-text-fill: black; -fx-font-weight: normal; " +
                                    "-fx-padding: 5; -fx-background-radius: 5;");
                        }
                    });
                }
            }
        });

        VBox pcBox = new VBox(5);
        Label pcLabel = new Label("PC STORAGE:");
        pcLabel.setFont(pokemonFontSmall);
        pcLabel.setTextFill(Color.BLACK);
        pcLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 2 5 2 5; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");
        pcBox.getChildren().addAll(pcLabel, pcListView);

        // Create the lists container with space between
        HBox listsContainer = new HBox(15);
        listsContainer.getChildren().addAll(partyBox, pcBox);
        listsContainer.setAlignment(Pos.CENTER);
        mainLayout.setCenter(listsContainer);

        // Create the details area with Pokemon preview
        HBox detailsContainer = new HBox(15);
        detailsContainer.setPadding(new Insets(10));
        detailsContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Image view for Pokemon preview
        pokemonPreviewImage = new ImageView();
        pokemonPreviewImage.setFitHeight(100);
        pokemonPreviewImage.setFitWidth(100);
        pokemonPreviewImage.setPreserveRatio(true);
        
        // Set default Pokéball image
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/Pokeball.png"));
            pokemonPreviewImage.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Could not load default Pokéball image: " + e.getMessage());
        }

        // Details label
        detailsLabel = new Label("Select a Pokémon to view details");
        detailsLabel.setFont(pokemonFontSmall);
        detailsLabel.setWrapText(true);
        detailsLabel.setTextFill(Color.BLACK);
        detailsLabel.setPadding(new Insets(5));
        detailsLabel.setStyle("-fx-background-color: #f0f0f0; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #aaaaaa; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");

        VBox infoBox = new VBox(5);
        infoBox.setPrefWidth(230);
        infoBox.getChildren().add(detailsLabel);

        detailsContainer.getChildren().addAll(pokemonPreviewImage, infoBox);

        // Create the buttons
        Button withdrawButton = createStyledButton("Withdraw", "#404040"); // Dark gray
        Button depositButton = createStyledButton("Deposit", "#606060"); // Medium gray
        Button closeButton = createStyledButton("Close", "#202020"); // Near black

        // Create the buttons container
        HBox buttonsBox = new HBox(15);
        buttonsBox.getChildren().addAll(withdrawButton, depositButton, closeButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));

        // Create the bottom container
        VBox bottomContainer = new VBox(15);
        bottomContainer.getChildren().addAll(detailsContainer, buttonsBox);
        mainLayout.setBottom(bottomContainer);

        // Add event handling
        withdrawButton.setOnAction(e -> withdrawPokemon());
        depositButton.setOnAction(e -> depositPokemon());
        closeButton.setOnAction(e -> {
            // Properly close the window
            WindowThings.mainWindow.unregisterWindow(stage);
            stage.close();
        });

        partyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pcListView.getSelectionModel().clearSelection();
                updateDetailsForParty(newVal);
            }
        });

        pcListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                partyListView.getSelectionModel().clearSelection();
                updateDetailsForPC(newVal);
            }
        });

        // Set the scene and show the stage
        Scene scene = new Scene(mainLayout, 600, 520);
        stage.setScene(scene);
        stage.setResizable(false);

        // Populate the list views
        populateListViews();
    }

    private Button createStyledButton(String text, String baseColor) {
        Button button = new Button(text);
        button.setFont(pokemonFontSmall);

        // White background with black text styling
        String buttonStyle = "-fx-background-color: white; " +
                "-fx-text-fill: black; " +
                "-fx-border-color: #000000; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 8 15 8 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);";

        String hoverStyle = "-fx-background-color: #f0f0f0; " +
                "-fx-scale-x: 1.03; " +
                "-fx-scale-y: 1.03; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);";

        String pressedStyle = "-fx-background-color: #e0e0e0; " +
                "-fx-scale-x: 0.98; " +
                "-fx-scale-y: 0.98; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);";

        button.setStyle(buttonStyle);
        button.setOnMouseEntered(e -> button.setStyle(buttonStyle + hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(buttonStyle));
        button.setOnMousePressed(e -> button.setStyle(buttonStyle + pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(buttonStyle));

        return button;
    }

    private void populateListViews() {
        // Clear the list views
        partyListView.getItems().clear();
        pcListView.getItems().clear();

        // Populate the party list view
        List<Pokemon> party = PokeText_Adventure.player.getParty();
        for (Pokemon pokemon : party) {
            partyListView.getItems()
                    .add(pokemon.getNickname() + " (Lv. " + pokemon.getLevel() + " " + pokemon.getName() + ")");
        }

        // Populate the PC list view
        List<Pokemon> pc = PokeText_Adventure.player.getPC();
        for (Pokemon pokemon : pc) {
            pcListView.getItems()
                    .add(pokemon.getNickname() + " (Lv. " + pokemon.getLevel() + " " + pokemon.getName() + ")");
        }
    }

    private void updateDetailsForParty(String selectedItem) {
        List<Pokemon> party = PokeText_Adventure.player.getParty();
        int index = partyListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < party.size()) {
            selectedPokemon = party.get(index);
            updatePokemonDetails(selectedPokemon);
        }
    }

    private void updateDetailsForPC(String selectedItem) {
        List<Pokemon> pc = PokeText_Adventure.player.getPC();
        int index = pcListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < pc.size()) {
            selectedPokemon = pc.get(index);
            updatePokemonDetails(selectedPokemon);
        }
    }

    private void updatePokemonDetails(Pokemon pokemon) {
        // Update details text
        String healthStatus = "Normal";
        if (pokemon.getRemainingHealth() <= pokemon.getHp() * 0.25) {
            healthStatus = "Critical!";
        } else if (pokemon.getRemainingHealth() <= pokemon.getHp() * 0.5) {
            healthStatus = "Low";
        }

        String formattedDetails = String.format("""
                Name: %s
                Species: %s
                Level: %d
                HP: %d/%d (%s)
                Type: %s%s
                Exp: %d/%d""",
                pokemon.getNickname(),
                pokemon.getName(),
                pokemon.getLevel(),
                pokemon.getRemainingHealth(),
                pokemon.getHp(),
                healthStatus,
                pokemon.getType1(),
                (pokemon.getType2() != null && !pokemon.getType2().isEmpty()) ? "/" + pokemon.getType2() : "",
                pokemon.getExperience(),
                pokemon.getLevelTreshhold());

        detailsLabel.setText(formattedDetails);

        // Update pokemon image
        try {
            Image pokemonImage = new Image(getClass().getResourceAsStream("/" + pokemon.getSpritePath()));
            pokemonPreviewImage.setImage(pokemonImage);
        } catch (Exception e) {
            System.out.println("Could not load pokemon image: " + e.getMessage());
            try {
                // Use the correct path to the Pokéball image
                Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/Pokeball.png"));
                pokemonPreviewImage.setImage(defaultImage);
            } catch (Exception ex) {
                // If even the default image can't be loaded, just clear it
                System.out.println("Could not load default Pokéball image: " + ex.getMessage());
                pokemonPreviewImage.setImage(null);
            }
        }
    }

    private void withdrawPokemon() {
        int selectedIndex = pcListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            displayMessage("Please select a Pokémon from the PC to withdraw.");
            return;
        }

        List<Pokemon> party = PokeText_Adventure.player.getParty();
        if (party.size() >= 6) {
            displayMessage("Your party is full! You need to deposit a Pokémon first.");
            return;
        }

        // Withdraw the selected Pokémon
        boolean success = PokeText_Adventure.player.withdrawPokemonFromPC(selectedIndex);
        if (success) {
            displayMessage("Pokémon withdrawn to your party!");
            populateListViews();
            detailsLabel.setText("Select a Pokémon to view details");
            try {
                // Use the correct path to the Pokéball image
                Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/Pokeball.png"));
                pokemonPreviewImage.setImage(defaultImage);
            } catch (Exception ex) {
                // If the default image can't be loaded, just clear it
                System.out.println("Could not load default Pokéball image: " + ex.getMessage());
                pokemonPreviewImage.setImage(null);
            }
        } else {
            displayMessage("Failed to withdraw Pokémon. Please try again.");
        }
    }

    private void depositPokemon() {
        int selectedIndex = partyListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            displayMessage("Please select a Pokémon from your party to deposit.");
            return;
        }

        List<Pokemon> party = PokeText_Adventure.player.getParty();
        if (party.size() <= 1) {
            displayMessage("You must keep at least one Pokémon in your party!");
            return;
        }

        // Deposit the selected Pokémon
        boolean success = PokeText_Adventure.player.depositPokemonToPC(selectedIndex);
        if (success) {
            displayMessage("Pokémon deposited to PC!");
            populateListViews();
            detailsLabel.setText("Select a Pokémon to view details");
            try {
                // Use the correct path to the Pokéball image
                Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/Pokeball.png"));
                pokemonPreviewImage.setImage(defaultImage);
            } catch (Exception ex) {
                // If the default image can't be loaded, just clear it
                System.out.println("Could not load default Pokéball image: " + ex.getMessage());
                pokemonPreviewImage.setImage(null);
            }
        } else {
            displayMessage("Failed to deposit Pokémon. Please try again.");
        }
    }

    private void displayMessage(String message) {
        // Create custom dialog instead of using Alert
        Stage dialogStage = new Stage();
        dialogStage.initOwner(stage);
        dialogStage.setTitle("PC System");
        dialogStage.setResizable(false);

        // Main container
        BorderPane dialogPane = new BorderPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");
        dialogPane.setPadding(new Insets(20));

        // Message text
        Label messageLabel = new Label(message);
        messageLabel.setFont(pokemonFontSmall);
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(15));
        messageLabel.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Create a simple container for the message
        HBox contentBox = new HBox(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.getChildren().add(messageLabel);
        dialogPane.setCenter(contentBox);

        // Create a styled OK button
        Button okButton = createStyledButton("OK", "#303030"); // Dark gray
        okButton.setOnAction(e -> dialogStage.close());

        // Put the button in an HBox for centering
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        dialogPane.setBottom(buttonBox);

        // Set up the scene
        Scene dialogScene = new Scene(dialogPane, 300, 150);

        // Add enter key handler to the scene
        dialogScene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                okButton.fire();
            }
        });

        dialogStage.setScene(dialogScene);

        // Show the dialog and wait for it to be closed
        dialogStage.showAndWait();
    }

    public void show() {
        // Refresh the list views when showing the window
        populateListViews();
        stage.show();
    }
}
