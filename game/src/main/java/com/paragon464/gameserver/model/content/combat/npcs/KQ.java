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
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.tickable.impl.PoisonTick;
import com.paragon464.gameserver.util.NumberUtils;

public class KQ extends NPCAttackLayout {

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
        boolean secondForm = npc.getId() == 1160;
        int anim = secondForm ? 6234 : combatType.equals(CombatType.MELEE) ? 6241 : 6240;
        npc.playAnimation(anim, AnimationPriority.HIGH);
        if (combatType.equals(CombatType.MAGIC)) {
            npc.playGraphic(278);
            Projectiles magicProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                mainTarget, 280, 45, 65, 50, 43, 35);
            npc.executeProjectile(magicProj);
        } else if (combatType.equals(CombatType.RANGED)) {
            Projectiles magicProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                mainTarget, 288, 35, 65, 50, 35, 25);
            npc.executeProjectile(magicProj);
        }
        final Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        World.getWorld().submit(new Tickable(combatType.equals(CombatType.MELEE) ? 0 : 1) {
            @Override
            public void execute() {
                this.stop();
                mainTarget.inflictDamage(hit, false);
                handleEndEffects(npc, mainTarget, hit);
            }
        });
    }

    @Override
    public void handleInitEffects(NPC npc, Mob target, Hit hit) {
        CombatType combatType = npc.getCombatState().getCombatType();
        if (combatType.equals(CombatType.MELEE)) {
            if (target.getCombatState().getPoisonCount() <= 0) {
                if (NumberUtils.random(10) < 7) {
                    target.submitTickable(new PoisonTick(target, 8));
                }
            }
        }
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType combatType = CombatType.MELEE;
        switch (NumberUtils.random(2)) {
            case 0:
                combatType = CombatType.MELEE;
                break;
            case 1:
                combatType = CombatType.MAGIC;
                break;
            case 2:
                combatType = CombatType.RANGED;
                break;
        }
        if (combatType.equals(CombatType.MELEE)) {
            if (!npc.getPosition().isWithinRadius(target, 1)) {
                switch (NumberUtils.random(1)) {
                    case 0:
                        combatType = CombatType.MAGIC;
                        break;
                    case 1:
                        combatType = CombatType.RANGED;
                        break;
                }
            }
        }
        npc.getCombatState().setCombatType(combatType);
        return npc.getCombatState().getCombatType();
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        int dist = !npc.getCombatState().getCombatType().equals(CombatType.MELEE) ? 8 : 1;
        return npc.getCoverage().correctCombatPosition(npc, target, target.getCoverage(), dist,
            npc.getCombatState().getCombatType());
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        final Hit hit = new Hit(npc, (int) (Math.random() * 31));
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
            } else if (combatType.equals(CombatType.RANGED)) {
                if (player.getPrayers().isPrayingRange()) {
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
