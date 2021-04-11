package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.shop.ShopManager;

public class SantaDialogue extends DialogueHandler {

    public SantaDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.options("Choose an Option", "Votes Exchange", "Credits Exchange");
                break;
            default:
                end();
                if (optionClicked == 1) {
                    ShopManager.openShop(player, 18);
                } else if (optionClicked == 2) {
                    player.getFrames().sendMessage("Credit exchange temporarily disabled!");
                    // TODO: Uncomment when reimplemented.
                    //ShopManager.openShop(player, 20);
                }
                break;
        }
    }
}
