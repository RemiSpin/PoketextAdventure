package BattleLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trainer {
    private String name;
    private int rewardMoney;
    private List<trainerPokemon> pokemonList;
    private trainerPokemon currentPokemon;

    public Trainer(String name, int rewardMoney, trainerPokemon... pokemons) {
        this.name = name;
        this.rewardMoney = rewardMoney;
        this.pokemonList = new ArrayList<>(Arrays.asList(pokemons));
    }

    public String getName() {
        return name;
    }

    public int getRewardMoney() {
        return rewardMoney;
    }

    public List<trainerPokemon> getPokemonList() {
        return pokemonList;
    }

    public boolean hasUsablePokemon() {
        for (trainerPokemon pokemon : pokemonList) {
            if (pokemon.getRemainingHealth() > 0) {
                return true;
            }
        }
        return false;
    }

    public trainerPokemon getCurrentPokemon() {
        return currentPokemon;
    }
}