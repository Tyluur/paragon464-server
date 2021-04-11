package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.miniquests.MageArenaBattles;

public class KolodionDialogue extends DialogueHandler {

    public KolodionDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    @Override
    public void sendDialogue() {
        boolean completed = player.getAttributes().is("mage_arena");
        switch (this.stage) {
            case 0:
                if (completed) {
                    this.npc("Great job in there " + player.getDetails().getName() + ".",
                        "Step through the pool and have access", "to the Gods equipment.");
                    this.stage = 15;
                    this.optionClicked = -1;
                } else {
                    this.player("Hello there. What is this place?");
                }
                break;
            case 1:
                this.npc("I am the great Kolodion, master of battle magic, and",
                    "this is my battle arena. Top wizards travel from all over",
                    "" + Config.SERVER_NAME + " to fight here.");
                break;
            case 2:
                this.player("Can i fight here?");
                break;
            case 3:
                this.npc("My arena is open to any high level wizard, but this is",
                    "no game. Many wizards fall in this arena, never to rise",
                    "again. The strongest mages have been destroyed.");
                break;
            case 4:
                this.npc("If you're sure you want in?");
                break;
            case 5:
                this.options("Choose an Option", "Yes indeedy.", "No I don't.");
                break;
            case 6:
                if (optionClicked == 1) {
                    this.player("Yes indeedy.");
                } else if (optionClicked == 2) {
                    this.player("No I don't.");
                    this.stage = 16;
                }
                break;
            case 7:
                this.npc("Good good. You have a healthy sense of competition.");
                break;
            case 8:
                this.npc("Remember traveler - in my arena, hand-to-hand",
                    "combat is useless. Your strength will diminish as you",
                    "enter the arena, but the spells you can learn are",
                    "amongst the most powerful in all of " + Config.SERVER_NAME + ".");
                break;
            case 9:
                this.npc("Before i accept you in, we must duel.");
                break;
            case 10:
                this.options("Choose an Option", "Okay, let's fight.", "No thanks.");
                break;
            case 11:
                if (optionClicked == 1) {
                    this.player("Okay, let's fight.");
                } else if (optionClicked == 2) {
                    this.player("No thanks.");
                    this.stage = 16;
                }
                break;
            case 12:
                this.npc("I must first check that you are up to scratch.");
                break;
            case 13:
                if (player.getSkills().getCurrentLevel(SkillType.MAGIC) >= 60) {
                    this.player("You don't need to worry about that.");
                } else {
                    this.npc("You don't meet the requirements to enter,",
                        "come back with 60 or higher Magic to enter.");
                    this.stage = 16;
                }
                break;
            case 14:
                this.npc("Not just any magician can enter - only the most",
                    "powerful and most feared. Before you can use the",
                    "power of this arena, you must prove yourself against", "me.");
                break;
            case 15:
                if (optionClicked == 1) {
                    if (player.getAttributes().isSet("battle_session")) {
                        break;
                    }
                    player.getControllerManager().startController(new MageArenaBattles(new NPC(907)));
                }
                end();
                break;
            default:
                end();
                break;
        }
    }
}
