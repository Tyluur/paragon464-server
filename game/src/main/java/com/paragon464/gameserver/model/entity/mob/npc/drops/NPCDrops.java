package com.paragon464.gameserver.model.entity.mob.npc.drops;

import com.paragon464.gameserver.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class NPCDrops {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPCDrop.class);

    public static HashMap<Integer, NPCDrop> definitions = new HashMap<>();
    public static HashMap<String, Set<DropItem>> generics = new HashMap<>();

    public static NPCDrop getDrops(int npcId) {
        return definitions.get(npcId);
    }

    public static HashMap<Integer, NPCDrop> getDropMap() {
        return definitions;
    }

    public static List<DropItem> getCertainDrops(int npc) {
        NPCDrop drop = definitions.get(npc);
        if (drop == null)
            return null;
        List<DropItem> drops = new ArrayList<>();
        for (DropItem items : drop.getUnique()) {
            if (items.getChance() == 100) {
                drops.add(items);
            }
        }
        return drops;
    }

    public static List<DropItem> getGenericDrops(int npc) {
        NPCDrop drop = definitions.get(npc);
        if (drop == null)
            return null;
        List<DropItem> drops = new ArrayList<>();
        double table_roll = NumberUtils.getRandomDouble(100);
        for (Entry<String, Double> genericTables : drop.getGeneric().entrySet()) {
            String tableName = genericTables.getKey();
            double chance = genericTables.getValue();
            if ((table_roll + 1) <= chance) {
                for (DropItem i : generics.get(tableName)) {
                    double item_roll = NumberUtils.getRandomDouble(100);
                    if (item_roll <= i.getChance()) {
                        LOGGER.debug("DROP TABLE[{}, {}], item_roll[{}, {}], accessed: {}", tableName, table_roll, item_roll, i.getChance(), item_roll <= i.getChance());
                        drops.add(i);
                    }
                }
            }
        }
        return drops;
    }

    public static List<DropItem> getRandomizedUniqueDrops(int npc) {
        NPCDrop drop = definitions.get(npc);
        if (drop == null)
            return null;
        return getDrops(drop.getUnique());
    }

    private static List<DropItem> getDrops(List<DropItem> items) {
        if (items == null) {
            return null;
        }
        List<DropItem> drops = new ArrayList<>();
        double roll = NumberUtils.getRandomDouble(100);
        for (DropItem item : items) {
            if (item.getChance() == 100) continue;
            if (roll <= (item.getChance() * 1.5)) {
                drops.add(item);
            }
        }
        Collections.shuffle(drops);
        return drops;
    }

    public static List<DropItem> getRandomizedRareDrops(int npc) {
        NPCDrop drop = definitions.get(npc);
        if (drop == null)
            return null;
        List<DropItem> items = new ArrayList<>();
        double roll = NumberUtils.getRandomDouble(100);
        for (DropItem item : drop.getUnique()) {
            if (item.isRare()) {
                if (roll <= (item.getChance() * 1.5)) {
                    items.add(item);
                }
            }
        }
        Collections.shuffle(items);
        return items;
    }
}
