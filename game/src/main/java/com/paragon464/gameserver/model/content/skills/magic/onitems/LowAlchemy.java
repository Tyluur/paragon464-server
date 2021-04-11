package com.paragon464.gameserver.model.content.skills.magic.onitems;

import com.paragon464.gameserver.model.content.skills.magic.Alchemy;
import com.paragon464.gameserver.model.item.Item;

public class LowAlchemy extends Alchemy {

    @Override
    public int requiredLevel() {
        return 21;
    }

    @Override
    public Item[] runes() {
        return new Item[]{new Item(554, 3), new Item(561, 1)};
    }

    @Override
    public double exp() {
        return 31;
    }

    @Override
    public int startAnim() {
        return 712;
    }

    @Override
    public int startGraphic() {
        return 112;
    }
}
