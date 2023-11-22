package PokemonLogic;

public class Exceptions extends Exception {
    public static class PokemonLevelException extends Throwable {
        public PokemonLevelException() {
            super("wrong level dumbass");
        }
    }
    public static class PokemonNameException extends Throwable {
        public PokemonNameException() {
            super("the pokemon does not exist stupid");
        }
    }
}