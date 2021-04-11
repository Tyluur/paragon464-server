package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.EnchantingSpells;
import com.paragon464.gameserver.model.item.Item;

public class RubyEnchanting extends EnchantingSpells {

    @Override
    public int[][] items() {
        return new int[][]{{1641, 2568}, // ring
            {1660, 11194}, // necklace
            {1698, 1725},// amulet
            {11085, 11088},//bracelet
        };
    }

    @Override
    public String sendReqMessage() {
        return "You need Ruby jewelry to cast this spell.";
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
        return 47;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(554, 5), new Item(564, 1)};
    }

    @Override
    public String name() {
        return "Lvl-3 Enchant";
    }

    @Override
    public double exp() {
        return 59;
    }
}
