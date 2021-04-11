package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.miniquests.rfd.RFDBattles;
import com.paragon464.gameserver.model.content.quests.QuestManager;
import com.paragon464.gameserver.model.content.quests.QuestManager.Quest;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.SizedPosition;
import com.paragon464.gameserver.tickable.Tickable;

public class RecipeForDisasterDialogue extends DialogueHandler {

    public RecipeForDisasterDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    public RecipeForDisasterDialogue(NPC npc, Player player, int stage) {
        super(npc, player, stage);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                if (player.getAttributes().getInt("rfd_stage") == 1) {
                    this.npc("You must find Gypsy!");
                    this.stage = 1000;
                } else {
                    this.npc("This can't be happening, not today!");
                }
                break;
            case 1:
                this.player("What's wrong?");
                break;
            case 2:
                this.npc("It's the dukes birthday today. And, I'm supposed", "to throw him a party. But, nobody has seen him for days!");
                break;
            case 3:
                this.npc("On top of that, the dining room door", "has been stuck for ages!");
                break;
            case 4:
                this.npc("My career is riding on this, I can't", "have this day get spoiled!");
                break;
            case 5:
                this.player("That's terrible. Is there anything I", "can do to help?");
                break;
            case 6:
                this.npc("Not unless you can find him before", "tonight! Everything is ruined!");
                break;
            case 7:
                this.player("Don't worry, I'm pretty good at finding things.", "Er, people too..");
                break;
            case 8:
                this.npc("I sincerely hope so! Last I heard, the", "guards were questioning the gypsy woman in Varrock.", "Maybe she could be of some use.");
                break;
            case 9:
                this.player("Thanks!");
                this.stage = 1000;
                player.getAttributes().set("rfd_stage", 1);
                break;
            //start of gypsy
            case 10:
                this.npc("Leave me alone, I've already told you", "everything I know.");
                break;
            case 11:
                this.player("Do I look like a guard? The cook of", "lumbridge sent me.");
                break;
            case 12:
                this.player("I was wondering if you could tell me what", "you know about the disappearance of the Duke...");
                break;
            case 13:
                this.npc("It's exhausting, you know? constantly being", "questioned. Everybody is always acting like I have the", "answers.");
                break;
            case 14:
                this.player("Well, your sign does say fortune teller...");
                break;
            case 15:
                this.player("Anyway, do you have any information or not?");
                break;
            case 16:
                this.npc("Not with that entitled attitude! All fortune", "tellings require the standard fee; 10,000 Gold Pieces.");
                break;
            case 17:
                this.npc("What? do you think you're special?");
                player.getAttributes().set("rfd_stage", 2);
                break;
            case 18:
                this.options("Pay for information?", "Alright, alright, take your gold..", "I don't have that kind of money on me.");
                break;
            case 19:
                if (optionClicked == 1) {
                    this.player("Alright, alright, take your gold..");
                } else if (optionClicked == 2) {
                    this.player("I don't have that kind of money on me.");
                    this.stage = 1000;
                }
                break;
            case 20:
                if (player.getAttributes().getInt("rfd_stage") == 3) {
                    this.npc("Great, what did you want to know? Oh, yes,", "the disappearance.");
                } else {
                    if (player.getInventory().hasItemAmount(995, 10000)) {
                        player.getInventory().deleteItem(995, 10000);
                        player.getInventory().refresh();
                        this.npc("Great, what did you want to know? Oh, yes,", "the disappearance.");
                        player.getAttributes().set("rfd_stage", 3);
                    } else {
                        this.npc("Like I said, standard fee. Come back when you", "have the money.");
                        this.stage = 1000;
                    }
                }
                break;
            case 21:
                this.npc("Well, a man in an apron came ranting to", "me a few days ago. Something about getting fired.");
                break;
            case 22:
                this.npc("He wanted a hex that could temporarily", "freeze something in time. So, you know, naturally,", "I obliged.");
                break;
            case 23:
                this.player("You did WHAT?");
                break;
            case 24:
                this.npc("Don't worry, don't worry, I can reverse it.");
                break;
            case 25:
                this.player("You better..");
                break;
            case 26:
                this.npc("Give me just a moment, don't move.");
                break;
            case 27:
                boolean hasScroll = (player.getBank().hasItem(1505) || player.getInventory().hasItem(1505));
                if (hasScroll && player.getAttributes().getInt("rfd_stage") > 3) {
                    this.stage = 28;
                    sendDialogue();
                } else {
                    if (player.getInventory().addItem(1505)) {
                        this.npc("Here you go! Use this to remove the hex.", "But, be careful, it is only good for one use.");
                        player.getInventory().refresh();
                        player.getAttributes().set("rfd_stage", 4);
                    } else {
                        this.npc("Free up some space in your inventory and", "come talk to me again.");
                        this.stage = 1000;
                    }
                }
                break;
            case 28:
                this.player("Hey, did that man mention where he was going?");
                break;
            case 29:
                this.npc("Hmm...");
                break;
            case 30:
                this.npc("Well, he grumbled something about a dining", "room, then ran off.");
                this.stage = 1000;
                break;
            //end of gypsy

            //scroll on door dialogue
            case 31:
                this.player("I have a strange feeling about this...");
                break;
            case 32:
                this.options("Ready for battle?", "It's probably nothing.", "On second thought, let's do this later..");
                break;
            case 33:
                if (optionClicked == 1) {
                    this.player("It's probably nothing.");
                } else if (optionClicked == 2) {
                    this.player("On second thought, let's do this later..");
                    this.stage = 1000;
                }
                break;
            case 34:
                Position north_east = new Position(1868, 5331, 0);
                Position south_west = new Position(1859, 5316, 0);
                final int width = (north_east.getX() - south_west.getX()) / 4;
                final int height = (north_east.getY() - south_west.getY()) / 4;
                int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
                MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                    width, height);
                SizedPosition baseLocation = new SizedPosition((newCoords[0] << 3) + 5, (newCoords[1] << 3) + 5, 0, width, height);
                player.getAttributes().set("stopActions", true);
                player.teleport(baseLocation);
                player.getInventory().deleteItem(1505);
                player.getInventory().refresh();
                //duke spawn
                NPC duke = new NPC(741);
                Position dukeLoc = new Position(baseLocation.getX(), baseLocation.getY() + 5, 0);
                duke.setPosition(dukeLoc);
                duke.setLastKnownRegion(dukeLoc);
                duke.face(baseLocation.getNorth());
                World.getWorld().addNPC(duke);
                //Culinaromancer spawn
                NPC culinaromancer = new NPC(3400);
                Position culinaromancerLoc = new Position(baseLocation.getX(), baseLocation.getY() + 3, 0);
                culinaromancer.setPosition(culinaromancerLoc);
                culinaromancer.setLastKnownRegion(culinaromancerLoc);
                culinaromancer.face(baseLocation.getNorth());
                World.getWorld().addNPC(culinaromancer);
                end();
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        stage = 35;
                        sendDialogue();
                    }
                });
                break;
            case 35:
                this.npc(3400, "I don't have time for this!");
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        stage = 36;
                        sendDialogue();
                    }
                });
                break;
            case 36:
                player.getAttributes().remove("stopActions");
                int starterNPC = 3493;
                if (player.getAttributes().getInt("rfd_stage") == 9) {
                    starterNPC = 3491;
                }
                player.getControllerManager().startController(new RFDBattles(new NPC(starterNPC)));
                end();
                break;
            case 37:
                this.npc(741, "Thank you so much! I don't know how to", "repay you.");
                player.getAttributes().set("rfd_stage", 11);
                break;
            case 38:
                this.npc(741, "I thought I was going to be trapped here forever.");
                break;
            case 39:
                this.player("No need to thank me, I couldn't have done", "it without your Cook!");
                break;
            case 40:
                this.npc(741, "I'm more than grateful. I'll be sure to thank", "him, too.");
                break;
            case 41:
                this.npc(741, "Let him know everything is okay, will you?");
                break;
            case 42:
                end();
                player.getAttributes().remove("stopActions");
                player.teleport(3207, 3214, 0);
                break;
            case 43:
                this.player("I defeated the Culinaromancer! The Duke is safe..");
                break;
            case 44:
                this.npc(278, "The who? Everything is okay?");
                break;
            case 45:
                this.player("Long story... Yes, everything is okay.");
                break;
            case 46:
                this.npc(278, "Oh, thank you!");
                break;
            case 47:
                this.npc(278, "You don't know how much you've done.", "I would have lost my job if it wasn't for you!");
                break;
            case 48:
                this.npc(278, "Here, take these. It doesn't cover what", "you've done for us all, but it's a start!");
                break;
            case 49:
                end();
                QuestManager.completed(player, Quest.Recipe_For_Disaster);
                player.getAttributes().set("rfd_stage", 12);
                break;
            default:
                end();
                break;
        }
    }
}
