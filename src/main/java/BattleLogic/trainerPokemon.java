package BattleLogic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class trainerPokemon {
    private byte level; // variables galore
    private int number;
    private String name;
    private String nickname;
    private int ivHP;
    private int ivAttack;
    private int ivDefense;
    private int ivSpAtk;
    private int ivSpDef;
    private int ivSpeed;
    private int Hp;
    private int Attack;
    private int Defense;
    private int SpecialAttack;
    private int SpecialDefense;
    private int Speed;
    private String type1;
    private String type2;
    private PokemonLogic.Pokemon.StatusCondition statusCondition;
    private List<Move> moveset;
    private moveFactory moveFactory;
    private List<Move> moves;
    private boolean fainted;
    private String spritePath;
    private int remainingHealth;

    private void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public void setHp(int hp) {
        Hp = hp;
    }

    public void setAttack(int attack) {
        Attack = attack;
    }

    public void setDefense(int defense) {
        Defense = defense;
    }

    public void setSpecialAttack(int specialAttack) {
        SpecialAttack = specialAttack;
    }

    public void setSpecialDefense(int specialDefense) {
        SpecialDefense = specialDefense;
    }

    public void setSpeed(int speed) {
        Speed = speed;
    }

    public void setMoveFactory(moveFactory factory) {
        this.moveFactory = factory;
    }

    public trainerPokemon(String name, int level, String... moveNames) throws IOException, JSONException {
        this.name = name;
        this.level = (byte) level;
        statusCondition = PokemonLogic.Pokemon.StatusCondition.none;
        this.moveset = new ArrayList<>();
        this.fainted = false;

        // Initialize IVs
        Random random = new Random();
        ivHP = random.nextInt(33);
        ivAttack = random.nextInt(33);
        ivDefense = random.nextInt(33);
        ivSpAtk = random.nextInt(33);
        ivSpDef = random.nextInt(33);
        ivSpeed = random.nextInt(33);

        moveFactory = new moveFactory();
        moveFactory.loadPokemonLearnsets(); // Load the learnsets
        setMoveFactory(moveFactory);
        moves = moveFactory.createMovesFromJson();

        for (String moveName : moveNames) {
            Move move = findMoveByName(moveName);
            if (move != null) {
                this.moveset.add(move);
            }
        }

        // Load data from the JSON file based on the name
        loadPokemonDataFromJson();

        // Recalculate stats when leveling up
        setHp(calculateHP(Hp, ivHP));
        setAttack(calculateStat(Attack, ivAttack));
        setDefense(calculateStat(Defense, ivDefense));
        setSpecialAttack(calculateStat(SpecialAttack, ivSpAtk));
        setSpecialDefense(calculateStat(SpecialDefense, ivSpDef));
        setSpeed(calculateStat(Speed, ivSpeed));

        remainingHealth = Hp;
    }


    public org.json.simple.JSONArray readJsonFile(String filename) {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/" + filename))) {
            return (org.json.simple.JSONArray) new JSONParser().parse(reader);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to load data from the JSON file based on the name
    private void loadPokemonDataFromJson() throws IOException {
        JSONArray jsonArray = readJsonFile("pokemon.json");

        // Find the JSON object that matches the name
        for (Object obj : jsonArray) {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
            if (jsonObject.get("Name").toString().equalsIgnoreCase(name)) {
                populatePokemonFromJson(jsonObject);
            }
        }
    }

    // Method to populate the PokemonLogic.Pokemon object from a JSON object
    private void populatePokemonFromJson(JSONObject jsonObject) {
        setNumber(jsonObject.get("#") instanceof Integer ? (Integer) jsonObject.get("#") : Integer.parseInt(jsonObject.get("#").toString()));
        setType1(jsonObject.get("Type 1").toString());
        setType2(jsonObject.get("Type 2") != null ? jsonObject.get("Type 2").toString() : "");
        setHp(jsonObject.get("HP") instanceof Integer ? (Integer) jsonObject.get("HP") : Integer.parseInt(jsonObject.get("HP").toString()));
        setAttack(jsonObject.get("Attack") instanceof Integer ? (Integer) jsonObject.get("Attack") : Integer.parseInt(jsonObject.get("Attack").toString()));
        setDefense(jsonObject.get("Defense") instanceof Integer ? (Integer) jsonObject.get("Defense") : Integer.parseInt(jsonObject.get("Defense").toString()));
        setSpecialAttack(jsonObject.get("Sp. Atk") instanceof Integer ? (Integer) jsonObject.get("Sp. Atk") : Integer.parseInt(jsonObject.get("Sp. Atk").toString()));
        setSpecialDefense(jsonObject.get("Sp. Def") instanceof Integer ? (Integer) jsonObject.get("Sp. Def") : Integer.parseInt(jsonObject.get("Sp. Def").toString()));
        setSpeed(jsonObject.get("Speed") instanceof Integer ? (Integer) jsonObject.get("Speed") : Integer.parseInt(jsonObject.get("Speed").toString()));
        setSpritePath(jsonObject.get("Sprite").toString());
    }

    private int calculateHP(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + level + 10);
    }

    private int calculateStat(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + 5);
    }

    public void displayMoveset() {
        for (int i = 0; i < moveset.size(); i++) {
            System.out.println(moveset.get(i).getName());
        }
    }

    private Move findMoveByName(String moveName) {
        for (Move move : moves) {
            if (move.getName().equals(moveName)) {
                return move;
            }
        }
        return null;
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

    public PokemonLogic.Pokemon.StatusCondition getStatusCondition() {
        return statusCondition;
    }

    public void setStatusCondition(PokemonLogic.Pokemon.StatusCondition condition) {
        statusCondition = condition;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public int getHp() {
        return Hp;
    }

    public int getRemainingHealth() {
        return remainingHealth;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        StringBuilder moves = new StringBuilder();
        for (Move move : moveset) {
            moves.append(move.getName()).append("\n");
        }
        // Remove the trailing newline
        if (moves.length() > 0) {
            moves.setLength(moves.length() - 1);
        }

        return "Name: " + nickname +
                "\nSpecies Name: " + name +
                "\nType 1: " + type1 +
                "\nType 2: " + type2 +
                "\nLevel: " + level +
                "\nStatus: " + statusCondition +
                "\nHP: " + Hp +
                "\nAttack: " + Attack +
                "\nDefense: " + Defense +
                "\nSpecial Attack: " + SpecialAttack +
                "\nSpecial Defense: " + SpecialDefense +
                "\nSpeed: " + Speed +
                "\n" + moves.toString();
    }

    public List<Move> getMoves() {
        return moveset;
    }

    public int getAttack() {
        return Attack;
    }

    public void setRemainingHealth(int remainingHealth) {
        this.remainingHealth = remainingHealth;
        if (this.remainingHealth <= 0) {
            this.remainingHealth = 0;
        }
    }

    public int getDefense() {
        return Defense;
    }
}