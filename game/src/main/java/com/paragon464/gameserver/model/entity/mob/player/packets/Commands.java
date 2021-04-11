package com.paragon464.gameserver.model.entity.mob.player.packets;

import com.google.common.base.Preconditions;
import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.cache.definitions.CachedNpcDefinition;
import com.paragon464.gameserver.cache.definitions.CachedObjectDefinition;
import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.io.database.table.definition.shop.ShopTable;
import com.paragon464.gameserver.io.database.table.definition.shop.StockTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCBonuses;
import com.paragon464.gameserver.model.entity.mob.npc.NPCCombatDefinition;
import com.paragon464.gameserver.model.entity.mob.npc.NPCDefinition;
import com.paragon464.gameserver.model.entity.mob.npc.NPCLoaders;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSkills;
import com.paragon464.gameserver.model.entity.mob.npc.drops.NPCDrops;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.Skills;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.BankTransfer;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.CharacterDesign;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.content.miniquests.rfd.RFDBattles;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.ItemLoaders;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.shop.ShopManager;
import com.paragon464.gameserver.tickable.impl.SystemUpdateTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands {

    private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);

    public static void handleGlobalCommands(final Player player, String command) {
        String[] cmd = command.split(" ");
    }

    public static void handleModeratorCommands(Player player, String command) {
        final String[] cmd = command.split(" ");
        if (cmd[0].equals("kick")) {
            if (Areas.isInAttackableArea(player) && !Config.DEBUG_MODE) {
                player.getFrames().sendMessage("You can't use this at the moment.");
                return;
            }
            String user = command.substring(5).replace("_", " ");
            Player other = World.getWorld().getPlayerByName(user);
            if (other != null) {
                if (Areas.isInAttackableArea(other)) {
                    player.getFrames().sendMessage("That player is not in a safe area.");
                    return;
                }
                other.getFrames().forceLogout();
                player.getFrames().sendMessage("You kicked " + other.getDetails().getName() + ".");
            }
        }

        if (cmd[0].matches("x?(go|tele|tp)(2|to)")) {
            final String targetPlayerName = Arrays.toString(Arrays.copyOfRange(cmd, 1, cmd.length)).replaceAll("_", " ")
                .replaceAll("(['\"\\[\\]]|,(?=\\s))", "");
            final Player targetPlayer = World.getWorld().getPlayerByName(targetPlayerName);

            player.teleport(targetPlayer.getPosition());
            player.getFrames().sendMessage("You have teleported to " + targetPlayerName);
            targetPlayer.getFrames().sendMessage(player.getDetails().getName() + " has teleported to you.");
        }

        if (cmd[0].matches("(x?(bring|tele|tp)(2|to)me|bring(player|user)?)")) {
            final String targetPlayerName = Arrays.toString(Arrays.copyOfRange(cmd, 1, cmd.length)).replaceAll("_", " ")
                .replaceAll("(['\"\\[\\]]|,(?=\\s))", "");
            final Player targetPlayer = World.getWorld().getPlayerByName(targetPlayerName);

            targetPlayer.teleport(player.getPosition());
            targetPlayer.getFrames().sendMessage("You have teleported to " + player.getDetails().getName());
            player.getFrames().sendMessage(targetPlayerName + " has teleported to you.");
        }

        if (cmd[0].matches("(tele(port)?|tp|go)(2|to)?")) {
            final String args = Arrays.toString(Arrays.copyOfRange(cmd, 1, cmd.length)).replaceAll("([\\[\\]]|,(?=\\s))", "");

            if (args.matches("\\d+\\s\\d+(\\s\\d+)?")) {
                final int x = Integer.parseInt(cmd[1]);
                final int y = Integer.parseInt(cmd[2]);
                final int z = cmd.length == 4 ? Integer.parseInt(cmd[3]) : player.getPosition().getZ();

                player.teleport(new Position(x, y, z));
            } else if (args.matches("(\\d{1,3}?,?){5}")) {
                String[] command2 = command.split(",");
                final int x = (Integer.parseInt(command2[1]) << 6) + Integer.parseInt(command2[3]);
                final int y = (Integer.parseInt(command2[2]) << 6) + Integer.parseInt(command2[4]);
                final int z = player.getPosition().getZ();

                player.teleport(new Position(x, y, z));
            } else if (args.matches("([\"'][\\w\\s]+[\"']|\\s)+")) {
                final String[] players = args.replaceAll("((?<!\\w)[\"\\s]|\"(?!.))", "").split("\"");
                final Player dstPlayer = World.getWorld().getPlayerByName(players[players.length - 1]);
                final Player srcPlayer = players.length >= 2 ? World.getWorld().getPlayerByName(players[0]) : player;

                dstPlayer.getFrames().sendMessage("Player: \"" + srcPlayer.getDetails().getName() + "\" has teleported to you.");
                srcPlayer.getFrames().sendMessage("You have been teleported to player \"" + dstPlayer.getDetails().getName() + "\".");

                if (!srcPlayer.equals(player))
                    player.getFrames().sendMessage("You have teleported \"" + srcPlayer.getDetails().getName() +
                        "\" to \"" + dstPlayer.getDetails().getName() + "\".");

                srcPlayer.teleport(dstPlayer.getPosition());
            }
        }
    }

    public static void handleAdministratorCommands(Player player, String command) {
        String[] cmd = command.split(" ");
        if (cmd[0].equals("reloadnpcs")) {
            for (NPC npcs : World.getWorld().getNPCS()) {
                if (npcs != null) {
                    World.getWorld().unregister(npcs);
                }
            }
            NPCCombatDefinition.definitions.clear();
            NPCSkills.definitions.clear();
            NPCDefinition.definitions.clear();
            NPCBonuses.definitions.clear();
            NPCDrops.generics.clear();
            ConnectionPool.execute(NPCLoaders::init);
        } else if (cmd[0].equals("reloaditems")) {
            ItemDefinition.definitions.clear();
            ConnectionPool.execute(ItemLoaders::init);
        } else if (cmd[0].equals("reloadshops")) {
            ShopManager.shop_definitions.clear();
            try {
                ShopTable.load();
                StockTable.load();
            } catch (Exception e) {
                LOGGER.error("An error occurred whilst reloading shops!", e);
            }
        }

        if (cmd[0].equals("s")) {
            int min = Integer.valueOf(cmd[1]);
            int max = Integer.valueOf(cmd[2]);
            int interfaceId = Integer.valueOf(cmd[3]);
            for (int i = min; i < max; i++) {
                player.getFrames().modifyText("string" + i, interfaceId, i);
            }
        }
        if (cmd[0].equals("varp")) {
            player.getFrames().sendVarp(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
        }

        if (cmd[0].equals("shop")) {
            int id = Integer.valueOf(cmd[1]);
            ShopManager.openShop(player, id);
        } else if (cmd[0].equals("char")) {
            CharacterDesign.open(player);
        } else if (cmd[0].equals("kickall")) {
            for (Player p : World.getWorld().getPlayers()) {
                if (p == null) continue;
                p.getFrames().forceLogout();
            }
            World.getWorld().getEngine().setUpdateTimer(5000);
        } else if (cmd[0].equals("update")) {
            // 10mins
            if (!World.getWorld().getEngine().systemUpdating()) {
                World.getWorld().getEngine().setUpdateTimer(1000);
                for (Player players : World.getWorld().getPlayers()) {
                    if (players != null) {
                        players.getFrames().sendSystemUpdate(20);
                    }
                }
                World.getWorld().submit(new SystemUpdateTick(1));
            }
        } else if (cmd[0].equals("quickrestart")) {
            // 1min
            if (!World.getWorld().getEngine().systemUpdating()) {
                World.getWorld().getEngine().setUpdateTimer(100);
                for (Player players : World.getWorld().getPlayers()) {
                    if (players != null) {
                        players.getFrames().sendSystemUpdate(2);
                    }
                }
                World.getWorld().submit(new SystemUpdateTick(1));
            }
        } else if (cmd[0].equals("cs")) {
            Item[] rewards = {new Item(1038, 1), new Item(1040, 1), new Item(1042, 1), new Item(1044, 1),
                new Item(1046, 1), new Item(1048, 1)};
            player.getFrames().sendClueScroll(rewards);
            for (int i = 0; i < rewards.length; i++) {
                player.getInventory().addItem(rewards[i]);
            }
        } else if (cmd[0].matches("i(te?m)?[_-]?((search|find)(er)?|lookup)")) {
            List<String> results = new ArrayList<>();
            int count = 0;
            String searched = cmd[1];
            if (cmd.length > 2)
                searched = cmd[1] + " " + cmd[2];
            if (searched.length() < 4) {
                player.getFrames().sendMessage("Please enter at least 4 characters in your search.");
                return;
            }
            for (int i = 0; i < ItemDefinition.definitions.size(); i++) {
                ItemDefinition def = ItemDefinition.forId(i);
                if (def == null || def.getName() == null)
                    continue;
                String name = def.getName().toLowerCase();
                if (name.contains(searched) || name.equalsIgnoreCase(searched)) {
                    results.add("'" + def.getName() + "' - " + def.getId());
                    count++;
                }
            }
            player.getInterfaceSettings().openInterface(275);
            player.getFrames().modifyText(count + " results for '" + searched + "'", 275, 2);
            int line = 4;
            for (int i = 0; i < count && i < 54; i++) {
                if (results.get(i) != null) {
                    player.getFrames().modifyText(results.get(i), 275, line);
                    line++;
                }
            }
            if (results.size() > 50) {
                player.getFrames()
                    .sendMessage("'" + searched + "' returned more than 50 results, refine your search terms.");
            }
            for (int extraLines = line; extraLines < 134; extraLines++) {
                player.getFrames().modifyText("", 275, extraLines);
            }
        } else if (cmd[0].matches("(item|pickup)")) {
            try {
                int itemId = Integer.parseInt(cmd[1]);
                int amount = 1;
                if (cmd.length > 2)
                    amount = Integer.parseInt(cmd[2]);
                ItemDefinition def = ItemDefinition.forId(itemId);
                if (def == null && !Config.DEBUG_MODE) {
                    player.getFrames().sendMessage("" + itemId + " isn't added to game.");
                    return;
                }
                player.getInventory().addItem(new Item(itemId, amount));
                player.getInventory().refresh();
            } catch (NumberFormatException e) {
                // Do nothing, search was using string
            }
            String itemName = cmd[1];
            int amount = 1;
            if (cmd.length > 2) {
                amount = Integer.parseInt(cmd[2]);
            }
            for (int i = 0; i < ItemDefinition.definitions.size(); i++) {
                ItemDefinition def = ItemDefinition.forId(i);
                if (def == null || def.getName() == null) {
                    continue;
                }
                if (def.getName().equalsIgnoreCase(itemName.replaceAll("_", " ").toLowerCase())) {
                    player.getInventory().addItem(new Item(def.getId(), amount));
                    break;
                }
            }
            player.getInventory().refresh();
        } else if (cmd[0].matches("o(bj(ect)?)?[_-]?((search|find)(er)?|lookup)")) {
            List<String> results = new ArrayList<>();
            int count = 0;
            String searched = cmd[1];
            if (cmd.length > 2)
                searched = cmd[1] + " " + cmd[2];
            if (searched.length() < 4) {
                player.getFrames().sendMessage("Please enter at least 4 characters in your search.");
                return;
            }
            for (int i = 0; i < 40000; i++) {
                CachedObjectDefinition def = CachedObjectDefinition.forId(i);
                if (def == null || def.name == null)
                    continue;
                String name = def.name.toLowerCase();
                if (name.contains(searched) || name.equalsIgnoreCase(searched)) {
                    results.add("'" + def.name + "' - " + i);
                    count++;
                }
            }
            player.getInterfaceSettings().openInterface(275);
            player.getFrames().modifyText(count + " results for '" + searched + "'", 275, 2);
            int line = 4;
            for (int i = 0; i < count && i < 54; i++) {
                if (results.get(i) != null) {
                    player.getFrames().modifyText(results.get(i), 275, line);
                    line++;
                }
            }
            if (results.size() > 50) {
                player.getFrames().sendMessage(
                    "'" + searched + "' returned more than 50 results, please refine your search terms.");
            }
            for (int extraLines = line; extraLines < 134; extraLines++) {
                player.getFrames().modifyText("", 275, extraLines);
            }
        } else if (cmd[0].matches("reset(skills|levels|stats|lvls)?")) {
            player.getSkills().resetSkills();
        } else if (cmd[0].matches("(max|master)")) {
            player.getSkills().getSkillSet().forEach(skill -> player.getSkills().setLevel(skill, 99));
        } else if (cmd[0].matches("(set)?(lvl|level|stat)")) {
            Preconditions.checkArgument(cmd[0] != null && cmd[1] != null);
            int level = Integer.parseInt(cmd[2]);
            SkillType skillType = null;

            try {
                skillType = SkillType.fromName(cmd[1]);
            } catch (IllegalArgumentException ae) {
                try {
                    skillType = SkillType.fromId(Integer.parseInt(cmd[1]));
                } catch (Exception e) {
                    player.getFrames().sendMessage("Invalid skill chosen.");
                }
            }

            if (level > 99)
                level = 99;

            if (level < 1)
                level = 1;

            player.getSkills().setExperience(skillType, Skills.getExperienceForLevel(level));
            player.getFrames().sendMessage("Set " + skillType.getDisplayName() + " to level: " + level);
        } else if (cmd[0].equals("switch")) {
            int new_book = Integer.valueOf(cmd[1]);
            player.getSettings().setMagicType(new_book == 0 ? 1 : new_book == 1 ? 2 : 3);
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 71 : 92, player.getSettings().getMagicType() == 1 ? 192
                : player.getSettings().getMagicType() == 2 ? 193 : 430);
        } else if (cmd[0].equals("switchp")) {
            player.getSettings().toggleCurses(!player.getSettings().isCursesEnabled());
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 70 : 91, player.getSettings().isCursesEnabled() ? 597 : 271);
        } else if (cmd[0].equals("copy")) {
            String user = command.substring(5).replace("_", " ");
            for (Player players : World.getWorld().getPlayers()) {
                if (players != null) {
                    if (players.getDetails().getName().equalsIgnoreCase(user)) {
                        for (int i = 0; i < Equipment.SIZE; i++)
                            player.getEquipment().set(players.getEquipment().get(i), i, true);
                        for (int i = 0; i < Inventory.SIZE; i++)
                            player.getInventory().set(players.getInventory().get(i), i, true);
                    }
                }
            }
        } else if (cmd[0].equals("size")) {
            int npc = Integer.valueOf(cmd[1]);
            int size = CachedNpcDefinition.getNPCDefinitions(npc).size;
            player.getFrames().sendMessage("Size: " + size);
        } else if (cmd[0].equals("obj")) {
            int id = Integer.valueOf(cmd[1]);
            int type = Integer.valueOf(cmd[2]);
            int f = Integer.valueOf(cmd[3]);
            player.getFrames().createObject(id, player.getPosition(), f, type);
        } else if (cmd[0].equals("interface")) {
            player.getInterfaceSettings().openInterface(Integer.parseInt(cmd[1]));
        } else if (cmd[0].equals("config")) {
            int id = Integer.valueOf(cmd[1]);
            int value = Integer.valueOf(cmd[2]);
            player.getFrames().sendVarp(id, value);
        } else if (cmd[0].equals("gfx")) {
            int gfx = Integer.valueOf(cmd[1]);
            player.playGraphic(Graphic.create(gfx));
        } else if (cmd[0].equals("anim")) {
            int anim = Integer.valueOf(cmd[1]);
            player.playAnimation(Animation.create(anim, AnimationPriority.HIGH));
        } else if (cmd[0].equals("npc")) {
            int id = Integer.valueOf(cmd[1]);
            NPC npc = new NPC(id);
            npc.setPosition(player.getPosition());
            npc.setLastKnownRegion(player.getPosition());
            World.getWorld().addNPC(npc);
        } else if (cmd[0].equals("bank")) {
            player.getVariables().setTransferContainer(new BankTransfer(player));
        } else if (cmd[0].equals("spec")) {
            player.getSettings().resetSpecial();
        } else if (cmd[0].equals("hit")) {
            Hit hit = new Hit(player, Integer.parseInt(cmd[1]));
            player.inflictDamage(hit, false);
        } else if (cmd[0].matches("(emptyi?|clear)")) {
            player.getFrames().sendMessage("Your inventory was cleared!");
            player.getInventory().clear();
        } else if (cmd[0].equals("emptye")) {
            player.getFrames().sendMessage("Your equipment was cleared!");
            player.getEquipment().clear();
            player.getEquipment().refresh();
            player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        } else if (cmd[0].equals("emptyb")) {
            player.getFrames().sendMessage("Your bank was cleared!");
            player.getBank().clear();
            player.getVariables().tab1items = 0;
            player.getVariables().tab2items = 0;
            player.getVariables().tab3items = 0;
            player.getVariables().tab4items = 0;
            player.getVariables().tab5items = 0;
            player.getVariables().tab6items = 0;
            player.getVariables().tab7items = 0;
            player.getVariables().tab8items = 0;
            player.getVariables().tab9items = 0;
            for (int i = 1900; i < 1909; i++) {
                player.getFrames().sendVarp(i, 0);
            }
        } else if (cmd[0].equals("mem")) {
            player.getFrames().sendMessage("[MEMORY] Used: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() / 1048576) + ", Free: " + (Runtime.getRuntime().freeMemory() / 1048576) + ", total: " + (Runtime.getRuntime().totalMemory() / 1048576) + ".");
        } else if (cmd[0].equals("pos")) {
        	player.getFrames().sendMessage(""+player.getPosition().toString());
        } else if (cmd[0].equals("bc")) {
        	player.getControllerManager().startController(new RFDBattles(new NPC(3493)));
        	System.out.println("controller set: " + player.getControllerManager().getController());
        }
    }
}
