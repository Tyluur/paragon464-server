package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.EnchantingSpells;
import com.paragon464.gameserver.model.item.Item;

public class SapphireEnchanting extends EnchantingSpells {

    @Override
    public int[][] items() {
        return new int[][]{{1637, 2550}, // ring
            {1656, 3853}, // necklace
            {1694, 1727},// amulet
            {11072, 3853}//bracelet
        };
    }

    @Override
    public String sendReqMessage() {
        return "You need Sapphire jewelry to cast this spell.";
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
        return 7;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(555, 1), new Item(564, 1)};
    }

    @Override
    public String name() {
        return "Lvl-1 Enchant";
    }

    @Override
    public double exp() {
        return 17.5;
    }
}
