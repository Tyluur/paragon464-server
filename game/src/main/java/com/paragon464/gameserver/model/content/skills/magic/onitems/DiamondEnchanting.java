package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.EnchantingSpells;
import com.paragon464.gameserver.model.item.Item;

public class DiamondEnchanting extends EnchantingSpells {

    @Override
    public int[][] items() {
        return new int[][]{{1643, 2570}, // ring
            {1662, 11090}, // necklace
            {1700, 1731},// amulet
        };
    }

    @Override
    public String sendReqMessage() {
        return "You need Diamond jewelry to cast this spell.";
    }

    @Override
    public int startAnim() {
        return 720;
    }

    @Override
    public int startGraphic() {
        return 115;
    }

    @Override
    public int requiredLevel() {
        return 57;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(557, 10), new Item(564, 1)};
    }

    @Override
    public String name() {
        return "Lvl-4 Enchant";
    }

    @Override
    public double exp() {
        return 67;
    }
}
