package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class SpiritualRanger extends NPCAttackLayout {

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
        int projectile = 1188;
        int startZ = 45;
        int startSpeed = 45;
        int airSpeed = 55;
        int endZ = 40;
        switch (npc.getId()) {
            case 6230:
                projectile = 1190;
                startZ = 75;
                break;
            case 6276:
                startZ = 25;
                endZ = 25;
                projectile = 1195;
                airSpeed = 70;
                startSpeed = 25;
                break;
        }
        Projectiles rangeProjectile = Projectiles.create(npc.getPosition(), mainTarget.getPosition(), mainTarget,
            projectile, startSpeed, airSpeed, 50, startZ, endZ, 15, 48);
        npc.executeProjectile(rangeProjectile);
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        final Hits.Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        World.getWorld().submit(new Tickable(1) {
            @Override
            public void execute() {
                this.stop();
                mainTarget.inflictDamage(hit, false);
                handleEndEffects(npc, mainTarget, hit);
            }
        });
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        return CombatType.RANGED;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        return npc.getPosition().isWithinRadius(target.getPosition(), 7);
    }

    @Override
    public Hits.Hit getDamage(NPC npc, Mob target) {
        int maxHit = npc.getCombatDefinition().getMaxHit();
        final Hits.Hit hit = new Hits.Hit(npc, NumberUtils.random(maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, CombatType.RANGED, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (player.getPrayers().isPrayingRange()) {
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
        npc.getCombatState().setCombatType(CombatType.RANGED);
    }
}
