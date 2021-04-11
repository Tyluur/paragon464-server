package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class Dragons extends NPCAttackLayout {

    @SuppressWarnings("incomplete-switch")
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
        final Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        switch (combatType) {
            case FIRE_BREATH:
                anim = 81;
                npc.playGraphic(1, 0, 100);
                World.getWorld().submit(new Tickable(0) {
                    @Override
                    public void execute() {
                        this.stop();
                        mainTarget.inflictDamage(hit, false);
                        handleEndEffects(npc, mainTarget, hit);
                    }
                });
                break;
            case MELEE:
                World.getWorld().submit(new Tickable(0) {
                    @Override
                    public void execute() {
                        this.stop();
                        mainTarget.inflictDamage(hit, false);
                        handleEndEffects(npc, mainTarget, hit);
                    }
                });
                break;
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.MELEE;
        switch (NumberUtils.random(2)) {
            case 1:
                type = CombatType.FIRE_BREATH;
                break;
        }
        return type;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        int distance = 1;
        return !TileControl.locationOccupied(npc, target) && npc.getPosition().isWithinRadius(target.getPosition(), distance);
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        if (type.equals(CombatType.FIRE_BREATH)) {
            maxHit = 57;
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
            } else if (type.equals(CombatType.FIRE_BREATH)) {
                double percentage = 0.0;
                boolean praying = player.getPrayers().isPrayingMagic();
                if (player.getEquipment().hasItem(11283) || player.getEquipment().hasItem(1540)) {
                    percentage = 0.8;
                    // player.getFrames().sendMessage("Your shield absorbs most
                    // of the dragon fire!");
                } else if (praying) {
                    percentage = 0.6;
                    // player.getFrames().sendMessage("Your prayers resist some
                    // of the dragon fire.");
                }
                if (percentage > 0.0) {
                    hit.deductDamage((int) (hit.getDamage() * percentage));
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
