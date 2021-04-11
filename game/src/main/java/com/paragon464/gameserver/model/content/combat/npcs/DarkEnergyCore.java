package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.List;

public class DarkEnergyCore extends NPCAttackLayout {

    public NPC npc;
    private CorporealBeast beast;
    private Mob target;
    private int changeTarget;
    private int delay;

    public DarkEnergyCore(CorporealBeast b) {
        this.npc = new NPC(8127);
        this.npc.setAttackLayout(this);
        this.beast = b;
        this.changeTarget = 2;
        npc.setPosition(b.npc.getCombatState().getTarget().getPosition().getNorth());
        npc.setLastKnownRegion(b.npc.getCombatState().getTarget().getPosition().getNorth());
        World.getWorld().addNPC(npc);
    }

    public void tick() {
        if (delay > 0) {
            delay--;
            return;
        }
        if (changeTarget > 0) {
            if (changeTarget == 1) {
                List<Player> possibleTarget = World.getSurroundingPlayers(npc.getCentreLocation());
                if (possibleTarget.isEmpty()) {
                    beast.removeCore();
                    return;
                }
                target = possibleTarget.get(NumberUtils.random(possibleTarget
                    .size() - 1));
                npc.teleport(target.getPosition().getNorth());
                Projectiles proj = Projectiles.create(npc.getCentreLocation(), target.getPosition().getNorth(),
                    null, 1828, 41, 70, 16, 95, 16);
                World.sendProjectile(npc.getCentreLocation(), proj);
                /*World.sendProjectile(this, this, target, 1828, 0, 0, 40, 40,
                        20, 0);*/
            }
            changeTarget--;
            return;
        }
        if (target == null || !TileControl.locationOccupied(npc, target)) {
            changeTarget = 5;
            return;
        }
        int damage = NumberUtils.random(50) + 50;
        target.inflictDamage(new Hit(npc, NumberUtils.random(1, 13)), false);
        beast.npc.heal(damage);
        delay = npc.getCombatState().getPoisonCount() > 0 ? 10 : 3;
        if (target.isPlayer()) {
            Player player = (Player) target;
            player.getFrames().sendMessage("The dark core creature steals some life from you for its master.");
        }
    }

    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        /*boolean withinDist = isWithinRadius(npc, mainTarget);
        if (!withinDist)
            return;
        if (!canAttack(npc, mainTarget)) {
            npc.getCombatState().end(1);
            return;
        }
        int combatSpeed = npc.getCombatState().getAttackTimer();
        if (combatSpeed > 0)
            return;

        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());*/
        //int anim = npc.getCombatDefinition().getAttackAnim();
        //handleInitEffects(npc, mainTarget, null);
        //npc.playAnimation(anim, AnimationPriority.HIGH);
        /*World.getWorld().submit(new Tickable(0) {
            @Override
            public void execute() {
                this.stop();
                final Hits.Hit hit = getDamage(npc, mainTarget);
                mainTarget.inflictDamage(hit, false);
                handleEndEffects(npc, mainTarget, hit);
            }
        });*/
    }

/*    @Override
    public Hit getDamage(NPC npc, Mob target) {
        final Hit hit = new Hit(npc, (int) (Math.random() * npc.getCombatDefinition().getMaxHit()));
        boolean accurate = Formulas.isAccurate(npc, target, CombatType.MELEE, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (player.getPrayers().isPrayingMelee()) {
                hit.setDamage(0);
            }
        }
        return hit;
    }*/

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
