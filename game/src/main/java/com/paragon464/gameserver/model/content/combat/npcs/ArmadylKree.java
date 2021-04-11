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
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;

public class ArmadylKree extends NPCAttackLayout {

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
        int anim = npc.getCombatDefinition().getAttackAnim();
        int projectile = -1;
        int endGfx = -1;
        if (combatType.equals(CombatType.RANGED)) {
            anim = 6976;
            projectile = 1197;
        } else if (combatType.equals(CombatType.MAGIC)) {
            anim = 6976;
            projectile = 1198;
            endGfx = 1196;
        }
        if (combatType.equals(CombatType.MELEE)) {
            if (!canAttack(npc, mainTarget)) {
                return;
            }
            npc.playAnimation(anim, AnimationPriority.HIGH);
            npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
            final Hit hit = getDamage(npc, mainTarget);
            handleInitEffects(npc, mainTarget, hit);
            World.getWorld().submit(new Tickable(0) {
                @Override
                public void execute() {
                    this.stop();
                    mainTarget.inflictDamage(hit, false);
                    handleEndEffects(npc, mainTarget, hit);
                }
            });
        } else {
            for (Mob targets : getPossibleTargets(npc, mainTarget)) {
                if (!canAttack(npc, targets))
                    continue;
                npc.playAnimation(anim, AnimationPriority.HIGH);
                npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
                final Hit hit = getDamage(npc, targets);
                handleInitEffects(npc, targets, hit);
                Projectiles send_projectile = Projectiles.create(npc.getCentreLocation(), targets.getPosition(),
                    targets, projectile, 32, 70, 50, 40, 34);
                npc.executeProjectile(send_projectile);
                final int finalEndGfx = endGfx;
                World.getWorld().submit(new Tickable(combatType.equals(CombatType.MAGIC) ? 2 : 1) {
                    @Override
                    public void execute() {
                        this.stop();
                        if (combatType.equals(CombatType.MAGIC)) {
                            if (hit.getDamage() > 0) {
                                targets.playGraphic(finalEndGfx);
                                targets.inflictDamage(hit, false);
                                handleEndEffects(npc, targets, hit);
                            } else {
                                targets.playGraphic(85, 0, 100);
                            }
                        } else {
                            targets.inflictDamage(hit, false);
                            handleEndEffects(npc, targets, hit);
                        }
                    }
                });
            }
        }
    }

    public static ArrayList<Mob> getPossibleTargets(NPC npc, Mob mainTarget) {
        ArrayList<Mob> possibleTarget = new ArrayList<>();
        for (Player targets : World.getSurroundingPlayers(npc.getPosition())) {
            if (targets == null)
                continue;
            if (targets.getCombatState().isDead())
                continue;
            if (!Areas.atArmadylChamber(targets.getPosition()))
                continue;
            possibleTarget.add(targets);
        }
        return possibleTarget;
    }

    @Override
    public void handleInitEffects(NPC npc, Mob target, Hit hit) {
        CombatEffects.init_effects(npc, target, -1, hit);
    }

    @Override
    public void handleEndEffects(NPC npc, Mob target, Hit hit) {
        // TODO Auto-generated method stub
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.MELEE;
        switch (NumberUtils.random(2)) {
            case 0:
                type = CombatType.RANGED;
                break;
            case 1:
                type = CombatType.MAGIC;
                break;
        }
        if (!npc.getPosition().isWithinRadius(target, 1)) {
            switch (NumberUtils.random(1)) {
                case 0:
                    type = CombatType.RANGED;
                    break;
                case 1:
                    type = CombatType.MAGIC;
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
        int maxHit = npc.getCombatDefinition().getMaxHit();
        CombatType type = npc.getCombatState().getCombatType();
        if (type.equals(CombatType.RANGED)) {
            maxHit = 71;
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
            } else if (type.equals(CombatType.RANGED)) {
                if (player.getPrayers().isPrayingRange()) {
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
        NPCFollowing.executePathFinding(npc, target, true);
    }

    @Override
    public void loadAttack(NPC npc) {
        npc.getCombatState().setCombatType(CombatType.MELEE);
    }
}
