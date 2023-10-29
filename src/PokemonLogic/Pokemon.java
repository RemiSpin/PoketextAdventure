package PokemonLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Pokemon {
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
    private JSONObject jsonData;

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
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

    // Constructor with name and level that loads data from the JSON file
    public Pokemon(String name, int level) throws IOException, JSONException {
        this.name = name;
        this.level = (byte) level;

        // Initialize IVs
        Random random = new Random();
        ivHP = random.nextInt(33);
        ivAttack = random.nextInt(33);
        ivDefense = random.nextInt(33);
        ivSpAtk = random.nextInt(33);
        ivSpDef = random.nextInt(33);
        ivSpeed = random.nextInt(33);

        // Load data from the JSON file based on the name
        loadPokemonDataFromJson();

        // Calculate stats using base stats, IVs, and level
        int hp = Hp;
        int attack = Attack;
        int defense = Defense;
        int specialAttack = SpecialAttack;
        int specialDefense = SpecialDefense;
        int speed = Speed;
        setHp(calculateHP(hp, ivHP));
        setAttack(calculateStat(attack, ivAttack));
        setDefense(calculateStat(defense, ivDefense));
        setSpecialAttack(calculateStat(specialAttack, ivSpAtk));
        setSpecialDefense(calculateStat(specialDefense, ivSpDef));
        setSpeed(calculateStat(speed, ivSpeed));

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
    }


    private String readJsonFile() throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("pokemon.json"))) {
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
        // Populate the PokemonLogic.Pokemon object from JSON data
        setNumber(jsonObject.getInt("#"));
        setType1(jsonObject.getString("Type 1"));
        setType2(jsonObject.optString("Type 2", ""));
        setHp(jsonObject.getInt("HP"));
        setAttack(jsonObject.getInt("Attack"));
        setDefense(jsonObject.getInt("Defense"));
        setSpecialAttack(jsonObject.getInt("Sp. Atk"));
        setSpecialDefense(jsonObject.getInt("Sp. Def"));
        setSpeed(jsonObject.getInt("Speed"));
        setEvolution(jsonObject.optString("Evolution", ""));
        if (jsonObject.has("EvolutionLevel") && !jsonObject.isNull("EvolutionLevel")) {
            setEvolutionLevel(jsonObject.getInt("EvolutionLevel"));
        }
        setBaseExperience(jsonObject.getInt("BaseExperience"));
        setExperienceGrowth(jsonObject.getString("ExperienceGrowth"));
    }

    public void gainExperience() throws IOException {
        int experienceGained = (int) (100 /* PUT ENEMY BASE EXPERIENCE HERE WHEN DONE */ * 5 /* PUT ENEMY LEVEL HERE WHEN DONE */ * 1.5) / 7;
        experience += experienceGained;

        // Display the experience gained message
        System.out.println(nickname + " gained " + experienceGained + " experience points.");

        while (experience >= levelTreshhold) {
            level++;
            experience -= levelTreshhold;

            if (level == evolutionLevel && !evolution.isEmpty()) {
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
                System.out.println("Congratulations! " + originalName + " has evolved into " + name + "!");
                loadPokemonDataFromJson(); // Reload data for the evolved form
            } else {
                System.out.println(nickname + " leveled up to " + level + "!");
            }

            // Recalculate stats when leveling up
            int hp = Hp;
            int attack = Attack;
            int defense = Defense;
            int specialAttack = SpecialAttack;
            int specialDefense = SpecialDefense;
            int speed = Speed;

            setHp(calculateHP(hp, ivHP));
            setAttack(calculateStat(attack, ivAttack));
            setDefense(calculateStat(defense, ivDefense));
            setSpecialAttack(calculateStat(specialAttack, ivSpAtk));
            setSpecialDefense(calculateStat(specialDefense, ivSpDef));
            setSpeed(calculateStat(speed, ivSpeed));

            // Recalculate levelTreshhold for the next level
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

            // Print the updated PokemonLogic.Pokemon info
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





    // Calculate HP using the formula
    private int calculateHP(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + level + 10);
    }

    // Calculate other stats using the formula
    private int calculateStat(int base, int iv) {
        return (int) (Math.floor(0.01 * (2 * base + iv) * level) + 5);
    }

    public String getName() {
        return name;
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

    public enum StatusCondition {
        NONE, BURNED, PARALYZED, ASLEEP, FROZEN, POISONED;
    }

    @Override
    public String toString() {
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
                "\nExperience: " + experience + " / " + levelTreshhold;
    }
}