package WindowThings;

import java.util.HashMap;
import java.util.Map;

import BattleLogic.Battle;
import Overworld.Buildings.OaksLab;
import Overworld.Buildings.PokemonCenter;
import Overworld.EncounterPool;
import Overworld.Route;
import Overworld.Routes.Route2North;
import Overworld.Routes.Route2South;
import Overworld.Routes.ViridianForest;
import Overworld.Town;
import Overworld.Towns.Pallet;
import Overworld.Towns.Pewter;
import PlayerRelated.PCWindow;
import PokemonLogic.Pokemon;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("unused")
public class exploreWindow {
    // UI Constants
    private static final int BUTTON_AREA_HEIGHT = 50;
    private static final double FIXED_HEIGHT = 400;
    private static final String BUTTON_STYLE_NORMAL = "-fx-background-color: white; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-text-fill: black; " +
            "-fx-border-color: #000000; " +
            "-fx-border-radius: 5; " +
            "-fx-border-width: 2px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);";
    private static final String BUTTON_STYLE_HOVER = "-fx-background-color: #f0f0f0; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-text-fill: black; " +
            "-fx-border-color: #000000; " +
            "-fx-border-radius: 5; " +
            "-fx-border-width: 2px; " +
            "-fx-scale-x: 1.03; " +
            "-fx-scale-y: 1.03; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);";
    private static final String BUTTON_STYLE_PRESSED = "-fx-background-color: #e0e0e0; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-text-fill: black; " +
            "-fx-border-color: #000000; " +
            "-fx-border-radius: 5; " +
            "-fx-border-width: 2px; " +
            "-fx-scale-x: 0.98; " +
            "-fx-scale-y: 0.98; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);";

    // Font cache
    private static Font pokemonFont = null;
    // Image cache
    private static final Map<String, Image> imageCache = new HashMap<>();

    private Stage stage;
    private ImageView townImageView;
    private Town currentTown;
    private BorderPane mainLayout;
    private boolean exitingPokemonCenter = false;
    private Label mapNameLabel;
    private SequentialTransition currentAnimation;

    public static Town playerCurrentTown;

    public exploreWindow(Town currentTown) {
        this.currentTown = currentTown;
        playerCurrentTown = currentTown;

        // Initialize Pokemon font once
        if (pokemonFont == null) {
            try {
                pokemonFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 14);
            } catch (Exception e) {
                System.out.println("Could not load Pokémon font: " + e.getMessage());
                pokemonFont = Font.font("Arial", FontWeight.BOLD, 14);
            }
        }

        setupStage();
        setupMainLayout();
        setupMapNameLabel();

        // Set up the window to display entry message when shown
        stage.setOnShown(e -> {
            currentTown.enter(PokeText_Adventure.player);
            updateButtonsForTown();
            // Animate the map name when entering a new area
            animateMapName(currentTown.getName());
        });

        // Automatically show the window upon construction
        stage.show();
    }

    private void setupStage() {
        stage = new Stage();
        stage.setTitle(currentTown.getName());

        // Make the explore window dependent on the main window
        Stage mainStage = mainWindow.getPrimaryStage();
        if (mainStage != null) {
            stage.initOwner(mainStage);
        }

        // Make the window non-closeable when user tries to close it directly
        stage.setOnCloseRequest(event -> {
            event.consume();
            System.out.println("This window cannot be closed directly. Use navigation buttons instead.");
        });

        // Register with main window
        mainWindow.registerWindow(stage);

        // We can't set userData here because scene doesn't exist yet
    }

    private void setupMainLayout() {
        mainLayout = new BorderPane();
        StackPane imageContainer = new StackPane();
        townImageView = new ImageView();
        imageContainer.getChildren().add(townImageView);
        mainLayout.setCenter(imageContainer);

        try {
            // Try to get image from cache first
            String imagePath = "/Maps/" + currentTown.getImageFile();
            Image townImage = getOrLoadImage(imagePath);
            townImageView.setImage(townImage);

            double aspectRatio = townImage.getWidth() / townImage.getHeight();
            double scaledWidth = FIXED_HEIGHT * aspectRatio;

            // Configure the image view
            townImageView.setFitHeight(FIXED_HEIGHT);
            townImageView.setFitWidth(scaledWidth);
            townImageView.setPreserveRatio(true);

            // Create scene with extra height for buttons
            Scene scene = new Scene(mainLayout, scaledWidth, FIXED_HEIGHT + BUTTON_AREA_HEIGHT);
            stage.setScene(scene);

            // Now we can safely tag this window as an exploreWindow using userData
            scene.getRoot().setUserData(this);

            // Ensure window matches the new dimensions exactly
            stage.sizeToScene();
        } catch (Exception e) {
            System.out.println("Error loading town image: " + e.getMessage());
            // Fallback dimensions if image fails to load
            Scene scene = new Scene(mainLayout, 300, 200);
            stage.setScene(scene);
        }
    }

    private Image getOrLoadImage(String path) {
        if (!imageCache.containsKey(path)) {
            try {
                imageCache.put(path, new Image(getClass().getResourceAsStream(path)));
            } catch (Exception e) {
                System.out.println("Error caching image " + path + ": " + e.getMessage());
                return null;
            }
        }
        return imageCache.get(path);
    }

    private void setupMapNameLabel() {
        mapNameLabel = new Label();
        mapNameLabel.setAlignment(Pos.CENTER);
        mapNameLabel.setMinWidth(200);
        mapNameLabel.setMinHeight(40);
        mapNameLabel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                "-fx-padding: 10; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2;");
        mapNameLabel.setTextFill(Color.WHITE);

        // Use cached Pokemon font if available
        if (pokemonFont != null) {
            mapNameLabel.setFont(Font.font(pokemonFont.getFamily(), FontWeight.BOLD, 18));
        } else {
            mapNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        }

        // Position it initially off-screen (above the visible area)
        mapNameLabel.setTranslateY(-100);

        // Add to the layout
        StackPane imageContainer = (StackPane) mainLayout.getCenter();
        StackPane.setAlignment(mapNameLabel, Pos.TOP_CENTER);
        imageContainer.getChildren().add(mapNameLabel);
    }

    /**
     * Animates the map name label to slide in and out
     */
    private void animateMapName(String mapName) {
        // Stop any existing animation to prevent conflicts
        if (currentAnimation != null && currentAnimation.getStatus() != javafx.animation.Animation.Status.STOPPED) {
            currentAnimation.stop();
            // Reset label position to ensure clean state
            mapNameLabel.setTranslateY(-100);
        }

        // Set the text to the current map name
        mapNameLabel.setText(mapName);

        // Create slide-in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), mapNameLabel);
        slideIn.setFromY(-100); // Start from above the visible area
        slideIn.setToY(20); // Move to visible position

        // Create pause animation (stay visible for 3 seconds)
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        // Create slide-out animation
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(600), mapNameLabel);
        slideOut.setFromY(20); // Start from visible position
        slideOut.setToY(-100); // Move back up off-screen

        // Combine the animations to run in sequence
        currentAnimation = new SequentialTransition(slideIn, pause, slideOut);

        // Make sure to reset label when animation completes
        currentAnimation.setOnFinished(event -> mapNameLabel.setTranslateY(-100));

        // Play the animation
        currentAnimation.play();
    }

    // Method to update buttons based on current town
    private void updateButtonsForTown() {
        // Clear any existing buttons
        mainLayout.setBottom(null);

        // Use a location-based approach to determine which buttons to show
        if (currentTown instanceof Overworld.Buildings.PlayerHome) {
            createPlayerHomeButtons();
        } else if (currentTown instanceof Pallet) {
            createPalletTownButtons();
        } else if (isRoute("Route 1")) {
            createRoute1Buttons();
        } else if (currentTown.getName().equals("Viridian City")) {
            createViridianCityButtons();
        } else if (isRoute("Route 22")) {
            createRoute22Buttons();
        } else if (isRoute("Route 2 South")) {
            createRoute2SouthButtons();
        } else if (isRoute("Viridian Forest")) {
            createViridianForestButtons();
        } else if (isRoute("Route 2 North")) {
            createRoute2NorthButtons();
        } else if (currentTown.getName().equals("Pewter City")) {
            createPewterCityButtons();
        } else if (currentTown.getName().equals("Pewter Gym")) {
            createPewterGymButtons();
        } else if (currentTown instanceof Overworld.Buildings.PokemonCenterBuilding) {
            createPokemonCenterButtons();
        } else if (currentTown instanceof Overworld.Buildings.OaksLab) {
            createOaksLabButtons();
        }
    }

    private boolean isRoute(String routeName) {
        return currentTown instanceof Route && currentTown.getName().equals(routeName);
    }

    private void createPlayerHomeButtons() {
        Button healButton = createTextButton("Rest", e -> {
            PokemonCenter pokemonCenter = currentTown.getPokemonCenter();
            pokemonCenter.healPokemon(PokeText_Adventure.player);
            WindowThings.mainWindow.appendToOutput(
                    "Mom: Good morning, sleepyhead! I made your favorite breakfast. Your Pokémon look well-rested too! Remember to call sometimes during your journey!");
        });
        healButton.setTooltip(new Tooltip("Rest in your bed to heal your Pokémon"));

        Button pcButton = createButtonWithIcon("/Icons/pc.png", "Access PC", e -> {
            PCWindow pcWindow = new PCWindow();
            pcWindow.show();
            WindowThings.mainWindow.appendToOutput("You access your personal computer.");
        });

        Button returnButton = createDirectionalButton("down", "Go To Pallet", e -> {
            Pallet palletTown = (Pallet) ((Overworld.Buildings.PlayerHome) currentTown).getParentTown();
            updateTown(palletTown);
        });

        addButtonsToLayout(healButton, pcButton, returnButton);
    }

    private void createPalletTownButtons() {
        Pallet pallet = (Pallet) currentTown;

        Button goHomeButton = createTextButton("Go Home", e -> {
            PokemonCenter pokemonCenter = currentTown.getPokemonCenter();
            if (pokemonCenter instanceof Town towns) {
                updateTown(towns);
            }
        });
        goHomeButton.setTooltip(new Tooltip("Return to your house"));

        Button oaksLabButton = createTextButton("Oak's Lab", e -> {
            OaksLab oaksLab = pallet.getOaksLab();
            updateTown(oaksLab);
            WindowThings.mainWindow.appendToOutput("You head to Professor Oak's Laboratory.");
        });
        oaksLabButton.setTooltip(new Tooltip("Visit Professor Oak's Laboratory"));

        Button routeButton = createDirectionalButton("up", "To Route 1", e -> {
            // Check if the player has any Pokémon before allowing them to leave Pallet Town
            if (PokeText_Adventure.player.getParty().isEmpty()) {
                // Warn the player that they need a Pokémon to leave town
                WindowThings.mainWindow.appendToOutput("Mom's voice echoes in your head: It's dangerous to go alone!");
                WindowThings.mainWindow.appendToOutput(
                        "You should visit Professor Oak's Laboratory first to get your starter Pokémon.");
            } else {
                // Player has at least one Pokémon, allow them to proceed
                Route route1 = pallet.getRoute1();
                updateTown(route1);
            }
        });

        addButtonsToLayout(goHomeButton, oaksLabButton, routeButton);
    }

    private void createRoute1Buttons() {
        Route route1 = (Route) currentTown;

        Button toViridianButton = createDirectionalButton("up", "To Viridian City", e -> {
            updateTown(route1.getDestination2());
        });

        Button toPalletButton = createDirectionalButton("down", "To Pallet Town", e -> {
            updateTown(route1.getDestination1());
        });

        Button encounterButton = createEncounterButton();

        addButtonsToLayout(toPalletButton, toViridianButton, encounterButton);
    }

    private void createViridianCityButtons() {
        Button pokeCenterButton = createButtonWithIcon("/Icons/Center.png", "Pokémon Center", e -> {
            PokemonCenter pokemonCenter = currentTown.getPokemonCenter();
            if (pokemonCenter instanceof Town pokemonCenterTown) {
                updateTown(pokemonCenterTown);
                WindowThings.mainWindow.appendToOutput("You enter the " + pokemonCenter.getName() + ".");
            }
        });

        Button toRoute1Button = createDirectionalButton("down", "To Route 1", e -> {
            Pallet pallet = new Pallet();
            updateTown(pallet.getRoute1());
        });

        Button toRoute22Button = createDirectionalButton("left", "To Route 22", e -> {
            if (currentTown instanceof Overworld.Towns.Viridian viridian) {
                updateTown(viridian.getRoute22());
            }
        });

        // Route 2 button with roadblock logic
        Button toRoute2Button = createDirectionalButton("up", "To Route 2", e -> {
            if (currentTown instanceof Overworld.Towns.Viridian viridian) {
                // Check if player has delivered Oak's Parcel
                if (PokeText_Adventure.player.hasDeliveredOaksParcel()) {
                    // Allow passage to Route 2
                    updateTown(viridian.getRoute2South());
                    WindowThings.mainWindow.appendToOutput("You head north toward Route 2.");
                } else {
                    // Implement the "sleeping man" roadblock
                    WindowThings.mainWindow.appendToOutput("A man is sprawled on the ground, blocking the path north.");
                    WindowThings.mainWindow.appendToOutput(
                            "Man: *Snore*... Hmmm... Can't... go through... I haven't had my coffee yet...");
                }
            }
        });

        addButtonsToLayout(pokeCenterButton, toRoute1Button, toRoute22Button, toRoute2Button);
    }

    private void createRoute22Buttons() {
        Route route22 = (Route) currentTown;

        Button toViridianButton = createDirectionalButton("right", "To Viridian City", e -> {
            updateTown(route22.getDestination1());
        });

        Button toIndigoPlateauButton = createDirectionalButton("left", "To Indigo Plateau", e -> {
            int badgeCount = PokeText_Adventure.player.getBadges().size();
            if (badgeCount >= 8) {
                WindowThings.mainWindow.appendToOutput(
                        "You show your " + badgeCount + " badges. The path to the Indigo Plateau opens!");
                // Later: Add code here to transition to the Indigo Plateau
            } else {
                WindowThings.mainWindow.appendToOutput(
                        "A guard blocks the way. 'Only trainers with all 8 Kanto Gym Badges may pass!' You need "
                                + (8 - badgeCount) + " more badge(s).");
            }
        });

        Button encounterButton = createEncounterButton();

        addButtonsToLayout(toViridianButton, toIndigoPlateauButton, encounterButton);
    }

    private void createRoute2SouthButtons() {
        Button toViridianButton = createDirectionalButton("down", "To Viridian City", e -> {
            updateTown(((Route) currentTown).getDestination1());
        });

        Button toForestButton = createDirectionalButton("up", "To Viridian Forest", e -> {
            if (currentTown instanceof Route2South route2South) {
                updateTown(route2South.getViridianForest());
                WindowThings.mainWindow.appendToOutput("You enter the dense Viridian Forest.");
            }
        });

        Button encounterButton = createEncounterButton();

        addButtonsToLayout(toViridianButton, toForestButton, encounterButton);
    }

    private void createViridianForestButtons() {
        Button toRoute2SouthButton = createDirectionalButton("down", "To Route 2 (South)", e -> {
            updateTown(((Route) currentTown).getDestination1());
            WindowThings.mainWindow.appendToOutput("You exit the forest to the south.");
        });

        // Check if all trainers are defeated before allowing exit to the north
        ViridianForest forest = (ViridianForest) currentTown;
        boolean allTrainersDefeated = forest.areAllTrainersDefeated();

        Button toRoute2NorthButton = createDirectionalButton("up", "To Route 2 (North)", e -> {
            if (forest.areAllTrainersDefeated()) {
                if (currentTown instanceof ViridianForest viridianForest) {
                    updateTown(viridianForest.getRoute2North());
                    WindowThings.mainWindow.appendToOutput("You exit the forest to the north.");
                }
            } else {
                int remaining = forest.getTotalTrainers() - forest.getCurrentTrainerIndex();
                WindowThings.mainWindow.appendToOutput(remaining + " more trainer(s) are blocking your path!");
            }
        });

        Button encounterButton = createEncounterButton();

        // Replace generic trainer button with an actual battle button
        Button trainerButton = createTextButton("Challenge Trainer", e -> {
            if (forest.getNextTrainer() != null) {
                forest.startTrainerBattle(PokeText_Adventure.player);
            } else {
                WindowThings.mainWindow.appendToOutput("You've defeated all the trainers in the forest!");
            }
        });
        trainerButton.setTooltip(new Tooltip("Challenge the next bug catcher"));

        // If there are no more trainers to battle, update the button text
        if (forest.getNextTrainer() == null) {
            trainerButton.setText("No More Trainers");
            trainerButton.setDisable(true);
        }

        addButtonsToLayout(toRoute2SouthButton, toRoute2NorthButton, encounterButton, trainerButton);
    }

    private void createRoute2NorthButtons() {
        Button toForestButton = createDirectionalButton("down", "To Viridian Forest", e -> {
            updateTown(((Route) currentTown).getDestination1());
            WindowThings.mainWindow.appendToOutput("You enter the dense Viridian Forest.");
        });

        Button toPewterCityButton = createDirectionalButton("up", "To Pewter City", e -> {
            if (currentTown instanceof Route2North route2North) {
                updateTown(route2North.getPewterCity());
                WindowThings.mainWindow.appendToOutput("You enter Pewter City, the City of Stone.");
            }
        });

        Button encounterButton = createEncounterButton();

        addButtonsToLayout(toForestButton, toPewterCityButton, encounterButton);
    }

    private void createPewterCityButtons() {
        Button pokeCenterButton = createButtonWithIcon("/Icons/Center.png", "Pokémon Center", e -> {
            PokemonCenter pokemonCenter = currentTown.getPokemonCenter();
            if (pokemonCenter instanceof Town pokemonCenterTown) {
                updateTown(pokemonCenterTown);
                WindowThings.mainWindow.appendToOutput("You enter the " + pokemonCenter.getName() + ".");
            }
        });

        Button toRoute2Button = createDirectionalButton("down", "To Route 2", e -> {
            if (currentTown instanceof Pewter pewter) {
                updateTown(pewter.getRoute2North());
                WindowThings.mainWindow.appendToOutput("You head south toward Route 2.");
            }
        });

        Button toGymButton = createButtonWithIcon("/Icons/BoulderBadge.png", "To Pewter Gym", e -> {
            if (currentTown instanceof Pewter pewter) {
                updateTown(pewter.getPewterGym());
                WindowThings.mainWindow.appendToOutput("You enter the Pewter Gym, ready to face the challenge within.");
            }
        });

        addButtonsToLayout(pokeCenterButton, toRoute2Button, toGymButton);
    }

    private void createPewterGymButtons() {
        Button exitButton = createDirectionalButton("down", "Exit Gym", e -> {
            if (currentTown instanceof Overworld.Buildings.PewterGym pewterGym) {
                updateTown(pewterGym.getParentTown());
                WindowThings.mainWindow.appendToOutput("You exit the Pewter Gym and return to Pewter City.");
            }
        });

        addButtonsToLayout(exitButton);
    }

    private void createPokemonCenterButtons() {
        Overworld.Buildings.PokemonCenterBuilding pokemonCenterBuilding = (Overworld.Buildings.PokemonCenterBuilding) currentTown;

        Button healButton = createTextButton("Heal Pokémon", e -> {
            pokemonCenterBuilding.healPokemon(PokeText_Adventure.player);
        });
        healButton.setTooltip(new Tooltip("Restore your Pokémon to full health"));

        Button pcButton = createButtonWithIcon("/Icons/pc.png", "Access PC", e -> {
            PCWindow pcWindow = new PCWindow();
            pcWindow.show();
            WindowThings.mainWindow.appendToOutput("You access the Pokémon Storage System.");
        });

        Button returnButton = createDirectionalButton("down", "Exit", e -> {
            Town parentTown = pokemonCenterBuilding.getParentTown();
            exitingPokemonCenter = true;
            updateTown(parentTown);
            WindowThings.mainWindow.appendToOutput("You exit the Pokémon Center.");
        });

        addButtonsToLayout(healButton, pcButton, returnButton);
    }

    private void createOaksLabButtons() {
        Button exitButton = createDirectionalButton("down", "Exit Lab", e -> {
            if (currentTown instanceof Overworld.Buildings.OaksLab oaksLab) {
                updateTown(oaksLab.getParentTown());
                WindowThings.mainWindow
                        .appendToOutput("You leave Professor Oak's Laboratory and return to Pallet Town.");
            }
        });

        Button talkButton = createTextButton("Talk to Oak", e -> {
            if (PokeText_Adventure.player.getParty().isEmpty()) {
                WindowThings.mainWindow.appendToOutput("Professor Oak: You should choose your starter Pokémon first!");

                // Show starter selection if player still doesn't have a Pokémon
                javafx.application.Platform.runLater(() -> {
                    StarterSelectionWindow starterWindow = new StarterSelectionWindow(PokeText_Adventure.player);
                    starterWindow.show();
                });
            } else if (PokeText_Adventure.player.hasOaksParcel()
                    && !PokeText_Adventure.player.hasDeliveredOaksParcel()) {
                // Player has the parcel to deliver
                WindowThings.mainWindow.appendToOutput("You hand OAK'S PARCEL to Professor Oak.");
                WindowThings.mainWindow
                        .appendToOutput("Professor Oak: Oh, this is the custom Poké Ball I ordered! Thank you!");
                WindowThings.mainWindow.appendToOutput("Professor Oak: By the way, how is your Pokédex coming along?");
                WindowThings.mainWindow.appendToOutput(
                        "Professor Oak: There are many Pokémon in the Viridian Forest north of Viridian City.");
                WindowThings.mainWindow.appendToOutput("Professor Oak: You should go explore there next!");

                // Mark the parcel as delivered
                PokeText_Adventure.player.setHasOaksParcel(false);
                PokeText_Adventure.player.setDeliveredOaksParcel(true);
            } else {
                WindowThings.mainWindow
                        .appendToOutput("Professor Oak: How is your journey going? Have you caught any new Pokémon?");
            }
        });

        addButtonsToLayout(exitButton, talkButton);
    }

    private Button createEncounterButton() {
        return createButtonWithIcon("/Icons/Grass.png", "Look for Pokémon", e -> {
            Pokemon wildPokemon = EncounterPool.getRandomEncounter(currentTown.getName());

            if (wildPokemon != null) {
                WindowThings.mainWindow.appendToOutput("A wild " + wildPokemon.getName() + " appeared!");

                javafx.application.Platform.runLater(() -> {
                    try {
                        if (PokeText_Adventure.player.getCurrentPokemon() == null) {
                            Pokemon firstPokemon = PokeText_Adventure.player.getFirstPokemon();

                            if (firstPokemon != null) {
                                PokeText_Adventure.player.setCurrentPokemon(firstPokemon);
                            } else {
                                WindowThings.mainWindow.appendToOutput("You don't have any Pokémon to battle with!");
                                return;
                            }
                        }

                        new Battle(PokeText_Adventure.player.getCurrentPokemon(),
                                wildPokemon,
                                PokeText_Adventure.player,
                                true);
                    } catch (Exception ex) {
                        System.out.println("Error starting battle: " + ex.getMessage());
                    }
                });
            } else {
                WindowThings.mainWindow.appendToOutput("No Pokémon found!");
            }
        });
    }

    private Button createTextButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        applyButtonStyle(button);
        return button;
    }

    private Button createButtonWithIcon(String iconPath, String tooltip,
            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button();
        button.setTooltip(new Tooltip(tooltip));
        button.setOnAction(action);

        try {
            Image icon = getOrLoadImage(iconPath);
            if (icon != null) {
                ImageView iconView = new ImageView(icon);
                iconView.setFitHeight(19);
                iconView.setFitWidth(19);
                iconView.setPreserveRatio(true);
                button.setGraphic(iconView);
            } else {
                button.setText(tooltip);
                applyButtonStyle(button);
                return button;
            }

            button.setStyle(BUTTON_STYLE_NORMAL);

            button.setOnMouseEntered(e -> button.setStyle(BUTTON_STYLE_HOVER));
            button.setOnMouseExited(e -> button.setStyle(BUTTON_STYLE_NORMAL));
            button.setOnMousePressed(e -> button.setStyle(BUTTON_STYLE_PRESSED));

        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
            button.setText(tooltip);
            applyButtonStyle(button);
        }

        return button;
    }

    private Button createDirectionalButton(String direction, String tooltip,
            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        // Direction should be "up", "down", "left", or "right"
        return createButtonWithIcon("/Icons/" + direction + ".png", tooltip, action);
    }

    private void addButtonsToLayout(Button... buttons) {
        HBox buttonBar = new HBox(10);
        buttonBar.getChildren().addAll(buttons);
        buttonBar.setAlignment(Pos.CENTER);

        StackPane buttonContainer = new StackPane(buttonBar);
        buttonContainer.setPadding(new Insets(10));
        buttonContainer.setStyle("-fx-background-color: transparent;");
        mainLayout.setBottom(buttonContainer);
    }

    // Keep this method for compatibility with existing code
    public void show() {
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    public void updateTown(Town newTown) {
        // If we're closing this window due to navigation to another town,
        // we should unregister it when appropriate
        if (newTown == null) {
            mainWindow.unregisterWindow(stage);
            stage.close();
            return;
        }

        this.currentTown = newTown;
        playerCurrentTown = newTown;

        stage.setTitle(newTown.getName());
        try {
            Image newImage = getOrLoadImage("/Maps/" + newTown.getImageFile());
            townImageView.setImage(newImage);

            double aspectRatio = newImage.getWidth() / newImage.getHeight();
            double scaledWidth = FIXED_HEIGHT * aspectRatio;

            // Configure the image view
            townImageView.setFitHeight(FIXED_HEIGHT);
            townImageView.setFitWidth(scaledWidth);
            townImageView.setPreserveRatio(true);

            // Update stage size
            stage.setWidth(scaledWidth);
            stage.setHeight(FIXED_HEIGHT + BUTTON_AREA_HEIGHT);
            stage.sizeToScene();

            // Pass the exiting flag to the enter method
            newTown.enter(PokeText_Adventure.player, exitingPokemonCenter);
            exitingPokemonCenter = false;

            // Trigger the map name animation
            animateMapName(newTown.getName());
            updateButtonsForTown();
        } catch (Exception e) {
            System.out.println("Error updating town image: " + e.getMessage());
        }
    }

    private void applyButtonStyle(Button button) {
        // Use cached Pokemon font if available
        if (pokemonFont != null) {
            button.setFont(pokemonFont);
        }

        button.setStyle(BUTTON_STYLE_NORMAL);
        button.setOnMouseEntered(e -> button.setStyle(BUTTON_STYLE_HOVER));
        button.setOnMouseExited(e -> button.setStyle(BUTTON_STYLE_NORMAL));
        button.setOnMousePressed(e -> button.setStyle(BUTTON_STYLE_PRESSED));
    }

    // Add method to properly close the window
    public void close() {
        mainWindow.unregisterWindow(stage);
        stage.close();
    }

    // Add a helper method to identify this window type
    public static boolean isExploreWindow(Stage window) {
        if (window == null || window.getScene() == null || window.getScene().getRoot() == null) {
            return false;
        }
        return window.getScene().getRoot().getUserData() instanceof exploreWindow;
    }
}