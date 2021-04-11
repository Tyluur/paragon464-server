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
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class JungleDemon extends NPCAttackLayout {

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
        npc.getCombatState().setAttackTimer(4);
        int anim = npc.getCombatDefinition().getAttackAnim();
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        int endGfx = -1;
        if (combatType.equals(CombatType.MAGIC)) {
            int gfx = 158;//air
            int projectile = 159;
            endGfx = 160;
            switch (NumberUtils.random(3)) {
                case 0://water
                    gfx = 161;
                    projectile = 162;
                    endGfx = 163;
                    break;
                case 1://earth
                    gfx = 164;
                    projectile = 165;
                    endGfx = 166;
                    break;
                case 2://fire
                    gfx = 155;
                    projectile = 156;
                    endGfx = 157;
                    break;
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
            npc.playGraphic(gfx, 10, 100);
            Projectiles mageProjectile = Projectiles.create(npc.getCentreLocation(), mainTarget.getPosition(), mainTarget,
                projectile, 20, clientSpeed, 50, 45, 40, 15, 48);
            npc.executeProjectile(mageProjectile);
        }
        final Hits.Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        final int finalEndGfx = endGfx;
        World.getWorld().submit(new Tickable(combatType.equals(CombatType.MELEE) ? 0 : 2) {
            @Override
            public void execute() {
                this.stop();
                if (hit.getDamage() > 0) {
                    mainTarget.inflictDamage(hit, false);
                    mainTarget.playGraphic(Graphic.create(finalEndGfx, 0, 100));
                    handleEndEffects(npc, mainTarget, hit);
                } else {
                    mainTarget.playGraphic(Graphic.create(85, 0, 100));
                }
            }
        });
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.MELEE;
        switch (NumberUtils.random(1)) {
            case 0:
                type = CombatType.MAGIC;
                break;
        }
        if (!npc.getPosition().isWithinRadius(target, 1)) {
            type = CombatType.MAGIC;
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
    public Hits.Hit getDamage(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        final Hits.Hit hit = new Hits.Hit(npc, NumberUtils.random(maxHit));
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
