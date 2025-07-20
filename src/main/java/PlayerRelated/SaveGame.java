package PlayerRelated;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import Overworld.Routes.Route22;
import Overworld.Routes.ViridianForest;
import PokemonLogic.Pokemon;

@SuppressWarnings("static-access")

public class SaveGame {
    private Connection conn;
    private Player player;

    public SaveGame(Player player) {
        this.player = player;
        try {
            // Create save directory if it doesn't exist
            String saveDir = System.getProperty("user.home") + File.separator + ".poketextadventure";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String url = "jdbc:sqlite:" + saveDir + File.separator + "savegame.db";
            conn = DriverManager.getConnection(url);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Player (name TEXT NOT NULL, money INTEGER NOT NULL, has_oaks_parcel BOOLEAN NOT NULL, delivered_oaks_parcel BOOLEAN NOT NULL, chosen_starter TEXT)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Party_Pokemon (pokemon_id INTEGER PRIMARY KEY, name TEXT NOT NULL, nickname TEXT NOT NULL, level INTEGER NOT NULL, hp INTEGER NOT NULL, attack INTEGER NOT NULL, defense INTEGER NOT NULL, specialAttack INTEGER NOT NULL, specialDefense INTEGER NOT NULL, speed INTEGER NOT NULL, remaining_health INTEGER NOT NULL, status_condition TEXT NOT NULL, spritePath TEXT NOT NULL)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS PC_Pokemon (pokemon_id INTEGER PRIMARY KEY, name TEXT NOT NULL, nickname TEXT NOT NULL, level INTEGER NOT NULL, hp INTEGER NOT NULL, attack INTEGER NOT NULL, defense INTEGER NOT NULL, specialAttack INTEGER NOT NULL, specialDefense INTEGER NOT NULL, speed INTEGER NOT NULL, remaining_health INTEGER NOT NULL, status_condition TEXT NOT NULL, spritePath TEXT NOT NULL)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Badges (badge_id INTEGER PRIMARY KEY, badge_name TEXT NOT NULL)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS VisitedTowns (town_name TEXT PRIMARY KEY, visited INTEGER NOT NULL)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS CurrentLocation (town_name TEXT NOT NULL)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Pokedex (pokemon_id INTEGER PRIMARY KEY)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS RouteTrainers (route_name TEXT NOT NULL, trainer_index INTEGER NOT NULL, defeated BOOLEAN NOT NULL, PRIMARY KEY (route_name, trainer_index))");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS RouteBattleFlags (route_name TEXT PRIMARY KEY, rival_battle_occurred BOOLEAN)");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public void saveGame() {
        try {
            // First delete all existing data
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM Player");
            stmt.execute("DELETE FROM Party_Pokemon");
            stmt.execute("DELETE FROM PC_Pokemon");
            stmt.execute("DELETE FROM Badges");
            stmt.execute("DELETE FROM VisitedTowns");
            stmt.execute("DELETE FROM CurrentLocation");
            stmt.execute("DELETE FROM Pokedex");
            stmt.execute("DELETE FROM RouteTrainers");
            stmt.execute("DELETE FROM RouteBattleFlags");

            // Save player data
            PreparedStatement playerStmt = conn.prepareStatement(
                    "INSERT INTO Player (name, money, has_oaks_parcel, delivered_oaks_parcel, chosen_starter) VALUES (?, ?, ?, ?, ?)");
            playerStmt.setString(1, player.getName());
            playerStmt.setInt(2, player.getMoney());
            playerStmt.setBoolean(3, player.hasOaksParcel());
            playerStmt.setBoolean(4, player.hasDeliveredOaksParcel());
            playerStmt.setString(5, player.getChosenStarter()); // Save chosen starter
            playerStmt.executeUpdate();

            // Save Pokemon data
            PreparedStatement partyPokemonStmt = conn.prepareStatement(
                    "INSERT INTO Party_Pokemon (pokemon_id, name, nickname, level, hp, attack, defense, specialAttack, specialDefense, speed, remaining_health, status_condition, spritePath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement pcPokemonStmt = conn.prepareStatement(
                    "INSERT INTO PC_Pokemon (pokemon_id, name, nickname, level, hp, attack, defense, specialAttack, specialDefense, speed, remaining_health, status_condition, spritePath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Pokemon pokemon : player.getParty()) {
                savePokemon(partyPokemonStmt, pokemon);
            }
            for (Pokemon pokemon : player.getPC()) {
                savePokemon(pcPokemonStmt, pokemon);
            }

            // Save badges
            PreparedStatement badgeStmt = conn
                    .prepareStatement("INSERT OR REPLACE INTO Badges (badge_name) VALUES (?)");
            for (String badge : player.getBadges()) {
                badgeStmt.setString(1, badge);
                badgeStmt.executeUpdate();
            }

            // Save visited towns
            PreparedStatement townStmt = conn
                    .prepareStatement("INSERT OR REPLACE INTO VisitedTowns (town_name, visited) VALUES (?, ?)");
            for (String townName : player.getVisitedTowns()) {
                townStmt.setString(1, townName);
                townStmt.setInt(2, 1); // 1 = visited
                townStmt.executeUpdate();
            }

            // Save current location
            if (WindowThings.exploreWindow.playerCurrentTown != null) {
                PreparedStatement locationStmt = conn
                        .prepareStatement("INSERT INTO CurrentLocation (town_name) VALUES (?)");
                locationStmt.setString(1, WindowThings.exploreWindow.playerCurrentTown.getName());
                locationStmt.executeUpdate();
            }

            // Save Pokedex entries
            PreparedStatement pokedexStmt = conn.prepareStatement("INSERT INTO Pokedex (pokemon_id) VALUES (?)");
            for (int pokemonId : player.getPokedexCaught()) {
                pokedexStmt.setInt(1, pokemonId);
                pokedexStmt.executeUpdate();
            }

            // Save Route22 rival battle state
            PreparedStatement routeFlagsStmt = conn.prepareStatement(
                    "INSERT OR REPLACE INTO RouteBattleFlags (route_name, rival_battle_occurred) VALUES (?, ?)");
            routeFlagsStmt.setString(1, "Route 22");
            routeFlagsStmt.setBoolean(2, Route22.hasRivalBattleOccurred());
            routeFlagsStmt.executeUpdate();

            // Save route trainer states - check if we have access to ViridianForest
            if (WindowThings.exploreWindow.viridianForest != null) {
                ViridianForest viridianForest = WindowThings.exploreWindow.viridianForest;
                saveRouteTrainerStates("Viridian Forest", viridianForest.getDefeatedTrainers());
            }

            System.out.println("Game saved!");
        } catch (SQLException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error closing database connection: " + ex.getMessage());
            }
        }
    }

    private void saveRouteTrainerStates(String routeName, List<Boolean> defeatedTrainers) throws SQLException {
        if (defeatedTrainers == null) {
            return;
        }

        PreparedStatement trainerStmt = conn.prepareStatement(
                "INSERT OR REPLACE INTO RouteTrainers (route_name, trainer_index, defeated) VALUES (?, ?, ?)");

        for (int i = 0; i < defeatedTrainers.size(); i++) {
            trainerStmt.setString(1, routeName);
            trainerStmt.setInt(2, i);
            trainerStmt.setBoolean(3, defeatedTrainers.get(i));
            trainerStmt.executeUpdate();
        }
    }

    private void savePokemon(PreparedStatement pokemonStmt, Pokemon pokemon) throws SQLException {
        pokemonStmt.setInt(1, pokemon.getId());
        pokemonStmt.setString(2, pokemon.getName());
        pokemonStmt.setString(3, pokemon.getNickname());
        pokemonStmt.setInt(4, pokemon.getLevel());
        pokemonStmt.setInt(5, pokemon.getHp());
        pokemonStmt.setInt(6, pokemon.getAttack());
        pokemonStmt.setInt(7, pokemon.getDefense());
        pokemonStmt.setInt(8, pokemon.getSpecialAttack());
        pokemonStmt.setInt(9, pokemon.getSpecialDefense());
        pokemonStmt.setInt(10, pokemon.getSpeed());
        pokemonStmt.setInt(11, pokemon.getRemainingHealth());
        pokemonStmt.setString(12, pokemon.getStatusCondition().toString());
        pokemonStmt.setString(13, pokemon.getSpritePath());
        pokemonStmt.executeUpdate();
    }
}