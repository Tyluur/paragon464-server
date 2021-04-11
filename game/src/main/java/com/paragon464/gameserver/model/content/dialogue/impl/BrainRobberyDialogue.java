package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.World;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.quests.QuestManager;
import com.paragon464.gameserver.model.content.quests.QuestTab;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

public class BrainRobberyDialogue extends DialogueHandler {

    public BrainRobberyDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    public BrainRobberyDialogue(NPC npc, Player player, int stage) {
        super(npc, player, stage);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.npc(5616, "I'm hungry!");
                break;
            case 1:
                this.npc(3079, "Not now!");
                break;
            case 2:
                this.player("What's going on here?");
                break;
            case 3:
                this.npc(5616, "Feeding!");
                break;
            case 4:
                this.npc(3079, "Nothing! Absolutely nothing!");
                break;
            case 5:
                this.player("Aren't monks supposed to be... you know.. nice?");
                break;
            case 6:
                this.npc(3079, "Yeah, and most monks also live in fancy monasteries.", "But, obviously things are different for us.");
                break;
            case 7:
                this.player("Good point! What are you two doing on a pirate island?");
                break;
            case 8:
                this.npc(3079, "It�s not like I wanted to be here.", "But, bird-brain over here thought it'd be a good", "idea to get his brain eaten!");
                break;
            case 9:
                this.npc(3079, "'Oh, faith healing doesn't work.' they", "said. 'Let the surgeon stay on the island.' they said...");
                break;
            case 10:
                this.npc(3079, "And now? Barbarians! Truly unruly!");
                break;
            case 11:
                this.player("Well, I'm pretty good at dealing with", "things. Maybe I could help!");
                break;
            case 12:
                this.player("Although, brain-eating barbarians are", "a little out of my expertise...");
                break;
            case 13:
                this.npc(3079, "If you can find a way back to", "Harmony Island, then you've got my 'blessing'.");
                break;
            case 14:
                this.player("I've always wanted to be blessed!");
                player.getAttributes().set("brain_robbery_stage", 1);
                QuestTab.sendQuests(player);
                this.stage = 1000;
                break;
            //stage 1
            case 15:
                this.npc(3079, "Any news?");
                break;
            case 16:
                this.options("Choose An Option", "Could you remind me where to go?", "No news, sorry.");
                break;
            case 17:
                if (optionClicked == 1) {
                    player.getFrames().openURL("https://runenova.com/en/wiki/great-brain-robbery/");
                    end();
                } else if (optionClicked == 2) {
                    this.player("No news, sorry.");
                    this.stage = 1000;
                }
                break;
            //bill teach
            case 18:
                this.npc("I'm busy, what do you want?");
                break;
            case 19:
                this.player("Interesting, a pirate with a work ethic!");
                break;
            case 20:
                this.npc("Interesting, someone digging their own grave!");
                break;
            case 21:
                this.npc("So, get to the point.");
                break;
            case 22:
                this.player("I'm here to provide aide to Harmony Island!");
                break;
            case 23:
                this.npc("Well, you've come to the right place,", "seeing as I'm the only one still sailing to the island.");
                break;
            case 24:
                this.player("Great! Where should I si...");
                break;
            case 25:
                this.npc("Not so fast! How much do you weigh?", "You know, never mind; flat rate.");
                break;
            case 26:
                this.npc("10,000 Gold pieces or no deal.");
                player.getAttributes().set("brain_robbery_stage", 2);
                this.stage = 1000;
                break;
            case 27:
                if (npc.getSpawnPosition().getX() == 3798 && npc.getSpawnPosition().getY() == 2873) {
                    this.npc("Ready to go? I don't have all day.");
                } else {
                    this.npc("Thought about my offer?");
                }
                break;
            case 28:
                if (npc.getSpawnPosition().getX() == 3798 && npc.getSpawnPosition().getY() == 2873) {
                    this.options("Go back to the main Island?", "Yes", "No.");
                } else {
                    this.options("Pay for a ticket?", "A little pricey, but okay...", "Are you insane? for a single person?");
                }
                break;
            case 29:
                if (npc.getSpawnPosition().getX() == 3798 && npc.getSpawnPosition().getY() == 2873) {
                    if (optionClicked == 1) {
                        this.player("Yes");
                    } else if (optionClicked == 2) {
                        this.player("No.");
                        this.stage = 1000;
                    }
                } else {
                    if (optionClicked == 1) {
                        this.player("A little pricey, but okay...");
                    } else if (optionClicked == 2) {
                        this.player("Are you insane? for a single person?");
                        this.stage = 1000;
                    }
                }
                break;
            case 30:
                if (npc.getSpawnPosition().getX() == 3798 && npc.getSpawnPosition().getY() == 2873) {
                    player.teleport(3678, 2954, 0);
                    end();
                } else {
                    if (player.getInventory().hasItemAmount(995, 10000)) {
                        player.getInventory().deleteItem(new Item(995, 10000));
                        player.getInventory().refresh();
                        player.teleport(3797, 2867, 0);
                        end();
                    } else {
                        this.npc("10,000 Gold pieces or no deal.");
                        this.stage = 1000;
                    }
                }
                break;
            case 31:
                this.player("It's a bit quiet around here...");
                break;
            case 32:
                if (player.getAttributes().getInt("brain_robbery_zombie_kc") >= 3) {
                    player.getAttributes().remove("brain_robbery_zombie_kc");
                    this.npc(5613, "Just one more...");
                } else {
                    boolean zombiesAvail = false;
                    for (NPC npc : World.getSurroundingNPCS(player.getPosition())) {
                        if (npc.getId() == 73) {
                            if (npc.getSpawnedBy() == player) {
                                player.getFrames().sendMessage("You haven't killed all zombies yet.");
                                zombiesAvail = true;
                                break;
                            }
                        }
                    }
                    if (!zombiesAvail) {
                        player.getAttributes().set("force_multi", true);
                        Position placement = new Position(3803, 2844, 0);
                        NPC npc = new NPC(73);
                        npc.setPosition(placement);
                        npc.setLastKnownRegion(placement);
                        World.getWorld().addNPC(npc);
                        npc.getAttributes().set("spawned_by", player);
                        npc.getAttributes().set("brain_robbery_zombie", true);
                        npc.getAttributes().set("force_multi", true);
                        CombatAction.beginCombat(npc, player);
                        placement = new Position(3803, 2845, 0);
                        npc = new NPC(73);
                        npc.setPosition(placement);
                        npc.setLastKnownRegion(placement);
                        World.getWorld().addNPC(npc);
                        npc.getAttributes().set("spawned_by", player);
                        npc.getAttributes().set("brain_robbery_zombie", true);
                        npc.getAttributes().set("force_multi", true);
                        CombatAction.beginCombat(npc, player);
                        placement = new Position(3803, 2843, 0);
                        npc = new NPC(73);
                        npc.setPosition(placement);
                        npc.setLastKnownRegion(placement);
                        World.getWorld().addNPC(npc);
                        npc.getAttributes().set("spawned_by", player);
                        npc.getAttributes().set("brain_robbery_zombie", true);
                        npc.getAttributes().set("force_multi", true);
                        CombatAction.beginCombat(npc, player);
                    }
                    end();
                }
                break;
            case 33:
                this.npc(5613, "It's alive!");
                break;
            case 34:
                this.player("What is?");
                break;
            case 35:
                this.npc(5613, "And what are you doing here?");
                break;
            case 36:
                player.getControllerManager().startController(new BattleController(new NPC(5666)));
                end();
                break;
            case 37:
                this.player("Ew! Maybe I should return this.");
                this.stage = 1000;
                break;
            case 38:
                this.player("I've cleared out the island!");
                break;
            case 39:
                this.player("And, I found something that might belong", "to your friend..");
                break;
            case 40:
                player.getInventory().deleteItem(4199);
                player.getInventory().refresh();
                player.getAttributes().set("brain_robbery_stage", 4);
                this.npc(3079, "Ugh.. I was just beginning to like it here...");
                break;
            case 41:
                this.player("You know, you could try being a little grateful.");
                break;
            case 42:
                this.npc(3079, "Alright alright, thanks. I'm sure Brother", "Tranquility will appreciate your.. erm.. gift...");
                break;
            case 43:
                if (player.getAttributes().getInt("brain_robbery_stage") < 5) {
                    QuestManager.completed(player, QuestManager.Quest.Great_Brain_Robbery);
                    player.getAttributes().set("brain_robbery_stage", 5);
                    QuestTab.sendQuests(player);
                }
                boolean hasAnchor = player.getBank().hasItem(10887) || player.getInventory().hasItem(10887);
                if (hasAnchor) {
                    this.npc("Very nice looking anchor Bill made you.");
                    this.stage = 1000;
                } else {
                    this.player("So, any idea where I can get this anchor repaired?");
                }
                break;
            case 44:
                this.npc(3079, "Do I look like a sailor? Try Bill.");
                this.stage = 1000;
                break;
            //bill teach repairing
            case 45:
                this.options("Choose An Option", "Travel Home", "Barrelchest Anchor");
                break;
            case 46:
                if (optionClicked == 1) {
                    end();
                    player.teleport(2340, 3675, 0);
                } else {
                    if (player.getInventory().hasItem(10888)) {
                        this.player("Would you have any clue about how", "to fix an anchor?");
                    } else {
                        hasAnchor = player.getBank().hasItem(10887) || player.getInventory().hasItem(10887);
                        if (!hasAnchor) {
                            this.player("Would you happen to have any spare anchors?");
                        } else {
                            this.npc("Can't thank you enough for your braveness. Hope", "you're enjoying the anchor.");
                            this.stage = 1000;
                        }
                    }
                }
                break;
            case 47:
                this.npc("Sure do. But it'll cost you.");
                break;
            case 48:
                if (player.getInventory().hasItem(10888)) {
                    this.npc("50,000 gold pieces should cover the profits", "I've lost thanks to you clearing the path to the island.");
                } else {
                    this.npc("75,000 should be fair.");
                }
                break;
            case 49:
                if (player.getInventory().hasItem(10888)) {
                    this.options("Pay for the repairs?", "Fine, take it.", "50,000? No way!");
                } else {
                    this.options("Purchase a new anchor?", "Fine, take it.", "75,000? No way!");
                }
                break;
            case 50:
                if (optionClicked == 1) {
                    this.player("Fine, take it.");
                } else if (optionClicked == 2) {
                    if (player.getInventory().hasItem(10888)) {
                        this.player("50,000? No way!");
                    } else {
                        this.player("75,000? No way!");
                    }
                    this.stage = 1000;
                }
                break;
            case 51:
                boolean repair = player.getInventory().hasItem(10888);
                int cost = repair ? 50000 : 75000;
                if (!player.getInventory().hasItemAmount(995, cost)) {
                    this.npc("Come back when you have enough gold.");
                    break;
                }
                if (repair) {
                    player.getInventory().deleteItem(new Item(995, 50000));
                    player.getInventory().deleteItem(10888);
                    player.getInventory().addItem(10887);
                    player.getInventory().refresh();
                } else {
                    player.getInventory().deleteItem(new Item(995, 75000));
                    player.getInventory().addItem(10887);
                    player.getInventory().refresh();
                }
                end();
                break;
            default:
                end();
                break;
        }
    }
}
