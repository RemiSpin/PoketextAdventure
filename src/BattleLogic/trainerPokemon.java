package BattleLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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


    private String readJsonFile() throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("src/PokemonLogic/pokemon.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        return jsonContent.toString();
    }

    // Method to load data from the JSON file based on the name
    private void loadPokemonDataFromJson() throws IOException, JSONException {
        String jsonContent = readJsonFile();
        JSONArray jsonArray = new JSONArray(jsonContent);

        // Find the JSON object that matches the name
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("Name").equalsIgnoreCase(name)) {
                populatePokemonFromJson(jsonObject);
                return;
            }
        }
    }

    // Method to populate the PokemonLogic.Pokemon object from a JSON object
    private void populatePokemonFromJson(JSONObject jsonObject) throws JSONException {
        setNumber(jsonObject.getInt("#"));
        setType1(jsonObject.getString("Type 1"));
        setType2(jsonObject.optString("Type 2", ""));
        setHp(jsonObject.getInt("HP"));
        setAttack(jsonObject.getInt("Attack"));
        setDefense(jsonObject.getInt("Defense"));
        setSpecialAttack(jsonObject.getInt("Sp. Atk"));
        setSpecialDefense(jsonObject.getInt("Sp. Def"));
        setSpeed(jsonObject.getInt("Speed"));
        setSpritePath(jsonObject.getString("Sprite"));
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
}