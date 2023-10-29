package BattleLogic;
import PokemonLogic.*;

public interface Move {
    String getName();
    int getPower();
    int getAccuracy();
    String getType();
    void perform(Pokemon user, Pokemon target);
    void reduceAttack(int stages);
    void reduceAttack(int stages, int chance);
    void reduceDefense(int stages);
    void reduceDefense(int stages, int chance);
    void reduceSpecialAttack(int stages);
    void reduceSpecialAttack(int stages, int chance);
    void reduceSpecialDefense(int stages);
    void reduceSpecialDefense(int stages, int chance);
    void reduceSpeed(int stages);
    void reduceSpeed(int stages, int chance);
    void raiseAttack(int stages);
    void raiseAttack(int stages, int chance);
    void raiseDefense(int stages);
    void raiseDefense(int stages, int chance);
    void raiseSpecialAttack(int stages);
    void raiseSpecialAttack(int stages, int chance);
    void raiseSpecialDefense(int stages);
    void raiseSpecialDefense(int stages, int chance);
    void raiseSpeed(int stages);
    void raiseSpeed(int stages, int chance);
    void raiseAccuracy(int stages);
    void raiseAccuracy(int stages, int chance);
    void reduceAccuracy(int stages);
    void reduceAccuracy(int stages, int chance);

    void burnChance(int chance);
    void poisonChance(int chance);
    void paralyzeChance(int chance);
    void sleepChance(int chance);

    void flinch(int chance);
    void priority();
    void critIncrease();
    void multipleAttack();
}