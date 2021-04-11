package com.paragon464.gameserver.model.shop.impl;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;

public class PestShopSession extends ShopSession {

    public PestShopSession(Player player, Shop shop) {
        super(player, shop);
        player.getFrames().modifyText("Pest control points: " + player.getAttributes().getInt("zombies_points"), 614, 28);
    }

    public static boolean wandNeeded(final Player player) {
        int wep = player.getEquipment().getItemInSlot(3);
        int lvl = player.getAttributes().getInt("wand_lvl");
        switch (lvl) {
            case 0:
                return (wep == 6908);
            case 1:
                return (wep == 6910);
            case 2:
                return wep == 6912;
        }
        return false;
    }

    @Override
    public boolean purchaseItem(int slot, int initAmount) {
        if (slot < 0 || slot > 40) {
            return false;
        }
        ShopItem shopItem = getStockItem(slot);
        if (shopItem == null) {
            return false;
        }
        if (shopItem.getId() >= 6908 && shopItem.getId() <= 6914) {
            if (!canBuyWand(player, shopItem)) {
                return false;
            }
        }
        return super.purchaseItem(slot, initAmount);
    }

    private static boolean canBuyWand(final Player player, final Item item) {
        int casts = player.getAttributes().getInt("wand_cast");
        int lvl = player.getAttributes().getInt("wand_lvl");
        switch (item.getId()) {
            case 6910:// Apprentice
                if (casts < 100)
                    player.getFrames()
                        .sendMessage("You must use spells with your Beginner wand 100 times in Pest control first.");
                return (lvl >= 1);
            case 6912:// Teacher
                if (casts < 200)
                    player.getFrames()
                        .sendMessage("You must use spells with an Apprentice wand 100 times in Pest control first.");
                return (lvl >= 2);
            case 6914:// Master
                if (casts < 300)
                    player.getFrames()
                        .sendMessage("You must use spells with a Teacher wand 100 times in Pest control first.");
                return (lvl >= 3);
        }
        return true;
    }
}
