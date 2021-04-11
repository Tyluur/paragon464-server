package com.paragon464.gameserver.model.shop.impl;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;

public class DefaultShopSession extends ShopSession {

    public DefaultShopSession(Player player, Shop shop) {
        super(player, shop);
    }
}
