package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class Wallasaki extends NPCAttackLayout {

    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        CombatType combatType = npc.getCombatState().getCombatType();
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
        int clientSpeed = 100;
        if (npc.getPosition().isWithinRadius(mainTarget, 1)) {
            clientSpeed = 70;
        } else if (npc.getPosition().isWithinRadius(mainTarget, 5)) {
            clientSpeed = 90;
        } else if (npc.getPosition().isWithinRadius(mainTarget, 8)) {
            clientSpeed = 110;
        } else {
            clientSpeed = 130;
        }
        Projectiles mageProjectile = Projectiles.create(npc.getCentreLocation(), mainTarget.getPosition(), mainTarget,
            162, 20, clientSpeed, 50, 25, 40, 15, 48);
        npc.executeProjectile(mageProjectile);
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        final Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        World.getWorld().submit(new Tickable(2) {
            @Override
            public void execute() {
                this.stop();
                if (hit.getDamage() > 0) {
                    mainTarget.inflictDamage(hit, false);
                    mainTarget.playGraphic(Graphic.create(163, 0, 100));
                    handleEndEffects(npc, mainTarget, hit);
                } else {
                    mainTarget.playGraphic(Graphic.create(85, 0, 100));
                }
            }
        });
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        return CombatType.MAGIC;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        return npc.getPosition().isWithinRadius(target.getPosition(), 6);
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        int maxHit = npc.getCombatDefinition().getMaxHit();
        final Hit hit = new Hit(npc, NumberUtils.random(maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, CombatType.MAGIC, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (player.getPrayers().isPrayingMagic()) {
                hit.setDamage(0);
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
