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
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class Spinolayp extends NPCAttackLayout {

    @Override
    @SuppressWarnings("incomplete-switch")
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
        int projectile = -1;
        int projectileStartSpeed = 20;
        int projectileSpeed = 65;
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        npc.playAnimation(2868, AnimationPriority.HIGH);
        final Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        switch (combatType) {
            case MAGIC:
                projectile = 94;
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        if (hit.getDamage() > 0) {
                            mainTarget.inflictDamage(hit, false);
                            mainTarget.playGraphic(Graphic.create(95, 0, 100));
                            handleEndEffects(npc, mainTarget, hit);
                        } else {
                            mainTarget.playGraphic(Graphic.create(85, 0, 100));
                        }
                    }
                });
                break;
            case RANGED:
                projectile = 295;
                projectileSpeed = 40;
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        mainTarget.inflictDamage(hit, false);
                        handleEndEffects(npc, mainTarget, hit);
                    }
                });
                break;
        }
        if (projectile != -1) {
            Projectiles sendProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                mainTarget, projectile, projectileStartSpeed, projectileSpeed, 50, 20, 35);
            npc.executeProjectile(sendProj);
        }
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.RANGED;
        switch (NumberUtils.random(5)) {
            case 0:
                type = CombatType.MAGIC;
                break;
        }
        return type;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        return npc.getPosition().isWithinRadius(target.getPosition(), 7);
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        final Hit hit = new Hit(npc, NumberUtils.random(maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, combatType, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (combatType.equals(CombatType.RANGED)) {
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
        npc.getCombatState().setCombatType(CombatType.RANGED);
    }
}
