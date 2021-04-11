package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class WaterFiend extends NPCAttackLayout {

    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        CombatType combatType = getCombatType(npc, mainTarget);
        npc.getCombatState().setCombatType(combatType);
        npc.setInteractingMob(mainTarget);
        if (!isWithinRadius(npc, mainTarget)) {
            return;
        } else {
            npc.getWalkingQueue().reset();
        }
        if (npc.getCombatState().getAttackTimer() > 0) {
            return;
        }
        if (!canAttack(npc, mainTarget)) {
            return;
        }
        int anim = npc.getCombatDefinition().getAttackAnim();
        int projectileId = -1;
        int start_speed = 45;
        int air_speed = 85;
        int end_height = 30;
        if (combatType.equals(CombatType.RANGED)) {
            air_speed = 65;
            start_speed = 35;
            projectileId = 16;
            anim = 299;
        } else if (combatType.equals(CombatType.MAGIC)) {
            projectileId = 136;
            anim = 299;
        }
        if (projectileId != -1) {
            Projectiles projectile = Projectiles.create(npc.getCentreLocation(), mainTarget.getPosition(), mainTarget,
                projectileId, start_speed, air_speed, 50, 32, end_height);
            npc.executeProjectile(projectile);
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        final Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        World.getWorld().submit(
            new Tickable(combatType.equals(CombatType.RANGED) ? 1 : combatType.equals(CombatType.MAGIC) ? 2 : 0) {
                @Override
                public void execute() {
                    this.stop();
                    if (combatType.equals(CombatType.MAGIC)) {
                        if (hit.getDamage() > 0) {
                            mainTarget.inflictDamage(hit, false);
                            handleEndEffects(npc, mainTarget, hit);
                        } else {
                            mainTarget.playGraphic(Graphic.create(85, 0, 100));
                        }
                    } else {
                        mainTarget.inflictDamage(hit, false);
                        handleEndEffects(npc, mainTarget, hit);
                    }
                }
            });
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.MELEE;
        if (!npc.getPosition().isWithinRadius(target, 1)) {
            switch (NumberUtils.random(1)) {
                case 0:
                    type = CombatType.MAGIC;
                    break;
                case 1:
                    type = CombatType.RANGED;
                    break;
            }
        } else {
            switch (NumberUtils.random(2)) {
                case 0:
                    type = CombatType.MAGIC;
                    break;
                case 1:
                    type = CombatType.RANGED;
                    break;
                case 2:
                    type = CombatType.MELEE;
                    break;
            }
        }
        return type;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        int distance = 1;
        if (!(npc.getCombatState().getCombatType().equals(CombatType.MELEE))) {
            distance = 8;
        }
        return !TileControl.locationOccupied(npc, target) && npc.getPosition().isWithinRadius(target, distance);
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        if (!npc.getCombatState().getCombatType().equals(CombatType.MELEE)) {
            maxHit = 8;
        }
        final Hit hit = new Hit(npc, NumberUtils.random(maxHit));
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
            } else if (type.equals(CombatType.RANGED)) {
                if (player.getPrayers().isPrayingRange()) {
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
        npc.getCombatState().setCombatType(CombatType.MAGIC);
    }
}
