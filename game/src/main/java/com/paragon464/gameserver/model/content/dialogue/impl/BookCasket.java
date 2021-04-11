package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;

public class BookCasket extends DialogueHandler {

    public BookCasket(Player player, boolean send) {
        super(player, send);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.options("Choose a Book", "Unholy book", "Holy book", "Book of balance");
                break;
            default:
                if (optionClicked == 1) {
                    player.getInventory().replaceItem(405, 3842);
                } else if (optionClicked == 2) {
                    player.getInventory().replaceItem(405, 3840);
                } else if (optionClicked == 3) {
                    player.getInventory().replaceItem(405, 3844);
                }
                end();
                break;
        }
    }
}
