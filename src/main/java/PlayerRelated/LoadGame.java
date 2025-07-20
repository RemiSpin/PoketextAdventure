package PlayerRelated;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Overworld.Routes.Route22;
import PokemonLogic.Pokemon;
import WindowThings.exploreWindow;

@SuppressWarnings("unused")

public class LoadGame {
    private Connection conn;

    public LoadGame() {
        try {
            // Create save directory if it doesn't exist
            String saveDir = System.getProperty("user.home") + File.separator + ".poketextadventure";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String url = "jdbc:sqlite:" + saveDir + File.separator + "savegame.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public Player loadGame() {
        Player player = null;
        String currentTownName = null;

        try {
            // Load player data
            PreparedStatement playerStmt = conn
                    .prepareStatement(
                            "SELECT name, money, has_oaks_parcel, delivered_oaks_parcel, chosen_starter FROM Player");
            ResultSet playerRs = playerStmt.executeQuery();

            if (playerRs.next()) {
                String name = playerRs.getString("name");
                int money = playerRs.getInt("money");
                boolean hasOaksParcel = playerRs.getBoolean("has_oaks_parcel");
                boolean deliveredOaksParcel = playerRs.getBoolean("delivered_oaks_parcel");
                String chosenStarter = playerRs.getString("chosen_starter");

                player = new Player(name);
                player.setMoney(money);
                player.setHasOaksParcel(hasOaksParcel);
                player.setDeliveredOaksParcel(deliveredOaksParcel);
                if (chosenStarter != null) {
                    player.setChosenStarter(chosenStarter);
                }

                // Load party Pokemon
                List<Pokemon> party = loadPokemonFromTable("Party_Pokemon");
                for (Pokemon pokemon : party) {
                    player.addToParty(pokemon);
                }

                // Load PC Pokemon
                List<Pokemon> pc = loadPokemonFromTable("PC_Pokemon");
                for (Pokemon pokemon : pc) {
                    player.addToPC(pokemon);
                }

                // Load badges
                PreparedStatement badgeStmt = conn.prepareStatement("SELECT badge_name FROM Badges");
                ResultSet badgeRs = badgeStmt.executeQuery();
                while (badgeRs.next()) {
                    player.addBadge(badgeRs.getString("badge_name"));
                }

                // Load visited towns
                PreparedStatement townStmt = conn
                        .prepareStatement("SELECT town_name FROM VisitedTowns WHERE visited = 1");
                ResultSet townRs = townStmt.executeQuery();
                while (townRs.next()) {
                    player.addVisitedTown(townRs.getString("town_name"));
                }

                // Load current location
                PreparedStatement locationStmt = conn.prepareStatement("SELECT town_name FROM CurrentLocation");
                ResultSet locationRs = locationStmt.executeQuery();
                if (locationRs.next()) {
                    currentTownName = locationRs.getString("town_name");
                    player.setCurrentTownName(currentTownName);
                }

                // Load Pokedex entries
                PreparedStatement pokedexStmt = conn.prepareStatement("SELECT pokemon_id FROM Pokedex");
                ResultSet pokedexRs = pokedexStmt.executeQuery();
                while (pokedexRs.next()) {
                    player.registerPokemonCaught(pokedexRs.getInt("pokemon_id"));
                }

                // Load Route22 rival battle state
                PreparedStatement rivalBattleStmt = conn.prepareStatement(
                        "SELECT rival_battle_occurred FROM RouteBattleFlags WHERE route_name = ?");
                rivalBattleStmt.setString(1, "Route 22");
                ResultSet rivalBattleRs = rivalBattleStmt.executeQuery();
                if (rivalBattleRs.next()) {
                    boolean rivalBattleOccurred = rivalBattleRs.getBoolean("rival_battle_occurred");
                    Route22.setRivalBattleOccurred(rivalBattleOccurred);
                }

                // Load route trainer states
                Map<String, List<Boolean>> routeTrainers = loadRouteTrainerStates();

                // Apply trainer states to the routes if they exist
                if (routeTrainers.containsKey("Viridian Forest") && exploreWindow.viridianForest != null) {
                    exploreWindow.viridianForest.setDefeatedTrainers(routeTrainers.get("Viridian Forest"));
                }

                System.out.println("Game loaded successfully!");
            } else {
                System.out.println("No save data found.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error closing database connection: " + ex.getMessage());
            }
        }
        return player;
    }

    private Map<String, List<Boolean>> loadRouteTrainerStates() throws SQLException {
        Map<String, List<Boolean>> routeTrainers = new HashMap<>();

        PreparedStatement stmt = conn.prepareStatement(
                "SELECT route_name, trainer_index, defeated FROM RouteTrainers ORDER BY route_name, trainer_index");
        ResultSet rs = stmt.executeQuery();

        String currentRoute = null;
        List<Boolean> currentRouteTrainers = null;

        while (rs.next()) {
            String routeName = rs.getString("route_name");
            int trainerIndex = rs.getInt("trainer_index");
            boolean defeated = rs.getBoolean("defeated");

            // If we're processing a new route
            if (currentRoute == null || !currentRoute.equals(routeName)) {
                // If we were already processing a route, add it to the map
                if (currentRoute != null) {
                    routeTrainers.put(currentRoute, currentRouteTrainers);
                }

                // Start new route
                currentRoute = routeName;
                currentRouteTrainers = new ArrayList<>();
            }

            // Ensure we have enough entries in the list
            while (currentRouteTrainers.size() <= trainerIndex) {
                currentRouteTrainers.add(false);
            }

            // Set the trainer's state
            currentRouteTrainers.set(trainerIndex, defeated);
        }

        // Add the last route if there is one
        if (currentRoute != null) {
            routeTrainers.put(currentRoute, currentRouteTrainers);
        }

        return routeTrainers;
    }

    private List<Pokemon> loadPokemonFromTable(String tableName) throws SQLException {
        List<Pokemon> pokemonList = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT pokemon_id, name, nickname, level, hp, attack, defense, " +
                        "specialAttack, specialDefense, speed, remaining_health, status_condition, spritePath " +
                        "FROM " + tableName);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("pokemon_id");
            String name = rs.getString("name");
            String nickname = rs.getString("nickname");
            int level = rs.getInt("level");
            int hp = rs.getInt("hp");
            int attack = rs.getInt("attack");
            int defense = rs.getInt("defense");
            int specialAttack = rs.getInt("specialAttack");
            int specialDefense = rs.getInt("specialDefense");
            int speed = rs.getInt("speed");
            int remainingHealth = rs.getInt("remaining_health");
            String spritePath = rs.getString("spritePath");

            Pokemon pokemon = new Pokemon(name, level);
            pokemon.setNickname(nickname);
            pokemon.setHp(hp);
            pokemon.setAttack(attack);
            pokemon.setDefense(defense);
            pokemon.setSpecialAttack(specialAttack);
            pokemon.setSpecialDefense(specialDefense);
            pokemon.setSpeed(speed);
            pokemon.setRemainingHealth(remainingHealth);
            pokemon.setSpritePath(spritePath);

            pokemonList.add(pokemon);
        }

        return pokemonList;
    }
}