package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.item.Item;

public enum StrungAmulet {

    GOLD(new Item(1673), new Item(1692), 8),
    SAPPHIRE(new Item(1675), new Item(1694), 24),
    EMERALD(new Item(1677), new Item(1696), 31),
    RUBY(new Item(1679), new Item(1698), 50),
    DIAMOND(new Item(1681), new Item(1700), 70),
    DRAGONSTONE(new Item(1683), new Item(1702), 80),
    ONYX(new Item(6579), new Item(6581), 90);

    private Item unfinished;
    private Item finished;
    private int level;

    StrungAmulet(Item unf, Item fin, int lvl) {
        this.unfinished = unf;
        this.finished = fin;
        this.level = lvl;
    }

    public Item getUnfinished() {
        return unfinished;
    }

    public Item getFinished() {
        return finished;
    }

    public int getLevel() {
        return level;
    }

}
