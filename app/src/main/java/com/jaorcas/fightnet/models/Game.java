package com.jaorcas.fightnet.models;

import com.jaorcas.fightnet.enums.EnumGames;

public class Game {

    private String name;
    private EnumGames enumGame;

    public Game(){}

    public Game(String name, EnumGames enumGame) {
        this.name = name;
        this.enumGame = enumGame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumGames getEnumGame() {
        return enumGame;
    }

    public void setEnumGame(EnumGames enumGame) {
        this.enumGame = enumGame;
    }
}
