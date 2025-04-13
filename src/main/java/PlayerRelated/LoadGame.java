package PlayerRelated;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import PokemonLogic.Pokemon;

@SuppressWarnings("unused")

public class LoadGame {
    private Connection conn;

    public LoadGame() {
        try {
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "savegame.db";
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
            PreparedStatement playerStmt = conn.prepareStatement("SELECT name, money FROM Player");
            ResultSet playerRs = playerStmt.executeQuery();

            if (playerRs.next()) {
                String name = playerRs.getString("name");
                int money = playerRs.getInt("money");
                player = new Player(name);
                player.setMoney(money);

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
                PreparedStatement locationStmt = conn
                        .prepareStatement("SELECT town_name FROM CurrentLocation");
                ResultSet locationRs = locationStmt.executeQuery();
                if (locationRs.next()) {
                    currentTownName = locationRs.getString("town_name");
                    // The actual town object will be set later
                    player.setCurrentTownName(currentTownName);
                }

                System.out.println("Game loaded successfully!");
            } else {
                System.out.println("No save data found.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to load game: " + e.getMessage());
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