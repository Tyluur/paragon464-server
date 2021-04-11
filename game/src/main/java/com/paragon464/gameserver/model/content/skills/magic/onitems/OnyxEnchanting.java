package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.EnchantingSpells;
import com.paragon464.gameserver.model.item.Item;

public class OnyxEnchanting extends EnchantingSpells {

    @Override
    public int[][] items() {
        return new int[][]{{6575, 6583}, // ring
            {6577, 11128}, // necklace
            {6581, 6585},// amulet
            {11130, 11133}, //bracelet
        };
    }

    @Override
    public String sendReqMessage() {
        return "You need Onyx jewelry to cast this spell.";
    }

    @Override
    public int startAnim() {
        return 721;
    }

    @Override
    public int startGraphic() {
        return 452;
    }

    @Override
    public int requiredLevel() {
        return 87;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(557, 20), new Item(554, 20), new Item(564, 1)};
    }

    @Override
    public String name() {
        return "Lvl-6 Enchant";
    }

    @Override
    public double exp() {
        return 97;
    }
}
