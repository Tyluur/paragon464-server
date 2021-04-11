package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.quests.QuestManager;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;

public class MountainerDaughterDialogue extends DialogueHandler {

    public MountainerDaughterDialogue(NPC npc, Player player, int stage) {
        super(npc, player, stage);
    }

    public MountainerDaughterDialogue(NPC npc, Player player) {
        super(npc, player, 0);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                if (player.getAttributes().getInt("md_stage") <= 0) {
                    this.npc("Get out of my way!");
                } else if (player.getAttributes().getInt("md_stage") == 3) {
                    if (!player.getBank().hasItem(4502) && !player.getInventory().hasItem(4502)) {
                        this.player("I seemed to have lost the hat you gave me.");
                    }
                }
                break;
            case 1:
                if (player.getAttributes().getInt("md_stage") <= 0) {
                    this.npc(1816, "Hey, you. Get over here.");
                } else if (player.getAttributes().getInt("md_stage") == 3) {
                    this.npc("Oh, dear.. Let me take a look.");
                }
                break;
            case 2:
                if (player.getAttributes().getInt("md_stage") <= 0) {
                    this.player("Why is everyone so hostile around here?");
                } else if (player.getAttributes().getInt("md_stage") == 3) {
                    this.npc("Ah, here it is! be careful next time.");
                    this.stage = 1000;
                    player.getInventory().addItem(4502);
                    player.getInventory().refresh();
                }
                break;
            case 3:
                this.npc(1816, "Terrible things have been happening. Animals", "slaughtered, tents destroyed, children missing.");
                break;
            case 4:
                this.npc(1816, "His daughter was the latest one.");
                break;
            case 5:
                this.player("That's awful! Is there anything I can do to help?");
                break;
            case 6:
                this.npc(1816, "Well, we've tracked the beast that has been", "causing the trouble. But, no-one is equipped to slay it.");
                break;
            case 7:
                this.npc(1816, "It's taken the young men and our chieftain", "is simply too old.");
                break;
            case 8:
                this.player("I think I can do something about it. I'm an", "adventurer, you know.");
                break;
            case 9:
                this.player("Where is it?");
                break;
            case 10:
                this.npc(1816, "Oh, thank you! Hamal tracked it down to a", "cave further up the mountain, near the icy part", "of the lake.");
                break;
            case 11:
                this.player("Don't worry, I'll take care of it.");
                this.stage = 1000;
                player.getAttributes().set("md_stage", 1);
                break;
            case 12:
                this.npc(1816, "Did you take care of the beast?");
                break;
            case 13:
                this.options("Choose an Option", "Not yet, sorry.", "Could you remind me what to do?");
                break;
            case 14:
                if (optionClicked == 1) {
                    this.player("Not yet, sorry.");
                    this.stage = 1000;
                } else if (optionClicked == 2) {
                    player.getFrames().openURL("https://runenova.com/en/wiki/mountain-daughter/");
                    end();
                }
                break;
            case 15:
                this.npc(1816, "Oh, my...You should take her to Hamal.");
                this.stage = 1000;
                break;
            case 16:
                this.player("I think you should see something..");
                break;
            case 17:
                this.npc("Where... where did you find her?");
                break;
            case 18:
                this.player("I found her in the cave. I slew the beast", "terrorizing the camp.");
                break;
            case 19:
                this.npc("I'm not sure how to feel.");
                break;
            case 20:
                this.npc("She would have wanted you to have this. I think", "it is time we had a new leader.");
                break;
            case 21:
                end();
                QuestManager.completed(player, QuestManager.Quest.Mountain_Daughter);
                player.getFrames().sendMessage("Congratulations, You've completed Mountain's daughter!");
                player.getAttributes().set("md_stage", 3);
                if (player.getInventory().addItem(4502)) {
                    player.getInventory().refresh();
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(new Item(4502, 1), player));
                }
                break;
            default:
                end();
                break;
        }
    }
}
