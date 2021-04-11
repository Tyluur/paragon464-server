package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;

public class NPCAttackLayout implements NPCAttack {

    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleInitEffects(NPC npc, Mob target, Hit hit) {
        CombatEffects.init_effects(npc, target, -1, hit);
    }

    @Override
    public void handleEndEffects(NPC npc, Mob target, Hit hit) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canAttack(NPC npc, Mob target) {
        if (npc.getPosition().getZ() != target.getPosition().getZ()) {
            npc.getCombatState().end(1);
            return false;
        }
        if (npc.getCombatState().isDead() || target.getCombatState().isDead()) {
            npc.getCombatState().end(1);
            npc.getCombatState().setOutOfCombat();
            return false;
        }
        boolean projectileBlocked = !ProjectilePathFinder.hasLineOfSight(npc, target);
        if (projectileBlocked) {
            return false;
        }
        if (!npc.getAttributes().isSet("multi") || !target.getAttributes().isSet("multi")) {
            int attackersLastAttackedInSeconds = npc.getCombatState().previouslyAttackedInSecs();
            Mob attackersLastAttacker = npc.getCombatState().getLastAttackedBy();
            if (attackersLastAttackedInSeconds > 6) {
                if (attackersLastAttacker != null) {
                    if (attackersLastAttacker != target) {
                        npc.getCombatState().end(2);
                        return false;
                    }
                }
            }
            int targetsLastAttackedInSeconds = target.getCombatState().previouslyAttackedInSecs();
            Mob targetsLastAttacker = target.getCombatState().getLastAttackedBy();
            if (targetsLastAttacker != null) {
                if (targetsLastAttacker != npc) {
                    if (targetsLastAttackedInSeconds > 6) {
                        npc.getCombatState().end(2);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        return null;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        return false;
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        return null;
    }

    @Override
    public void processFollow(NPC npc, Mob target) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadAttack(NPC npc) {
        // TODO Auto-generated method stub

    }
}
