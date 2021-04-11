package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.minigames.NightMareZone;

public class JustinServilDialogue extends DialogueHandler {

    public JustinServilDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                if (player.getAttributes().isSet("prayers_unlocked")) {
                    this.npc("Are you ready to join the Nightmare Zone?");
                } else {
                    this.options("Choose An Option", "Join Battle", "Unlock Prayers");
                }
                break;
            case 1:
                if (player.getAttributes().isSet("prayers_unlocked")) {
                    this.player("Yes, let's do this.");
                } else {
                    if (this.optionClicked == 1) {
                        player.getAttributes().set("nightmare_zone", new NightMareZone(player));
                        end();
                    } else if (this.optionClicked == 2) {
                        if (!player.getAttributes().isSet("prayers_unlocked")) {
                            int points = player.getAttributes().getInt("nightmare_points");
                            if (points < 2000) {
                                this.npc("You need 2,000 Nightmare Zone", "points to unlock Piety & Chivalry.");
                                this.stage = 1000;
                            } else {
                                this.npc("Are you sure u want to spend 2,000 Nightmare Zone",
                                    "points to unlock Piety & Chivalry prayers?");
                            }
                        }
                    }
                }
                break;
            case 2:
                if (player.getAttributes().isSet("prayers_unlocked")) {
                    player.getAttributes().set("nightmare_zone", new NightMareZone(player));
                    end();
                    break;
                }
                this.options("Choose an Option", "Yes I'm sure.", "Nevermind, thanks.");
                break;
            case 3:
                if (this.optionClicked == 1) {
                    this.player("Yes, I'm sure.");
                } else if (this.optionClicked == 2) {
                    this.player("Nevermind, thanks.");
                }
                break;
            case 4:
                if (this.optionClicked == 1) {
                    if (player.getAttributes().getInt("nightmare_points") >= 2000) {
                        player.getAttributes().set("prayers_unlocked", true);
                        player.getAttributes().subtractInt("nightmare_points", 2000);
                        this.npc("Piety & Chivalry are now available to you.");
                    }
                } else if (this.optionClicked == 2) {
                    end();
                }
                break;
            default:
                end();
                break;
        }
    }
}
