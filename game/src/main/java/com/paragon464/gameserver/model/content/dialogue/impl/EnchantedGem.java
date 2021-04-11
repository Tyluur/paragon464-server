package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;

public class EnchantedGem extends DialogueHandler {

    public EnchantedGem(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.npc("Hello, " + player.getDetails().getName() + ". What can i help with you?");
                break;
            case 1:
                this.options("Choose an Option", "How am i doing so far?", "Where is my task located?");
                break;
            case 2:
                if (optionClicked == 1) {
                    this.player("How am i doing so far?");
                } else if (optionClicked == 2) {
                    this.player("Where is my task located?");
                }
                break;
            case 3:
                if (optionClicked == 1) {
                    if (player.getSlayer().getTask() != null) {
                        this.npc("You are still hunting " + player.getSlayer().getAmount() + " "
                            + player.getSlayer().getTask() + ".");
                    } else {
                        this.npc("You have no Slayer task.", "Come speak to me for another one.");
                    }
                } else if (optionClicked == 2) {
                    if (player.getSlayer().getTask() != null) {
                        this.npc("Your Slayer task can be located at", "" + player.getSlayer().getArea() + ".");
                    } else {
                        this.npc("You have no Slayer task.", "Come speak to me for another one.");
                    }
                }
                break;
            default:
                end();
                break;
        }
    }
}
