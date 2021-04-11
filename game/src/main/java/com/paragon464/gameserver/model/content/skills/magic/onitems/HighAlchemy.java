package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.Alchemy;
import com.paragon464.gameserver.model.item.Item;

public class HighAlchemy extends Alchemy {

    @Override
    public int requiredLevel() {
        return 55;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(554, 5), new Item(561, 1)};
    }

    @Override
    public double exp() {
        return 65;
    }

    @Override
    public int startAnim() {
        return 9633;
    }

    @Override
    public int startGraphic() {
        return 1693;
    }
}
