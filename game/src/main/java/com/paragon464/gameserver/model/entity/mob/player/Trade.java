package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.item.Item;

public final class Trade {
    private final Item[] sentItems;
    private final Item[] receivedItems;
    private final Player sender;
    private final Player receipient;

    public Trade(Item[] sentItems, Item[] receivedItems, Player sender, Player receipient) {
        this.sentItems = sentItems;
        this.receivedItems = receivedItems;
        this.sender = sender;
        this.receipient = receipient;
    }

    public Item[] getSentItems() {
        return sentItems;
    }

    public Item[] getReceivedItems() {
        return receivedItems;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceipient() {
        return receipient;
    }
}
