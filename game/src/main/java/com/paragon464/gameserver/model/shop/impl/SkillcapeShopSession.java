package com.paragon464.gameserver.model.shop.impl;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;

public class SkillcapeShopSession extends ShopSession {

    public static int[] UNTRIMMED_CAPES = {9747, // attack
        9753, // def
        9750, // str
        9768, // hp
        9756, // ranging
        9759, // prayer
        9762, // mage
        9801, // cooking
        9807, //woodcutting
        9783, //fletching
        9798, //fishing
        9804, //firemaking
        9780, //crafting
        9795, //smithing
        9792, //mining
        9774, //herblore
        9771, //agility
        9777, //thieving
        9786, //slayer
        9765, //runecraft
    };

    public SkillcapeShopSession(Player player, Shop shop) {
        super(player, shop);
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

        final boolean purchased = super.purchaseItem(slot, initAmount);
        if (purchased) {
            int index = getSlotForHoodId(shopItem.getId());
            if (index == 20) {
                index = 19;
            }

            final int skillCape = UNTRIMMED_CAPES[index];
            if (player.getInventory().freeSlots() > 0) {
                player.getInventory().addItem(skillCape);
            } else if (player.getBank().hasEnoughRoomFor(skillCape)) {
                player.getBank().addItem(skillCape);
            } else {
                GroundItemManager.registerGroundItem(new GroundItem(new Item(skillCape, 1), player));
            }
        }
        return purchased;
    }

    public static int getSlotForHoodId(int hood) {
        switch (hood) {
            case 9749:
                return 0;
            case 9755:
                return 1;
            case 9752:
                return 2;
            case 9770:
                return 3;
            case 9758:
                return 4;
            case 9761:
                return 5;
            case 9764:
                return 6;
            case 9803:
                return 7;
            case 9809:
                return 8;
            case 9785:
                return 9;
            case 9800:
                return 10;
            case 9806:
                return 11;
            case 9782:
                return 12;
            case 9797:
                return 13;
            case 9794:
                return 14;
            case 9776:
                return 15;
            case 9773:
                return 16;
            case 9779:
                return 17;
            case 9788:
                return 18;
            case 9767:
                return 20;
        }
        return -1;
    }
}
