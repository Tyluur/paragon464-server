package com.paragon464.gameserver.model.entity.mob.npc.drops;

import com.paragon464.gameserver.model.item.Item;

public class DropItem extends Item {
    private final int min;
    private boolean rare;
    private double chance;

    public DropItem(final int id, int min, final int max) {
        super(id, max);
        this.min = min;
        this.chance = 100;
    }

    public DropItem(int id, int min, int max, double chance, boolean rare) {
        super(id, max);
        this.min = min;
        this.chance = chance;
        this.rare = rare;
    }

    public DropItem withMax(final int max) {
        return getAmount() == max ? this : new DropItem(getId(), min, max, chance, rare);
    }

    public int getMin() {
        return min;
    }

    public int getExtraAmount() {
        if ((min > 0 && this.getAmount() > 0) && (min < this.getAmount()))
            return this.getAmount() - min;
        return 0;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public boolean isRare() {
        return rare;
    }
}
