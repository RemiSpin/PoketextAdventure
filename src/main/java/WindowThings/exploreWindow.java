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
import PokemonLogic.Pokemon;
import Utils.MusicManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
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
    private static final String BUTTON_STYLE_DISABLED = "-fx-background-color: #d3d3d3; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-text-fill: grey; " +
            "-fx-border-color: #000000; " +
            "-fx-border-radius: 5; " +
            "-fx-border-width: 2px;";

    // Direction enum for animation
    private enum Direction {
        NORTH, SOUTH, EAST, WEST, NONE
    }

    // Animation type enum
    private enum AnimationType {
        DIRECTIONAL, FADE, NONE
    }

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
    private HBox currentButtonBar;
    private boolean isAnimating = false;

    public static Town playerCurrentTown;

    // Store ViridianForest instance for saving/loading game state
    public static ViridianForest viridianForest;

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
            // Play location music for the initial location
            MusicManager.getInstance().playLocationMusic(currentTown.getName());
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
    }

    private void setupMainLayout() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: black;"); // Set the main layout background to black

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: black;"); // Set the image container background to black

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

        // Set the animating flag
        isAnimating = true;

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
        currentAnimation.setOnFinished(event -> {
            mapNameLabel.setTranslateY(-100);
            isAnimating = false; // Clear the animating flag when done
            updateButtonStates(); // Update button styles after animation
        });

        // Play the animation
        currentAnimation.play();
    }

    /**
     * Animates a transition between two town images based on the direction of
     * movement
     */
    private void animateDirectionalTransition(Town newTown, Direction direction) {
        try {
            // Set the animation flag to prevent button clicks
            isAnimating = true;
            updateButtonStates(); // Update button styles
            // Create new ImageView for the next town
            ImageView nextTownView = new ImageView();
            Image newImage = getOrLoadImage("/Maps/" + newTown.getImageFile());
            nextTownView.setImage(newImage);

            double aspectRatio = newImage.getWidth() / newImage.getHeight();
            double scaledWidth = FIXED_HEIGHT * aspectRatio;

            nextTownView.setFitHeight(FIXED_HEIGHT);
            nextTownView.setFitWidth(scaledWidth);
            nextTownView.setPreserveRatio(true);

            // Position the new image view based on the direction
            switch (direction) {
                case NORTH:
                    nextTownView.setTranslateY(-FIXED_HEIGHT);
                    break;
                case SOUTH:
                    nextTownView.setTranslateY(FIXED_HEIGHT);
                    break;
                case EAST:
                    nextTownView.setTranslateX(scaledWidth);
                    break;
                case WEST:
                    nextTownView.setTranslateX(-scaledWidth);
                    break;
                default:
                    // No specific direction, just replace the image
                    townImageView.setImage(newImage);
                    isAnimating = false; // Clear flag since we're not actually animating
                    updateButtonStates(); // Update button styles
                    return;
            }

            // Add the new view to the stack pane
            StackPane imageContainer = (StackPane) mainLayout.getCenter();
            imageContainer.getChildren().add(nextTownView);

            // Create animations
            double duration = 600; // animation duration in milliseconds

            // Animation for current town to slide out
            TranslateTransition slideOutCurrent = new TranslateTransition(Duration.millis(duration), townImageView);
            switch (direction) {
                case NORTH:
                    slideOutCurrent.setToY(FIXED_HEIGHT);
                    break;
                case SOUTH:
                    slideOutCurrent.setToY(-FIXED_HEIGHT);
                    break;
                case EAST:
                    slideOutCurrent.setToX(-scaledWidth);
                    break;
                case WEST:
                    slideOutCurrent.setToX(scaledWidth);
                    break;
                default:
                    break;
            }

            // Animation for new town to slide in
            TranslateTransition slideInNext = new TranslateTransition(Duration.millis(duration), nextTownView);
            slideInNext.setToX(0);
            slideInNext.setToY(0);

            // Play animations simultaneously
            slideOutCurrent.play();
            slideInNext.play();

            // Clean up after animation completes and trigger map name animation
            slideInNext.setOnFinished(e -> {
                townImageView.setImage(newImage);
                townImageView.setTranslateX(0);
                townImageView.setTranslateY(0);
                imageContainer.getChildren().remove(nextTownView);
                // Trigger map name animation *after* the transition is complete
                animateMapName(newTown.getName());
                // Update buttons after transition
                updateButtonsForTown();
            });
        } catch (Exception e) {
            System.out.println("Error during animation: " + e.getMessage());
            e.printStackTrace();

            // Make sure to clear the animation flag in case of error
            isAnimating = false;

            // Fallback to direct image update if animation fails
            try {
                townImageView.setImage(getOrLoadImage("/Maps/" + newTown.getImageFile()));
                animateMapName(newTown.getName()); // Still try to animate name
                updateButtonsForTown(); // Update buttons on fallback
            } catch (Exception ex) {
                System.out.println("Error updating town image: " + ex.getMessage());
            }
        }
    }

    /**
     * Determines if a location is considered a building (indoor location)
     */
    private boolean isBuilding(Town town) {
        return town instanceof Overworld.Buildings.PokemonCenterBuilding ||
                town instanceof Overworld.Buildings.OaksLab ||
                town instanceof Overworld.Buildings.PlayerHome ||
                town instanceof Overworld.Buildings.PewterGym;
    }

    /**
     * Performs a fade transition between the current town and the new town
     */
    private void animateFadeTransition(Town newTown) {
        try {
            // Set the animation flag to prevent button clicks
            isAnimating = true;
            updateButtonStates(); // Update button styles
            // 1. Load new image
            Image newImage = getOrLoadImage("/Maps/" + newTown.getImageFile());
            if (newImage == null) {
                isAnimating = false; // Clear flag if we're exiting early
                updateButtonStates(); // Update button styles
                throw new Exception("Failed to load image for " + newTown.getName());
            }

            // 2. Prepare Fade Out
            FadeTransition imageFadeOut = new FadeTransition(Duration.millis(400), townImageView);
            imageFadeOut.setFromValue(1.0);
            imageFadeOut.setToValue(0.0);

            FadeTransition buttonFadeOut = new FadeTransition(Duration.millis(300));
            if (currentButtonBar != null && currentButtonBar.getOpacity() > 0) {
                buttonFadeOut.setNode(currentButtonBar);
                buttonFadeOut.setFromValue(currentButtonBar.getOpacity());
                buttonFadeOut.setToValue(0.0);
            } else {
                // No buttons or already invisible, make fade-out instant
                buttonFadeOut.setDuration(Duration.millis(1));
            }

            ParallelTransition fadeOutParallel = new ParallelTransition(imageFadeOut, buttonFadeOut);

            // 3. Prepare Fade In
            FadeTransition imageFadeIn = new FadeTransition(Duration.millis(400), townImageView);
            imageFadeIn.setFromValue(0.0);
            imageFadeIn.setToValue(1.0);

            FadeTransition buttonFadeIn = new FadeTransition(Duration.millis(400));
            buttonFadeIn.setFromValue(0.0);
            buttonFadeIn.setToValue(1.0);
            // buttonFadeIn node will be set after new buttons are created

            ParallelTransition fadeInParallel = new ParallelTransition(imageFadeIn, buttonFadeIn);

            // 4. Intermediate Action (Change content while invisible)
            // Use a PauseTransition with an onFinished handler to perform actions
            PauseTransition changeContentAction = new PauseTransition(Duration.millis(1));
            changeContentAction.setOnFinished(e -> {
                // Change image
                townImageView.setImage(newImage);

                // Remove old buttons container from layout
                if (mainLayout.getBottom() != null) {
                    mainLayout.setBottom(null);
                }

                // Create new buttons (they are created with opacity 0 by addButtonsToLayout)
                createButtonsForCurrentTown(); // This sets currentButtonBar and adds it to layout

                // Set the target for the button fade-in animation
                if (currentButtonBar != null) {
                    buttonFadeIn.setNode(currentButtonBar);
                } else {
                    // If no buttons, make the button fade-in part instant/noop
                    buttonFadeIn.setDuration(Duration.millis(1));
                }
            });

            // 5. Create Sequence
            SequentialTransition fadeSequence = new SequentialTransition(
                    fadeOutParallel,
                    changeContentAction,
                    fadeInParallel);

            // 6. Set final action
            fadeSequence.setOnFinished(e -> {
                // Ensure final state is correct (opacity 1) in case animation is
                // skipped/interrupted
                townImageView.setOpacity(1.0);
                if (currentButtonBar != null) {
                    currentButtonBar.setOpacity(1.0);
                }
                // Trigger map name animation *after* the fade-in is complete
                animateMapName(newTown.getName());
            });

            // 7. Play
            fadeSequence.play();

        } catch (Exception e) {
            System.out.println("Error during fade animation: " + e.getMessage());
            e.printStackTrace();

            // Make sure to clear the animation flag in case of error
            isAnimating = false;
            updateButtonStates();

            // Fallback to direct update if animation fails
            try {
                townImageView.setImage(getOrLoadImage("/Maps/" + newTown.getImageFile()));
                mainLayout.setBottom(null); // Clear old buttons
                createButtonsForCurrentTown(); // Create new buttons
                // Manually make buttons visible in fallback
                if (currentButtonBar != null) {
                    currentButtonBar.setOpacity(1.0);
                }
                animateMapName(newTown.getName()); // Still try to animate name
            } catch (Exception ex) {
                System.out.println("Error updating town image during fallback: " + ex.getMessage());
            }
        }
    }

    // Original updateTown method as a convenience method with no animation
    public void updateTown(Town newTown) {
        updateTown(newTown, Direction.NONE);
    }

    // Enhanced updateTown method with direction parameter
    public void updateTown(Town newTown, Direction direction) {
        updateTown(newTown, direction, determineAnimationType(newTown));
    }

    // Helper method to determine the appropriate animation type
    private AnimationType determineAnimationType(Town newTown) {
        // If either current or new location is a building, use fade
        if (currentTown != null && (isBuilding(currentTown) || isBuilding(newTown))) {
            return AnimationType.FADE;
        }

        // Otherwise use directional if a direction is provided
        return AnimationType.DIRECTIONAL;
    }

    // Full updateTown method with all parameters
    public void updateTown(Town newTown, Direction direction, AnimationType animationType) {
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

        // Play location music
        MusicManager.getInstance().playLocationMusic(newTown.getName());

        // Choose animation based on type
        switch (animationType) {
            case DIRECTIONAL:
                if (direction != Direction.NONE) {
                    animateDirectionalTransition(newTown, direction);
                }
                break;

            case FADE:
                animateFadeTransition(newTown);
                break;

            case NONE:
            default:
                try {
                    // Set the animation flag temporarily to prevent button spam
                    setTemporaryAnimationLock();

                    townImageView.setImage(getOrLoadImage("/Maps/" + newTown.getImageFile()));
                    animateMapName(newTown.getName()); // Trigger the map name animation
                    updateButtonsForTown(); // Update buttons for no animation case
                } catch (Exception e) {
                    System.out.println("Error updating town image: " + e.getMessage());
                    // Clear the animation flag in case of error
                    isAnimating = false;
                    updateButtonStates();
                }
                break;
        }

        try {
            // Update stage size
            Image newImage = getOrLoadImage("/Maps/" + newTown.getImageFile());
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
        } catch (Exception e) {
            System.out.println("Error updating window size: " + e.getMessage());
        }

        // Set flag to true when coming from Oak's Lab to Pallet Town
        boolean fromExternalLocation = false;
        if (newTown instanceof Pallet && currentTown instanceof Overworld.Buildings.OaksLab) {
            fromExternalLocation = true;
        }

        // Pass the fromExternalLocation flag to the enter method
        newTown.enter(PokeText_Adventure.player, fromExternalLocation || exitingPokemonCenter);
        exitingPokemonCenter = false;
    }

    // Method to update buttons based on current town
    private void updateButtonsForTown() {

        Runnable createAndFadeInButtons = () -> {
            mainLayout.setBottom(null); // Clear previous buttons container first
            createButtonsForCurrentTown(); // Creates buttons with opacity 0 and adds to layout

            // Now, fade in the newly created buttons
            if (currentButtonBar != null) {
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), currentButtonBar);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
        };

        // Animate button area fade-out before clearing, only if buttons exist and are
        // visible
        if (currentButtonBar != null && currentButtonBar.getOpacity() > 0) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentButtonBar);
            fadeOut.setFromValue(currentButtonBar.getOpacity()); // Fade from current opacity
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                createAndFadeInButtons.run(); // Create and fade in new buttons after fade-out
            });
            fadeOut.play();
        } else {
            // If no buttons or already faded out, just create and fade in new ones directly
            createAndFadeInButtons.run();
        }
    }

    // Helper method to create appropriate buttons based on current location
    private void createButtonsForCurrentTown() {
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
        });
        healButton.setTooltip(new Tooltip("Rest in your bed to heal your Pokémon"));

        Button pcButton = createButtonWithIcon("/Icons/pc.png", "Access PC", e -> {
            PCWindow pcWindow = new PCWindow();
            pcWindow.show();
            WindowThings.mainWindow.appendToOutput("You access your personal computer.");
        });

        Button returnButton = createDirectionalButton("down", "Go To Pallet", e -> {
            Pallet palletTown = (Pallet) ((Overworld.Buildings.PlayerHome) currentTown).getParentTown();
            updateTown(palletTown, Direction.NONE, AnimationType.FADE);
        });

        addButtonsToLayout(healButton, pcButton, returnButton);
    }

    private void createPalletTownButtons() {
        Pallet pallet = (Pallet) currentTown;

        Button goHomeButton = createTextButton("Go Home", e -> {
            PokemonCenter pokemonCenter = currentTown.getPokemonCenter();
            if (pokemonCenter instanceof Town towns) {
                updateTown(towns, Direction.NONE, AnimationType.FADE);
            }
        });
        goHomeButton.setTooltip(new Tooltip("Return to your house"));

        Button oaksLabButton = createTextButton("Oak's Lab", e -> {
            OaksLab oaksLab = pallet.getOaksLab();
            updateTown(oaksLab, Direction.NONE, AnimationType.FADE);
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
                updateTown(route1, Direction.NORTH);
            }
        });

        addButtonsToLayout(goHomeButton, oaksLabButton, routeButton);
    }

    private void createRoute1Buttons() {
        Route route1 = (Route) currentTown;

        Button toViridianButton = createDirectionalButton("up", "To Viridian City", e -> {
            updateTown(route1.getDestination2(), Direction.NORTH);
        });

        Button toPalletButton = createDirectionalButton("down", "To Pallet Town", e -> {
            updateTown(route1.getDestination1(), Direction.SOUTH);
        });

        Button encounterButton = createEncounterButton();

        addButtonsToLayout(toPalletButton, toViridianButton, encounterButton);
    }

    private void createViridianCityButtons() {
        Button pokeCenterButton = createButtonWithIcon("/Icons/Center.png", "Pokémon Center", e -> {
            PokemonCenter pokemonCenter = currentTown.getPokemonCenter();
            if (pokemonCenter instanceof Town pokemonCenterTown) {
                updateTown(pokemonCenterTown, Direction.NONE, AnimationType.FADE);
            }
        });

        Button toRoute1Button = createDirectionalButton("down", "To Route 1", e -> {
            Pallet pallet = new Pallet();
            updateTown(pallet.getRoute1(), Direction.SOUTH);
        });

        Button toRoute22Button = createDirectionalButton("left", "To Route 22", e -> {
            if (currentTown instanceof Overworld.Towns.Viridian viridian) {
                updateTown(viridian.getRoute22(), Direction.WEST);
            }
        });

        // Route 2 button with roadblock logic
        Button toRoute2Button = createDirectionalButton("up", "To Route 2", e -> {
            if (currentTown instanceof Overworld.Towns.Viridian viridian) {
                // Check if player has delivered Oak's Parcel
                if (PokeText_Adventure.player.hasDeliveredOaksParcel()) {
                    // Allow passage to Route 2
                    updateTown(viridian.getRoute2South(), Direction.NORTH);
                } else {
                    // Implement the "sleeping man" roadblock
                    WindowThings.mainWindow.appendToOutput("A man is sprawled on the ground, blocking the path north.");
                    WindowThings.mainWindow.appendToOutput(
                            "Man: ZZZZZ... Hmmm... Can't... go through... I haven't had my coffee yet...", "grey");
                }
            }
        });

        addButtonsToLayout(pokeCenterButton, toRoute1Button, toRoute22Button, toRoute2Button);
    }

    private void createRoute22Buttons() {
        Route route22 = (Route) currentTown;

        Button toViridianButton = createDirectionalButton("right", "To Viridian City", e -> {
            updateTown(route22.getDestination1(), Direction.EAST);
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
            updateTown(((Route) currentTown).getDestination1(), Direction.SOUTH);
        });

        Button toForestButton = createDirectionalButton("up", "To Viridian Forest", e -> {
            if (currentTown instanceof Route2South route2South) {
                ViridianForest forest = route2South.getViridianForest();
                viridianForest = forest; // Store reference to the forest
                updateTown(forest, Direction.NORTH);
            }
        });

        Button encounterButton = createEncounterButton();

        addButtonsToLayout(toViridianButton, toForestButton, encounterButton);
    }

    private void createViridianForestButtons() {
        Button toRoute2SouthButton = createDirectionalButton("down", "To Route 2 (South)", e -> {
            updateTown(((Route) currentTown).getDestination1(), Direction.SOUTH);
            WindowThings.mainWindow.appendToOutput("You exit the forest to the south.");
        });

        // Check if all trainers are defeated before allowing exit to the north
        ViridianForest forest = (ViridianForest) currentTown;
        boolean allTrainersDefeated = forest.areAllTrainersDefeated();

        Button toRoute2NorthButton = createDirectionalButton("up", "To Route 2 (North)", e -> {
            if (forest.areAllTrainersDefeated()) {
                if (currentTown instanceof ViridianForest viridianForest) {
                    updateTown(viridianForest.getRoute2North(), Direction.NORTH);
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
            updateTown(((Route) currentTown).getDestination1(), Direction.SOUTH);
            WindowThings.mainWindow.appendToOutput("You enter the dense Viridian Forest.");
        });

        Button toPewterCityButton = createDirectionalButton("up", "To Pewter City", e -> {
            if (currentTown instanceof Route2North route2North) {
                updateTown(route2North.getPewterCity(), Direction.NORTH);
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
                updateTown(pokemonCenterTown, Direction.NONE, AnimationType.FADE);
            }
        });

        Button toRoute2Button = createDirectionalButton("down", "To Route 2", e -> {
            if (currentTown instanceof Pewter pewter) {
                updateTown(pewter.getRoute2North(), Direction.SOUTH);
                WindowThings.mainWindow.appendToOutput("You head south toward Route 2.");
            }
        });

        Button toGymButton = createButtonWithIcon("/Icons/BoulderBadge.png", "To Pewter Gym", e -> {
            if (currentTown instanceof Pewter pewter) {
                updateTown(pewter.getPewterGym(), Direction.NONE, AnimationType.FADE);
                WindowThings.mainWindow.appendToOutput("You enter the Pewter Gym, ready to face the challenge within.");
            }
        });

        addButtonsToLayout(pokeCenterButton, toRoute2Button, toGymButton);
    }

    private void createPewterGymButtons() {
        Overworld.Buildings.PewterGym pewterGym = (Overworld.Buildings.PewterGym) currentTown;

        Button exitButton = createDirectionalButton("down", "Exit Gym", e -> {
            updateTown(pewterGym.getParentTown(), Direction.NONE, AnimationType.FADE);
        });

        // Add button for challenging trainers
        Button challengeButton = createTextButton("Challenge Trainer", e -> {
            if (pewterGym.getNextTrainer() != null) {
                pewterGym.startTrainerBattle(PokeText_Adventure.player);
            } else {
                WindowThings.mainWindow.appendToOutput("You've already defeated all the trainers in this gym!");
            }
        });
        challengeButton.setTooltip(new Tooltip("Challenge the next trainer in the gym"));

        if (pewterGym.getNextTrainer() == null) {
            challengeButton.setText("No More Trainers");
            challengeButton.setDisable(true);
        }

        addButtonsToLayout(exitButton, challengeButton);
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
            updateTown(parentTown, Direction.NONE, AnimationType.FADE);
            WindowThings.mainWindow.appendToOutput("You exit the Pokémon Center.");
        });

        addButtonsToLayout(healButton, pcButton, returnButton);
    }

    private void createOaksLabButtons() {
        Button exitButton = createDirectionalButton("down", "Exit Lab", e -> {
            if (currentTown instanceof Overworld.Buildings.OaksLab oaksLab) {
                updateTown(oaksLab.getParentTown(), Direction.NONE, AnimationType.FADE);
            }
        });

        Button talkButton = createTextButton("Talk to Oak", e -> {
            if (PokeText_Adventure.player.getParty().isEmpty()) {
                WindowThings.mainWindow.appendToOutput("Professor Oak: You should choose your starter Pokémon first!",
                        "blue");

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
                        .appendToOutput("Professor Oak: Oh, this is the custom Poké Ball I ordered! Thank you!",
                                "blue");
                WindowThings.mainWindow.appendToOutput("Professor Oak: By the way, how is your Pokédex coming along?",
                        "blue");
                WindowThings.mainWindow.appendToOutput(
                        "Professor Oak: There are many Pokémon in the Viridian Forest north of Viridian City.", "blue");
                WindowThings.mainWindow.appendToOutput("Professor Oak: You should go explore there next!", "blue");

                // Mark the parcel as delivered
                PokeText_Adventure.player.setHasOaksParcel(false);
                PokeText_Adventure.player.setDeliveredOaksParcel(true);
            } else {
                WindowThings.mainWindow
                        .appendToOutput("Professor Oak: How is your journey going? Have you caught any new Pokémon?",
                                "blue");
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

                        String battleLocation = "field";
                        if (currentTown != null && currentTown.getName().toLowerCase().contains("forest")) {
                            battleLocation = "forest";
                        }
                        new Battle(PokeText_Adventure.player.getCurrentPokemon(),
                                wildPokemon,
                                PokeText_Adventure.player,
                                true,
                                null,
                                battleLocation);
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

        // Wrap the action in a check for animation state
        button.setOnAction(e -> {
            if (!isAnimating) {
                // Set the temporary animation lock to prevent rapid clicking
                setTemporaryAnimationLock();
                // Execute the original action
                action.handle(e);
            }
        });

        applyButtonStyle(button);
        return button;
    }

    private Button createButtonWithIcon(String iconPath, String tooltip,
            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button();
        button.setTooltip(new Tooltip(tooltip));

        // Wrap the action in a check for animation state
        button.setOnAction(e -> {
            if (!isAnimating) {
                // Set the temporary animation lock to prevent rapid clicking
                setTemporaryAnimationLock();
                // Execute the original action
                action.handle(e);
            }
        });

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
        return createButtonWithIcon("/Icons/" + direction + ".png", tooltip, action);
    }

    private void addButtonsToLayout(Button... buttons) {
        HBox buttonBar = new HBox(10);
        buttonBar.getChildren().addAll(buttons);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setOpacity(0.0);

        StackPane buttonContainer = new StackPane(buttonBar);
        buttonContainer.setPadding(new Insets(10));
        buttonContainer.setStyle("-fx-background-color: transparent;");

        currentButtonBar = buttonBar; // Track the current button bar
        mainLayout.setBottom(buttonContainer); // Add the container (with invisible buttons) to the layout
    }

    // Keep this method for compatibility with existing code
    public void show() {
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    private void applyButtonStyle(Button button) {
        // Use cached Pokemon font if available
        if (pokemonFont != null) {
            button.setFont(pokemonFont);
        }

        button.setStyle(BUTTON_STYLE_NORMAL);
        button.setOnMouseEntered(e -> {
            if (!isAnimating) {
                button.setStyle(BUTTON_STYLE_HOVER);
            }
        });
        button.setOnMouseExited(e -> button.setStyle(BUTTON_STYLE_NORMAL));
        button.setOnMousePressed(e -> {
            if (!isAnimating) {
                button.setStyle(BUTTON_STYLE_PRESSED);
            }
        });
    }

    /**
     * Updates the visual state of all buttons based on whether an animation is in
     * progress
     */
    private void updateButtonStates() {
        if (currentButtonBar == null) {
            return;
        }

        for (javafx.scene.Node node : currentButtonBar.getChildren()) {
            if (node instanceof Button button) {
                if (isAnimating) {
                    button.setStyle(BUTTON_STYLE_DISABLED);
                    button.setCursor(javafx.scene.Cursor.WAIT); // Change cursor to wait during animation
                } else {
                    button.setStyle(BUTTON_STYLE_NORMAL);
                    button.setCursor(javafx.scene.Cursor.HAND); // Reset cursor to hand when not animating
                }
            }
        }
    }

    /**
     * Sets a temporary animation lock for a short duration to prevent rapid button
     * clicks
     */
    private void setTemporaryAnimationLock() {
        // Set the animation flag
        isAnimating = true;
        updateButtonStates();

        // Create a delayed action to clear the flag after 1 second
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> {
            isAnimating = false;
            updateButtonStates();
        });
        delay.play();
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