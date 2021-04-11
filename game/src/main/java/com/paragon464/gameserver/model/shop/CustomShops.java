package com.paragon464.gameserver.model.shop;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.shop.impl.PestShopSession;
import com.paragon464.gameserver.model.shop.impl.SkillcapeShopSession;

public class CustomShops {

    static final ShopItem[] SKILL_HOODS = new ShopItem[]{new ShopItem(9749, 99000), new ShopItem(9755, 99000), new ShopItem(9752, 99000),
        new ShopItem(9770, 99000), new ShopItem(9758, 99000), new ShopItem(9761, 99000), new ShopItem(9764, 99000), new ShopItem(9803, 99000),
        new ShopItem(9809, 99000), new ShopItem(9785, 99000), new ShopItem(9800, 99000), new ShopItem(9806, 99000), new ShopItem(9782, 99000),
        new ShopItem(9797, 99000), new ShopItem(9794, 99000), new ShopItem(9776, 99000), new ShopItem(9773, 99000), new ShopItem(9779, 99000),
        new ShopItem(9788, 99000), new ShopItem(9767, 99000)};

    static final ShopItem[] WC_GUILD_SHOP = new ShopItem[]{
        new ShopItem(110941, 100), new ShopItem(110939, 150), new ShopItem(110940, 150), new ShopItem(110933, 50)
    };

    public static void init() {
        //skillcapes
    }

    public static boolean open(final Player player, final NPC npc, final int option) {
        if (npc.getId() == 3786) {// Pest shop
            if (option == 2) {
                Shop shop = ShopManager.getShop(13);
                PestShopSession session = new PestShopSession(player, shop);
                player.getAttributes().set("shop_session", session);
                return true;
            }
        } else if (npc.getId() == 198) {// Combat master
            if (option == 2) {
                player.getVariables().Skillcapes.setName("Master skillcapes");
                player.getVariables().Skillcapes.setStock(SKILL_HOODS);
                ShopSession session = new SkillcapeShopSession(player, player.getVariables().Skillcapes);
                session.setStock(SKILL_HOODS);
                player.getAttributes().set("shop_session", session);
                return true;
            }
        }
        return false;
    }
}
