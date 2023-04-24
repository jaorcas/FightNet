package com.jaorcas.fightnet.models;

public class Attack {

    private Integer startUpFrames, framesInBlock, damageRaw, damageScaled;

    public Attack(){

    }
    public Attack(Integer startUpFrames, Integer framesInBlock, Integer damageRaw, Integer damageScaled) {
        this.startUpFrames = startUpFrames;
        this.framesInBlock = framesInBlock;
        this.damageRaw = damageRaw;
        this.damageScaled = damageScaled;
    }

    public Integer getStartUpFrames() {
        return startUpFrames;
    }

    public void setStartUpFrames(Integer startUpFrames) {
        this.startUpFrames = startUpFrames;
    }

    public Integer getFramesInBlock() {
        return framesInBlock;
    }

    public void setFramesInBlock(Integer framesInBlock) {
        this.framesInBlock = framesInBlock;
    }

    public Integer getDamageRaw() {
        return damageRaw;
    }

    public void setDamageRaw(Integer damageRaw) {
        this.damageRaw = damageRaw;
    }

    public Integer getDamageScaled() {
        return damageScaled;
    }

    public void setDamageScaled(Integer damageScaled) {
        this.damageScaled = damageScaled;
    }
}
