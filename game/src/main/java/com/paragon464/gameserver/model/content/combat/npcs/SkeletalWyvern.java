package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.util.NumberUtils;

public class SkeletalWyvern extends NPCAttackLayout {

    @SuppressWarnings("incomplete-switch")
    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        CombatType combatType = getCombatType(npc, mainTarget);
        npc.getCombatState().setCombatType(combatType);
        boolean withinDist = isWithinRadius(npc, mainTarget);
        if (!withinDist)
            return;
        if (!canAttack(npc, mainTarget)) {
            npc.getCombatState().end(1);
            return;
        }
        int combatSpeed = npc.getCombatState().getAttackTimer();
        if (combatSpeed > 0)
            return;
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        switch (combatType) {
            case MAGIC:
                break;
            case MELEE:
                break;
            case RANGED:
                break;
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType combatType = CombatType.MELEE;
        switch (NumberUtils.random(2)) {
            case 0:
                combatType = CombatType.MELEE;
                break;
            case 1:
                combatType = CombatType.MAGIC;
                break;
            case 2:
                combatType = CombatType.RANGED;
                break;
        }
        if (combatType.equals(CombatType.MELEE)) {
            if (!npc.getPosition().isWithinRadius(target, 1)) {
                switch (NumberUtils.random(1)) {
                    case 0:
                        combatType = CombatType.MAGIC;
                        break;
                    case 1:
                        combatType = CombatType.RANGED;
                        break;
                }
            }
        }
        return npc.getCombatState().getCombatType();
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        int dist = !npc.getCombatState().getCombatType().equals(CombatType.MELEE) ? 8 : 1;
        return npc.getCoverage().correctCombatPosition(npc, target, target.getCoverage(), dist,
            npc.getCombatState().getCombatType());
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        int maxHit = 14;
        switch (combatType) {
            case RANGED:
                maxHit = 13;
                break;
            case MAGIC:
                maxHit = 50;
                break;
        }
        final Hit hit = new Hit(npc, (int) (Math.random() * maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, combatType, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (combatType.equals(CombatType.MELEE)) {
                if (player.getPrayers().isPrayingMelee()) {
                    hit.setDamage(0);
                }
            } else if (combatType.equals(CombatType.RANGED)) {
                if (player.getPrayers().isPrayingRange()) {
                    hit.setDamage(0);
                }
            } else if (combatType.equals(CombatType.MAGIC)) {
                if (player.getPrayers().isPrayingMagic()) {
                    hit.setDamage(0);
                }
            }
        }
        return hit;
    }

    @Override
    public void processFollow(NPC npc, Mob target) {
        if (isWithinRadius(npc, target)) {
            return;
        }
        NPCFollowing.executePathFinding(npc, target, true);
    }

    @Override
    public void loadAttack(NPC npc) {
        npc.getCombatState().setCombatType(CombatType.MELEE);
    }
}
