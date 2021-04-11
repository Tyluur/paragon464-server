package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.shop.impl.SkillcapeShopSession;

public class CombatMasterDialogue extends DialogueHandler {

    public CombatMasterDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.options("Choose an Option", player.getVariables().isExpLocked() ? "Unlock exp" : "Lock exp",
                    "Trim skillcapes");
                break;
            default:
                if (optionClicked == 1) {
                    player.getVariables().setExpLocked(!player.getVariables().isExpLocked());
                } else if (optionClicked == 2) {
                    for (int i = 0; i < 28; i++) {
                        Item item = player.getInventory().get(i);
                        if (item == null) continue;
                        for (int l = 0; l < SkillcapeShopSession.UNTRIMMED_CAPES.length; l++) {
                            if (item.getId() == SkillcapeShopSession.UNTRIMMED_CAPES[l]) {
                                player.getInventory().replaceItemInSlot(item.getId(), i, SkillcapeShopSession.UNTRIMMED_CAPES[l] + 1);
                            }
                        }
                    }
                }
                end();
                break;
        }
    }
}
