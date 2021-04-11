package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.miniquests.hfd.DagBattle;
import com.paragon464.gameserver.model.content.quests.QuestManager;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;
import com.paragon464.gameserver.model.shop.impl.DefaultShopSession;

public class HorrorFromDeepDialogue extends DialogueHandler {

    public HorrorFromDeepDialogue(NPC npc, Player player) {
        super(npc, player);
    }

    public HorrorFromDeepDialogue(NPC npc, Player player, int stage) {
        super(npc, player, stage);
    }

    @Override
    public void sendDialogue() {
        boolean hasBread = false;
        boolean hasWater = false;
        switch (this.stage) {
            case 0:
                this.npc("Hey, do you have a minute?");
                break;
            case 1:
                this.player("Something wrong?");
                break;
            case 2:
                this.npc("My wife, Larrissa, has been trapped in", "the lighthouse for days now.");
                break;
            case 3:
                this.npc("I left my post for just a few hours and", "by the time I got back, these creatures had", "moved in.");
                break;
            case 4:
                this.npc("We managed to corner them into the basement.", "But, my wife is too afraid to come down.");
                break;
            case 5:
                this.player("Creatures? How difficult could this be.", "Why don't you just slay them?");
                break;
            case 6:
                this.npc("Don't you think I've tried that? The big", "one seems to have some sort of..powers..I can't explain", "it, but it is like my attacks aren't even connecting!");
                break;
            case 7:
                this.player("I'm sure it's no big deal. Let me handle it.");
                break;
            case 8:
                this.npc("Thank you so much! Could you bring this", "to my wife? I don't think she has had any sustenance", "in days.");
                player.getAttributes().set("hfd_stage", 1);
                break;
            case 9:
                if (player.getInventory().freeSlots() >= 2) {
                    player.getInventory().addItem(1865);
                    player.getInventory().addItem(4460);
                    player.getInventory().refresh();
                    this.player("Sure, no problem!");
                    this.stage = 1000;
                } else {
                    this.npc("You need some space in your inventory first.");
                    this.stage = 1000;
                }
                break;
            //stage 1
            case 10:
                hasBread = (player.getBank().hasItem(1865) || player.getInventory().hasItem(1865));
                hasWater = (player.getBank().hasItem(4460) || player.getInventory().hasItem(4460));
                boolean lost = (!hasBread || !hasWater);
                if (lost) {
                    this.options("Choose An Option", "I've lost the supplies you gave me.", "Could you remind me what to do?");
                } else {
                    this.npc("Any news?");
                }
                break;
            case 11:
                if (optionClicked == 1) {
                    this.player("I've lost the supplies you gave me.");
                } else if (optionClicked == 2) {
                    player.getFrames().openURL("https://runenova.com/en/wiki/horror-from-the-deep/");
                    end();
                } else {
                    this.options("Choose An Option", "Nothing, sorry.", "Could you remind me what to do?");
                }
                break;
            case 12:
                if (optionClicked == 1) {
                    this.player("Nothing, sorry.");
                    this.stage = 1000;
                } else if (optionClicked == 2) {
                    player.getFrames().openURL("https://runenova.com/en/wiki/horror-from-the-deep/");
                    end();
                } else {
                    this.npc("It's fine, I have more. Try not to lose them this time!");
                }
                break;
            case 13:
                if (player.getInventory().freeSlots() >= 2) {
                    hasBread = (player.getBank().hasItem(1865) || player.getInventory().hasItem(1865));
                    hasWater = (player.getBank().hasItem(4460) || player.getInventory().hasItem(4460));
                    if (!hasBread) {
                        player.getInventory().addItem(1865);
                    }
                    if (!hasWater) {
                        player.getInventory().addItem(4460);
                    }
                    player.getInventory().refresh();
                    end();
                } else {
                    this.npc("You need some space in your inventory first.");
                    this.stage = 1000;
                }
                break;
            //larrissa
            case 14:
                hasBread = (player.getBank().hasItem(1865) || player.getInventory().hasItem(1865));
                hasWater = (player.getBank().hasItem(4460) || player.getInventory().hasItem(4460));
                if (!hasBread || !hasWater) {
                    this.player("I should bring Larrissa her supplies before", "they get cold.");
                    this.stage = 1000;
                } else {
                    this.player("Larrissa?");
                }
                break;
            case 15:
                this.npc("Yes?");
                break;
            case 16:
                this.player("Jossik is worried about you. He", "had me bring you these.");
                break;
            case 17:
                this.player("Won't you come down?");
                break;
            case 18:
                this.npc("Thank you, but I can't. Not until", "those things are gone!");
                player.getAttributes().set("hfd_stage", 2);
                player.getInventory().deleteItem(1865);
                player.getInventory().deleteItem(4460);
                player.getInventory().refresh();
                this.stage = 1000;
                break;
            //trap door
            case 19:
                this.player("I should bring Larrissa her supplies before", "they get cold.");
                this.stage = 1000;
                break;
            //dag battle
            case 20:
                this.player("What have I got myself into...");
                break;
            case 21:
                if (player.getControllerManager().isMinigameOrMiniquest()) {
            		break;
            	}
            	player.getControllerManager().startController(new DagBattle(new NPC(3497)));
                end();
                break;
            case 23:
                this.npc("Any news?");
                break;
            case 24:
                this.player("It's done. I slew the beasts and", "brought Larissa your gifts.");
                break;
            case 25:
                this.npc("Thank you so much! I'll let her know", "it's safe to come down.");
                break;
            case 26:
                this.npc("Here, take this. I don't have much to", "my name, but it is the least I can give for your time.");
                break;
            case 27:
                end();
                player.getAttributes().set("hfd_stage", 4);
                QuestManager.completed(player, QuestManager.Quest.Horror_From_The_Deep);
                if (player.getInventory().addItem(405)) {
                    player.getInventory().refresh();
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(new Item(405, 1), player));
                }
                break;
            case 28:
                this.player("The casket you gave me, it had a", "book in it. Have you noticed any others washing up", "on shore?");
                break;
            case 29:
                this.npc("Sure have!");
                break;
            case 30:
                this.npc("But, I was already offered me some gold", "for them. However, if you want them, they are 50,000", "coins each.");
                break;
            case 31:
                this.npc("Still interested?");
                break;
            case 32:
                this.options("Choose An Option", "Sure am!", "Thanks, but no thanks.");
                break;
            case 33:
                if (optionClicked == 1) {
                    this.player("Sure am!");
                } else if (optionClicked == 2) {
                    this.player("Thanks, but no thanks.");
                    this.stage = 1000;
                }
                break;
            case 34:
                end();
                Shop booksShop = new Shop();
                ShopItem[] items = new ShopItem[]{new ShopItem(405, 50000)};
                booksShop.setStock(items);
                booksShop.setName("Jossik's Supplies");
                ShopSession session = new DefaultShopSession(player, booksShop);
                player.getAttributes().set("shop_session", session);
                break;
            default:
                end();
                break;
        }
    }
}
