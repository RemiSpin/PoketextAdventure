package BattleLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trainer {
    private String name;
    private int rewardMoney;
    private List<trainerPokemon> pokemonList;

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
}