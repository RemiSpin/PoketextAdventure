package BattleLogic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class moveFactory {
    public static Move createMoveFromJSON(JSONObject moveData) {
        String name = moveData.getString("name");

        // Construct the class name based on the move name
        String className = "PokemonLogic." + name;

        try {
            // Dynamically create an instance of the move class using reflection
            Class<?> moveClass = Class.forName(className);
            Object moveObject = moveClass.getConstructor().newInstance();

            if (moveObject instanceof Move) {
                return (Move) moveObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if the class is not found or not an instance of Move
    }

    public static List<Move> createMovesFromJSON(String jsonData) {
        List<Move> moves = new ArrayList<>();

        JSONArray movesArray = new JSONArray(jsonData);
        for (int i = 0; i < movesArray.length(); i++) {
            JSONObject moveData = movesArray.getJSONObject(i);
            Move move = createMoveFromJSON(moveData);
            if (move != null) {
                moves.add(move);
            }
        }

        return moves;
    }
}