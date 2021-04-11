package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.skills.crafting.LeatherMaking;
import com.paragon464.gameserver.model.item.Item;

public class DommikDialogue extends DialogueHandler {

    public DommikDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.player("Can you tan my hides?");
                break;
            case 1:
                this.npc("Yes, one moment please.");
                break;
            case 2:
                boolean found = false;
                for (int index = 0; index < Inventory.SIZE; index++) {
                    Item item = player.getInventory().get(index);
                    if (item == null) continue;
                    for (int i = 0; i < LeatherMaking.UNTANNED_HIDE.length; i++) {
                        int untanned = LeatherMaking.UNTANNED_HIDE[i];
                        int tanned = LeatherMaking.TANNED_HIDE[i];
                        if (item.getId() == untanned) {
                            player.getInventory().replaceItem(item.getId(), tanned);
                            found = true;
                        }
                    }
                }
                if (found) {
                    this.npc("Your hides have been tanned.");
                } else {
                    this.npc("You don't have any hides for me.");
                }
                break;
            default:
                end();
                break;
        }
    }
}
