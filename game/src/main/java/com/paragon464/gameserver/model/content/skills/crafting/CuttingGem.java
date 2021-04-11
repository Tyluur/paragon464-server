package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.item.Item;

public enum CuttingGem {

    OPAL(new Item(1625), new Item(1609), 1, 15.0, 886),
    JADE(new Item(1627), new Item(1611), 13, 20.0, 886),
    RED_TOPAZ(new Item(1629), new Item(1613), 16, 25.0, 887),
    SAPPHIRE(new Item(1623), new Item(1607), 20, 50.0, 888),
    EMERALD(new Item(1621), new Item(1605), 27, 67.5, 887),
    RUBY(new Item(1619), new Item(1603), 34, 85.0, 887),
    DIAMOND(new Item(1617), new Item(1601), 43, 107.5, 886),
    DRAGONSTONE(new Item(1631), new Item(1615), 55, 137.5, 885),
    ONYX(new Item(6571), new Item(6573), 67, 167.5, 2717),
    ;

    private Item uncut;
    private Item cut;
    private int level;
    private double exp;
    private int animation;

    CuttingGem(Item uncut, Item cut, int level, double exp, int animation) {
        this.uncut = uncut;
        this.cut = cut;
        this.level = level;
        this.exp = exp;
        this.animation = animation;
    }

    public Item getUncut() {
        return uncut;
    }

    public Item getCut() {
        return cut;
    }

    public int getLevel() {
        return level;
    }

    public double getExp() {
        return exp;
    }

    public int getAnimation() {
        return animation;
    }
}
