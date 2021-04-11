package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.miniquests.dt.DesertTreasure;
import com.paragon464.gameserver.model.content.quests.QuestManager;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;
import com.paragon464.gameserver.model.shop.impl.DefaultShopSession;

public class DesertTreasureDialogue extends DialogueHandler {

    public DesertTreasureDialogue(NPC npc, Player player, int stage) {
        super(npc, player, stage);
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.npc("Watch where you're going!");
                } else if (player.getAttributes().getInt("dt_stage") == 1) {
                    this.npc("What are you doing back here so early? Get to Canifis!");
                } else if (player.getAttributes().getInt("dt_stage") >= 2 && player.getAttributes().getInt("dt_stage") <= 4) {
                    this.npc("Shouldn�t you be somewhere else?", "Make like a sherpa and get going!");
                } else if (player.getAttributes().getInt("dt_stage") == 5) {
                    this.npc("Back so soon? Quickly, run along!");
                } else if (player.getAttributes().getInt("dt_stage") == 6) {
                    this.npc("Why haven't you gotten the diamond? We're so close!");
                } else if (player.getAttributes().getInt("dt_stage") == 8) {
                    this.options("Choose an Option", "View shop", "Ancient Pyramid");
                }
                break;
            case 1:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.player("What got you in such a foul mood?");
                } else if (player.getAttributes().getInt("dt_stage") == 1) {
                    this.options("Choose an Option", "Alright, alright, I'll get going.", "I need more information.");
                } else if (player.getAttributes().getInt("dt_stage") >= 2 && player.getAttributes().getInt("dt_stage") <= 4) {
                    this.options("Choose an Option", "Alright, you don't have to yell...", "I'm a little confused.");
                } else if (player.getAttributes().getInt("dt_stage") == 5) {
                    this.options("Choose an Option", "Alright, I'll go.", "Can you be more specific than 'Ali'?");
                } else if (player.getAttributes().getInt("dt_stage") == 6) {
                    this.options("Choose an Option", "I'll get on with it.", "I'm not too sure where I should be going.");
                } else if (player.getAttributes().getInt("dt_stage") == 8) {
                    end();
                    if (optionClicked == 1) {
                        Shop ancients = new Shop();
                        ShopItem[] items = new ShopItem[]{new ShopItem(6109, 65), new ShopItem(6107, 120), new ShopItem(6108, 120),
                            new ShopItem(6110, 40), new ShopItem(6106, 30), new ShopItem(6111, 55), new ShopItem(4675, 95000),};
                        ancients.setStock(items);
                        ancients.setName("Archaeologist's Supplies");
                        ShopSession session = new DefaultShopSession(player, ancients);
                        player.getAttributes().set("shop_session", session);
                    } else if (optionClicked == 2) {
                        player.teleport(3233, 9318, 0);
                    }
                }
                break;
            case 2:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.npc("I�m sorry, I�m just a little agitated.", "I was returning from the dig site when I realized", " I must have been robbed.");
                } else if (player.getAttributes().getInt("dt_stage") == 1) {
                    if (optionClicked == 1) {
                        this.player("Alright, alright, I'll get going.");
                        this.stage = 1000;
                    } else if (optionClicked == 2) {
                        player.getFrames().openURL("https://runenova.com/en/wiki/desert-treasure/");
                        end();
                    }
                } else if (player.getAttributes().getInt("dt_stage") >= 2 && player.getAttributes().getInt("dt_stage") <= 4) {
                    if (optionClicked == 1) {
                        this.player("Alright, you don't have to yell...");
                        this.stage = 1000;
                    } else if (optionClicked == 2) {
                        player.getFrames().openURL("https://runenova.com/en/wiki/desert-treasure/#kamil");
                        end();
                    }
                } else if (player.getAttributes().getInt("dt_stage") == 5) {
                    if (optionClicked == 1) {
                        this.player("Alright, I'll go.");
                        this.stage = 1000;
                    } else if (optionClicked == 2) {
                        player.getFrames().openURL("https://runenova.com/en/wiki/desert-treasure/#fareed");
                        end();
                    }
                } else if (player.getAttributes().getInt("dt_stage") == 6) {
                    if (optionClicked == 1) {
                        this.player("I'll get on with it.");
                        this.stage = 1000;
                    } else if (optionClicked == 2) {
                        player.getFrames().openURL("https://runenova.com/en/wiki/desert-treasure/#damis");
                        end();
                    }
                }
                break;
            case 3:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.player("What did they take?");
                }
                break;
            case 4:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.npc("The gems we found.", "I was on my way to get them identified.", "But, now they are gone!");
                }
                break;
            case 5:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.player("I�m pretty good at finding lost things.", "Maybe I could offer my services�");
                }
                break;
            case 6:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.npc("Really? That'd be grea-");
                }
                break;
            case 7:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.player("...for a price.");
                }
                break;
            case 8:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.npc("Well, I guess we can make a deal.", "If you bring me the gems so that I can get them", " identified, I�ll let you have them.");
                }
                break;
            case 9:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    this.options("Are u In?", "Defintely!", "I'm too busy.");
                }
                break;
            case 10:
                if (player.getAttributes().getInt("dt_stage") <= 0) {
                    if (optionClicked == 1) {
                        player.getAttributes().addInt("dt_stage", 1);
                        this.npc("Great! Last I heard, one of the diamonds", "made their way to Canifis.");
                    } else if (optionClicked == 2) {
                        this.npc("Thanks for getting my hopes up...");
                        this.stage = 1000;
                    }
                }
                break;
            case 11:
                if (player.getAttributes().getInt("dt_stage") == 1) {
                    this.player("Okay, I'll get right on it!");
                    this.stage = 1000;
                }
                break;
            //start of bar dialogue
            case 12:
                this.npc(1042, "Alright, what do you want?");
                break;
            case 13:
                this.npc("I'll have a beer and some brea-");
                break;
            case 14:
                this.player("Boo!");
                break;
            case 15:
                this.npc("Run, it's him!");
                break;
            case 16:
                this.player("Erm... yes, it's me...");
                break;
            case 17:
                this.npc("Phew, I thought you were someone else.");
                break;
            case 18:
                this.player("Who has you all worried?");
                break;
            case 19:
                this.npc("A man who has-");
                break;
            case 20:
                this.npc(1042, "I'm not even sure if it is a man..");
                break;
            case 21:
                this.npc("Stop interrupting! Anyway, a man with skin", "as pale as a ghost and--");
                break;
            case 22:
                this.npc(1042, "I think it is a vampire, not a ghost.");
                break;
            case 23:
                this.npc("A man with skin as pale as a ghost who", "has been ravaging the town and our livestock.");
                break;
            case 24:
                this.player("*muttering* This sounds like the guy..");
                break;
            case 25:
                this.player("Where can I find this 'ghost?");
                break;
            case 26:
                this.npc(1042, "Vampire, actually.");
                break;
            case 27:
                this.npc("I've heard rumors it is staying by the old coffins.");
                break;
            case 28:
                this.player("Thanks for the information!");
                this.stage = 1000;
                break;
            //end of bar dialogue
            case 29:
                this.npc("Great, you found it! Only three more to go.");
                break;
            case 30:
                this.player("Alright, where next?");
                break;
            case 31:
                this.npc("Well, I heard it might've been sold to a man", "up near the Burthorpe mountains.");
                break;
            case 32:
                this.player("Mountains, great... Off I go...");
                this.stage = 1000;
                player.getAttributes().set("dt_stage", 2);
                break;
            //start of wounded soldier
            case 33:
                this.npc("*mumbling* God, my leg is killing me...");
                break;
            case 34:
                this.player("Hey, I'm looking for-");
                break;
            case 35:
                this.npc("Don't you see I'm dying here?");
                break;
            case 36:
                this.player("I'm just looking for some information.");
                break;
            case 37:
                this.npc("I'll make you a deal. Get me some something", "to heal this leg, then we'll talk.");
                break;
            case 38:
                this.player("You don't even know what I want yet.");
                break;
            case 39:
                this.npc("Do we have a deal or not?");
                break;
            case 40:
                this.player("Fine, fine. I'll go get you something.");
                this.stage = 1000;
                player.getAttributes().set("dt_stage", 3);
                break;
            case 41:
                this.npc("Where is my food? didn't we have a deal?");
                break;
            case 42:
                this.player("Sorry, I'll get right on it!");
                this.stage = 1000;
                break;
            case 43:
                this.npc("Great! Alright, What did you want?");
                break;
            case 44:
                this.player("I�m not really sure. I�m looking for something", "that was stolen. Have you seen any suspicious behavior?");
                break;
            case 45:
                this.npc("Well, there was one shady fellow who", "wandered up into the mountain a few days ago.", "Try the icy trail.");
                break;
            case 46:
                this.npc("Here, take this.");
                this.stage = 1000;
                if (player.getInventory().addItem(6950)) {
                    player.getInventory().refresh();
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(new Item(6950, 1), player));
                }
                break;
            //end of wounded soldier
            case 47:
                this.npc("Thanks! Only two more.");
                break;
            case 48:
                this.player("Glad to help. Where next?");
                break;
            case 49:
                this.npc("I believe the next one was sold to a shady", "fellow based in Al-Kharid. A fence named Ali.");
                break;
            case 50:
                this.player("Great, so specific...");
                this.stage = 1000;
                break;
            //start of ali m
            case 51:
                this.npc("Plan on purchasing anything?");
                break;
            case 52:
                this.player("Yeah, I have this flyer about a diamond.");
                break;
            case 53:
                this.npc("I don't remember giving out any fliers...");
                break;
            case 54:
                this.player("Aha! Found you!");
                break;
            case 55:
                this.player("Give the diamond here! it's not yours to sell!");
                break;
            case 56:
                this.npc("I might have sold it already...");
                break;
            case 57:
                this.player("'Might have?' To whom?");
                break;
            case 58:
                this.npc("A scary looking man. I think he said his name", "was Fareed. I saw him take the magic carpet just", "outside the border.");
                break;
            case 59:
                this.player("Thanks, I guess.");
                this.stage = 1000;
                break;
            //end of ali m

            //start of rug merch
            case 60:
                this.player("I�m looking for someone you might have seen.", "Faree--");
                break;
            case 61:
                this.npc("You too? Get on.");
                this.stage = 1000;
                player.teleport(3307, 9376, 0);
                DesertTreasure.enterFareed(player);
                break;
            //end of rug merch

            case 62:
                this.npc("Perfect! just one more!");
                break;
            case 63:
                this.player("Any tips?");
                break;
            case 64:
                this.npc("I heard someone tried to sell it to a salesman", "near Baxtorian Falls.");
                break;
            case 65:
                this.player("I'm on my way!");
                this.stage = 1000;
                break;
            //start of rasolo
            case 66:
                this.player("I was sent to find you. I heard someone had tried", " to sell you a gem recently.");
                break;
            case 67:
                this.npc("For an absurd price, yes he did!");
                break;
            case 68:
                this.npc("Strange man. Tanned and overly angry.");
                break;
            case 69:
                this.npc("He dropped this. Maybe it�ll be of some use to you.");
                this.stage = 1000;
                if (player.getInventory().addItem(6950)) {
                    player.getInventory().refresh();
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(new Item(6950, 1), player));
                }
                break;
            case 70:
                this.player("I found the last one!");
                break;
            case 71:
                this.npc("Amazing! I'll take these to the Varrock historian.");
                break;
            case 72:
                this.npc("As I promised, I will hold up my end of the bargain.");
                break;
            case 73:
                this.npc("I will send you to the Ancient pyramid", "where you will have access to new dark magic", "I have discovered.");
                break;
            case 74:
                this.npc("You will be able to toggle between magics there.", "Talk to me to be teleported at anytime you want.");
                break;
            case 75:
                end();
                QuestManager.completed(player, QuestManager.Quest.Desert_Treasure);
                player.getAttributes().set("dt_stage", 8);
                player.teleport(3233, 9318, 0);
                break;
            default:
                end();
                break;
        }
    }
}
