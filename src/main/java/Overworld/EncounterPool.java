package Overworld;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import PokemonLogic.Pokemon;

public class EncounterPool {
    private static JSONObject encounterPoolData;
    private static final Random random = new Random();

    // Initialize the encounter pool data from JSON
    static {
        try {
            InputStream inputStream = EncounterPool.class.getResourceAsStream("/EncounterPool.json");
            if (inputStream != null) {
                try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                    String jsonContent = scanner.useDelimiter("\\A").next();
                    encounterPoolData = new JSONObject(jsonContent);
                }
                inputStream.close();
            } else {
                System.err.println("Error: EncounterPool.json not found in resources.");
                encounterPoolData = new JSONObject();
            }
        } catch (Exception e) {
            System.err.println("Error loading EncounterPool.json: " + e.getMessage());
            encounterPoolData = new JSONObject();
        }
    }

    /**
     * Get a random Pokemon from the route's encounter pool
     * 
     * @param routeName Name of the route (e.g., "Route 1")
     * @return A randomly generated Pokemon from the route's encounter pool, or null
     *         if no encounters defined
     */
    public static Pokemon getRandomEncounter(String routeName) {
        if (!encounterPoolData.has("routes") || !encounterPoolData.getJSONObject("routes").has(routeName)) {
            System.err.println("No encounter data found for " + routeName);
            return null;
        }

        JSONObject routeData = encounterPoolData.getJSONObject("routes").getJSONObject(routeName);
        JSONArray encounters = routeData.getJSONArray("encounters");

        if (encounters.length() == 0) {
            return null;
        }

        // Calculate total weight
        int totalWeight = 0;
        for (int i = 0; i < encounters.length(); i++) {
            JSONObject encounter = encounters.getJSONObject(i);
            totalWeight += encounter.getInt("weight");
        }

        // Random number between 0 and totalWeight
        int randomValue = random.nextInt(totalWeight);

        // Select a Pokemon based on weight
        int currentWeight = 0;
        for (int i = 0; i < encounters.length(); i++) {
            JSONObject encounter = encounters.getJSONObject(i);
            currentWeight += encounter.getInt("weight");

            if (randomValue < currentWeight) {
                String pokemonName = encounter.getString("pokemon");
                int minLevel = encounter.getInt("minLevel");
                int maxLevel = encounter.getInt("maxLevel");

                // Generate random level within range
                int level = random.nextInt(maxLevel - minLevel + 1) + minLevel;

                return new Pokemon(pokemonName, level);
            }
        }

        // This should never happen if weights are positive
        return null;
    }
}