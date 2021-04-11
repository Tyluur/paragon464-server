package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.miniquests.LunarBattles;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;
import com.paragon464.gameserver.model.shop.impl.DefaultShopSession;

public class OneiromancerDialogue extends DialogueHandler {

    public OneiromancerDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        boolean complete = player.getAttributes().is("lunar_complete");
        switch (this.stage) {
            case 0:
                this.options("Choose an Option", "Begin battle", "View shop", "Switch books");
                break;
            default:
                if (optionClicked == 1) {
                    if (!complete) {
                        if (player.getAttributes().isSet("battle_session")) {
                            break;
                        }
                        player.getControllerManager().startController(new LunarBattles(new NPC(5902)));
                    }
                    end();
                } else if (optionClicked == 2) {
                    end();
                    if (complete) {
                        Shop lunars = new Shop();
                        ShopItem[] items = new ShopItem[]{new ShopItem(9096, 2500), new ShopItem(9097, 5000), new ShopItem(9098, 5000),
                            new ShopItem(9099, 1500), new ShopItem(9100, 1000), new ShopItem(9101, 6000), new ShopItem(9084, 10000),};
                        lunars.setStock(items);
                        lunars.setName("Oneiromancer's Supplies");
                        ShopSession session = new DefaultShopSession(player, lunars);
                        player.getAttributes().set("shop_session", session);
                    } else {
                        player.getFrames().sendMessage("You need to defeat all bosses first.");
                    }
                } else if (optionClicked == 3) {
                    if (complete) {
                        final int magicTabId = player.getSettings().isInResizable() ? 71 : 92;
                        int childId = 192;

                        if (player.getSettings().getMagicType() != 3) {
                            player.getSettings().setMagicType(3);
                            childId = 430;
                        } else {
                            player.getSettings().setMagicType(1);
                        }

                        player.getFrames().sendTab(magicTabId, childId);
                    } else {
                        player.getFrames().sendMessage("You need to defeat all bosses first.");
                    }
                    end();
                }
                break;
        }
    }
}
