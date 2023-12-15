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
    public Map<String, Map<Integer, List<String>>> pokemonLearnsets = new HashMap<>(); // Map of pokemon names and their learnsets, with their name being the key

    public static List<Move> createMovesFromJson() {
        List<Move> moves = new ArrayList<>();

        try (FileReader reader = new FileReader("src/BattleLogic/moves.json")) {
            JSONTokener tokener = new JSONTokener(reader); // reads JSON from stream
            JSONArray moveArray = new JSONArray(tokener); // creates an array from JSON objects

            for (int i = 0; i < moveArray.length(); i++) {
                JSONObject moveJson = moveArray.getJSONObject(i); // JSON from i
                Move move = createMoveFromJson(moveJson); // creates the move
                moves.add(move); // adds it to the list of moves
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

            for (String pokemonName : data.keySet()) { // iterate through the keys
                JSONObject pokemonData = data.getJSONObject(pokemonName); // get the data from the key
                JSONArray movesetArray = pokemonData.getJSONArray("moveset"); // get the moveset with the help of the key

                Map<Integer, List<String>> learnset = new HashMap<>(); //create a new map for the learnset
                for (int i = 0; i < movesetArray.length(); i++) { //iterate through moveset
                    JSONObject moveData = movesetArray.getJSONObject(i); // get data from learnset
                    String moveName = moveData.getString("move"); // get move name
                    int level = moveData.getInt("level"); // get level

                    learnset.computeIfAbsent(level, k -> new ArrayList<>()).add(moveName); // add move to learnset
                }

                pokemonLearnsets.put(pokemonName, learnset); // add learnset to pokemon
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Move createMoveFromJson(JSONObject moveJson) {
        String name = moveJson.getString("name");
        String type = moveJson.getString("type");
        String category = moveJson.getString("category");
        int power = moveJson.getInt("power");
        double accuracy = moveJson.getDouble("accuracy");
        int pp = moveJson.getInt("pp");

        // Concrete class that implements the Move interface
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
            public int getPp() {
                return pp;
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