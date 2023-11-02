package BattleLogic;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class moveFactory {
    private Map<String, Map<Integer, List<String>>> pokemonLearnsets;
    public static List<Move> createMovesFromJson() {
        List<Move> moves = new ArrayList<>();

        try (FileReader reader = new FileReader("src/BattleLogic/moves.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray moveArray = new JSONArray(tokener);

            for (int i = 0; i < moveArray.length(); i++) {
                JSONObject moveJson = moveArray.getJSONObject(i);
                Move move = createMoveFromJson(moveJson);
                moves.add(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return moves;
    }

    public void loadPokemonLearnsets() {
        try (FileReader reader = new FileReader("src/BattleLogic/movesets.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject data = new JSONObject(tokener);

            for (String pokemonName : data.keySet()) {
                JSONObject pokemonData = data.getJSONObject(pokemonName);
                JSONArray movesetArray = pokemonData.getJSONArray("moveset");

                Map<Integer, List<String>> learnset = new HashMap<>();
                for (int i = 0; i < movesetArray.length(); i++) {
                    JSONObject moveData = movesetArray.getJSONObject(i);
                    String moveName = moveData.getString("move");
                    int level = moveData.getInt("level");

                    learnset.computeIfAbsent(level, k -> new ArrayList<>()).add(moveName);
                }

                pokemonLearnsets.put(pokemonName, learnset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Move createMoveFromJson(JSONObject moveJson) {
        // Extract move attributes from the JSON object
        String name = moveJson.getString("name");
        String type = moveJson.getString("type");
        String category = moveJson.getString("category");
        int power = moveJson.getInt("power");
        double accuracy = moveJson.getDouble("accuracy");

        // Create a concrete class that implements the Move interface
        Move move = new Move() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getPower() {
                return power;
            }

            @Override
            public int getAccuracy() {
                return (int) (accuracy * 100);
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public void reduceAttack(int stages) {

            }

            @Override
            public void reduceAttack(int stages, int chance) {

            }

            @Override
            public void reduceDefense(int stages) {

            }

            @Override
            public void reduceDefense(int stages, int chance) {

            }

            @Override
            public void reduceSpecialAttack(int stages) {

            }

            @Override
            public void reduceSpecialAttack(int stages, int chance) {

            }

            @Override
            public void reduceSpecialDefense(int stages) {

            }

            @Override
            public void reduceSpecialDefense(int stages, int chance) {

            }

            @Override
            public void reduceSpeed(int stages) {

            }

            @Override
            public void reduceSpeed(int stages, int chance) {

            }

            @Override
            public void raiseAttack(int stages) {

            }

            @Override
            public void raiseAttack(int stages, int chance) {

            }

            @Override
            public void raiseDefense(int stages) {

            }

            @Override
            public void raiseDefense(int stages, int chance) {

            }

            @Override
            public void raiseSpecialAttack(int stages) {

            }

            @Override
            public void raiseSpecialAttack(int stages, int chance) {

            }

            @Override
            public void raiseSpecialDefense(int stages) {

            }

            @Override
            public void raiseSpecialDefense(int stages, int chance) {

            }

            @Override
            public void raiseSpeed(int stages) {

            }

            @Override
            public void raiseSpeed(int stages, int chance) {

            }

            @Override
            public void raiseAccuracy(int stages) {

            }

            @Override
            public void raiseAccuracy(int stages, int chance) {

            }

            @Override
            public void reduceAccuracy(int stages) {

            }

            @Override
            public void reduceAccuracy(int stages, int chance) {

            }

            @Override
            public void burnChance(int chance) {

            }

            @Override
            public void poisonChance(int chance) {

            }

            @Override
            public void paralyzeChance(int chance) {

            }

            @Override
            public void sleepChance(int chance) {

            }

            @Override
            public void flinch(int chance) {

            }

            @Override
            public void priority() {

            }

            @Override
            public void critIncrease() {

            }

            @Override
            public void multipleAttack() {

            }
        };
        return move;
    }
}