package PlayerRelated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import PokemonLogic.Pokemon;
import WindowThings.PokeText_Adventure;
import WindowThings.mainWindow;
import javafx.application.Platform;
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
public class PokedexWindow {
    private Stage stage;
    private ListView<PokedexEntry> pokemonListView;
    private Label detailsLabel;
    private Font pokemonFont;
    private Font pokemonFontSmall;
    private ImageView pokemonImageView;
    private List<PokedexEntry> pokedexEntries;

    public PokedexWindow() {
        // Create the stage
        stage = new Stage();
        stage.setTitle("Pokédex");

        // Load the custom font
        try {
            pokemonFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 18);
            pokemonFontSmall = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 11);
        } catch (Exception e) {
            System.out.println("Could not load Pokémon font: " + e.getMessage());
            pokemonFont = Font.font("Arial", FontWeight.BOLD, 18);
            pokemonFontSmall = Font.font("Arial", 11);
        }

        // Make the Pokedex window dependent on the main window
        Stage mainStage = mainWindow.getPrimaryStage();
        if (mainStage != null) {
            stage.initOwner(mainStage);
        }

        // Register with main window
        WindowThings.mainWindow.registerWindow(stage);

        // Add proper close handler to ensure unregistration
        stage.setOnCloseRequest(event -> {
            WindowThings.mainWindow.unregisterWindow(stage);
        });

        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #d0d0d0);");

        // Create the title
        Label titleLabel = new Label("Pokédex");
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

        // Add drop shadow
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

        // Create the Pokemon list view with styling
        pokemonListView = new ListView<>();
        pokemonListView.setPrefHeight(400);
        pokemonListView.setPrefWidth(200);
        pokemonListView.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Replace the existing pokemonListView.setCellFactory call with this method
        // call
        setupListCellFactory();

        // Create details panel
        VBox detailsContainer = new VBox(10);
        detailsContainer.setPadding(new Insets(15));
        detailsContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #000000; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

        // Pokemon image view
        pokemonImageView = new ImageView();
        pokemonImageView.setFitHeight(150);
        pokemonImageView.setFitWidth(150);
        pokemonImageView.setPreserveRatio(true);

        // Set default image (Pokeball)
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/Pokeball.png"));
            pokemonImageView.setImage(defaultImage);
        } catch (Exception e) {
            System.out.println("Could not load default image: " + e.getMessage());
        }

        HBox imageContainer = new HBox(pokemonImageView);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(10));

        // Details text
        detailsLabel = new Label("Select a Pokémon to view details.");
        detailsLabel.setFont(pokemonFontSmall);
        detailsLabel.setWrapText(true);
        detailsLabel.setPadding(new Insets(15));
        detailsLabel.setMinHeight(100); // Add minimum height to ensure visibility
        detailsLabel.setStyle("-fx-background-color: #f0f0f0; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #aaaaaa; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");

        // Create a ScrollPane to contain the details label with proper configuration
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        scrollPane.setContent(detailsLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setMaxHeight(200); // Set maximum height to prevent expansion
        scrollPane.setPrefViewportWidth(300); // Set preferred viewport width
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

        // Fix the ScrollPane style to ensure content is visible
        scrollPane.setStyle("-fx-background-color: #f0f0f0; " +
                "-fx-background: #f0f0f0; " +
                "-fx-border-color: #aaaaaa; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5;");

        // Add padding to details container
        detailsContainer.setPadding(new Insets(15));
        detailsContainer.setSpacing(15); // Add spacing between elements
        detailsContainer.getChildren().addAll(imageContainer, scrollPane);

        // Create main content layout with list on left, details on right
        HBox contentLayout = new HBox(15);
        contentLayout.getChildren().addAll(pokemonListView, detailsContainer);
        HBox.setHgrow(detailsContainer, javafx.scene.layout.Priority.ALWAYS);
        // Force detailsContainer to use only its preferred size
        detailsContainer.setMaxWidth(350);
        detailsContainer.setPrefWidth(350);
        mainLayout.setCenter(contentLayout);

        // Create the close button
        Button closeButton = createStyledButton("Close", "#202020");
        closeButton.setOnAction(e -> {
            WindowThings.mainWindow.unregisterWindow(stage);
            stage.close();
        });

        // Button container
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        mainLayout.setBottom(buttonBox);

        // Add selection listener for Pokemon list
        pokemonListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updatePokemonDetails(newVal);
            }
        });

        // Load Pokedex entries
        loadPokedexEntries();

        // Set the scene
        Scene scene = new Scene(mainLayout, 600, 550);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private Button createStyledButton(String text, String baseColor) {
        Button button = new Button(text);
        button.setFont(pokemonFontSmall);

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

    private void loadPokedexEntries() {
        try {
            // Get list of caught Pokemon numbers
            Set<Integer> caughtPokemonNumbers = PokeText_Adventure.player.getPokedexCaught();

            // Read Pokemon data from JSON
            Pokemon dummyPokemon = new Pokemon("Bulbasaur", 5); // Just to access the readJsonFile method
            JSONArray pokemonJsonArray = dummyPokemon.readJsonFile("pokemon.json");

            pokedexEntries = new ArrayList<>();

            for (Object obj : pokemonJsonArray) {
                JSONObject pokemonJson = (JSONObject) obj;
                int number;
                Object numObj = pokemonJson.get("#");
                number = numObj != null
                        ? (numObj instanceof Integer ? (Integer) numObj : Integer.parseInt(numObj.toString()))
                        : 0;

                // Include all Pokémon with valid numbers, not just caught ones
                if (number > 0) {
                    String name = (String) pokemonJson.get("Name");
                    String type1 = (String) pokemonJson.get("Type 1");
                    String type2 = pokemonJson.get("Type 2") != null ? (String) pokemonJson.get("Type 2") : "";

                    String spritePath = (String) pokemonJson.get("Sprite");

                    // Extract Pokédex entry text
                    String pokedexEntry = pokemonJson.get("PokedexEntry") != null
                            ? (String) pokemonJson.get("PokedexEntry")
                            : "No data available.";

                    boolean caught = caughtPokemonNumbers.contains(number);

                    PokedexEntry entry = new PokedexEntry(
                            number, name, type1, type2, spritePath, caught, pokedexEntry);
                    pokedexEntries.add(entry);
                }
            }

            // Sort entries by number
            Collections.sort(pokedexEntries, Comparator.comparingInt(PokedexEntry::getNumber));

            // Add all entries to list view
            Platform.runLater(() -> {
                pokemonListView.getItems().setAll(pokedexEntries);
            });

        } catch (Exception e) {
            System.out.println("Error loading Pokedex entries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupListCellFactory() {
        pokemonListView.setCellFactory(list -> new ListCell<PokedexEntry>() {
            @Override
            protected void updateItem(PokedexEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Changed format from "#001" to "No.001" to avoid display issues
                    String displayText = String.format("No.%03d %s", item.getNumber(),
                            item.isCaught() ? item.getName() : "???");
                    setText(displayText);
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
    }

    private void updatePokemonDetails(PokedexEntry entry) {
        try {
            // Always load the actual Pokémon sprite
            Image pokemonImage = new Image(getClass().getResourceAsStream("/" + entry.getSpritePath()));
            pokemonImageView.setImage(pokemonImage);

            // If not caught, apply black silhouette effect
            if (!entry.isCaught()) {
                // Create a color adjust effect to make the sprite completely black
                javafx.scene.effect.ColorAdjust blackout = new javafx.scene.effect.ColorAdjust();
                blackout.setBrightness(-1.0); // Make it completely dark
                pokemonImageView.setEffect(blackout);
            } else {
                // Remove any effect for caught Pokémon
                pokemonImageView.setEffect(null);
            }
        } catch (Exception e) {
            System.out.println("Could not load pokemon image: " + e.getMessage());
            try {
                // Fallback to default image (Pokeball)
                Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/Pokeball.png"));
                pokemonImageView.setImage(defaultImage);
                pokemonImageView.setEffect(null);
            } catch (Exception ex) {
                System.out.println("Could not load default image: " + ex.getMessage());
            }
        }

        // Update details text based on caught status
        StringBuilder details = new StringBuilder();

        details.append(String.format("No. %03d\n", entry.getNumber()));

        if (entry.isCaught()) {
            // Show full details for caught Pokémon
            details.append("Name: ").append(entry.getName()).append("\n\n");
            details.append("Type: ").append(entry.getType1());

            if (entry.getType2() != null && !entry.getType2().isEmpty()) {
                details.append("/").append(entry.getType2());
            }

            details.append("\n\nStatus: CAUGHT");

            // Add the Pokédex entry
            details.append("\n\nPokédex Entry:\n");
            details.append(entry.getPokedexEntry());
        } else {
            // Show limited details for uncaught Pokémon
            details.append("Name: ???\n\n");
            details.append("This Pokémon has not been caught yet.\n");
            details.append("Catch it to record its data!");
        }

        detailsLabel.setText(details.toString());
    }

    public void show() {
        // Refresh data before showing window
        loadPokedexEntries();
        stage.show();
    }

    /**
     * Modified inner class to represent a Pokedex entry
     * Includes caught status and Pokédex entry
     */
    private static class PokedexEntry {
        private final int number;
        private final String name;
        private final String type1;
        private final String type2;
        private final String spritePath;
        private final boolean caught;
        private final String pokedexEntry;

        public PokedexEntry(int number, String name, String type1, String type2, String spritePath, boolean caught,
                String pokedexEntry) {
            this.number = number;
            this.name = name;
            this.type1 = type1;
            this.type2 = type2;
            this.spritePath = spritePath;
            this.caught = caught;
            this.pokedexEntry = pokedexEntry;
        }

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public String getType1() {
            return type1;
        }

        public String getType2() {
            return type2;
        }

        public String getSpritePath() {
            return spritePath;
        }

        public boolean isCaught() {
            return caught;
        }

        public String getPokedexEntry() {
            return pokedexEntry;
        }
    }
}
