package com.paragon464.gameserver.model.item;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Tolerate;
import lombok.experimental.Wither;
import javax.annotation.Nullable;

@Value @NonFinal @Wither
public class Item {

    private final int id;
    private final int amount;

    @Tolerate
    public Item(final int id) {
        this(id, 1);
    }

    @Nullable
    public ItemDefinition getDefinition() {
        return ItemDefinition.forId(id);
    }
}