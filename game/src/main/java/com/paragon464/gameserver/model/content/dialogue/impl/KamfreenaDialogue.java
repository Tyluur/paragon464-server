package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.minigames.wguild.CyclopSession;
import com.paragon464.gameserver.model.content.minigames.wguild.Defender;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.util.NumberUtils;

public class KamfreenaDialogue extends DialogueHandler {

    public KamfreenaDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.npc("Hello! How can I help you?");
                break;
            case 1:
                this.options("Choose an Option", "I'd like to kill some Cyclops please.",
                    "Never mind, sorry to bother you.");
                break;
            case 2:
                if (optionClicked == 1) {
                    this.player("I'd like to kill some Cyclops please.");
                } else if (optionClicked == 2) {
                    this.player("Never mind, sorry to bother you.");
                    this.stage = 4;
                }
                break;
            case 3:
                if (!player.getInventory().hasItemAmount(8851, 100)) {
                    this.optionClicked = -1;
                    this.npc("You need atleast 100 tokens in order",
                        "to enter the Cyclops room.");
                } else {
                    Defender type = CyclopSession.getDefender(player);
                    this.npc("Very well. The Cyclops will now drop:",
                        "" + ItemDefinition.forId(type.getDefender()).getName());
                }
                break;
            default:
                if (optionClicked == 1) {
                    CyclopSession cyclop_session = new CyclopSession(player, CyclopSession.getDefender(player));
                    player.teleport(2847, 3540 + NumberUtils.random(1), 2);
                    player.getAttributes().set("cyclop_session", cyclop_session);
                }
                end();
                break;
        }
    }
}
