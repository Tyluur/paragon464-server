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

public class Kolodions extends NPCAttackLayout {

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
        final int startGfx = npc.getId() == 907 ? 93 : -1;
        final int projectileId = npc.getId() == 907 ? 94 : -1;
        final int endGfx = npc.getId() == 907 ? 95 : NumberUtils.random(76, 78);
        if (projectileId != -1) {
            Projectiles mageProjectile = Projectiles.create(npc.getPosition(), mainTarget.getPosition(), mainTarget,
                projectileId, 50, clientSpeed, 50, 45, 40, 15, 48);
            npc.executeProjectile(mageProjectile);
        }
        if (startGfx != -1) {
            npc.playGraphic(startGfx, 0, 100);
        }
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        npc.playAnimation(anim, AnimationPriority.HIGH);
        final Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        World.getWorld().submit(new Tickable(npc.getId() == 907 ? 3 : 2) {
            @Override
            public void execute() {
                this.stop();
                if (hit.getDamage() > 0) {
                    mainTarget.inflictDamage(hit, false);
                    mainTarget.playGraphic(Graphic.create(endGfx, 0, 100));
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
        return npc.getPosition().isWithinRadius(target.getPosition(), 8);
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
