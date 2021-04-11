package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;

public class CommanderZilyana extends NPCAttackLayout {

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
            npc.getCombatState().end(1);
            return;
        }
        int anim = npc.getCombatDefinition().getAttackAnim();
        int attackTimer = npc.getCombatDefinition().getAttackSpeed();
        if (combatType.equals(CombatType.MAGIC)) {
            anim = 6967;
            attackTimer = 10;
        }
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        npc.getCombatState().setAttackTimer(attackTimer);
        if (combatType.equals(CombatType.MELEE)) {
            if (!canAttack(npc, mainTarget)) {
                return;
            }
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
                final Hit hit = getDamage(npc, targets);
                handleInitEffects(npc, targets, hit);
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        if (hit.getDamage() > 0) {
                            targets.playGraphic(1207, 0, 100);
                            targets.inflictDamage(hit, false);
                            handleEndEffects(npc, targets, hit);
                        } else {
                            targets.playGraphic(85, 0, 100);
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
            if (!Areas.atSaradominChamber(targets.getPosition()))
                continue;
            possibleTarget.add(targets);
        }
        return possibleTarget;
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
        return !TileControl.locationOccupied(npc, target) && npc.getPosition().isWithinRadius(target, distance);
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
            if (combatType.equals(CombatType.MELEE)) {
                if (player.getPrayers().isPrayingMelee()) {
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
        NPCFollowing.executePathFinding(npc, target, true);
    }

    @Override
    public void loadAttack(NPC npc) {
        npc.getCombatState().setCombatType(CombatType.MELEE);
    }
}
