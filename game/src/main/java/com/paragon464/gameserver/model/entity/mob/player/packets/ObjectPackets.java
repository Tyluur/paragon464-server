package com.paragon464.gameserver.model.entity.mob.player.packets;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.BankTransfer;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.content.BankPins;
import com.paragon464.gameserver.model.content.CrystalChest;
import com.paragon464.gameserver.model.content.DwarfCannonSession;
import com.paragon464.gameserver.model.content.GodStatues;
import com.paragon464.gameserver.model.content.dialogue.impl.BrainRobberyDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.DungeonDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.EdgeLeverDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.HorrorFromDeepDialogue;
import com.paragon464.gameserver.model.content.dialogue.impl.RecipeForDisasterDialogue;
import com.paragon464.gameserver.model.content.godwars.GodWars;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.minigames.barrows.CoffinSession;
import com.paragon464.gameserver.model.content.minigames.fightcaves.CavesBattleSession;
import com.paragon464.gameserver.model.content.minigames.pestcontrol.PestWaiting;
import com.paragon464.gameserver.model.content.minigames.wguild.CyclopSession;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.miniquests.dt.DesertTreasure;
import com.paragon464.gameserver.model.content.skills.agility.AlkharidCourse;
import com.paragon464.gameserver.model.content.skills.agility.BarbarianCourse;
import com.paragon464.gameserver.model.content.skills.agility.DraynorCourse;
import com.paragon464.gameserver.model.content.skills.agility.GnomeCourse;
import com.paragon464.gameserver.model.content.skills.agility.SeersCourse;
import com.paragon464.gameserver.model.content.skills.agility.VarrockCourse;
import com.paragon464.gameserver.model.content.skills.agility.WildernessCourse;
import com.paragon464.gameserver.model.content.skills.cooking.Cooking;
import com.paragon464.gameserver.model.content.skills.crafting.Spinning;
import com.paragon464.gameserver.model.content.skills.magic.StaffInterface;
import com.paragon464.gameserver.model.content.skills.mining.Mining;
import com.paragon464.gameserver.model.content.skills.prayer.PrayerAltars;
import com.paragon464.gameserver.model.content.skills.runecrafting.Runecrafting;
import com.paragon464.gameserver.model.content.skills.smithing.SmeltingAction;
import com.paragon464.gameserver.model.content.skills.thieving.StallAction;
import com.paragon464.gameserver.model.content.skills.woodcutting.Woodcutting;
import com.paragon464.gameserver.model.gameobjects.ChaosTunnels;
import com.paragon464.gameserver.model.gameobjects.DoorManager;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.gameobjects.Ladders;
import com.paragon464.gameserver.model.gameobjects.LeversHandler;
import com.paragon464.gameserver.model.gameobjects.WildernessDitch;
import com.paragon464.gameserver.model.gameobjects.WildernessObelisks;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.shop.ShopManager;
import com.paragon464.gameserver.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPackets {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectPackets.class);

    public static void handleOption(Player player) {
        GameObject object = player.getAttributes().get("packet_object");//sike its position

        int type = player.getAttributes().getInt("packet_interaction_type");

        player.resetActionAttributes();
        if (MinigameHandler.handleObjectClicks(player, object, type)) {
            return;
        }
        if (!player.getControllerManager().processObjectInteract(object, type)) {
        	return;
        }
        LOGGER.debug("Option: {}, {}", type, object.toString());
        switch (type) {
            case 1:
                handleObjectPacket1(player, object);
                break;
            case 2:
                handleObjectPacket2(player, object);
                break;
            case 3:
                handleObjectPacket3(player, object);
                break;
            case 4:
                handleObjectPacket4(player, object);
                break;
        }
        player.face(object.getCentreLocation());
    }

    private static void handleObjectPacket1(final Player player, GameObject object) {
        int id = object.getId();
        Position loc = object.getPosition();
        final int x = loc.getX();
        final int y = loc.getY();
        final int z = loc.getZ();
        Position playerLoc = player.getCentreLocation();
        final int playerX = playerLoc.getX();
        final int playerY = playerLoc.getY();
        if (DoorManager.handleDoor(player, object)) {
            return;
        }
        if (WildernessObelisks.usingObelisk(player, object)) {
            return;
        }
        LeversHandler.Lever lever = World.getWorld().getGlobalObjects().getLevers().getLever(object);
        if (lever != null) {
            World.getWorld().getGlobalObjects().getLevers().pull(player, lever, object);
            return;
        }
        if (World.getWorld().getGlobalObjects().getLockPickableDoors().handle_door(player, object, true)) {
            return;
        }
        if (PrayerAltars.isPrayerAltar(player, object)) {
            return;
        }
        if (CoffinSession.enterCoffinArea(player)) {
            return;
        } else if (player.getVariables().getCoffinSession().leaveStairs(player, object)) {
            return;
        }
        if (GnomeCourse.isGnomeCourse(player, object)) {
            return;
        } else if (BarbarianCourse.isBarbarianCourse(player, object)) {
            return;
        } else if (WildernessCourse.isWildernessCourse(player, object)) {
            return;
        } else if (DraynorCourse.isCourse(player, object)) {
            return;
        } else if (AlkharidCourse.isCourse(player, object)) {
            return;
        } else if (VarrockCourse.isCourse(player, object)) {
            return;
        } else if (SeersCourse.isCourse(player, object)) {
            return;
        }
        if (object.getName().equalsIgnoreCase("bank booth")) {
            BankPins pin_session = player.getPinSession();
            pin_session.open();
            return;
        }
        if (ChaosTunnels.usePortals(player, object)) {
            return;
        }
        switch (id) {
            case 28891://chaos tunnels - wildy entrance

                break;
            case 128857://redwood ladders
                if (x == 1575 && y == 3483 && z == 0) {
                    player.objectTeleport(object, 1574, 3483, 1);
                } else if (x == 1575 && y == 3493 && z == 0) {
                    player.objectTeleport(object, 1574, 3493, 1);
                }
                break;
            case 128858://redwood ladders
                if (x == 1575 && y == 3483 && z == 1) {
                    player.objectTeleport(object, 1576, 3483, 0);
                } else if (x == 1575 && y == 3493 && z == 1) {
                    player.objectTeleport(object, 1576, 3493, 0);
                }
                break;
            case 130366://mining guild - door
                if (playerX == 3043 && playerY == 9729) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 3043 && playerY == 9730) {
                    player.objectTeleport(object, x, y, z);
                }
                break;
            case 130365://mining guild - door
                if (playerX == 3019 && playerY == 9732) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 3019 && playerY == 9733) {
                    player.objectTeleport(object, x, y, z);
                }
                break;
            case 130364://mining guild - door
                if (playerX == 3046 && playerY == 9756) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 3046 && playerY == 9757) {
                    player.objectTeleport(object, x, y, z);
                }
                break;
            case 104483://bank chest - mining guild
                player.getVariables().setTransferContainer(new BankTransfer(player));
                break;
            case 126721://stepping roots
            case 126720://stepping roots
                if (playerX == 2395 && playerY == 9767) {
                    player.objectTeleport(object, x, y - 1, z);
                } else if (playerX == 2395 && playerY == 9765) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 2383 && playerY == 9753) {
                    player.objectTeleport(object, x, y - 1, z);
                } else if (playerX == 2383 && playerY == 9751) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 2381 && playerY == 9750) {
                    player.objectTeleport(object, x - 1, y, z);
                } else if (playerX == 2379 && playerY == 9750) {
                    player.objectTeleport(object, x + 1, y, z);
                } else if (playerX == 2367 && playerY == 9747) {
                    player.objectTeleport(object, x, y - 1, z);
                } else if (playerX == 2367 && playerY == 9745) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 2420 && playerY == 9751) {
                    player.objectTeleport(object, x, y - 1, z);
                } else if (playerX == 2420 && playerY == 9749) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 2418 && playerY == 9743) {
                    player.objectTeleport(object, x, y - 1, z);
                } else if (playerX == 2418 && playerY == 9741) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 2427 && playerY == 9746) {
                    player.objectTeleport(object, x, y + 1, z);
                } else if (playerX == 2427 && playerY == 9748) {
                    player.objectTeleport(object, x, y - 1, z);
                } else if (playerX == 2411 && playerY == 9755) {
                    player.objectTeleport(object, x - 1, y, z);
                } else if (playerX == 2409 && playerY == 9755) {
                    player.objectTeleport(object, x + 1, y, z);
                }
                break;
            case 100537://slayer cave entrance to kraken
                //TODO: XXX
                break;
            case 101418://KQ entrance from slayer cave
                if (x == 2435 && y == 9824) {
                    player.objectTeleport(object, 3747, 5849, 0);
                }
                break;
            case 126712://Neieves main cave from KQ
                if (x == 3749 && y == 5849) {
                    player.objectTeleport(object, 2437, 9824, 0);
                }
                break;
            case 126254://bank deposit
                for (int i = 0; i < Inventory.SIZE; i++) {
                    Item item = player.getInventory().get(i);
                    if (item == null)
                        continue;
                    if (player.getInventory().deleteItem(item)) {
                        player.getInventory().refresh();
                        player.getBank().addItem(item);
                    }
                }
                break;
            case 26969://bank deposit - rocktails
                for (int i = 0; i < Inventory.SIZE; i++) {
                    Item item = player.getInventory().get(i);
                    if (item == null)
                        continue;
                    Cooking.RawFish fish = Cooking.getFishType(item);
                    if (fish == null)
                        continue;
                    if (player.getInventory().deleteItem(item)) {
                        player.getInventory().refresh();
                        player.getBank().addItem(item);
                    }
                }
                break;
            case 37928://corp exit
                player.objectTeleport(object, 3162, 3847, 0);
                break;
            case 37749://corp entrance
                player.objectTeleport(object, 2885, 4372, 0);
                break;
            case 37929:
                if (playerX == 2917) {
                    player.objectTeleport(object, playerX + 4, playerY, z);
                } else if (playerX == 2921) {
                    player.objectTeleport(object, playerX - 4, playerY, z);
                }
                break;
            case 1767://lava maze - TD entrance
                if (x == 3069 && y == 3856) {
                    player.objectTeleport(object, 2575, 5734, 0);
                }
                break;
            case 2971://dragons lair - water field
                if (playerY == 4691) {
                    player.objectTeleport(object, playerX, 4690, z);
                } else if (playerY == 4690) {
                    player.objectTeleport(object, playerX, 4691, z);
                }
                break;
            case 26293://Gwd rope - exit
                player.objectTeleport(object, 3243, 3947, 0);
                break;
            case 1815://Edge lever
                if (x == 3090 && y == 3475) {
                    player.getAttributes().set("dialogue_session", new EdgeLeverDialogue(player, object));
                }
                break;
            case 12348://Rfd door
                if (player.getAttributes().getInt("rfd_stage") >= 4 && player.getAttributes().getInt("rfd_stage") <= 9) {
                    player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(null, player, 31));
                }
                break;
            case 4551://light rocks - north
                player.objectTeleport(object, 2514, 3620, 0);
                break;
            case 4558://light rocks - south
                player.objectTeleport(object, 2522, 3595, 0);
                break;
            case 4577://lighthouse door
                if (playerY == 3635) {
                    player.objectTeleport(object, playerX, playerY + 1, z);
                } else if (playerY == 3636) {
                    player.objectTeleport(object, playerX, playerY - 1, z);
                }
                break;
            case 4544://hfd dag boss wall
                player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(null, player, 20));
                break;
            case 4383://trap ladder - 1st floor
                if (player.getAttributes().getInt("hfd_stage") < 2) {
                    player.getAttributes().set("dialogue_session", new HorrorFromDeepDialogue(null, player, 19));
                } else {
                    player.objectTeleport(object, 2519, 4619, 1);
                }
                break;
            case 4412://trap ladder - basement
                player.objectTeleport(object, 2510, 3644, 0);
                break;
            case 4568://lighthouse - 1st floor
                if (player.getAttributes().getInt("hfd_stage") >= 1 && player.getAttributes().getInt("hfd_stage") <= 2) {
                    player.objectTeleport(object, playerX, playerY, 1);
                }
                break;
            case 4569://lighthouse - 2nd floor
                player.objectTeleport(object, 2505, 3641, 2);
                break;
            case 4570://lighthouse - 3rd floor
                player.objectTeleport(object, 2508, 3640, 0);
                break;
            case 22119://harmony church entrance
                if (player.getAttributes().getInt("brain_robbery_stage") == 2) {
                    player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(null, player, 31));
                }
                break;
            case 5857://bear cave entrance
                if (player.getAttributes().getInt("md_stage") == 1) {
                	player.getControllerManager().startController(new BattleController(new NPC(1813)));
                }
                break;
            case 6552://ancients altar
                final int magicTabId = player.getSettings().isInResizable() ? 71 : 92;
                int childId = 192;

                StaffInterface.cancel(player, true);
                if (player.getSettings().getMagicType() != 2) {
                    player.getSettings().setMagicType(2);
                    childId = 193;
                } else {
                    player.getSettings().setMagicType(1);
                }

                player.getFrames().sendTab(magicTabId, childId);
                break;
            case 4031://shantay pass
                if (playerY == 3117) {
                    player.objectTeleport(object, playerX, playerY - 2, z);
                } else if (playerY == 3115) {
                    player.objectTeleport(object, playerX, playerY + 2, z);
                }
                break;
            case 6461://kamil gates
            case 6462://
                if (player.getAttributes().getInt("dt_stage") == 4) {
                    player.objectTeleport(object, 2850, 3809, 2);
                    DesertTreasure.enterKamil(player);
                }
                break;
            case 6437://vampire tomb
                if (player.getAttributes().getInt("dt_stage") == 1) {
                    DesertTreasure.enterDessous(player);
                }
                break;
            case 38698:
                //player.objectTeleport(2815, 5511, 0);
                break;
            case 38700:
                //player.objectTeleport(3366, 3269, 0);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                if (player.getAttributes().get("cannon_session") != null) {
                    DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
                    if (cannon.getGameObject().getPosition().equals(loc)) {
                        if (id == 6) {
                            cannon.fire();
                        } else {
                            cannon.destroy();
                        }
                    } else {
                        player.getFrames().sendMessage("That is not your cannon.");
                    }
                } else {
                    player.getFrames().sendMessage("That is not your cannon.");
                }
                break;
            case 126762:
                if (x == 3241 && y == 3949) {
                    player.objectTeleport(object, 2882, 5311, 2);
                } else if (x == 3231 && y == 3936) {
                    player.objectTeleport(object, 3150, 5559, 0);
                }
                break;
            case 28779://bork entrance
                if (x == 3142 && y == 5545) {
                    player.objectTeleport(object, 3114, 5528, 0);
                }
                break;
            case 29537://bork exit
                if (x == 3115 && y == 5528) {
                    player.objectTeleport(object, 3148, 5545, 0);
                }
                break;
            case 26428:
                GodWars.zamorakDoor(player, playerY == 5331);
                break;
            case 26427:
                GodWars.saradominDoor(player, playerX == 2907);
                break;
            case 26426:
                GodWars.armadylDoor(player, playerY == 5296);
                break;
            case 26425:
                GodWars.bandosDoor(player, playerX == 2864);
                break;
            case 26384:
                if (playerX == 2851 && playerY == 5333) {
                    player.objectTeleport(object, playerX - 1, playerY, z);
                } else if (playerX == 2850 && playerY == 5333) {
                    player.objectTeleport(object, 2851, 5333, 2);
                }
                break;
            case 26303:
                if (playerX == 2871 && playerY == 5279) {
                    player.objectTeleport(object, 2871, 5269, 2);
                } else if (playerX == 2871 && playerY == 5269) {
                    player.objectTeleport(object, 2872, 5279, 2);
                }
                break;
            case 26439://ice bridge
                if (playerX == 2885 && playerY == 5332) {
                    player.objectTeleport(object, 2885, 5345, 2);
                } else if (playerX == 2885 && playerY == 5345) {
                    player.objectTeleport(object, 2885, 5332, 2);
                }
                break;
            case 11:
                if (x == 2920 && y == 5274) {
                    player.objectTeleport(object, 2920, 5276, 1);
                } else if (x == 2914 && y == 5300) {
                    player.objectTeleport(object, 2912, 5300, 2);
                } else if (x == 2425 && y == 4693) {
                    player.objectTeleport(object, 2335, 3664, 0);
                } else if (x == 3557 && y == 9949) {
                    player.objectTeleport(object, 2335, 3664, 0);
                }
                break;
            case 4500:
                if (x == 2809 && y == 10001) {
                    player.objectTeleport(object, 2335, 3664, 0);
                }
                break;
            case 195:
                if (x == 2505 && y == 9461) {
                    player.objectTeleport(object, 2335, 3664, 0);
                }
                break;
            case 26444:
                if (x == 2913 && y == 5300) {
                    player.objectTeleport(object, 2915, 5300, 1);
                }
                break;
            case 26445:
                if (x == 2920 && y == 5274) {
                    player.objectTeleport(object, 2919, 5274, 0);
                }
                break;
            case 9761:// Crystal chest
                CrystalChest.execute(player);
                break;
            case 14315:// enter pc boat
                PestWaiting.enter(player);
                break;
            case 14314:// leave pc boat
                PestWaiting.leave(player);
                break;
            case 25339:// Up to mith drags - mith drags cave
                if (x == 1778 && y == 5344) {
                    player.objectTeleport(object, 1778, 5343, 1);
                }
                break;
            case 25340:// Down to brutals - mith drags cave
                if (x == 1778 && y == 5344) {
                    player.objectTeleport(object, 1778, 5346, 0);
                }
                break;
            case 12309:// Rfd chest
                if (player.getAttributes().getInt("rfd_stage") < 4) {
                    player.getFrames().sendMessage("You can't access this chest yet!");
                    break;
                }
                player.getVariables().setTransferContainer(new BankTransfer(player));
                break;
            case 1734:// wildy dungeon up
                if (NumberUtils.random(1) == 0) {
                    player.objectTeleport(object, 3044, 3927, 0);
                } else {
                    player.objectTeleport(object, 3045, 3927, 0);
                }
                break;
            case 1733:// wildy dungeon down
                if (NumberUtils.random(1) == 0) {
                    player.objectTeleport(object, 3045, 10322, 0);
                } else {
                    player.objectTeleport(object, 3044, 10322, 0);
                }
                break;
            case 1755:// Barb downstairs ladder
                if (x == 2547 && y == 9951) {
                    Ladders.executeLadder(player, false, 2546, 3551, 0);
                } else if (x == 3097 && y == 9867) {
                    Ladders.executeLadder(player, false, 3096, 3468, 0);
                } else if (x == 2884 && y == 9797) {
                    player.objectTeleport(object, 2311, 3785, 0);
                }
                break;
            case 8929:// Daggs
                player.objectTeleport(object, 2442, 10147, 0);
                break;
            case 8960://Dags southern gate
                if (playerX == 2490 && playerY == 10130) {
                    Position check = new Position(2490, 10132, 0);
                    for (Player local : player.getLocalPlayers()) {
                        if (local != null) {
                            if (local.getPosition().equals(check)) {
                                World.removeObjectTemporary(object, 8);
                            }
                        }
                    }
                } else if (playerX == 2490 && playerY == 10132) {
                    Position check = new Position(2490, 10130, 0);
                    for (Player local : player.getLocalPlayers()) {
                        if (local != null) {
                            if (local.getPosition().equals(check)) {
                                World.removeObjectTemporary(object, 8);
                            }
                        }
                    }
                }
                break;
            case 8958://Dags Northern gate
                if (playerX == 2490 && playerY == 10164) {
                    Position check = new Position(2490, 10162, 0);
                    for (Player local : player.getLocalPlayers()) {
                        if (local != null) {
                            if (local.getPosition().equals(check)) {
                                World.removeObjectTemporary(object, 8);
                            }
                        }
                    }
                } else if (playerX == 2490 && playerY == 10162) {
                    Position check = new Position(2490, 10164, 0);
                    for (Player local : player.getLocalPlayers()) {
                        if (local != null) {
                            if (local.getPosition().equals(check)) {
                                World.removeObjectTemporary(object, 8);
                            }
                        }
                    }
                }
                break;
            case 8959:// Daggs center gate
                if (playerX == 2490 && playerY == 10146) {
                    Position check = new Position(2490, 10148, 0);
                    for (Player local : player.getLocalPlayers()) {
                        if (local != null) {
                            if (local.getPosition().equals(check)) {
                                World.removeObjectTemporary(object, 8);
                            }
                        }
                    }
                } else if (playerX == 2490 && playerY == 10148) {
                    Position check = new Position(2490, 10146, 0);
                    for (Player local : player.getLocalPlayers()) {
                        if (local != null) {
                            if (local.getPosition().equals(check)) {
                                World.removeObjectTemporary(object, 8);
                            }
                        }
                    }
                } else {
                    player.objectTeleport(object, 2490, 10147, 0);
                }
                break;
            case 10177:// to dag kings
                Ladders.executeLadder(player, false, 2900, 4449, 0);
                break;
            case 10229:// out of dag kings
                Ladders.executeLadder(player, false, 2545, 10143, 0);
                break;
            case 5094:// Brimhaven stair cases
            case 5096:// Brimhaven stair cases
            case 5097:// Brimhaven stair cases
            case 5098:// Brimhaven stair cases
                if (x == 2648 && y == 9592) {
                    player.objectTeleport(object, 2643, 9595, 2);
                } else if (x == 2644 && y == 9593) {
                    player.objectTeleport(object, 2649 + NumberUtils.random(1), 9591, 0);
                } else if (x == 2635 && y == 9511) {
                    player.objectTeleport(object, 2636 + NumberUtils.random(1), 9517, 0);
                } else if (x == 2635 && y == 9511) {
                    player.objectTeleport(object, 2636 + NumberUtils.random(1), 9510, 2);
                } else if (x == 2635 && y == 9514) {
                    player.objectTeleport(object, 2636 + NumberUtils.random(1), 9510, 2);
                }
                break;
            case 1568:// edge dungeon - down
            case 26933:
                player.objectTeleport(object, 3097, 9868, 0);
                break;
            case 5088:// Brimhaven log balance
            case 5090:// Brimhaven log balance
                if (playerX == 2682 && playerY == 9506) {
                    player.objectTeleport(object, 2687, 9506, 0);
                } else if (playerX == 2687 && playerY == 9506) {
                    player.objectTeleport(object, 2682, 9506, 0);
                }
                break;
            case 5103:// Brimhaven dungeon - Vines
            case 5104:// Brimhaven dungeon - Vines
            case 5105:// Brimhaven dungeon - Vines
            case 5106:// Brimhaven dungeon - Vines
            case 5107:// Brimhaven dungeon - Vines
                if (playerX == 2691 && playerY == 9564) {
                    player.objectTeleport(object, 2689, 9564, 0);
                } else if (playerX == 2689 && playerY == 9564) {
                    player.objectTeleport(object, 2691, 9564, 0);
                } else if (playerX == 2683 && playerY == 9568) {
                    player.objectTeleport(object, 2683, 9570, 0);
                } else if (playerX == 2683 && playerY == 9570) {
                    player.objectTeleport(object, 2683, 9568, 0);
                } else if (playerX == 2672 && playerY == 9499) {
                    player.objectTeleport(object, 2674, 9499, 0);
                } else if (playerX == 2674 && playerY == 9499) {
                    player.objectTeleport(object, 2672, 9499, 0);
                } else if (playerX == 2674 && playerY == 9479) {
                    player.objectTeleport(object, 2676, 9479, 0);
                } else if (playerX == 2676 && playerY == 9479) {
                    player.objectTeleport(object, 2674, 9479, 0);
                } else if (playerX == 2695 && playerY == 9482) {
                    player.objectTeleport(object, 2693, 9482, 0);
                } else if (playerX == 2693 && playerY == 9482) {
                    player.objectTeleport(object, 2695, 9482, 0);
                }
                break;
            case 5110:// Brimhaven dungeon - stepping stones
            case 5111:// Brimhaven dungeon - stepping stones
                if (playerX == 2649 && playerY == 9562) {
                    player.objectTeleport(object, 2647, 9557, 0);
                } else if (playerX == 2647 && playerY == 9557) {
                    player.objectTeleport(object, 2649, 9562, 0);
                }
                break;
            case 9356:// Fight caves entrance
                player.getAttributes().set("caves_session", new CavesBattleSession(player));
                break;
            case 2873:// Sara statue
            case 2874:// Zammy statue
            case 2875:// Guthix statue
                GodStatues.executeChanting(player, object);
                break;
            case 2878:// Sparkling pool going in
                if (!player.getAttributes().isSet("mage_arena")) {
                    player.getFrames().sendMessage("You need to complete Mage Arena to enter.");
                    break;
                }
                player.objectTeleport(object, 2509, 4689, 0);
                break;
            case 2879:// Sparkling pool going out
                player.objectTeleport(object, 2542, 4718, 0);
                break;
            case 2147:// Ladder
                if (x == 2335 && y == 3663) {
                    player.getAttributes().set("dialogue_session", new DungeonDialogue(player, true));
                }
                break;
            case 2491:// rune ess
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.PURE_ESS));
                break;
            case 14859:// Rune rock
            case 107461:
            case 107494:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Runite_Ore));
                break;
            case 11963:// Adamant rock
            case 107493:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Adamant_Ore));
                break;
            case 11945:// Mithril rock
            case 107492:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Mithril_Ore));
                break;
            case 11951:// Gold rock
            case 107458:
            case 107491:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Gold_Ore));
                break;
            case 11930:// Coal rock
            case 107456:
            case 107489:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Coal_Ore));
                break;
            case 11954:// Iron rock
            case 107455:
            case 107488:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Iron_Ore));
                break;
            case 11959:// Tin rock
            case 107485:
            case 107486:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Tin_Ore));
                break;
            case 11960:// Copper rock
            case 107453:
            case 107484:
                player.getVariables().skillActionExecuting(new Mining(player, Mining.RockDefinitions.Copper_Ore));
                break;
            case 6823:// Veracs Sarcophagus
                player.getVariables().setCoffinSession(CoffinSession.Brothers.VERAC);
                break;
            case 6821:// Ahrims Sarcophagus
                player.getVariables().setCoffinSession(CoffinSession.Brothers.AHRIM);
                break;
            case 6771:// Dharoks Sarcophagus
                player.getVariables().setCoffinSession(CoffinSession.Brothers.DHAROK);
                break;
            case 6822:// Karils Sarcophagus
                player.getVariables().setCoffinSession(CoffinSession.Brothers.KARIL);
                break;
            case 6772:// Torags Sarcophagus
                player.getVariables().setCoffinSession(CoffinSession.Brothers.TORAG);
                break;
            case 6773:// Guthans Sarcophagus
                player.getVariables().setCoffinSession(CoffinSession.Brothers.GUTHAN);
                break;
            case 2647:// Door
                if (x == 2611 && y == 3394 || x == 2933 && y == 3289) {
                    player.objectTeleport(object, Config.RESPAWN_POSITION);
                }
                break;
            case 1530:// fishing guild door
                if (x == 2611 && y == 3398) {
                    player.objectTeleport(object, Config.RESPAWN_POSITION);
                }
                break;
            case 15653:// Warriors guild door leaving
                if (x == 2877 && y == 3546) {
                    if (playerX == 2876 && playerY == 3546) {
                        player.objectTeleport(object, 2877, 3546, 0);
                    } else {
                        final int attackLevel = player.getSkills().getLevel(SkillType.ATTACK);
                        final int strengthLevel = player.getSkills().getLevel(SkillType.STRENGTH);
                        final int combinedLevel = attackLevel + strengthLevel;
                        if (combinedLevel < 130 && attackLevel != 99 && strengthLevel != 99) {
                            player.getFrames().sendMessage("Your Attack and Strength levels combined must total 130 or more.");
                            break;
                        }
                        player.objectTeleport(object, 2876, 3546, 0);
                    }
                }
                break;
            case 2465:// Portal
                if (x == 2841 && y == 4828) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                } else if (x == 3565 && y == 3308) {
                    player.objectTeleport(object, Config.RESPAWN_POSITION);
                } /*
                 * else if (x == 3305 && y >= 9375 && y <= 9376) {
                 * player.objectTeleport(Constants.LOGIN_AREA); }
                 */
                break;
            case 2467:// Portal
                if (x == 3495 && y == 4832) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2468:// Portal
                if (x == 2655 && y == 4829) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2469:// Portal
                if (x == 2574 && y == 4850) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2466:// Portal
                if (x == 2793 && y == 4827) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2471:// Portal
                if (x == 2142 && y == 4854) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2472:// Portal
                if (x == 2464 && y == 4817) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2473:// Portal
                if (x == 2400 && y == 4834) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2474:// Portal
                if (x == 2282 && y == 4837) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2475:// Portal
                if (x == 2208 && y == 4829) {// altar portals leave
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2477:// Portal
                if (x == 2468 && y == 4888 || x == 2161 && y == 3870) {
                    player.objectTeleport(object, 2323, 3679, 0);
                }
                break;
            case 2492:// Portal
                if (x == 2889 && y == 4813) {// rune ess leave portal
                    player.objectTeleport(object, 2329, 3673, 0);
                }
                break;
            case 2156:// Yanille altar portal
                player.getInterfaceSettings().openInterface(598);
                break;
            case 1722:// Yanille guild - Staircase - up
                if (x == 2590 && y == 3089) {
                    player.objectTeleport(object, 2590, 3087, 2);
                }
                break;
            case 1723:// Yanille guild - stairscase - down
                if (x == 2590 && y == 3085) {
                    player.objectTeleport(object, 2590, 3088, 0);
                }
                break;
            case 16640:// yanill - mine up
                if (x == 2330 && y == 10353) {
                    player.objectTeleport(object, 2594, 3086, 0);
                }
                break;
            case 1754:// yanille - mine down
                if (x == 2594 && y == 3085) {
                    player.objectTeleport(object, 2330, 10352, 2);
                }
                break;
            case 3832:// kq queen rope - up
                if (x == 3508 && y == 9494) {
                    player.objectTeleport(object, 3510, 9495, 2);
                }
                break;
            case 3828:// kq tunnels entrance
                if (x == 2943 && y == 3359) {
                    player.objectTeleport(object, 3484, 9509, 2);
                }
                break;
            case 3829:// kq tunnels - out
                if (x == 3483 && y == 9509) {
                    player.objectTeleport(object, 2346, 3665, 0);
                }
                break;
            case 2:// tzhaar entrance
                if (x == 2347 && y == 3667) {
                    player.objectTeleport(object, 2480, 5175, 0);
                }
                break;
            case 9359:// tzhaar leave
                if (x == 2479 && y == 5176) {
                    player.objectTeleport(object, 2346, 3668, 0);
                }
                break;
            case 1765:// Kbd ladder down
                if (x == 3017 && y == 3849) {
                    player.objectTeleport(object, 3069, 10255, 0);
                }
                break;
            case 1766:// Kbd ladder up
                if (x == 3069 && y == 10256) {
                    player.objectTeleport(object, 3017, 3850, 0);
                }
                break;
            case 15644:// warriors guild
            case 15641:// warriors guild
                if (player.getPosition().getX() == 2847) {
                    CyclopSession cyclop_session = player.getAttributes().get("cyclop_session");
                    if (cyclop_session != null) {
                        cyclop_session.end(false);
                    }
                }
                break;
            case 4493:// Slayer tower - up
                if (NumberUtils.random(1) == 0)
                    player.objectTeleport(object, 3433, 3538, 1);
                else
                    player.objectTeleport(object, 3433, 3537, 1);
                break;
            case 4494:// Slayer tower - down
                if (NumberUtils.random(1) == 0)
                    player.objectTeleport(object, 3438, 3538, 0);
                else
                    player.objectTeleport(object, 3438, 3537, 0);
                break;
            case 4495:// Slayer tower 1st floor - up
                if (NumberUtils.random(1) == 0)
                    player.objectTeleport(object, 3417, 3541, 2);
                else
                    player.objectTeleport(object, 3417, 3540, 2);
                break;
            case 4496:// Slayer tower 2nd floor - down
                if (NumberUtils.random(1) == 0)
                    player.objectTeleport(object, 3412, 3540, 1);
                else
                    player.objectTeleport(object, 3412, 3541, 1);
                break;
            case 1738:// warrior guild stairs - going up
                if (x == 2839 && y == 3537) {
                    player.objectTeleport(object, 2840, 3539, 2);
                }
                break;
            case 15638:// warriors guild stairs - going down
                player.objectTeleport(object, 2839, 3539, 0);
                break;
            case 245:// Ship's upstairs ladders going up
                if (player.getPosition().equals(new Position(3019, 3958, 1))) {
                    player.objectTeleport(object, 3019, 3960, 2);
                } else if (player.getPosition().equals(new Position(3017, 3958, 1))) {
                    player.objectTeleport(object, 3017, 3960, 2);
                }
                break;
            case 246:// Ships upstairs ladders going down
                if (player.getPosition().equals(new Position(3017, 3960, 2))) {
                    player.objectTeleport(object, 3017, 3958, 1);
                } else if (player.getPosition().equals(new Position(3019, 3960, 2))) {
                    player.objectTeleport(object, 3019, 3958, 1);
                }
                break;
            case 272:// Ship ladder
                Ladders.executeLadder(player, false, 3018, 3958, 1);
                break;
            case 273:// Ship ladder going down
                Ladders.executeLadder(player, true, 3018, 3958, 0);
                break;
            case 32048:// wildy dungeon up
                if (NumberUtils.random(1) == 0) {
                    player.objectTeleport(object, 3044, 3927, 0);
                } else {
                    player.objectTeleport(object, 3045, 3927, 0);
                }
                break;
            case 733:// Webs
            case 100733://
                World.getWorld().getGlobalObjects().slashWebs(player, object);
                break;
            case 1746:// shilo bank ladders (upstairs)
                if (y == 2954) {
                    if (x == 2844 || x == 2860) {
                        player.objectTeleport(object, new Position(player.getPosition().getX(), player.getPosition().getY(),
                            player.getPosition().getZ() - 1));
                    }
                }
                break;
            case 1747:// shilo bank ladders (downstairs)
                if (y == 2954) {
                    if (x == 2860 || x == 2844) {
                        player.objectTeleport(object, new Position(player.getPosition().getX(), player.getPosition().getY(),
                            player.getPosition().getZ() + 1));
                    }
                }
                break;
            case 23271:// Ditch
                WildernessDitch.jump(player, object);
                break;
            default:
                switch (object.getName()) {
                    case "Tree":// Normal logs
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.NORMAL));
                        break;
                    case "Oak":
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.OAK));
                        break;
                    case "Willow":
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.WILLOW));
                        break;
                    case "Maple tree":
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.MAPLE));
                        break;
                    case "Yew":
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.YEW));
                        break;
                    case "Magic tree":
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.MAGIC));
                        break;
                    case "Redwood":
                        player.getVariables().skillActionExecuting(new Woodcutting(player, Woodcutting.TreeDefinitions.REDWOOD));
                        break;
                    case "Altar":
                        player.getVariables().skillActionExecuting(new Runecrafting(player, object));
                        break;
                }
                break;
        }
    }

    private static void handleObjectPacket2(Player player, GameObject object) {
        int id = object.getId();
        Position loc = object.getPosition();
        final int x = loc.getX();
        final int y = loc.getY();
        final int z = loc.getZ();
        Position playerLoc = player.getCentreLocation();
        final int playerX = playerLoc.getX();
        final int playerY = playerLoc.getY();
        if (object.getName().equalsIgnoreCase("bank booth")) {
            player.getVariables().setTransferContainer(new BankTransfer(player));
            return;
        }
        if (World.getWorld().getGlobalObjects().getLockPickableDoors().handle_door(player, object, false)) {
            return;
        }
        switch (id) {
            case 2491:// rune ess
                player.getFrames().sendMessage("This rock contains Rune essance.");
                break;
            case 14859:// Rune rock
                player.getFrames().sendMessage("This rock contains Runite.");
                break;
            case 11963:// Adamant rock
                player.getFrames().sendMessage("This rock contains Adamamite.");
                break;
            case 11945:// Mithril rock
                player.getFrames().sendMessage("This rock contains Mithril.");
                break;
            case 11951:// Gold rock
                player.getFrames().sendMessage("This rock contains Gold.");
                break;
            case 11930:// Coal rock
                player.getFrames().sendMessage("This rock contains Coal.");
                break;
            case 11954:// Iron rock
                player.getFrames().sendMessage("This rock contains Iron.");
                break;
            case 11959:// Tin rock
                player.getFrames().sendMessage("This rock contains Tin.");
                break;
            case 11960:// Copper rock
                player.getFrames().sendMessage("This rock contains Copper.");
                break;
            case 6:
                if (player.getAttributes().get("cannon_session") != null) {
                    DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
                    if (loc.equals(cannon.getGameObject().getPosition())) {
                        cannon.destroy();
                    }
                }
                break;
            case 2560://Silk stall
                player.getVariables().skillActionExecuting(new StallAction(player, StallAction.STALL_TYPE.SILK));
                break;
            case 2561://Bakers stall
                player.getVariables().skillActionExecuting(new StallAction(player, StallAction.STALL_TYPE.BAKER));
                break;
            case 2562://Gem stall
                player.getVariables().skillActionExecuting(new StallAction(player, StallAction.STALL_TYPE.GEM));
                break;
            case 4277://Fish stall
                player.getVariables().skillActionExecuting(new StallAction(player, StallAction.STALL_TYPE.FISH));
                break;
            case 6189:// Furnace
                player.getVariables().skillActionExecuting(new SmeltingAction(player));
                break;
            case 2644:// Spinning wheel
                player.getVariables().skillActionExecuting(new Spinning(player));
                break;
        }
    }

    private static void handleObjectPacket3(final Player player, GameObject object) {
        int id = object.getId();
        Position loc = object.getPosition();
        final int x = loc.getX();
        final int y = loc.getY();
        final int z = loc.getZ();
        Position playerLoc = player.getCentreLocation();
        final int playerX = playerLoc.getX();
        final int playerY = playerLoc.getY();
        switch (id) {
            case 12309:// Rfd chest
                if (player.getAttributes().getInt("rfd_stage") < 4) {
                    player.getFrames().sendMessage("You can't access this chest yet!");
                    break;
                }
                ShopManager.openShop(player, 19);
                break;
            default:
                LOGGER.debug("Object[3]: {}", object.toString());
                break;
        }
    }

    private static void handleObjectPacket4(final Player player, GameObject object) {
        int id = object.getId();
        Position loc = object.getPosition();
        final int x = loc.getX();
        final int y = loc.getY();
        final int z = loc.getZ();
        Position playerLoc = player.getCentreLocation();
        final int playerX = playerLoc.getX();
        final int playerY = playerLoc.getY();
        switch (id) {
        }
    }
}
