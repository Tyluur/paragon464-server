package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;

public interface NPCAttack {

    void executeAttacks(final NPC npc, final Mob mainTarget);

    void handleInitEffects(NPC npc, Mob target, Hits.Hit hit);

    void handleEndEffects(NPC npc, Mob target, Hits.Hit hit);

    boolean canAttack(NPC npc, Mob target);

    CombatType getCombatType(NPC npc, Mob target);

    boolean isWithinRadius(NPC npc, Mob target);

    Hits.Hit getDamage(NPC npc, Mob target);

    void processFollow(NPC npc, Mob target);

    void loadAttack(NPC npc);
}
