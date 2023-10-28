package Moves;
import PokemonLogic.*;

public interface Move {
    String getName();
    int getPower();
    int getAccuracy();
    String getType();
    void perform(Pokemon user, Pokemon target);
}