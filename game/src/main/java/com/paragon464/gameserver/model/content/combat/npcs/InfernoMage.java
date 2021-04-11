package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class InfernoMage extends NPCAttackLayout {

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
        int projectileSpeed = 65;
        Projectiles mageProjectile = Projectiles.create(npc.getPosition(), mainTarget.getPosition(), mainTarget, 94, 45,
            projectileSpeed, 50, 45, 40, 15, 48);
        npc.executeProjectile(mageProjectile);
        npc.playGraphic(93, 0, 100);
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        final Hits.Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
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
    public Hits.Hit getDamage(NPC npc, Mob target) {
        int maxHit = npc.getCombatDefinition().getMaxHit();
        final Hits.Hit hit = new Hits.Hit(npc, NumberUtils.random(maxHit));
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
