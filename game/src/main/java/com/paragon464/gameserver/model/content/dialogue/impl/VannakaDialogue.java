package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.skills.slayer.TaskDifficulty;

public class VannakaDialogue extends DialogueHandler {

    public VannakaDialogue(NPC npc, Player player) {
        super(npc, player, 0);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                if (player.getSlayer().getTask().equalsIgnoreCase("none")) {
                    this.options("Choose an Option", "Low - Task", "Medium - Task", "High - Task");
                } else {
                    this.npc("You are still hunting " + player.getSlayer().getAmount() + " "
                        + player.getSlayer().getTask() + ".");
                }
                break;
            case 1:
                if (!player.getSlayer().getTask().equalsIgnoreCase("none")) {
                    this.npc("Would you like to reset your task? It'll", "cost you 20 Slayer points ... or 195,000 gold.");
                } else {
                    if (optionClicked == 1) {
                        player.getSlayer().assign(TaskDifficulty.LOW);
                    } else if (optionClicked == 2) {
                        player.getSlayer().assign(TaskDifficulty.MEDIUM);
                    } else if (optionClicked == 3) {
                        player.getSlayer().assign(TaskDifficulty.HIGH);
                    }
                    end();
                }
                break;
            case 2:
                this.options("Choose an Option", "Yes, reset my task!", "No thanks.");
                break;
            case 3:
                if (optionClicked == 1) {
                    this.player("Yes, reset my task!");
                } else if (optionClicked == 2) {
                    this.player("No thanks.");
                    end();
                }
                break;
            case 4:
                this.options("How would you like to pay?", "20 Slayer points", "195,000 gold pieces");
                break;
            case 5:
                if (optionClicked == 1) {
                    if (player.getAttributes().getInt("slayer_points") >= 20) {
                        player.getAttributes().subtractInt("slayer_points", 20);
                    } else {
                        this.npc("You don't have enough Slayer points. Come back", "when you have enough.");
                        this.stage = 1000;
                        break;
                    }
                } else if (optionClicked == 2) {
                    if (player.getInventory().hasItemAmount(995, 195000)) {
                        player.getInventory().deleteItem(995, 195000);
                        player.getInventory().refresh();
                    } else {
                        this.npc("Do I look like a charity case? Come back", "when you have enough!");
                        this.stage = 1000;
                        break;
                    }
                }
                player.getSlayer().setArea("none");
                player.getSlayer().setTask("none");
                player.getSlayer().setAmount((byte) 0);
                player.getFrames().sendMessage("Your Slayer task was reset.");
                end();
                break;
            default:
                end();
                break;
        }
    }
}
