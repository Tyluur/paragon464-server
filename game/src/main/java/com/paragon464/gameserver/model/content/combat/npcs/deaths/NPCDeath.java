package com.paragon464.gameserver.model.content.combat.npcs.deaths;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSpawns;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.Optional;

public class NPCDeath extends Tickable {

    private NPC npc;
    private Mob lastHitter;

    public NPCDeath(final NPC npc, final Mob mob) {
        super(npc.getCombatDefinition().dienTime);
        this.npc = npc;
        this.lastHitter = mob;
    }

    @Override
    public void execute() {
        this.stop();
        npc.getCombatState().getDamageMap().removeInvalidEntries();

        final var killer = Optional.ofNullable(npc.getCombatState().getDamageMap().highestDamage()).orElse(lastHitter);
        if (!MinigameHandler.handleDeath(lastHitter, npc)) {
            npc.dropLoot(killer);
        }
        World.getWorld().unregister(npc);
        final int respawn = npc.getDefinition().getRespawn();
        if (respawn != -1) {
            World.getWorld().submit(new Tickable(respawn) {
                @Override
                public void execute() {
                    this.stop();
                    NPCSpawns.load(npc);
                }
            });
        }
    }
}
