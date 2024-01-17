package PlayerRelated;

import PokemonLogic.Pokemon;

import java.io.File;
import java.sql.*;

public class SaveGame {
    private Connection conn;
    private Player player;

    public SaveGame(Player player) {
        this.player = player;
        try {
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "savegame.db";
            conn = DriverManager.getConnection(url);

            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Player (name TEXT NOT NULL, money INTEGER NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Party_Pokemon (pokemon_id INTEGER PRIMARY KEY, name TEXT NOT NULL, nickname TEXT NOT NULL, level INTEGER NOT NULL, hp INTEGER NOT NULL, attack INTEGER NOT NULL, defense INTEGER NOT NULL, specialAttack INTEGER NOT NULL, specialDefense INTEGER NOT NULL, speed INTEGER NOT NULL, remaining_health INTEGER NOT NULL, status_condition TEXT NOT NULL, spritePath TEXT NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS PC_Pokemon (pokemon_id INTEGER PRIMARY KEY, name TEXT NOT NULL, nickname TEXT NOT NULL, level INTEGER NOT NULL, hp INTEGER NOT NULL, attack INTEGER NOT NULL, defense INTEGER NOT NULL, specialAttack INTEGER NOT NULL, specialDefense INTEGER NOT NULL, speed INTEGER NOT NULL, remaining_health INTEGER NOT NULL, status_condition TEXT NOT NULL, spritePath TEXT NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Badges (badge_id INTEGER PRIMARY KEY, badge_name TEXT NOT NULL)");
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveGame() {
        if (!RoleManager.canSaveGame(player.getName())) {
            System.out.println("You do not have permission to save the game.");
            return;
        }
        try {
            // Check if player exists
            PreparedStatement checkPlayerStmt = conn.prepareStatement("SELECT * FROM Player WHERE name = ?");
            checkPlayerStmt.setString(1, player.getName());
            ResultSet rs = checkPlayerStmt.executeQuery();
            System.out.println("Saving game...");

            if (rs.next()) {
                // Player exists, update player
                PreparedStatement playerStmt = conn.prepareStatement("UPDATE Player SET money = ? WHERE name = ?");
                playerStmt.setInt(1, player.getMoney());
                playerStmt.setString(2, player.getName());
                playerStmt.executeUpdate();
            } else {
                // Player does not exist, insert player
                PreparedStatement playerStmt = conn.prepareStatement("INSERT INTO Player (name, money) VALUES (?, ?)");
                playerStmt.setString(1, player.getName());
                playerStmt.setInt(2, player.getMoney());
                playerStmt.executeUpdate();
            }

            // Save Pokemon and Badges
            PreparedStatement partyPokemonStmt = conn.prepareStatement("INSERT OR REPLACE INTO Party_Pokemon (pokemon_id, name, nickname, level, hp, attack, defense, specialAttack, specialDefense, speed, remaining_health, status_condition, spritePath) VALUES ((SELECT pokemon_id FROM Party_Pokemon WHERE pokemon_id = ?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement pcPokemonStmt = conn.prepareStatement("INSERT OR REPLACE INTO PC_Pokemon (pokemon_id, name, nickname, level, hp, attack, defense, specialAttack, specialDefense, speed, remaining_health, status_condition, spritePath) VALUES ((SELECT pokemon_id FROM PC_Pokemon WHERE pokemon_id = ?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Pokemon pokemon : player.getParty()) {
                savePokemon(partyPokemonStmt, pokemon);
            }
            for (Pokemon pokemon : player.getPC()) {
                savePokemon(pcPokemonStmt, pokemon);
            }

            PreparedStatement badgeStmt = conn.prepareStatement("INSERT OR REPLACE INTO Badges (badge_name) VALUES (?)");
            for (String badge : player.getBadges()) {
                badgeStmt.setString(1, badge);
                badgeStmt.executeUpdate();
            }
            System.out.println("Game saved!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void savePokemon(PreparedStatement pokemonStmt, Pokemon pokemon) throws SQLException {
        pokemonStmt.setString(1, pokemon.getId().toString());
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