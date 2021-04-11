package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.shop.ShopManager;

public class RichardsDialogue extends DialogueHandler {

    public RichardsDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.options("Choose a shop", "Teamcapes 1", "Teamcapes 2");
                break;
            default:
                end();
                if (optionClicked == 1) {
                    ShopManager.openShop(player, 10);
                } else if (optionClicked == 2) {
                    ShopManager.openShop(player, 11);
                }
                break;
        }
    }
}
