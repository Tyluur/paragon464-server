package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class ChaosElemental extends NPCAttackLayout {

    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        CombatType combatType = getCombatType(npc, mainTarget);
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
        int projectile = -1;
        final Hit hit = getDamage(npc, mainTarget);
        if (combatType.equals(CombatType.SPECIAL)) {
            int rand = NumberUtils.random(1);
            switch (rand) {
                case 0:
                    projectile = 551;
                    break;
                case 1:
                    projectile = 554;
                    break;
            }
        } else if (combatType.equals(CombatType.MELEE)) {
            projectile = 557;
        }
        if (projectile != -1) {
            Projectiles magicProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                mainTarget, projectile, 10, 70, 50, 43, 35);
            npc.executeProjectile(magicProj);
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
        handleInitEffects(npc, mainTarget, hit);
        final int proj = projectile;
        World.getWorld().submit(new Tickable(2) {
            @Override
            public void execute() {
                this.stop();
                if (combatType.equals(CombatType.MELEE)) {
                    mainTarget.inflictDamage(hit, false);
                }
                handleEndEffects(npc, mainTarget, hit);
                attackEffects(npc, mainTarget, hit, proj);
            }
        });
    }

    private void attackEffects(NPC npc, final Mob target, Hit hit, int projectile) {
        if (projectile != -1) {
            if (target.isPlayer()) {
                Player player = (Player) target;
                switch (projectile) {
                    case 551://disarming
                        int[][] slots = {{0, 1, 2}, {3, 4, 5, 7}, {9, 10, 12}};
                        for (int i = 0; i < 3; i++) {
                            int slot = slots[i][NumberUtils.random(slots[i].length - 1)];
                            Equipment.unequipItem(player, -1, slot, false);
                            player.playGraphic(550);
                        }
                        break;
                    case 554://teleporting
                        boolean negative = NumberUtils.random(2) == 1;
                        Position loc = player.getPosition().transform((negative ? NumberUtils.random(-10, 10) : NumberUtils.random(0, 10)), (negative ? NumberUtils.random(-10, 10) : NumberUtils.random(0, 10)), 0);
                        while (!ProjectilePathFinder.hasLineOfSight(player.getLastRegion(), player.getPosition(), loc, false)) {
                            loc = player.getPosition().transform((negative ? NumberUtils.random(-10, 10) : NumberUtils.random(0, 10)), (negative ? NumberUtils.random(-10, 10) : NumberUtils.random(0, 10)), 0);
                        }
                        player.teleport(loc);
                        break;
                }
            }
        }
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType combatType = CombatType.MELEE;
        switch (NumberUtils.random(6)) {
            case 0:
                combatType = CombatType.SPECIAL;
                break;
        }
        npc.getCombatState().setCombatType(combatType);
        return npc.getCombatState().getCombatType();
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        return !TileControl.locationOccupied(npc, target) && npc.getPosition().isWithinRadius(target, 8);
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        int maxHit = 28;
        final Hit hit = new Hit(npc, (int) (Math.random() * maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, combatType, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (combatType.equals(CombatType.MELEE)) {
                int random = NumberUtils.random(2);
                switch (random) {
                    case 0:
                        if (player.getPrayers().isPrayingMelee()) {
                            hit.setDamage(0);
                        }
                        break;
                    case 1:
                        if (player.getPrayers().isPrayingRange()) {
                            hit.setDamage(0);
                        }
                        break;
                    case 2:
                        if (player.getPrayers().isPrayingMagic()) {
                            hit.setDamage(0);
                        }
                        break;
                }
            }
        }
        return hit;
    }

    @Override
    public void processFollow(NPC npc, Mob other) {
        if (isWithinRadius(npc, other)) {
            return;
        }
        NPCFollowing.executePathFinding(npc, other, true);
    }

    @Override
    public void loadAttack(NPC npc) {
        npc.getCombatState().setCombatType(CombatType.MELEE);
    }
}
