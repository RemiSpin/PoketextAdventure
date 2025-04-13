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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import BattleLogic.Move;
import BattleLogic.moveFactory;
import BattleLogic.trainerPokemon;
import javafx.scene.control.TextInputDialog;

@SuppressWarnings({ "unused", "FieldMayBeFinal", "OverridableMethodCallInConstructor", "static-access" })

public class Pokemon implements Cloneable, IPokemon {
    private byte level;
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
    private static final Logger logger = LoggerFactory.getLogger(Pokemon.class);

    public void setSpritePath(String spritePath) {
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
    public Pokemon(String name, int level) throws JSONException {
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

        // Load data from the JSON file based on the name
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
            switch (experienceGrowth) {
                case "Fast" -> {
                    levelTreshhold = (int) ((Math.pow(level + 1, 3) * 0.8) - (Math.pow(level, 3) * 0.8));
                }
                case "MFast" -> {
                    levelTreshhold = (int) ((Math.pow(level + 1, 3) - Math.pow(level, 3)));
                }
                case "MSlow" -> {
                    levelTreshhold = (int) ((1.2 * Math.pow(level + 1, 3) - 15 * Math.pow(level + 1, 2)
                            + 100 * (level + 1) - 140)
                            - (1.2 * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140));
                }
                case "Slow" -> {
                    levelTreshhold = (int) ((1.25 * Math.pow(level + 1, 3)) - (1.25 * Math.pow(level, 3)));
                }
            }
            nickname = name;
            experience = 0;
            remainingHealth = Hp;
        } catch (IOException | JSONException e) {
            logger.error("Error initializing Pokemon {}: {}", name, e.getMessage());
            throw new RuntimeException("Failed to initialize Pokemon: " + e.getMessage(), e);
        }
    }

    public org.json.simple.JSONArray readJsonFile(String filename) {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/" + filename))) {
            return (org.json.simple.JSONArray) new JSONParser().parse(reader);
        } catch (IOException e) {
            logger.error("Error reading JSON file {}: {}", filename, e.getMessage());
            throw new RuntimeException("Failed to read JSON file: " + e.getMessage(), e);
        } catch (org.json.simple.parser.ParseException e) {
            logger.error("Error parsing JSON file {}: {}", filename, e.getMessage());
            throw new RuntimeException("Failed to parse JSON file: " + e.getMessage(), e);
        }
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
        Object numObj = jsonObject.get("#");
        setNumber(numObj != null ? (numObj instanceof Integer ? (Integer) numObj : Integer.parseInt(numObj.toString()))
                : 0);
        setType1(jsonObject.get("Type 1").toString());
        setType2(jsonObject.get("Type 2") != null ? jsonObject.get("Type 2").toString() : "");
        Object hpObj = jsonObject.get("HP");
        setHp(hpObj != null ? (hpObj instanceof Integer ? (Integer) hpObj : Integer.parseInt(hpObj.toString())) : 0);
        Object attackObj = jsonObject.get("Attack");
        setAttack(attackObj != null
                ? (attackObj instanceof Integer ? (Integer) attackObj : Integer.parseInt(attackObj.toString()))
                : 0);
        Object defenseObj = jsonObject.get("Defense");
        setDefense(defenseObj != null
                ? (defenseObj instanceof Integer ? (Integer) defenseObj : Integer.parseInt(defenseObj.toString()))
                : 0);
        Object spAtkObj = jsonObject.get("Sp. Atk");
        setSpecialAttack(spAtkObj != null
                ? (spAtkObj instanceof Integer ? (Integer) spAtkObj : Integer.parseInt(spAtkObj.toString()))
                : 0);
        Object spDefObj = jsonObject.get("Sp. Def");
        setSpecialDefense(spDefObj != null
                ? (spDefObj instanceof Integer ? (Integer) spDefObj : Integer.parseInt(spDefObj.toString()))
                : 0);
        Object speedObj = jsonObject.get("Speed");
        setSpeed(speedObj != null
                ? (speedObj instanceof Integer ? (Integer) speedObj : Integer.parseInt(speedObj.toString()))
                : 0);
        setEvolution(jsonObject.get("Evolution") != null ? jsonObject.get("Evolution").toString() : "");
        Object evolutionLevelObj = jsonObject.get("EvolutionLevel");
        if (evolutionLevelObj != null) {
            setEvolutionLevel(evolutionLevelObj instanceof Integer ? (Integer) evolutionLevelObj
                    : Integer.parseInt(evolutionLevelObj.toString()));
        } else {
            setEvolutionLevel(0);
        }
        Object baseExpObj = jsonObject.get("BaseExperience");
        setBaseExperience(baseExpObj != null
                ? (baseExpObj instanceof Integer ? (Integer) baseExpObj : Integer.parseInt(baseExpObj.toString()))
                : 0);
        setExperienceGrowth(jsonObject.get("ExperienceGrowth").toString());
        setSpritePath(jsonObject.get("Sprite").toString());
    }

    public void gainExperience(IPokemon defeatedPokemon) throws IOException {
        if (level < 100) {
            // Get the base experience and level from the defeated Pokémon
            int enemyBaseExp = 0;
            int enemyLevel = defeatedPokemon.getLevel();

            if (defeatedPokemon instanceof Pokemon) {
                // For regular Pokemon, directly access the baseExperience field
                Pokemon defeatedPoke = (Pokemon) defeatedPokemon;
                enemyBaseExp = defeatedPoke.baseExperience;
            } else if (defeatedPokemon instanceof trainerPokemon) {
                // For trainer Pokemon, try to get baseExperience through the method
                enemyBaseExp = ((trainerPokemon) defeatedPokemon).getBaseExperience();

                // If baseExperience is still 0, look up by name in our JSON data
                if (enemyBaseExp <= 0) {
                    // Look up the Pokemon's base experience in our JSON data by name
                    String pokemonName = defeatedPokemon.getName();
                    JSONArray pokemonData = readJsonFile("pokemon.json");

                    for (Object obj : pokemonData) {
                        JSONObject jsonPokemon = (JSONObject) obj;
                        if (jsonPokemon.get("Name").toString().equalsIgnoreCase(pokemonName)) {
                            Object baseExpObj = jsonPokemon.get("BaseExperience");
                            if (baseExpObj != null) {
                                enemyBaseExp = baseExpObj instanceof Integer ? (Integer) baseExpObj
                                        : Integer.parseInt(baseExpObj.toString());
                                break;
                            }
                        }
                    }
                }
            }

            // Calculate experience based on the formula
            int experienceGained = (int) (enemyBaseExp * enemyLevel * 1.5) / 7;
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
                        // If the nickname is the same as the species name, set it to the evolved form's
                        // name
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
                    case "Fast" -> {
                        levelTreshhold = (int) ((Math.pow(level + 1, 3) * 0.8) - (Math.pow(level, 3) * 0.8));
                    }
                    case "MFast" -> {
                        levelTreshhold = (int) ((Math.pow(level + 1, 3) - Math.pow(level, 3)));
                    }
                    case "MSlow" -> {
                        levelTreshhold = (int) ((1.2 * Math.pow(level + 1, 3) - 15 * Math.pow(level + 1, 2)
                                + 100 * (level + 1) - 140)
                                - (1.2 * Math.pow(level, 3) - 15 * Math.pow(level, 2) + 100 * level - 140));
                    }
                    case "Slow" -> {
                        levelTreshhold = (int) ((1.25 * Math.pow(level + 1, 3)) - (1.25 * Math.pow(level, 3)));
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

    /**
     * Calculates the HP stat using the standard Pokemon formula.
     * HP = floor(0.01 * (2 * base + iv) * level) + level + 10
     * 
     * @param base Base HP value
     * @param iv   Individual Value for HP (0-31)
     * @return The calculated HP stat
     */
    private int calculateHP(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + level + 10);
    }

    /**
     * Calculates a non-HP stat using the standard Pokemon formula.
     * Stat = floor(0.01 * (2 * base + iv) * level) + 5
     * 
     * @param base Base stat value
     * @param iv   Individual Value for the stat (0-31)
     * @return The calculated stat value
     */
    private int calculateStat(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + 5);
    }

    /**
     * Sets the remaining health and handles fainting if health reaches 0.
     * 
     * @param remainingHealth New HP value to set
     */
    public void setRemainingHealth(int remainingHealth) {
        this.remainingHealth = Math.max(0, remainingHealth);
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
                                    System.out.println(name + " forgot " + replacedMove.getName() + " and learned "
                                            + moveName + "!");
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

    public String getNickname() {
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

    @Override
    public int getSpecialAttack() {
        return this.SpecialAttack;
    }

    @Override
    public int getSpecialDefense() {
        return this.SpecialDefense;
    }

    public int getSpeed() {
        return Speed;
    }

    public int getId() {
        return id;
    }

    public String getMoves() {
        StringBuilder movesString = new StringBuilder("\n");
        for (Move move : moveset) {
            movesString.append(move.getName()).append("\n");
        }
        // Remove the trailing newline
        if (movesString.length() > 0) {
            movesString.setLength(movesString.length() - 1);
        }
        return movesString.toString();
    }

    public void useMove(Move move, trainerPokemon target) {
        // Apply the effects of the move to the target Pokemon
        int damage = move.getPower();
        target.setRemainingHealth(target.getRemainingHealth() - damage);
    }

    public boolean isUsable() {
        return !fainted;
    }

    /**
     * Get the list of moves this Pokemon knows.
     * This method name is standardized for the IPokemon interface.
     */
    @Override
    public List<Move> getMovesList() {
        return moveset;
    }

    public int getNumber() {
        return number;
    }

    public enum StatusCondition {
        none, BRN, PAR, SLP, FRZ, PSN;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}