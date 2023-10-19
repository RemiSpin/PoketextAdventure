import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokemonLoader {
    private final String jsonFilePath;

    public PokemonLoader() {
        this.jsonFilePath = "pokemon.json";
    }

    public List<Pokemon> loadPokemonFromJson() throws IOException, JSONException {
        List<Pokemon> pokemonList = new ArrayList<>();
        String jsonContent = readJsonFile();

        JSONArray jsonArray = new JSONArray(jsonContent);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Pokemon pokemon = createPokemonFromJson(jsonObject);
            pokemonList.add(pokemon);
        }

        return pokemonList;
    }

    private String readJsonFile() throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        return jsonContent.toString();
    }

    private Pokemon createPokemonFromJson(JSONObject jsonObject) throws JSONException {
        Pokemon pokemon = new Pokemon();

        // Populate the Pokemon object from JSON data
        pokemon.setNumber(jsonObject.getInt("#"));
        pokemon.setName(jsonObject.getString("Name"));
        pokemon.setType1(jsonObject.getString("Type 1"));
        pokemon.setType2(jsonObject.optString("Type 2", ""));
        pokemon.setHp(jsonObject.getInt("HP"));
        pokemon.setAttack(jsonObject.getInt("Attack"));
        pokemon.setDefense(jsonObject.getInt("Defense"));
        pokemon.setSpecialAttack(jsonObject.getInt("Sp. Atk"));
        pokemon.setSpecialDefense(jsonObject.getInt("Sp. Def"));
        pokemon.setSpeed(jsonObject.getInt("Speed"));
        pokemon.setEvolution(jsonObject.optString("Evolution", ""));
        if (jsonObject.has("EvolutionLevel") && !jsonObject.isNull("EvolutionLevel")) {
            pokemon.setEvolutionLevel(jsonObject.getInt("EvolutionLevel"));
        }

        return pokemon;
    }
}