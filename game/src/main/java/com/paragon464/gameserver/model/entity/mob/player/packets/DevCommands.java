package com.paragon464.gameserver.model.entity.mob.player.packets;

import com.paragon464.gameserver.api.Api;
import com.paragon464.gameserver.cache.definitions.CachedNpcDefinition;
import com.paragon464.gameserver.cache.definitions.CachedObjectDefinition;
import com.paragon464.gameserver.io.database.table.definition.ApiKeyTable;
import com.paragon464.gameserver.io.database.table.definition.map.DoorTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.npc.drops.DropItem;
import com.paragon464.gameserver.model.entity.mob.npc.drops.NPCDrop;
import com.paragon464.gameserver.model.entity.mob.npc.drops.NPCDrops;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.godwars.GodWars;
import com.paragon464.gameserver.model.gameobjects.DoorManager;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.util.NumberUtils;
import com.paragon464.gameserver.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevCommands.class);

    public static void execute(Player player, String command) {
        String[] cmd = command.split(" ");
        if (cmd[0].equals("test")) {
            player.getAttributes().set("test_kills", 0);
            player.getAttributes().set("item_count", 0);
            int npc = 1615;
            int itemId = 4151;
            int kills = 1000;//default
            NPCDrop drops = NPCDrops.definitions.get(npc);
            if (drops == null)
                return;
            for (int i = 0; i < kills; i++) {
                double roll = NumberUtils.getRandomDouble(100);
                LOGGER.debug("ROLLED: {}", roll);
                player.getAttributes().addInt("test_kills", 1);
                for (DropItem item : drops.getUnique()) {
                    if (item.getChance() == 100) continue;
                    if (item.getId() == itemId) {
                        boolean accessed = (roll <= (item.getChance() * 1.5));
                        if (accessed) {
                            player.getAttributes().addInt("item_count", 1);
                            LOGGER.trace("{} was dropped after {} kill(s).", ItemDefinition.forId(itemId).getName(), player.getAttributes().getInt("test_kills"));
                        }
                    }
                }
            }
            LOGGER.debug("{} was dropped {} time(s) out of {} kill(s).", ItemDefinition.forId(itemId).getName(), player.getAttributes().getInt("item_count"), player.getAttributes().getInt("test_kills"));
        }
        if (cmd[0].equals("minimapobjsprite")) {
            for (int i = 0; i < 20000; i++) {
                CachedObjectDefinition def = CachedObjectDefinition.forId(i);
                if (def == null)
                    continue;
                //main sprite = 318
                //components below
                //0 = gen store icon
                //5 = bank
                //6 = quest
                //12 = dungeon
                if (def.groundDecorationSprite == 12) {
                    LOGGER.debug("{}:{}", i, def.groundDecorationSprite);
                }
            }
        }
        if (cmd[0].equals("gwd")) {
            player.teleport(3233, 3939, 0);
        }
        if (cmd[0].equals("armadyl")) {
            player.teleport(2872, 5268, 2);
        }
        if (cmd[0].equals("pnpc")) {
            int npc = Integer.valueOf(cmd[1]);
            if (npc == -1) {
                player.getAppearance().setNpcId(npc);
                player.getVariables().setStandAnimation(0);
                player.getVariables().setWalkAnimation(0);
                player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                return;
            }
            CachedNpcDefinition def = CachedNpcDefinition.getNPCDefinitions(npc);
            if (def == null) {
                return;
            }
            player.getAppearance().setNpcId(npc);
            int stand = def.idleAnimation;
            int walk = def.walkAnimation;
            LOGGER.debug("walk[{}], stand[{}]", walk, stand);
            player.getVariables().setStandAnimation(stand);
            player.getVariables().setWalkAnimation(walk);
            player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        }
        if (cmd[0].equals("up")) {
            player.teleport(new Position(player.getPosition().getX(), player.getPosition().getY(),
                player.getPosition().getZ() + 1));
        }
        if (cmd[0].equals("down")) {
            player.teleport(new Position(player.getPosition().getX(), player.getPosition().getY(),
                player.getPosition().getZ() - 1));
        }
        if (cmd[0].equals("kc")) {
            player.getAttributes().addInt("armadyl_kc", 500);
            player.getAttributes().addInt("bandos_kc", 500);
            player.getAttributes().addInt("saradomin_kc", 500);
            player.getAttributes().addInt("zamorak_kc", 500);
            GodWars.display(player);
        }
        if (cmd[0].equals("objloop")) {
            for (GameObject obj : World.getRegionalObjects(player.getPosition())) {
                if (obj.getId() == 2759) {
                    LOGGER.trace(obj.toString());
                }
            }
            for (int i = 0; i < 60000; i++) {
                CachedObjectDefinition def = CachedObjectDefinition.forId(i);
                if (def != null) {
                    if (def.groundDecorationSprite == -1)
                        continue;
                    if (def.groundDecorationSprite != 5)
                        continue;
                    player.getFrames().sendMessage("sprite id: " + i + ", ground sprite:" + def.groundDecorationSprite);
                }
            }
        }
        if (cmd[0].equals("yo")) {
            //player.getAttributes().set("nightmare_zone", new NightMareZone(player));
            /*SizedPosition baseLocation = null;
            Position north_east = new Position(2288, 4714, 0);
            Position south_west = new Position(2252, 4678, 0);
            final int width = (north_east.getX() - south_west.getX()) / 6;
            final int height = (north_east.getY() - south_west.getY()) / 6;
            int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                    width, height);
            baseLocation = SizedPosition.create(newCoords[0] << 3, newCoords[1] << 3, 0);
            baseLocation.setWidth(width);
            baseLocation.setHeight(height);
            Position tele = new Position(baseLocation.getX() + 28, baseLocation.getY() + 10, 0);
            LOGGER.debug(tele.toString());
            player.teleport(tele);*/
        }
        if (cmd[0].equals("reloadkeys")) {
            Api.API_KEYS.clear();
            ApiKeyTable.load();
        }
        if (cmd[0].equals("objpos")) {
            List<Integer> ids = new ArrayList<>();
            String modelIds = null;
            for (Region region : player.getMapRegions()) {
                if (region == null)
                    continue;
                region.checkLoadMap();
                if (region.getAllObjects() == null)
                    continue;
                for (GameObject obj : region.getAllObjects()) {
                    if (obj != null && obj.getPosition().equals(player.getPosition())) {
                        player.getFrames().sendMessage(obj.toString());
                        player.getFrames().sendMessage("" + World.getMask(obj.getPosition()));
                    }
                }
            }
            modelIds = TextUtils.implode(ids, ",");
            player.getFrames().sendMessage("models: " + modelIds);
        }
        if (cmd[0].equals("customobj")) {
            int id = Integer.valueOf(cmd[1]);
            int x = Integer.valueOf(cmd[2]);
            int y = Integer.valueOf(cmd[3]);
            int z = player.getPosition().getZ();
            int dir = Integer.valueOf(cmd[4]);
            int type = Integer.valueOf(cmd[5]);
            boolean del = Integer.valueOf(cmd[6]) == 0;
            Position loc = new Position(x, y, z);
            int region = player.getPosition().getRegionId();
        }
        if (cmd[0].equals("vrunes")) {
            player.getInventory().addItem(new Item(9075, 500));
            player.getInventory().addItem(new Item(560, 500));
            player.getInventory().addItem(new Item(557, 500));
            player.getInventory().refresh();
        }
        if (cmd[0].equals("brunes")) {
            player.getInventory().addItem(new Item(565, 1000));
            player.getInventory().addItem(new Item(560, 1000));
            player.getInventory().addItem(new Item(555, 1000));
            player.getInventory().refresh();
        }
        if (cmd[0].equals("hybrid")) {
            player.getInventory().addItem(new Item(4708, 1));
            player.getInventory().addItem(new Item(6585, 1));
            player.getInventory().addItem(new Item(6914, 1));
            player.getInventory().addItem(new Item(6889, 1));
            player.getInventory().addItem(new Item(6920, 1));
            player.getInventory().addItem(new Item(7462, 1));
            player.getInventory().addItem(new Item(2412, 1));
            player.getInventory().addItem(new Item(4714, 1));
            player.getInventory().addItem(new Item(4712, 1));
            player.getInventory().refresh();
        }
        if (cmd[0].equals("rldoors")) {
            DoorManager.doors.clear();
            DoorTable.load();
        }
        if (cmd[0].matches("set(int(eger)?|bool(ean)?|str(ing)?)")) {
            try {
                final String targetName = Arrays.toString(Arrays.copyOfRange(cmd, 3, cmd.length)).replaceAll("([\"'\\[\\]]|,(?=\\s))", "");
                final Player target = cmd.length >= 4 && World.getWorld().getPlayerByName(targetName) != null
                    ? World.getWorld().getPlayerByName(targetName) : player;
                final String key = cmd[1];
                final String value = cmd[2];

                if (cmd[0].contains("str")) {
                    target.getAttributes().set(key, value);
                } else if (cmd[0].contains("int")) {
                    target.getAttributes().set(key, Integer.parseInt(value));
                } else {
                    target.getAttributes().set(key, value.matches("([Tt]rue|1)"));
                }
                player.getFrames().sendMessage("[<col=432891>Info</col>] <img=1>Server: <col=6d1509>Attribute: \"" + key + "\" set to \"" + value + "\"" +
                    (targetName.equalsIgnoreCase(player.getDetails().getName()) ? "." : " for player \"" + targetName + "\".</col>"));
            } catch (Exception e) {
                player.getFrames().sendMessage("[<col=432891>Help</col>] <img=1>Server: <col=6d1509>Syntax: ::" + cmd[0] + " key value \"player username\".</col>");
                player.getFrames().sendMessage("[<col=432891>Help</col>] <img=1>Server: <col=6d1509>If no player is specified, then the you are the target.</col>");
            }
        }
        if (cmd[0].matches("(re?m(ove)?|del(elete)?)attr(ibute)?")) {
            final String targetName = Arrays.toString(Arrays.copyOfRange(cmd, 2, cmd.length)).replaceAll("([\"'\\[\\]]|,(?=\\s))", "");
            final Player target = cmd.length >= 3 && World.getWorld().getPlayerByName(targetName) != null
                ? World.getWorld().getPlayerByName(targetName) : player;
            final String key = cmd[1];

            target.getAttributes().remove(key);
        }
    }
}
