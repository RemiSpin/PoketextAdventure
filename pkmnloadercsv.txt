import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokemonLoader {
    public static List<Pokemon> loadPokemonFromCSV(String filename) {
        List<Pokemon> pokemonList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true; // Skip the header line
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 12) {
                    String name = data[1];
                    int baseHP = Integer.parseInt(data[5]);
                    int attack = Integer.parseInt(data[6]);
                    int defense = Integer.parseInt(data[7]);
                    int spAtk = Integer.parseInt(data[8]);
                    int spDef = Integer.parseInt(data[9]);
                    int speed = Integer.parseInt(data[10]);
                    String type1 = data[2];
                    String type2 = data[3].isEmpty() ? null : data[3];

                    Pokemon pokemon = new Pokemon(name, baseHP, attack, defense, spAtk, spDef, speed, type1, type2);
                    pokemonList.add(pokemon);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pokemonList;
    }
}
