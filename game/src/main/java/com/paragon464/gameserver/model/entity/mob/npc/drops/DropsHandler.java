package com.paragon464.gameserver.model.entity.mob.npc.drops;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.List;

public class DropsHandler {

    public static void handle(NPC npc, Player player) {
        List<DropItem> certainDrops = NPCDrops.getCertainDrops(npc.getId());//100% drops
        if (certainDrops != null && certainDrops.size() > 0) {
            for (DropItem cDrop : certainDrops) {
                DropItem dropping = cDrop;
                if (dropping.getDefinition().isStackable()) {
                    dropping = dropping.withMax(dropping.getMin() + NumberUtils.random(dropping.getExtraAmount()));
                }
                GroundItemManager.registerGroundItem(new GroundItem(dropping, player, npc.getPosition()));
            }
        }
        List<DropItem> potentialDrops = NPCDrops.getGenericDrops(npc.getId());//generic tables drops
        if (potentialDrops != null && potentialDrops.size() > 0) {
            DropItem picked_drop = potentialDrops.get(NumberUtils.random(potentialDrops.size() - 1));
            DropItem dropping = picked_drop;
            if (picked_drop.getDefinition().isStackable()) {
                dropping = dropping.withMax(dropping.getMin() + NumberUtils.random(dropping.getExtraAmount()));
            }
            GroundItemManager.registerGroundItem(new GroundItem(dropping, player, npc.getPosition()));
        } else {//no potential drops were there
            potentialDrops = NPCDrops.getRandomizedUniqueDrops(npc.getId());//monster drops
            if (potentialDrops != null && potentialDrops.size() > 0) {
                DropItem picked_drop = potentialDrops.get(NumberUtils.random(potentialDrops.size() - 1));
                DropItem dropping = picked_drop;
                if (picked_drop.getDefinition().isStackable()) {
                    dropping = dropping.withMax(dropping.getMin() + NumberUtils.random(dropping.getExtraAmount()));
                }
                GroundItemManager.registerGroundItem(new GroundItem(dropping, player, npc.getPosition()));
            }
            //XXX: rare drops are handled seperately.
            potentialDrops = NPCDrops.getRandomizedRareDrops(npc.getId());
            if (potentialDrops != null && potentialDrops.size() > 0) {
                DropItem picked_drop = potentialDrops.get(NumberUtils.random(potentialDrops.size() - 1));
                DropItem dropping = picked_drop;
                if (picked_drop.getDefinition().isStackable()) {
                    dropping = dropping.withMax(dropping.getMin() + NumberUtils.random(dropping.getExtraAmount()));
                }
                GroundItemManager.registerGroundItem(new GroundItem(dropping, player, npc.getPosition()));
            }
        }
    }
}
