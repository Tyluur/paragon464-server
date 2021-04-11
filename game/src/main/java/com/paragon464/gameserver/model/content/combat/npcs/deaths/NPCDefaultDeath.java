package com.paragon464.gameserver.model.content.combat.npcs.deaths;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.tickable.Tickable;

public class NPCDefaultDeath {

    public static Tickable getTickable(final NPC npc, final Mob lastHitter) {
        if (npc.getDefinition().getName().equalsIgnoreCase("kalphite queen")) {
            return new KQDeath(npc, lastHitter);
        }
        return new NPCDeath(npc, lastHitter);
    }
}
