package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.EnchantingSpells;
import com.paragon464.gameserver.model.item.Item;

public class DragonStoneEnchanting extends EnchantingSpells {

    @Override
    public int[][] items() {
        return new int[][]{{1645, 2572}, // ring
            {1664, 11105}, // necklace
            {1702, 1712}, // amulet
            {11115, 11118}// bracelet
        };
    }

    @Override
    public String sendReqMessage() {
        return "You need DragonStone jewelry to cast this spell.";
    }

    @Override
    public int startAnim() {
        return 721;
    }

    @Override
    public int startGraphic() {
        return 116;
    }

    @Override
    public int requiredLevel() {
        return 68;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(555, 15), new Item(557, 15), new Item(564, 1)};
    }

    @Override
    public String name() {
        return "Lvl-5 Enchant";
    }

    @Override
    public double exp() {
        return 78;
    }
}
