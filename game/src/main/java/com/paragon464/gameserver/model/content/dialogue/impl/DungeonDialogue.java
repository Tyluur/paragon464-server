package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;

public class DungeonDialogue extends DialogueHandler {

    public DungeonDialogue(Player player, boolean send) {
        super(player, send);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.options("Choose an Area", "Training Dungeon", "Nova's Slayer Dungeon", "Experiments Dungeon");
                break;
            default:
                if (optionClicked == 1) {
                    player.teleport(2807, 10002, 0);
                } else if (optionClicked == 2) {
                    player.teleport(2438, 9821, 0);
                } else if (optionClicked == 4) {
                    player.teleport(3553, 9945, 0);
                }
            /*if (optionClicked == 1) {
                player.teleport(2807, 10002, 0);
            } else if (optionClicked == 2) {
                player.teleport(2506, 9461, 0);
            } else if (optionClicked == 3) {
                player.teleport(2425, 4694, 0);
            } else if (optionClicked == 4) {
                player.teleport(2589, 9412, 0);
            } else if (optionClicked == 5) {
                player.teleport(3553, 9945, 0);
            }*/
                end();
                break;
        }
    }
}
