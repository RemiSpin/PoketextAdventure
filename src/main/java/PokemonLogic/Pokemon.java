package PokemonLogic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import BattleLogic.Move;
import BattleLogic.moveFactory;
import BattleLogic.trainerPokemon;
import javafx.scene.control.TextInputDialog;

public class Pokemon {
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
    private String evolution;
    private int evolutionLevel;
    private StatusCondition statusCondition;
    private int baseExperience;
    private int experience;
    private String experienceGrowth;
    private int levelTreshhold;
    private List<Move> moveset;
    private moveFactory moveFactory;
    private List<Move> moves;
    private boolean fainted;
    private String spritePath;
    private int remainingHealth;
    private static int idCounter = 1;
    private final int id;

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

    public void setEvolution(String evolution) {
        this.evolution = evolution;
    }

    public void setEvolutionLevel(int evolutionLevel) {
        this.evolutionLevel = evolutionLevel;
    }

    private void setExperienceGrowth(String experienceGrowth) {
        this.experienceGrowth = experienceGrowth;
    }

    private void setBaseExperience(int baseExperience) {
        this.baseExperience = baseExperience;
    }
    public void setMoveFactory(moveFactory factory) {
        this.moveFactory = factory;
    }

    // Constructor with name and level that loads data from the JSON file
    public Pokemon(String name, int level) throws JSONException{
        this.id = idCounter++;
        this.name = name;
        this.nickname = name;
        this.level = (byte) level;
        statusCondition = StatusCondition.none;
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

        // Load data from the JSON file based on the name in a separate thread
        new Thread(() -> {
            try {
                loadPokemonDataFromJson();
                initializeMoves();
                assignMovesBasedOnLevel();

                // Recalculate stats when leveling up
                setHp(calculateHP(Hp, ivHP));
                setAttack(calculateStat(Attack, ivAttack));
                setDefense(calculateStat(Defense, ivDefense));
                setSpecialAttack(calculateStat(SpecialAttack, ivSpAtk));
                setSpecialDefense(calculateStat(SpecialDefense, ivSpDef));
                setSpeed(calculateStat(Speed, ivSpeed));

                // Set levelThreshold based on experienceGrowth
                switch(experienceGrowth){
                    case "Fast": {
                        levelTreshhold = (int) ((Math.pow(level + 1, 3) * 0.8) - (Math.pow(level, 3) * 0.8));
                        break;
                    }
                    case "MFast": {
                        levelTreshhold = (int) ((Math.pow(level + 1, 3) - Math.pow(level, 3)));
                        break;
                    }
                    case "MSlow": {
                        levelTreshhold = (int) ((1.2 * Math.pow(level + 1, 3) - 15 * Math.pow(level + 1, 2) + 100 * (level + 1) - 140) - (1.2 * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140));
                        break;
                    }
                    case "Slow": {
                        levelTreshhold = (int) ((1.25 * Math.pow(level + 1, 3)) - (1.25 * Math.pow(level, 3)));
                        break;
                    }
                }
                nickname = name;
                experience = 0;
                remainingHealth = Hp;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public org.json.simple.JSONArray readJsonFile(String filename) {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/" + filename))) {
            return (org.json.simple.JSONArray) new JSONParser().parse(reader);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

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
        setEvolution(jsonObject.get("Evolution") != null ? jsonObject.get("Evolution").toString() : "");
        if (jsonObject.containsKey("EvolutionLevel") && jsonObject.get("EvolutionLevel") != null) {
            setEvolutionLevel(jsonObject.get("EvolutionLevel") instanceof Integer ? (Integer) jsonObject.get("EvolutionLevel") : Integer.parseInt(jsonObject.get("EvolutionLevel").toString()));
        }
        setBaseExperience(jsonObject.get("BaseExperience") instanceof Integer ? (Integer) jsonObject.get("BaseExperience") : Integer.parseInt(jsonObject.get("BaseExperience").toString()));
        setExperienceGrowth(jsonObject.get("ExperienceGrowth").toString());
        setSpritePath(jsonObject.get("Sprite").toString());
    }

    public void gainExperience() throws IOException{
        if (level < 100){
            int experienceGained = (int) (100 /* PUT ENEMY BASE EXPERIENCE HERE WHEN DONE */ * 5 /* PUT ENEMY LEVEL HERE WHEN DONE */ * 1.5) / 7;
            experience += experienceGained;

            System.out.println(nickname + " gained " + experienceGained + " experience points.");

            while (experience >= levelTreshhold) {
                level++;
                experience -= levelTreshhold;

                loadPokemonDataFromJson();
                setHp(calculateHP(Hp, ivHP));
                setAttack(calculateStat(Attack, ivAttack));
                setDefense(calculateStat(Defense, ivDefense));
                setSpecialAttack(calculateStat(SpecialAttack, ivSpAtk));
                setSpecialDefense(calculateStat(SpecialDefense, ivSpDef));
                setSpeed(calculateStat(Speed, ivSpeed));
                assignMovesBasedOnLevel();

                if (level >= evolutionLevel && !evolution.isEmpty()) {
                    String originalName = nickname; // Store the original name
                    // Perform evolution
                    if (nickname.equals(name)) {
                        // If the nickname is the same as the species name, set it to the evolved form's name
                        name = evolution;
                        nickname = name;
                    } else {
                        // Keep the custom nickname and change only the species name
                        name = evolution;
                    }

                    loadPokemonDataFromJson();
                    setHp(calculateHP(Hp, ivHP));
                    setAttack(calculateStat(Attack, ivAttack));
                    setDefense(calculateStat(Defense, ivDefense));
                    setSpecialAttack(calculateStat(SpecialAttack, ivSpAtk));
                    setSpecialDefense(calculateStat(SpecialDefense, ivSpDef));
                    setSpeed(calculateStat(Speed, ivSpeed));
                    System.out.println("Congratulations! " + originalName + " has evolved into " + name + "!");
                } else {
                    System.out.println(nickname + " leveled up to " + level + "!");
                }

                switch (experienceGrowth) {
                    case "Fast": {
                        levelTreshhold = (int) ((Math.pow(level + 1, 3) * 0.8) - (Math.pow(level, 3) * 0.8));
                        break;
                    }
                    case "MFast": {
                        levelTreshhold = (int) ((Math.pow(level + 1, 3) - Math.pow(level, 3)));
                        break;
                    }
                    case "MSlow": {
                        levelTreshhold = (int) ((1.2 * Math.pow(level + 1, 3) - 15 * Math.pow(level + 1, 2) + 100 * (level + 1) - 140) - (1.2 * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140));
                        break;
                    }
                    case "Slow": {
                        levelTreshhold = (int) ((1.25 * Math.pow(level + 1, 3)) - (1.25 * Math.pow(level, 3)));
                        break;
                    }
                }
                System.out.println("Name: " + nickname);
                System.out.println("Level: " + level);
                System.out.println("HP: " + Hp);
                System.out.println("Attack: " + Attack);
                System.out.println("Defense: " + Defense);
                System.out.println("Special Attack: " + SpecialAttack);
                System.out.println("Special Defense: " + SpecialDefense);
                System.out.println("Speed: " + Speed);
                System.out.println("Experience: " + experience + " / " + levelTreshhold);
            }
        }
    }

        private int calculateHP(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + level + 10);
    }

    private int calculateStat(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + 5);
    }

    public void setRemainingHealth(int remainingHealth) {
        this.remainingHealth = remainingHealth;
        if (this.remainingHealth <= 0) {
            faint();
        }
    }

    public void initializeMoves() {
        Map<Integer, List<String>> learnset = moveFactory.pokemonLearnsets.get(name);

        for (int currentLevel = this.level; currentLevel >= 1; currentLevel--) {
            List<String> moveNames = learnset.get(currentLevel);
            if (moveNames != null) {
                for (String moveName : moveNames) {
                    Move move = findMoveByName(moveName);
                    if (move != null && !moveset.contains(move)) {
                        if (moveset.size() < 4) { // Limit the moveset size to 4
                            moveset.add(move);
                        }
                    }
                }
            }
        }
    }
    public void assignMovesBasedOnLevel() {
        Map<Integer, List<String>> learnset = moveFactory.pokemonLearnsets.get(name);

        for (int currentLevel = this.level; currentLevel >= level; currentLevel--) {
            List<String> moveNames = learnset.get(currentLevel);
            if (moveNames != null) {
                for (String moveName : moveNames) {
                    Move move = findMoveByName(moveName);
                    if (move != null && !moveset.contains(move)) {
                        boolean learnedInCurrentLevel = currentLevel == this.level;

                        if (moveset.size() < 4) {
                            moveset.add(move);

                            // Display the move learned message only for the current level
                            if (learnedInCurrentLevel) {
                                System.out.println(name + " learned " + moveName + "!");
                            }
                        } else {
                            System.out.println(name + " is trying to learn " + moveName + "!");
                            System.out.println("But " + name + " already knows 4 moves.");

                            // Prompt the player to choose a move to replace or skip
                            int moveIndex = promptUserForMove();

                            // Check if the input is within the valid range or 5 to skip
                            if (moveIndex >= 1 && moveIndex <= 4) {
                                // Replace the chosen move with the new move
                                Move replacedMove = moveset.set(moveIndex - 1, move);

                                // Display the move learned message only for the current level
                                if (learnedInCurrentLevel) {
                                    System.out.println(name + " forgot " + replacedMove.getName() + " and learned " + moveName + "!");
                                }
                            } else if (moveIndex == 5) {
                                // Skip move replacement
                                System.out.println(name + " did not learn " + moveName + ".");
                                return;
                            } else {
                                System.out.println("Invalid move index. " + name + " couldn't learn " + moveName + ".");
                            }
                        }
                    }
                }
            }
        }
    }

    private int promptUserForMove() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Move Replacement");
        dialog.setHeaderText("Choose a move to forget (1-4), or enter 5 to skip:");
        this.displayMoveset();
        dialog.setContentText("Move Index:");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid move index.");
                return promptUserForMove();
            }
        }
        return 5;
    }


    public void displayMoveset() {
        for (int i = 0; i < moveset.size(); i++) {
            System.out.println(moveset.get(i).getName());
        }
    }

    public Move findMoveByName(String moveName) {
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

    public int getRemainingHealth() {
        return remainingHealth;
    }

    private void faint() {
        this.fainted = true;
    }

    public String getNickname(){
        return nickname;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public StatusCondition getStatusCondition() {
        return statusCondition;
    }

    public void setStatusCondition(StatusCondition condition) {
        statusCondition = condition;
    }
    public String getSpritePath() {
        return spritePath;
    }

    public int getHp() {
        return Hp;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevelTreshhold() {
        return levelTreshhold;
    }

    public int getLevel() {
        return level;
    }

    public int getAttack() {
        return Attack;
    }

    public int getDefense() {
        return Defense;
    }

    public int getSpecialAttack() {
        return SpecialAttack;
    }

    public int getSpecialDefense() {
        return SpecialDefense;
    }

    public int getSpeed() {
        return Speed;
    }

    public int getId() {
        return id;
    }

    public String getMoves() {
        StringBuilder moves = new StringBuilder("\n");
        for (Move move : moveset) {
            moves.append(move.getName()).append("\n");
        }
        // Remove the trailing newline
        if (moves.length() > 0) {
            moves.setLength(moves.length() - 1);
        }
        return moves.toString();
    }

    public void useMove(Move move, trainerPokemon target) {
        // Apply the effects of the move to the target Pokemon
        // This is a simplified example, you should replace this with your actual move logic
        int damage = move.getPower();
        target.setRemainingHealth(target.getRemainingHealth() - damage);
    }

    public boolean isUsable() {
        return !fainted;
    }

    public List<Move> getMovesList() {
        return moveset;
    }

    public int getNumber() {
        return number;
    }

    public enum StatusCondition {
        none, BRN, PAR, SLP, FRZ, PSN;
    }
}