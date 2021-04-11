package com.paragon464.gameserver.model.entity.mob.player.packets;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.SheepShearing;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.BankTransfer;
import com.paragon464.gameserver.model.content.CharacterDesign;
import com.paragon464.gameserver.model.content.Foods;
import com.paragon464.gameserver.model.content.dialogue.impl.BrainRobberyDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.CombatMasterDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.DesertTreasureDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.DommikDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.HorrorFromDeepDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.JustinServilDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.KamfreenaDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.KolodionDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.MountainerDaughterDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.OneiromancerDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.RecipeForDisasterDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.SantaDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.VannakaDialogue;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.skills.fishing.Fishing;
import com.paragon464.gameserver.model.content.skills.thieving.PickPocketSession;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.pathfinders.DumbPathFinder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.shop.CustomShops;
import com.paragon464.gameserver.model.shop.ShopManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NPCPackets {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPCPackets.class);

    public static void handleOptions(Player player) {
        Item item = player.getAttributes().get("packet_item");
        int type = player.getAttributes().getInt("packet_interaction_type");
        NPC npc = player.getAttributes().get("packet_npc");

        if (player.getPosition().equals(npc.getPosition())) {
            DumbPathFinder.generateMovement(player);
            return;
        }
        player.resetActionAttributes();
        if (type == -1) {
            handleItemOnNPC(player, npc, item);
            return;
        }
        if (!player.getControllerManager().processNPCInteract(npc, type)) {
        	return;
        }
        if (!MinigameHandler.handleNpcClicks(player, npc, type)) {
            switch (type) {
                case 1:
                    handleNPCOption1(player, npc);
                    break;
                case 2:
                    handleNPCOption2(player, npc);
                    break;
                case 3:
                    handleNPCOption3(player, npc);
                    break;
                case 4:
                    handleNPCOption4(player, npc);
                    break;
            }
        }
    }

    private static void handleItemOnNPC(Player player, NPC npc, Item item) {
    	if (!player.getControllerManager().processItemOnNPC(npc, item)) {
    		return;
    	}
        if (MinigameHandler.handleItemOnNpc(player, npc, item)) {
            return;
        }
        switch (npc.getId()) {
            case 106562://mining guild shop
                if (item.getId() == 112012) {
                    int count = item.getAmount();
                    player.getInventory().deleteItem(item);
                    player.getInventory().refresh();
                    player.getAttributes().addInt("mine_guild_points", count);
                }
                break;
            case 1807://hamal chieftain
                if (item.getId() == 4488) {
                    if (player.getAttributes().getInt("md_stage") == 1) {
                        player.getInventory().deleteItem(item);
                        player.getInventory().refresh();
                        player.getAttributes().set("md_stage", 2);
                        player.getAttributes().set("dialogue_session", new MountainerDaughterDialogue(npc, player, 16));
                    }
                }
                break;
            case 1069://wounded soldier
                if (Foods.Food.forId(item.getId()) != null) {
                    if (player.getAttributes().getInt("dt_stage") == 3) {
                        player.getInventory().deleteItem(item);
                        player.getInventory().refresh();
                        player.getAttributes().set("dt_stage", 4);
                        player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 43));
                    }
                }
                break;
        }
        switch (npc.getDefinition().getName().toLowerCase()) {
            case "archaeologist":
                if (item.getId() == 4670) {//blood diamond
                    if (player.getAttributes().getInt("dt_stage") == 1) {
                        player.getInventory().deleteItem(4670);
                        player.getInventory().refresh();
                        player.getAttributes().set("dt_stage", 1);
                        player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 29));
                    }
                } else if (item.getId() == 4671) {//ice diamond
                    if (player.getAttributes().getInt("dt_stage") == 4) {
                        player.getInventory().deleteItem(4671);
                        player.getInventory().refresh();
                        player.getAttributes().set("dt_stage", 5);
                        player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 47));
                    }
                } else if (item.getId() == 4672) {//smoke diamond
                    if (player.getAttributes().getInt("dt_stage") == 5) {
                        player.getInventory().deleteItem(4672);
                        player.getInventory().refresh();
                        player.getAttributes().set("dt_stage", 6);
                        player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 62));
                    }
                } else if (item.getId() == 4673) {//shadow diamond
                    if (player.getAttributes().getInt("dt_stage") == 6) {
                        player.getInventory().deleteItem(4673);
                        player.getInventory().refresh();
                        player.getAttributes().set("dt_stage", 7);
                        player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 70));
                    }
                }
                break;
            default:
                LOGGER.debug("Item on NPC: {}, item: {}", npc.getId(), item.getId());
                break;
        }
    }

    private static void handleNPCOption1(Player player, NPC npc) {
        if (npc.getSpawnPosition() != null) {
            LOGGER.debug("NPC[1]: {}, {}", npc.getId(), npc.getSpawnPosition().toString());
        }
        if (CustomShops.open(player, npc, 1)) {
            return;
        }
        switch (npc.getId()) {
            case 4906://Master woodsman
                ShopManager.openShop(player, 22);
                break;
            case 267://Justin servil
                player.getAttributes().set("dialogue_session", new JustinServilDialogue(npc, player));
                break;
            case 1552://Vote shop
                player.getAttributes().set("dialogue_session", new SantaDialogue(npc, player));
                break;
            case 872:// runecraft shop
                ShopManager.openShop(player, 14);
                break;
            case 1597://Vannaka
                player.getAttributes().set("dialogue_session", new VannakaDialogue(npc, player));
                break;
            case 165:// Gnome shopkeeper
                ShopManager.openShop(player, 16);
                break;
            case 545:// Dommink tanning
                player.getAttributes().set("dialogue_session", new DommikDialogue(npc, player));
                break;
            case 198:// Combat master
                player.getAttributes().set("dialogue_session", new CombatMasterDialogue(npc, player));
                break;
            case 1783:// Richards teamcape 1
                ShopManager.openShop(player, 10);
                break;
            case 4289:// Kamfreena
                player.getAttributes().set("dialogue_session", new KamfreenaDialogue(npc, player));
                break;
            case 905:// Kolodion
                player.getAttributes().set("dialogue_session", new KolodionDialogue(npc, player));
                break;
            case 3079:// monk
            case 5616://brother tranquility
                if (player.getAttributes().getInt("brain_robbery_stage") == 0) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player));
                } else if (player.getAttributes().getInt("brain_robbery_stage") == 1) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 15));
                } else if (player.getAttributes().getInt("brain_robbery_stage") == 3) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 38));
                } else {
                    if (npc.getId() == 3079) {
                        if (player.getAttributes().getInt("brain_robbery_stage") >= 4) {
                            player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 43));
                        }
                    } else if (npc.getId() == 5616) {
                        if (player.getAttributes().getInt("brain_robbery_stage") >= 5) {
                            player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 43));
                        }
                    }
                }
                break;
            case 3155://Bill teach
                if (player.getAttributes().getInt("brain_robbery_stage") >= 5) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 45));
                } else if (player.getAttributes().getInt("brain_robbery_stage") >= 2) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 27));
                } else if (player.getAttributes().getInt("brain_robbery_stage") == 1) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(npc, player, 18));
                }
                break;
            case 4511:// Oneiromancer
                player.getAttributes().set("dialogue_session", new OneiromancerDialogue(npc, player));
                break;
            case 1918:// Archaeologist
                player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 0));
                break;
            case 1972://rasolo
                if (player.getAttributes().getInt("dt_stage") == 6) {
                    player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 66));
                }
                break;
            case 2292://rug merch
                if (player.getAttributes().getInt("dt_stage") == 5) {
                    player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 60));
                }
                break;
            case 1862://ali m
                if (player.getAttributes().getInt("dt_stage") == 5) {
                    player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 51));
                }
                break;
            case 1069://wounded soldier
                if (player.getAttributes().getInt("dt_stage") == 2) {
                    player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 33));
                } else if (player.getAttributes().getInt("dt_stage") == 3) {
                    player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 41));
                }
                break;
            //bar npcs\\
            case 6026:
            case 6043:
            case 6028:
            case 6034:
                //bar npcs\\
                if (player.getAttributes().getInt("dt_stage") == 1) {
                    player.getAttributes().set("dialogue_session", new DesertTreasureDialogue(npc, player, 12));
                }
                break;
            case 278://cook
                if (player.getAttributes().getInt("rfd_stage") <= 1) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(npc, player));
                } else if (player.getAttributes().getInt("rfd_stage") == 11) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(npc, player, 43));
                }
                break;
            case 882:// Gypsy
                if (player.getAttributes().getInt("rfd_stage") == 1) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(npc, player, 10));
                } else if (player.getAttributes().getInt("rfd_stage") == 2 || player.getAttributes().getInt("rfd_stage") == 3) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(npc, player, 20));
                } else if (player.getAttributes().getInt("rfd_stage") >= 4 && player.getAttributes().getInt("rfd_stage") < 12) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(npc, player, 27));
                }
                break;
            case 741:
                if (player.getAttributes().getInt("rfd_stage") == 11) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(npc, player, 37));
                }
                break;
            case 1816://camp dweller
                if (player.getAttributes().getInt("md_stage") == 1) {
                    if (player.getInventory().hasItem(4488)) {
                        player.getAttributes().set("dialogue_session", new MountainerDaughterDialogue(npc, player, 15));
                    } else {
                        player.getAttributes().set("dialogue_session", new MountainerDaughterDialogue(npc, player, 12));
                    }
                }
                break;
            case 1807:// HAMAL
                player.getAttributes().set("dialogue_session", new MountainerDaughterDialogue(npc, player));
                break;
            case 1336://Larrissa
                if (player.getAttributes().getInt("hfd_stage") == 1) {
                    player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(npc, player, 14));
                }
                break;
            case 1334:// jossik
                final int hfdStage = player.getAttributes().getInt("hfd_stage");
                LOGGER.trace("Horror from the deep progress for Player \"{}\" is currently: {}",
                    player.getDetails().getName(), hfdStage);
                if (hfdStage == 0) {
                    player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(npc, player));
                } else if (hfdStage == 1) {
                    player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(npc, player, 10));
                } else if (hfdStage == 3) {
                    player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(npc, player, 23));
                } else if (hfdStage == 4) {
                    player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(npc, player, 28));
                }
                break;
            case 308:
                ShopManager.openShop(player, 9);
                break;
            case 322:// Monkfish fishing spot
                player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.MONKFISH));
                break;
            case 309:// Trout/Salmon fishing spot
                player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.TROUT_SALMON));
                break;
            case 952:// Shrimp fishing spot
                player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.SHRIMP));
                break;
            case 321:// Lobster fishing spot
                player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.LOBSTER));
                break;
            case 2676:// Char design
                CharacterDesign.open(player);
                break;
            case 1304:// Sailor - teleport options
                player.getInterfaceSettings().openInterface(583);
                break;
            case 219:// Fisherman
                ShopManager.openShop(player, 3);
                break;
            case 43:// Sheep
                SheepShearing.attemptShearing(player, npc);
                break;
        }
    }

    private static void handleNPCOption2(Player player, NPC npc) {
        String name = npc.getDefinition().getName().toLowerCase();
        Position spawn = npc.getSpawnPosition();
        int spawnPosX = -1;
        int spawnPosY = -1;
        if (spawn != null) {
            spawnPosX = spawn.getX();
            spawnPosY = spawn.getY();
        }
        if (npc.getSpawnPosition() != null) {
            LOGGER.debug("NPC[2]: {}, {}", npc.getId(), npc.getSpawnPosition().toString());
        }
        switch (name) {
            case "jatix":
                ShopManager.openShop(player, 15);
                break;
            case "aubury":
                ShopManager.openShop(player, 2);
                break;
            case "lowe":
                ShopManager.openShop(player, 3);
                break;
            case "thessalia":
                ShopManager.openShop(player, 6);
                break;
            case "horvik":
                ShopManager.openShop(player, 1);
                break;
            case "shop keeper":
                ShopManager.openShop(player, 0);
                break;
            case "banker":
                player.getVariables().setTransferContainer(new BankTransfer(player));
                break;
        }
        if (CustomShops.open(player, npc, 2)) {
            return;
        }
        switch (npc.getId()) {
            case 106562://mine guild shop
                ShopManager.openShop(player, 23);
                break;
            case 267://Justin servil
                ShopManager.openShop(player, 21);
                break;
            case 904://chamber guardian
                ShopManager.openShop(player, 17);
                break;
            case 1385:
                player.teleport(2340, 3675, 0);
                break;
            case 1304:// Sailor - teleport options
                player.getInterfaceSettings().openInterface(583);
                break;
            case 2:// Man
                player.getVariables().skillActionExecuting(new PickPocketSession(player, npc, PickPocketSession.NPC_TYPE.MAN));
                break;
            case 20:// Paladin
                player.getVariables().skillActionExecuting(new PickPocketSession(player, npc, PickPocketSession.NPC_TYPE.PALADIN));
                break;
            case 1783:// Richards teamcape 2
                ShopManager.openShop(player, 11);
                break;
            case 309:// Pike fishing spot
                player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.PIKE));
                break;
            case 321:// Tuna/sword fishing spot
                player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.TUNA_SWORDFISH));
                break;
            case 322:// Shark fishing spot
                if (spawnPosX == 3050 && spawnPosY == 3704)
                    player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.ROCKTAIL));
                else
                    player.getVariables().skillActionExecuting(new Fishing(player, Fishing.SpotType.SHARK));
                break;
            case 2620:// Tzhaar equipment shop
                ShopManager.openShop(player, 8);
                break;
            case 545:// craft shop
                ShopManager.openShop(player, 7);
                break;
            case 902:// Mb banker
                player.getVariables().setTransferContainer(new BankTransfer(player));
                break;
            case 2619:// tzhaar banker
                player.getVariables().setTransferContainer(new BankTransfer(player));
                break;
        }
    }

    private static void handleNPCOption3(Player player, NPC npc) {
        if (npc.getSpawnPosition() != null) {
            LOGGER.debug("NPC[3]: {}, {}", npc.getId(), npc.getSpawnPosition().toString());
        }
    }

    private static void handleNPCOption4(Player player, NPC npc) {
        String name = npc.getDefinition().getName().toLowerCase();
        if (npc.getSpawnPosition() != null) {
            LOGGER.debug("NPC[4]: {}, {}", npc.getId(), npc.getSpawnPosition().toString());
        }
        switch (name) {
            case "vannaka":
                ShopManager.openShop(player, 12);
                break;
        }
    }
}
