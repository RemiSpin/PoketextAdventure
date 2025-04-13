package PokemonLogic;

import java.util.List;

import BattleLogic.Move;

/**
 * Common interface for both Pokemon and trainerPokemon classes.
 * This centralizes the shared functionality and allows for cleaner code
 * in the Battle class.
 */
public interface IPokemon {
    String getName();

    String getNickname();

    int getLevel();

    int getHp();

    int getRemainingHealth();

    void setRemainingHealth(int health);

    int getAttack();

    int getDefense();

    int getSpeed();

    String getType1();

    String getType2();

    String getSpritePath();

    List<Move> getMovesList();
}
