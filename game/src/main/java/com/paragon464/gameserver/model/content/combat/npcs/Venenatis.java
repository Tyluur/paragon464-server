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
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;

public class Venenatis extends NPCAttackLayout {

    private int attackStyle = 0;
    //2 = web attack
    //3 = prayer drain

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
        final Hit hit = getDamage(npc, mainTarget);
        Projectiles proj = null;
        if (combatType.equals(CombatType.SPECIAL)) {
            switch (attackStyle) {
                case 2://web attack
                    anim = 105327;
                    proj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                        mainTarget, 101254, 10, 70, 50, 40, 25);
                    break;
                case 3://prayer drain
                    npc.playGraphic(102);
                    proj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                        mainTarget, 103, 10, 70, 50, 40, 25);
                    break;
            }
        } else if (combatType.equals(CombatType.MAGIC)) {
            proj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                mainTarget, 165, 10, 70, 50, 40, 25);
        }
        if (proj != null) {
            npc.executeProjectile(proj);
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
        handleInitEffects(npc, mainTarget, hit);
        final Projectiles projectile = proj;
        World.getWorld().submit(new Tickable(1) {
            @Override
            public void execute() {
                this.stop();
                //mainTarget.inflictDamage(hit, false);
                handleEndEffects(npc, mainTarget, hit);
                attackEffects(npc, mainTarget, hit, projectile);
            }
        });
    }

    private void attackEffects(NPC npc, final Mob target, Hit hit, Projectiles proj) {
        if (target.isPlayer()) {
            Player player = (Player) target;
            switch (attackStyle) {
                case 3:
                    player.getSettings().decreasePrayerPoints(0.30);
                    player.getFrames().sendMessage("Your prayer was drained!");
                    player.playGraphic(170);
                    break;
            }
        }
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType combatType = CombatType.SPECIAL;
        attackStyle = 2;
        /*switch (NumberUtils.random(2)) {
        case 0:
            combatType = CombatType.MAGIC;
            attackStyle = 1;
            break;
        case 1:
            combatType = CombatType.SPECIAL;
            attackStyle = 2 + NumberUtils.random(1);
            break;
        default:
            attackStyle = 0;
            break;
        }*/
        npc.getCombatState().setCombatType(combatType);
        return npc.getCombatState().getCombatType();
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
        CombatType combatType = npc.getCombatState().getCombatType();
        int maxHit = attackStyle == 2 ? 50 : 35;
        if (!npc.getAttributes().isSet("multi")) {
            maxHit = 6;
        }
        final Hit hit = new Hit(npc, (int) (Math.random() * maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, combatType, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {//TODO - web blockage?
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
    public void processFollow(NPC npc, Mob other) {
        NPCFollowing.executePathFinding(npc, other, true);
    }

    @Override
    public void loadAttack(NPC npc) {
        npc.getCombatState().setCombatType(CombatType.MELEE);
    }
}
