package com.paragon464.gameserver.model.shop;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.shop.impl.DefaultShopSession;

import java.util.ArrayList;
import java.util.List;

public class ShopManager {

    public static List<Shop> shop_definitions = new ArrayList<>();

    public static void handleTabs(final Player player, boolean mainTab) {
        ShopSession session = player.getAttributes().get("shop_session");
        if (session != null) {
            int currentId = session.shop.getId();
            if (!mainTab) {
                int next = session.shop.getId() + 1;
                if (next > currentId + 1)
                    return;
                Shop shop = getShop(next);
                if (shop == null) {
                    return;
                }
                session.setStock(shop.getStock());
                session.refresh();
                session.handleTabs(false);
            } else {
                Shop shop = getShop(session.shop.getId());
                if (shop == null) {
                    return;
                }
                session.setStock(shop.getStock());
                session.refresh();
                session.handleTabs(true);
            }
        }
    }

    public static Shop getShop(int id) {
        for (Shop shops : shop_definitions) {
            if (shops.getId() == id) {
                return shops;
            }
        }
        return null;
    }

    public static void valueItem(final Player player, final int slot, final int interfaceId) {
        ShopSession session = player.getAttributes().get("shop_session");
        if (session != null) {
            final ShopItem shopItem;
            if (interfaceId == 614) {
                shopItem = session.getStock()[slot];
            } else {
                shopItem = null;
            }
            session.value(shopItem == null ? player.getInventory().get(slot) : shopItem, interfaceId);
        }
    }

    public static void purchaseItem(final Player player, final int slot, final int initialAmount) {
        ShopSession session = player.getAttributes().get("shop_session");
        if (session != null) {
            session.purchaseItem(slot, initialAmount);
        }
    }

    public static void sellItem(final Player player, final int slot, final int initialAmount) {
        ShopSession session = player.getAttributes().get("shop_session");
        if (session != null) {
            session.sellItem(slot, initialAmount);
        }
    }

    public static void openShop(Player player, int id) {
        Shop shop = getShop(id);
        if (shop == null) {
            return;
        }
        ShopSession session = new DefaultShopSession(player, shop);
        player.getAttributes().set("shop_session", session);
    }

    public static void refreshShopping(Player player) {
        player.getInventory().refresh();
        // player.getFrames().sendItems(301, 0, 93,
        // player.getInventory().getItems());
        // player.getFrames().sendItems(300, 75, 93, shop.getStock());
    }
}
