package com.paragon464.gameserver.model.entity.mob.npc.drops;

public class UniqueTable {

    private int id;
    private int minimum;
    private int maximum;
    private double chance;

    public UniqueTable(int id, int min, int max, double chance) {
        this.setId(id);
        this.setMinimum(min);
        this.setMaximum(max);
        this.setChance(chance);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getExtraAmount() {
        if ((minimum > 0 && maximum > 0) && (minimum < maximum))
            return maximum - minimum;
        return 0;
    }
}
