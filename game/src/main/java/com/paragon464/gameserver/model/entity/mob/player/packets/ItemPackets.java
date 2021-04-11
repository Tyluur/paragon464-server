package com.paragon464.gameserver.model.entity.mob.player.packets;

import com.paragon464.gameserver.io.database.table.log.PacketTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.CasketRewards;
import com.paragon464.gameserver.model.content.ConstructItem;
import com.paragon464.gameserver.model.content.Dicing;
import com.paragon464.gameserver.model.content.DwarfCannonSession;
import com.paragon464.gameserver.model.content.Foods;
import com.paragon464.gameserver.model.content.HolidayCracker;
import com.paragon464.gameserver.model.content.PotionMixing;
import com.paragon464.gameserver.model.content.Potions;
import com.paragon464.gameserver.model.content.bonds.CreditBond;
import com.paragon464.gameserver.model.content.dialogue.impl.BookCasket;
import com.paragon464.gameserver.model.content.dialogue.impl.EnchantedGem;
import com.paragon464.gameserver.model.content.dialogue.impl.RecipeForDisasterDialogue;
import com.paragon464.gameserver.model.content.imbuements.Imbuements;
import com.paragon464.gameserver.model.content.itemsets.ItemSets;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.minigames.barrows.CoffinSession;
import com.paragon464.gameserver.model.content.minigames.wguild.AnimatedArmourSession;
import com.paragon464.gameserver.model.content.miniquests.dt.DesertTreasure;
import com.paragon464.gameserver.model.content.skills.cooking.Cooking;
import com.paragon464.gameserver.model.content.skills.crafting.AmuletStringing;
import com.paragon464.gameserver.model.content.skills.crafting.GemCutting;
import com.paragon464.gameserver.model.content.skills.crafting.JewelryMaking;
import com.paragon464.gameserver.model.content.skills.crafting.LeatherMaking;
import com.paragon464.gameserver.model.content.skills.firemaking.FireData;
import com.paragon464.gameserver.model.content.skills.firemaking.FiremakingAction;
import com.paragon464.gameserver.model.content.skills.fletching.AmmoMaking;
import com.paragon464.gameserver.model.content.skills.fletching.BoltTips;
import com.paragon464.gameserver.model.content.skills.fletching.BowMaking;
import com.paragon464.gameserver.model.content.skills.herblore.CreateFinishedPotionAction;
import com.paragon464.gameserver.model.content.skills.herblore.CreateUnfinishedPotionAction;
import com.paragon464.gameserver.model.content.skills.herblore.FinishedPotionData;
import com.paragon464.gameserver.model.content.skills.herblore.HerbIdentification;
import com.paragon464.gameserver.model.content.skills.herblore.UnfinishedPotionData;
import com.paragon464.gameserver.model.content.skills.magic.TeleTabs;
import com.paragon464.gameserver.model.content.skills.prayer.AltarBoneSession;
import com.paragon464.gameserver.model.content.skills.prayer.BoneBurying;
import com.paragon464.gameserver.model.content.skills.smithing.ItemMaking;
import com.paragon464.gameserver.model.content.skills.smithing.SmithingAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemPackets {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPackets.class);

    public static void handleGroundItemOptions(Player player) {
        Item item = player.getAttributes().get("packet_item");
        int option = player.getAttributes().getInt("packet_interaction_type");

        player.resetActionAttributes();
        LOGGER.debug("2nd ground item click: {}", item.toString());
    }

    public static void handleItemPickup(Player player) {
        GroundItem item = player.getAttributes().get("packet_item");
        PacketTable.save(player, "itemInteract[item-pickup]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ".");
        player.resetActionAttributes();

        if (item != null) {
            GroundItemManager.pickupGroundItem(item, player);
        }
    }

    public static void handleItemOnObject(Player player) {
        GameObject object = player.getAttributes().get("packet_object");
        Item item = player.getAttributes().get("packet_item");
        player.resetActionAttributes();
        player.face(object.getCentreLocation());
        if (!player.getControllerManager().processItemOnObject(object, item)) {
        	return;
        }
        int objX = object.getPosition().getX();
        int objY = object.getPosition().getY();
        switch (object.getId()) {
            case 12348://cooks dining room door
                if (item.getId() == 1505) {
                    if (player.getAttributes().getInt("rfd_stage") >= 4 && player.getAttributes().getInt("rfd_stage") <= 9) {
                        player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(null, player, 31));
                    }
                }
                break;
            case 6:
                if (player.getAttributes().get("cannon_session") != null) {
                    DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
                    if (object.getPosition().equals(cannon.getGameObject().getPosition())) {
                        if (item.getId() == 2) {
                            int cannonBalls = cannon.getCannonBalls();
                            if (cannonBalls >= 30) {
                                player.getFrames().sendMessage("Your cannon is already full.");
                                return;
                            }
                            int newCannonBalls = item.getAmount();
                            if (newCannonBalls > 30) {
                                newCannonBalls = 30;
                            }
                            if (newCannonBalls + cannonBalls > 30) {
                                newCannonBalls = 30 - cannonBalls;
                            }
                            if (newCannonBalls < 1) {
                                return;
                            }
                            player.getInventory().deleteItem(new Item(2, newCannonBalls));
                            player.getInventory().refresh();
                            cannon.addCannonBalls(newCannonBalls);
                            player.getFrames().sendMessage("You load " + newCannonBalls + " cannonball" + (newCannonBalls > 1 ? "s" : "") + " into your cannon.");
                        }
                    }
                }
                break;
            case 7:
                if (player.getAttributes().get("cannon_session") != null) {
                    DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
                    if (object.getPosition().equals(cannon.getGameObject().getPosition())) {
                        if (item.getId() == 8) {
                            cannon.addPart(new Item(8, 1));
                        }
                    }
                }
                break;
            case 8:
                if (player.getAttributes().get("cannon_session") != null) {
                    DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
                    if (object.getPosition().equals(cannon.getGameObject().getPosition())) {
                        if (item.getId() == 10) {
                            cannon.addPart(new Item(10, 1));
                        }
                    }
                }
                break;
            case 9:
                if (player.getAttributes().get("cannon_session") != null) {
                    DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
                    if (object.getPosition().equals(cannon.getGameObject().getPosition())) {
                        if (item.getId() == 12) {
                            cannon.addPart(new Item(12, 1));
                        }
                    }
                }
                break;
            case 2783:// anvil
                if (item.getId() == 11286) {
                    ItemMaking.makeDFS(player, item);
                    break;
                } else if (item.getId() == 14478) {
                    if (player.getSkills().getLevel(SkillType.SMITHING) < 92) {
                        player.getFrames().sendMessage("You need a Smithing level of 92 to make a Dragon platebody.");
                        break;
                    }
                    if (player.getInventory().freeSlots() < 3) {
                        player.getFrames().sendMessage("You don't have enough inventory space to make this.");
                        break;
                    }
                    int[] pieces = {14472, 14474, 14476};
                    for (int i = 0; i < pieces.length; i++) {
                        if (!player.getInventory().hasItem(pieces[i])) {
                            player.getFrames().sendMessage("You need all 3 Ruined Dragon Armour pieces!");
                            break;
                        }
                    }
                    player.getSkills().addExperience(SkillType.SMITHING, 2000);
                    player.getInventory().deleteItem(14472);
                    player.getInventory().deleteItem(14474);
                    player.getInventory().deleteItem(14476);
                    player.getInventory().deleteItem(14478);
                    player.getInventory().addItem(14479);
                    player.getInventory().refresh();
                    break;
                }
                player.getVariables().skillActionExecuting(new SmithingAction(player, item));
                break;
            case 879:// Fountain
                if (item.getId() == 1704) {// uncharged glory
                    boolean bool = true;
                    if (player.getSkills().getCurrentLevel(SkillType.CRAFTING) < 80) {
                        bool = false;
                        player.getFrames().sendMessage("You need 80 crafting to recharge glories.");
                    }
                    if (bool) {
                        player.getFrames().sendMessage("You dip the glory inside the water..");
                        player.getFrames().sendMessage("Your Amulet of glory is restored.");
                        player.getInventory().replaceItem(1704, 1712);
                    }
                }
                break;
            case 13661:// Crystal of power
                if (item.getId() == 4207) {
                    player.getFrames().sendMessage("You place the Crystal Seed on the orb..");
                    player.getFrames().sendMessage("The Crystal orb creates a bow.");
                    player.getInventory().replaceItem(4207, 4212);
                }
                break;
            case 409:// Altar
                if (item.getDefinition().getName().toLowerCase().endsWith("bones")) {
                    player.getVariables().skillActionExecuting(new AltarBoneSession(player, item));
                }
                break;
            case 15621:// Magical Animator
                player.getAttributes().set("animator_session", new AnimatedArmourSession(player, object, item));
                break;
            case 2732:// fire object
            case 114:// Cooking range
            case 9682:// bandit camp range
                player.getVariables().skillActionExecuting(new Cooking(player, Cooking.getFishType(item), object));
                break;
            case 2643:// Pottery Oven
                JewelryMaking.displayInterface(player);
                break;
            case 3830:// kq
                if (item.getId() == 954) {
                    if (objX == 3509 && objY == 9497) {
                        player.teleport(3507, 9494, 0);
                    } else if (objX == 2347 && objY == 3664) {
                        player.teleport(3484, 9509, 2);
                    }
                }
                break;
            default:
                LOGGER.debug("Item on object: {}, item: {}", object.toString(), item.toString());
                break;
        }
    }

    public static void handleRightClickOne(Player player, Item item) {
        switch (item.getId()) {
            default:
                LOGGER.debug("Item right click 1: {}", item.toString());
                break;
        }
    }

    public static void handleRightClickTwo(Player player, Item item) {
        switch (item.getId()) {
            default:
                LOGGER.debug("Item right click 2: {}", item.toString());
                break;
        }
    }

    public static void handleItemOnItem(Player player, Item usedItem, Item usedWith) {
        Item itemUsed = usedItem;
        Item itemWith = usedWith;
        if (!player.getControllerManager().processItemOnItem(itemUsed, usedWith)) {
        	return;
        }
        for (int i = 0; i < 2; i++) {
            if (i == 1) {
                itemUsed = usedWith;
                itemWith = usedItem;
            }
            if (Imbuements.imbueItem(player, usedItem, usedWith)) {
                return;
            }
            if (itemUsed.getDefinition().getName().endsWith("bolt tips")) {
                if (player.getVariables().skillActionExecuting(new AmmoMaking(player, itemUsed, itemWith))) {
                    return;
                }
            }
            if (PotionMixing.mixDoses(player, itemUsed.getId(), itemWith.getId())) {
                return;
            }
            if (player.getVariables().skillActionExecuting(new LeatherMaking(player, itemUsed, itemWith))) {
                return;
            }
            FinishedPotionData unfPotion = FinishedPotionData.forId(itemUsed.getId(), itemWith.getId());
            if (unfPotion != null) {
                player.getVariables().skillActionExecuting(new CreateFinishedPotionAction(player, unfPotion));
                return;
            }
            switch (itemUsed.getId()) {
                case 1465://Weapon poison
                    if (itemWith.getId() == 1215) {//dragon dagger
                        if (player.getInventory().deleteItem(1465)) {
                            player.getInventory().deleteItem(1215);
                            player.getInventory().addItem(5698);
                            player.getInventory().refresh();
                        }
                    }
                    break;
                case CreateUnfinishedPotionAction.VIAL_OF_WATER:
                    UnfinishedPotionData data = UnfinishedPotionData.forId(itemWith.getId());
                    if (data != null) {
                        player.getVariables().skillActionExecuting(new CreateUnfinishedPotionAction(player, data));
                        return;
                    }
                    break;
                case 590:
                    FireData fireData = FireData.forId(itemWith.getId());
                    if (fireData != null) {
                        player.getVariables().skillActionExecuting(new FiremakingAction(player, fireData, 10));
                        player.playAnimation(733, AnimationPriority.HIGH);
                        return;
                    }
                    break;
                case 314:// Feather
                    if (player.getVariables().skillActionExecuting(new AmmoMaking(player, itemUsed, itemWith))) {
                        return;
                    }
                    break;
                case 1777:// Bowstring
                    if (player.getVariables().skillActionExecuting(new BowMaking(player, itemWith))) {
                        return;
                    }
                    break;
                case 946:// Knife
                    if (player.getVariables().skillActionExecuting(new BowMaking(player, itemWith))) {
                        return;
                    }
                    break;
                case 1759:// ball of wool
                    if (player.getVariables().skillActionExecuting(new AmuletStringing(player, itemWith.getId()))) {
                        return;
                    }
                    break;
                case 1755:// chisel
                    if (player.getVariables().skillActionExecuting(new GemCutting(player, itemWith.getId(), itemWith.getAmount()))) {
                        return;
                    } else if (player.getVariables().skillActionExecuting(new BoltTips(player, itemWith))) {
                        return;
                    }
                    break;
                default:
                    ConstructItem.check(player, itemUsed.getId(), itemWith.getId());
                    LOGGER.debug("Item on item: {}, used on: {}", itemUsed.toString(), itemWith.toString());
                    break;
            }
        }
    }

    public static void handleInventoryClick(Player player, Item item, int slot) {
        if (Foods.eat(player, item, slot)) {
            return;
        }
        if (Potions.canDrink(player, item, slot)) {
            return;
        }
        if (ItemSets.open(player, item)) {
            return;
        }
        if (TeleTabs.executing(player, item)) {
            return;
        }
        if (BoneBurying.executing(player, item)) {
            return;
        }
        if (HerbIdentification.executing(player, item)) {
            return;
        }
        if (CreditBond.redeemBond(player, item)) {
            return;
        }
        switch (item.getId()) {
            case 5070://bird nests
                player.getInventory().deleteItem(item);
                player.getInventory().refresh();
                player.getAttributes().addInt("wc_guild_points", 2);
                break;
            case 18642://Super set
                if (player.getInventory().freeSlots() >= 5) {
                    if (player.getInventory().deleteItem(item)) {
                        player.getInventory().addItem(new Item(2441, 50));
                        player.getInventory().addItem(new Item(2437, 50));
                        player.getInventory().addItem(new Item(2443, 50));
                        player.getInventory().addItem(new Item(2445, 50));
                        player.getInventory().addItem(new Item(3041, 50));
                        player.getInventory().refresh();
                    }
                } else {
                    player.getFrames().sendMessage("You don't have enough inventory space to do this.");
                }
                break;
            case 18641://500 bolt racks
                if (player.getInventory().deleteItem(item)) {
                    player.getInventory().addItem(new Item(4740, 500));
                    player.getInventory().refresh();
                }
                break;
            case 18639://250 drag bolts (e)
                if (player.getInventory().deleteItem(item)) {
                    player.getInventory().addItem(new Item(9244, 250));
                    player.getInventory().refresh();
                }
                break;
            case 18640://500 diamond bolts (e)
                if (player.getInventory().deleteItem(item)) {
                    player.getInventory().addItem(new Item(9243, 500));
                    player.getInventory().refresh();
                }
                break;
            case 10586://Double XP 24 hours
                if (player.getInventory().deleteItem(10586)) {
                    player.getInventory().refresh();
                    player.getAttributes().addInt("bonus_xp_ticks", 144000);
                    player.getFrames().sendMessage("You've added 24 Hours of Double XP to your account.");
                }
                break;
            case 15098://Dice bag
                Dicing.rollDice(player);
                break;
            case 18636://Elemental combo runes
                if (player.getInventory().deleteItem(18636)) {
                    player.getInventory().addItem(12850, 1000);
                    player.getInventory().refresh();
                }
                break;
            case 18637://Catalytic combo runes
                if (player.getInventory().deleteItem(18637)) {
                    player.getInventory().addItem(12851, 500);
                    player.getInventory().refresh();
                }
                break;
            case 962://Holiday cracker
                HolidayCracker.pull(player);
                break;
            case 6950://magical orb - dt
                if (player.getAttributes().getInt("dt_stage") == 4) {
                    player.teleport(2838, 3802, 1);
                    player.getInventory().deleteItem(6950);
                    player.getInventory().refresh();
                } else if (player.getAttributes().getInt("dt_stage") == 6) {
                    player.teleport(2739, 5075, 0);
                    player.getInventory().deleteItem(6950);
                    player.getInventory().refresh();
                    DesertTreasure.enterDamis(player);
                }
                break;
            case 6://cannon base
                if (MinigameHandler.minigameArea(player)) {
                    player.getFrames().sendMessage("You can't set this up in here!");
                    break;
                }
                player.getAttributes().set("cannon_session", new DwarfCannonSession(player, player.getPosition()));
                break;
            case 4155:
                NPC master = World.getWorld().findNPC(1597);
                player.getAttributes().set("dialogue_session", new EnchantedGem(master, player));
                break;
            case 2714:
                CasketRewards.open_casket(player);
                break;
            case 2717:
                if (player.getInventory().deleteItem(2717)) {
                    player.getInventory().addItem(995, NumberUtils.random(30000, 60000));
                    player.getInventory().refresh();
                }
                break;
            case 405:
                player.getAttributes().set("dialogue_session", new BookCasket(player, true));
                break;
            case 952:
                if (CoffinSession.enterCoffinArea(player)) {
                    break;
                }
                break;
            default:
                LOGGER.debug("Inventory click item: {}", item.toString());
                break;
        }
    }
}
