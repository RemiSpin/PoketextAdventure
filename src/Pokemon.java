import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Pokemon {
    public byte level;
    public int number;
    public String name;
    public String nickname;
    public int ivHP;
    public int ivAttack;
    public int ivDefense;
    public int ivSpAtk;
    public int ivSpDef;
    public int ivSpeed;
    public int Hp;
    public int Attack;
    public int Defense;
    public int SpecialAttack;
    public int SpecialDefense;
    public int Speed;
    public String type1;
    public String type2;
    public String evolution;
    public int evolutionLevel;
    public StatusCondition statusCondition;
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

    public int getBaseExperience() {
        if (jsonData != null && jsonData.has("BaseExperience")) {
            return jsonData.getInt("BaseExperience");
        }
        return 0;
    }

    public double getExperienceMultiplier() {
        if (jsonData != null && jsonData.has("ExperienceMultiplier")) {
            return jsonData.getDouble("ExperienceMultiplier");
        }
        return 1.0;
    }
    public void gainExperience(int defeatedPokemonLevel) {
        int baseExperience = getBaseExperience();
        double experienceMultiplier = getExperienceMultiplier();

        int levelDifference = defeatedPokemonLevel - level;

        double levelFactor = 1.0;  // Default multiplier

        if (levelDifference >= 10) {
            levelFactor = 0.5; // 10 or more level difference
        } else if (levelDifference >= 5) {
            levelFactor = 0.8; // 5 or more level difference
        } else if (levelDifference >= 3) {
            levelFactor = 0.9; // 3 or more level difference
        }

        int experienceGained = (int) (baseExperience * experienceMultiplier * levelFactor);
    }

    // Constructor with name and level that loads data from the JSON file
    public Pokemon(String name, int level) throws IOException, JSONException {
        this.name = name;
        this.level = (byte) level;
        this.nickname = null;

        // Initialize IVs
        Random random = new Random();
        ivHP = random.nextInt(33);
        ivAttack = random.nextInt(33);
        ivDefense = random.nextInt(33);
        ivSpAtk = random.nextInt(33);
        ivSpDef = random.nextInt(33);
        ivSpeed = random.nextInt(33);

        // Initialize status condition
        statusCondition = StatusCondition.NONE;

        // Load data from the JSON file based on the name
        loadPokemonDataFromJson();
    }

    private String readJsonFile(String jsonFilePath) throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        return jsonContent.toString();
    }

    // Method to load data from the JSON file based on the name
    private void loadPokemonDataFromJson() throws IOException, JSONException {

        String jsonContent = readJsonFile("pokemon.json");
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

    // Method to populate the Pokemon object from a JSON object
    private void populatePokemonFromJson(JSONObject jsonObject) throws JSONException {
        // Populate the Pokemon object from JSON data
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
        return "Name: " + name +
                "\nSpecies Name: " + nickname +
                "\nType 1: " + type1 +
                "\nType 2: " + type2 +
                "\nStatus: " + statusCondition;
    }
}