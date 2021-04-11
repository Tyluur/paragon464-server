package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.EnchantingSpells;
import com.paragon464.gameserver.model.item.Item;

public class EmeraldEnchanting extends EnchantingSpells {

    @Override
    public int[][] items() {
        return new int[][]{{1639, 2552}, // ring
            {1658, 5521}, // necklace
            {1696, 1729},// amulet
            {11076, 11079}, //bracelet
        };
    }

    @Override
    public String sendReqMessage() {
        return "You need Emerald jewelry to cast this spell.";
    }

    @Override
    public int startAnim() {
        return 719;
    }

    @Override
    public int startGraphic() {
        return 114;
    }

    @Override
    public int requiredLevel() {
        return 27;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(556, 3), new Item(564, 1)};
    }

    @Override
    public String name() {
        return "Lvl-2 Enchant";
    }

    @Override
    public double exp() {
        return 37;
    }
}
