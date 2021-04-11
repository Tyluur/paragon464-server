package com.paragon464.gameserver.model.entity.mob.npc.drops;

public class GenericTable {

    private String name;
    private double chance;

    public GenericTable(String name, double chance) {
        this.setName(name);
        this.setChance(chance);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
