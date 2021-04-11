package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.util.NumberUtils;

public class PestZombie extends NPCAttackLayout {

    @Override
    public void executeAttacks(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        if (!isWithinRadius(npc, target)) {
            return;
        } else {
            npc.getWalkingQueue().reset();
        }
        if (npc.getCombatState().getAttackTimer() > 0) {
            return;
        }
        if (!canAttack(npc, target)) {
            return;
        }
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        int projectile = -1;
        if (combatType.equals(CombatType.MAGIC)) {
            projectile = 378;
            Projectiles magicProj = Projectiles.create(npc.getCentreLocation(), target.getCentreLocation(), target,
                projectile, 35, 65, 50, 43, 35);
            npc.executeProjectile(magicProj);
        }
        target.getCombatState().refreshLastAttacked();
        npc.getCombatState().refreshLastHit();
        final Hit hit = getDamage(npc, target);
        target.inflictDamage(hit, false);
        handleInitEffects(npc, target, hit);
    }

    @Override
    public void handleEndEffects(NPC npc, Mob target, Hit hit) {
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        /*switch (NumberUtils.random(1)) {
        case 0:
            combatType = CombatType.MELEE;
            break;
        case 1:
            combatType = CombatType.MAGIC;
            break;
        }
        if (!npc.getPosition().isWithinRadius(target, 1)) {
            combatType = CombatType.MAGIC;
        }*/
        return CombatType.MELEE;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int dist = 8;
        if (type.equals(CombatType.MELEE)) {
            dist = 1;
        }
        return !TileControl.locationOccupied(npc, target) && npc.getPosition().isWithinRadius(target, dist);
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        final Hit hit = new Hit(npc, NumberUtils.random(npc.getCombatDefinition().getMaxHit()));
        boolean accurate = Formulas.isAccurate(npc, target, type, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (type.equals(CombatType.MELEE)) {
                if (player.getPrayers().isPrayingMelee()) {
                    hit.setDamage(0);
                }
            } else if (type.equals(CombatType.MAGIC)) {
                if (player.getPrayers().isPrayingMagic()) {
                    hit.setDamage(0);
                }
            }
        }
        return hit;
    }

    @Override
    public void processFollow(NPC npc, Mob other) {
        NPCFollowing.executePathFinding(npc, other, true);
    }

    @Override
    public void loadAttack(NPC npc) {
        if (NumberUtils.random(2) == 1) {
            npc.getCombatState().setCombatType(CombatType.MAGIC);
        } else {
            npc.getCombatState().setCombatType(CombatType.MELEE);
        }
    }
}
