package PlayerRelated;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import PokemonLogic.Pokemon;

@SuppressWarnings("static-access")

public class SaveGame {
    private Connection conn;
    private Player player;

    public SaveGame(Player player) {
        this.player = player;
        try {
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "savegame.db";
            conn = DriverManager.getConnection(url);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS Player (name TEXT NOT NULL, money INTEGER NOT NULL)");
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

            // Now save the new data
            System.out.println("Saving. Please wait...");
            PreparedStatement playerStmt = conn.prepareStatement("INSERT INTO Player (name, money) VALUES (?, ?)");
            playerStmt.setString(1, player.getName());
            playerStmt.setInt(2, player.getMoney());
            playerStmt.executeUpdate();

            // Save Pokemon and Badges
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