package com.jaorcas.fightnet.models;

public class Attack extends TableRowAttack {

    private String attackName, startUp, block;

    public Attack(){

    }
    public Attack(String attackName, String startUp, String block) {
        this.attackName = attackName;
        this.startUp = startUp;
        this.block = block;

    }

    public String getStartUp() {
        return startUp;
    }

    public void setStartUp(String startUp) {
        this.startUp = startUp;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }
}
