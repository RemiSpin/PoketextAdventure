package WindowThings;

import BattleLogic.Battle;
import Overworld.Buildings.PokemonCenter;
import Overworld.EncounterPool;
import Overworld.Route;
import Overworld.Town;
import Overworld.Towns.Pallet;
import PlayerRelated.PCWindow;
import PokemonLogic.Pokemon;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@SuppressWarnings("unused")

public class exploreWindow {
    private Stage stage;
    private ImageView townImageView;
    private Town currentTown;
    private BorderPane mainLayout;
    // Add a flag to track if we're exiting from a Pokemon Center
    private boolean exitingPokemonCenter = false;

    // Add a constant for button area height
    private static final int BUTTON_AREA_HEIGHT = 50;

    public static Town playerCurrentTown;

    public exploreWindow(Town currentTown) {
        this.currentTown = currentTown;
        playerCurrentTown = currentTown;

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
            System.out.println("This window cannot be closed directly.");
        });

        // Change to BorderPane for more structured layout
        mainLayout = new BorderPane();
        StackPane imageContainer = new StackPane();
        townImageView = new ImageView();
        imageContainer.getChildren().add(townImageView);
        mainLayout.setCenter(imageContainer);

        try {
            Image townImage = new Image(getClass().getResourceAsStream("/Maps/" + currentTown.getImageFile()));
            townImageView.setImage(townImage);

            // Change to 400 to match updateTown method
            final double FIXED_HEIGHT = 400;
            double aspectRatio = townImage.getWidth() / townImage.getHeight();
            double scaledWidth = FIXED_HEIGHT * aspectRatio;

            // Configure the image view
            townImageView.setFitHeight(FIXED_HEIGHT);
            townImageView.setFitWidth(scaledWidth);
            townImageView.setPreserveRatio(true);

            // Create scene with extra height for buttons
            Scene scene = new Scene(mainLayout, scaledWidth, FIXED_HEIGHT + BUTTON_AREA_HEIGHT);
            stage.setScene(scene);

            // Ensure window matches the new dimensions exactly
            stage.sizeToScene();

        } catch (Exception e) {
            System.out.println("Error loading town image: " + e.getMessage());
            // Fallback dimensions if image fails to load
            Scene scene = new Scene(mainLayout, 300, 200);
            stage.setScene(scene);
        }

        // Set up the window to display entry message when shown
        stage.setOnShown(e -> {
            currentTown.enter(PokeText_Adventure.player);
            updateButtonsForTown();
        });

        // Automatically show the window upon construction
        stage.show();
    }

    // Method to update buttons based on current town
    private void updateButtonsForTown() {
        // Clear any existing buttons
        mainLayout.setBottom(null);

        // For PlayerHome, add heal button and return to Pallet button
        if (currentTown instanceof Overworld.Buildings.PlayerHome playerHome) {
            // Create a heal button
            Button healButton = new Button("Rest");
            healButton.setOnAction(e -> {
                // Get the PokemonCenter functionality from the current town
                PokemonCenter pokemonCenter = currentTown.getPokemonCenter();

                // Heal all Pokémon in the player's party
                pokemonCenter.healPokemon(PokeText_Adventure.player);

                // Add a custom mom message after healing
                WindowThings.mainWindow.appendToOutput(
                        "Mom: Good morning, sleepyhead! I made your favorite breakfast. Your Pokémon look well-rested too! Remember to call sometimes during your journey!");
            });

            // Create a PC button
            Button pcButton = createButtonWithIcon("/Icons/pc.png");
            pcButton.getTooltip().setText("Access PC");
            pcButton.setOnAction(e -> {
                // Open the PC window
                PCWindow pcWindow = new PCWindow();
                pcWindow.show();
                WindowThings.mainWindow.appendToOutput("You access your personal computer.");
            });

            // Create a return to Pallet button
            Button returnButton = new Button("Go To Pallet");
            returnButton.setOnAction(e -> {
                // Get the parent town (Pallet) and update to it
                Pallet palletTown = (Pallet) playerHome.getParentTown();
                updateTown(palletTown);
            });

            // Apply the same styling to both buttons
            for (Button button : new Button[] { healButton, returnButton }) {
                applyButtonStyle(button);
            }

            // Create a horizontal layout for the buttons
            javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10); // 10 pixels spacing
            buttonBar.getChildren().addAll(healButton, pcButton, returnButton);
            buttonBar.setAlignment(javafx.geometry.Pos.CENTER);

            // Add the buttons to the layout
            StackPane buttonContainer = new StackPane(buttonBar);
            buttonContainer.setPadding(new javafx.geometry.Insets(10));
            buttonContainer.setStyle("-fx-background-color: transparent;");
            mainLayout.setBottom(buttonContainer);
        }
        // The rest of your existing code for Pallet Town...
        else if (currentTown instanceof Pallet pallet) {
            // Existing code for the "Go Home" button remains unchanged
            Button goHomeButton = new Button("Go Home");
            goHomeButton.setOnAction(e -> {
                // Get the player's home from Pallet Town
                PokemonCenter pokemonCenter = currentTown.getPokemonCenter();

                // Check if the PokemonCenter is also a Towns instance
                if (pokemonCenter instanceof Town towns) {
                    // If it is a Towns, update the town
                    updateTown(towns);
                }
            });

            applyButtonStyle(goHomeButton);

            // Add "To Route 1" button
            Button routeButton = new Button("To Route 1");
            routeButton.setOnAction(e -> {
                Pallet palletTown = pallet;
                Route route1 = palletTown.getRoute1();
                updateTown(route1);
            });

            applyButtonStyle(routeButton);

            // Create a horizontal layout for both buttons
            javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10);
            buttonBar.getChildren().addAll(goHomeButton, routeButton);
            buttonBar.setAlignment(javafx.geometry.Pos.CENTER);

            // Use this buttonBar instead of just the goHomeButton
            StackPane buttonContainer = new StackPane(buttonBar);
            buttonContainer.setPadding(new javafx.geometry.Insets(10));
            buttonContainer.setStyle("-fx-background-color: transparent;");
            mainLayout.setBottom(buttonContainer);
        }
        // Add specific buttons for Route 1
        else if (currentTown instanceof Route && currentTown.getName().equals("Route 1")) {
            // Create button to go to Viridian City
            Button toViridianButton = new Button("To Viridian City");
            toViridianButton.setOnAction(e -> {
                Route route1 = (Route) currentTown;
                // Get Viridian City (should be destination2)
                Town viridianCity = route1.getDestination2();
                updateTown(viridianCity);
            });

            // Create button to go back to Pallet Town
            Button toPalletButton = new Button("To Pallet Town");
            toPalletButton.setOnAction(e -> {
                Route route1 = (Route) currentTown;
                // Get Pallet Town (should be destination1)
                Town palletTown = route1.getDestination1();
                updateTown(palletTown);
            });

            // Create button to force a wild Pokémon encounter with grass icon
            Button encounterButton = createButtonWithIcon("/Icons/Grass.png");
            // Update tooltip text for the grass icon
            encounterButton.getTooltip().setText("Look for Pokémon");
            encounterButton.setOnAction(e -> {
                // Get a random Pokémon from the encounter pool for this route
                Pokemon wildPokemon = EncounterPool.getRandomEncounter(currentTown.getName());

                if (wildPokemon != null) {
                    WindowThings.mainWindow.appendToOutput("A wild " + wildPokemon.getName() + " appeared!");

                    // Start battle in a separate thread
                    javafx.application.Platform.runLater(() -> {
                        try {
                            // Make sure player has a current Pokémon set
                            if (PokeText_Adventure.player.getCurrentPokemon() == null) {
                                // If current Pokémon is null, use the first Pokémon in the party
                                Pokemon firstPokemon = PokeText_Adventure.player.getFirstPokemon();

                                if (firstPokemon != null) {
                                    PokeText_Adventure.player.setCurrentPokemon(firstPokemon);
                                } else {
                                    WindowThings.mainWindow
                                            .appendToOutput("You don't have any Pokémon to battle with!");
                                    return;
                                }
                            }

                            Battle battle = new Battle(PokeText_Adventure.player.getCurrentPokemon(),
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

            // Apply styling only to the text buttons
            for (Button button : new Button[] { toViridianButton, toPalletButton }) {
                applyButtonStyle(button);
            }

            // Arrange buttons in a horizontal box
            javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10);
            buttonBar.getChildren().addAll(toPalletButton, toViridianButton, encounterButton);
            buttonBar.setAlignment(javafx.geometry.Pos.CENTER);

            // Add the buttons to the layout
            StackPane buttonContainer = new StackPane(buttonBar);
            buttonContainer.setPadding(new javafx.geometry.Insets(10));
            buttonContainer.setStyle("-fx-background-color: transparent;");
            mainLayout.setBottom(buttonContainer);
        }
        // Add specific buttons for Viridian City
        else if (currentTown.getName().equals("Viridian City")) {
            // Create button to access Pokemon Center with icon
            Button pokeCenterButton = createButtonWithIcon("/Icons/center.png");
            pokeCenterButton.setOnAction(e -> {
                // Get the PokemonCenter functionality from the current town
                PokemonCenter pokemonCenter = currentTown.getPokemonCenter();

                // Check if the PokemonCenter is also a Town instance
                if (pokemonCenter instanceof Town pokemonCenterTown) {
                    // If it is a Town, update the town to navigate to it
                    updateTown(pokemonCenterTown);

                    // Display an appropriate message
                    WindowThings.mainWindow.appendToOutput("You enter the " + pokemonCenter.getName() + ".");
                }
            });

            // Add "To Route 1" button
            Button toRoute1Button = new Button("To Route 1");
            toRoute1Button.setOnAction(e -> {
                Pallet pallet = new Pallet(); // Create temporary instance to get Route1
                Route route1 = pallet.getRoute1();
                updateTown(route1);
            });
            
            // Add "To Route 22" button
            Button toRoute22Button = new Button("To Route 22");
            toRoute22Button.setOnAction(e -> {
                // Access Route 22 from Viridian City
                if (currentTown instanceof Overworld.Towns.Viridian viridian) {
                    Route route22 = viridian.getRoute22();
                    updateTown(route22);
                    WindowThings.mainWindow.appendToOutput("You head west toward Route 22.");
                }
            });

            // Apply styling to the text buttons
            applyButtonStyle(toRoute1Button);
            applyButtonStyle(toRoute22Button);

            // Create a horizontal layout for the buttons
            javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10);
            buttonBar.getChildren().addAll(pokeCenterButton, toRoute1Button, toRoute22Button);
            buttonBar.setAlignment(javafx.geometry.Pos.CENTER);

            // Add the button to the layout
            StackPane buttonContainer = new StackPane(buttonBar);
            buttonContainer.setPadding(new javafx.geometry.Insets(10));
            buttonContainer.setStyle("-fx-background-color: transparent;");
            mainLayout.setBottom(buttonContainer);
        }
        // Add handling for Route 22
        else if (currentTown instanceof Route && currentTown.getName().equals("Route 22")) {
            // Create button to go back to Viridian City
            Button toViridianButton = new Button("To Viridian City");
            toViridianButton.setOnAction(e -> {
                Route route22 = (Route) currentTown;
                // Get Viridian City (should be destination1)
                Town viridianCity = route22.getDestination1();
                updateTown(viridianCity);
            });
            
            // Create button to force a wild Pokémon encounter with grass icon
            Button encounterButton = createButtonWithIcon("/Icons/Grass.png");
            // Update tooltip text for the grass icon
            encounterButton.getTooltip().setText("Look for Pokémon");
            encounterButton.setOnAction(e -> {
                // Get a random Pokémon from the encounter pool for this route
                Pokemon wildPokemon = EncounterPool.getRandomEncounter(currentTown.getName());

                if (wildPokemon != null) {
                    WindowThings.mainWindow.appendToOutput("A wild " + wildPokemon.getName() + " appeared!");

                    // Start battle in a separate thread
                    javafx.application.Platform.runLater(() -> {
                        try {
                            // Make sure player has a current Pokémon set
                            if (PokeText_Adventure.player.getCurrentPokemon() == null) {
                                // If current Pokémon is null, use the first Pokémon in the party
                                Pokemon firstPokemon = PokeText_Adventure.player.getFirstPokemon();

                                if (firstPokemon != null) {
                                    PokeText_Adventure.player.setCurrentPokemon(firstPokemon);
                                } else {
                                    WindowThings.mainWindow
                                            .appendToOutput("You don't have any Pokémon to battle with!");
                                    return;
                                }
                            }

                            Battle battle = new Battle(PokeText_Adventure.player.getCurrentPokemon(),
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

            // Apply styling to the text button
            applyButtonStyle(toViridianButton);

            // Arrange buttons in a horizontal box
            javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10);
            buttonBar.getChildren().addAll(toViridianButton, encounterButton);
            buttonBar.setAlignment(javafx.geometry.Pos.CENTER);

            // Add the buttons to the layout
            StackPane buttonContainer = new StackPane(buttonBar);
            buttonContainer.setPadding(new javafx.geometry.Insets(10));
            buttonContainer.setStyle("-fx-background-color: transparent;");
            mainLayout.setBottom(buttonContainer);
        }
        // Add handling for Pokemon Center buildings
        else if (currentTown instanceof Overworld.Buildings.PokemonCenterBuilding pokemonCenterBuilding) {
            // Create a heal button
            Button healButton = new Button("Heal Pokémon");
            healButton.setOnAction(e -> {
                // Heal the player's Pokemon
                pokemonCenterBuilding.healPokemon(PokeText_Adventure.player);
            });

            // Create a PC button
            Button pcButton = createButtonWithIcon("/Icons/pc.png");
            pcButton.getTooltip().setText("Access PC");
            pcButton.setOnAction(e -> {
                // Open the PC window
                PCWindow pcWindow = new PCWindow();
                pcWindow.show();
                WindowThings.mainWindow.appendToOutput("You access the Pokémon Storage System.");
            });

            // Create a return button
            Button returnButton = new Button("Exit");
            returnButton.setOnAction(e -> {
                // Return to the parent town
                Town parentTown = pokemonCenterBuilding.getParentTown();
                // Set the flag before updating the town
                exitingPokemonCenter = true;
                updateTown(parentTown);
                WindowThings.mainWindow.appendToOutput("You exit the Pokémon Center.");
            });

            // Apply styling to both buttons
            for (Button button : new Button[] { healButton, returnButton }) {
                applyButtonStyle(button);
            }

            // Arrange buttons in a horizontal box
            javafx.scene.layout.HBox buttonBar = new javafx.scene.layout.HBox(10);
            buttonBar.getChildren().addAll(healButton, pcButton, returnButton);
            buttonBar.setAlignment(javafx.geometry.Pos.CENTER);

            // Add the buttons to the layout
            StackPane buttonContainer = new StackPane(buttonBar);
            buttonContainer.setPadding(new javafx.geometry.Insets(10));
            buttonContainer.setStyle("-fx-background-color: transparent;");
            mainLayout.setBottom(buttonContainer);
        }
    }

    // Add this helper method to create buttons with icons
    private Button createButtonWithIcon(String iconPath) {
        Button button = new Button();

        // Always create tooltip first, regardless of icon loading success
        button.setTooltip(new Tooltip(""));

        try {
            // Load the icon
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iconView = new ImageView(icon);

            // Make icon fill the entire button
            iconView.setFitHeight(19);
            iconView.setFitWidth(19);
            iconView.setPreserveRatio(true);

            // Set icon as the button graphic
            button.setGraphic(iconView);

            // Use tooltip instead of text
            String tooltipText = "Pokemon Center";
            button.getTooltip().setText(tooltipText);

            // White background with black text styling
            button.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-background-radius: 5; " +
                            "-fx-padding: 6 12 6 12; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #000000; " +
                            "-fx-border-radius: 5; " +
                            "-fx-border-width: 2px; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);");

            button.setOnMouseEntered(e -> {
                button.setStyle(
                        "-fx-background-color: #f0f0f0; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 6 12 6 12; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-width: 2px; " +
                                "-fx-scale-x: 1.03; " +
                                "-fx-scale-y: 1.03; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);");
            });

            button.setOnMouseExited(e -> {
                button.setStyle(
                        "-fx-background-color: white; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 6 12 6 12; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-width: 2px; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);");
            });

            button.setOnMousePressed(e -> {
                button.setStyle(
                        "-fx-background-color: #e0e0e0; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 6 12 6 12; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #000000; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-width: 2px; " +
                                "-fx-scale-x: 0.98; " +
                                "-fx-scale-y: 0.98; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);");
            });

        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());

            // Fallback to text if icon can't be loaded
            button.setText("Action"); // Generic action text instead of hardcoded "PC"
            applyButtonStyle(button);
        }

        return button;
    }

    // Keep this method for compatibility with existing code
    public void show() {
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    public void updateTown(Town newTown) {
        this.currentTown = newTown;
        playerCurrentTown = newTown;

        stage.setTitle(newTown.getName());
        try {
            Image newImage = new Image(getClass().getResourceAsStream("/Maps/" + newTown.getImageFile()));
            townImageView.setImage(newImage);

            // Changed to 400 as requested
            final double FIXED_HEIGHT = 400;
            double aspectRatio = newImage.getWidth() / newImage.getHeight();
            double scaledWidth = FIXED_HEIGHT * aspectRatio;

            // Configure the image view
            townImageView.setFitHeight(FIXED_HEIGHT);
            townImageView.setFitWidth(scaledWidth);
            townImageView.setPreserveRatio(true);

            // Instead of creating a new scene, modify current scene dimensions
            Scene currentScene = stage.getScene();
            stage.setWidth(scaledWidth);
            stage.setHeight(FIXED_HEIGHT + BUTTON_AREA_HEIGHT); // Add space for buttons

            // Make sure the stage is properly sized
            stage.sizeToScene();

            // Pass the exiting flag to the enter method
            newTown.enter(PokeText_Adventure.player, exitingPokemonCenter);

            // Reset the flag after use
            exitingPokemonCenter = false;

            updateButtonsForTown();
        } catch (Exception e) {
            System.out.println("Error updating town image: " + e.getMessage());
        }
    }

    // Add this method to the exploreWindow class
    private void applyButtonStyle(Button button) {
        try {
            Font pokemonFont = Font.loadFont(getClass().getResourceAsStream("/RBYGSC.ttf"), 14);
            button.setFont(pokemonFont);
        } catch (Exception e) {
            System.out.println("Could not load Pokémon font: " + e.getMessage());
        }

        // White background with black text styling
        button.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-text-fill: black; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-radius: 5; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);");

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-text-fill: black; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-radius: 5; " +
                        "-fx-border-width: 2px; " +
                        "-fx-scale-x: 1.03; " +
                        "-fx-scale-y: 1.03; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 2);"));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-text-fill: black; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-radius: 5; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 3, 0, 0, 1);"));

        button.setOnMousePressed(e -> button.setStyle(
                "-fx-background-color: #e0e0e0; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-text-fill: black; " +
                        "-fx-border-color: #000000; " +
                        "-fx-border-radius: 5; " +
                        "-fx-border-width: 2px; " +
                        "-fx-scale-x: 0.98; " +
                        "-fx-scale-y: 0.98; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);"));
    }
}