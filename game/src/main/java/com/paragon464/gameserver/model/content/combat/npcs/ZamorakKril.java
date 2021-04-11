package com.paragon464.gameserver.model.content.combat.npcs;

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
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.tickable.impl.PoisonTick;
import com.paragon464.gameserver.util.NumberUtils;

public class ZamorakKril extends NPCAttackLayout {

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
        int attackTimer = npc.getCombatDefinition().getAttackSpeed();
        if (combatType.equals(CombatType.MAGIC)) {
            anim = 6947;
            attackTimer = 6;
            npc.playGraphic(1210, 0, 100);
        }
        npc.playAnimation(anim, Animation.AnimationPriority.HIGH);
        npc.getCombatState().setAttackTimer(attackTimer);
        final Hits.Hit hit = getDamage(npc, mainTarget);
        handleInitEffects(npc, mainTarget, hit);
        if (combatType.equals(CombatType.MELEE)) {
            World.getWorld().submit(new Tickable(0) {
                @Override
                public void execute() {
                    this.stop();
                    mainTarget.inflictDamage(hit, false);
                    handleEndEffects(npc, mainTarget, hit);
                }
            });
        } else if (combatType.equals(CombatType.MAGIC)) {
            World.getWorld().submit(new Tickable(2) {
                @Override
                public void execute() {
                    this.stop();
                    if (hit.getDamage() > 0) {
                        mainTarget.inflictDamage(hit, false);
                        handleEndEffects(npc, mainTarget, hit);
                        // mainTarget.playGraphic(369, 0, 0);
                    } else {
                        mainTarget.playGraphic(85, 0, 100);
                    }
                }
            });
        }
    }

    @Override
    public void handleEndEffects(NPC npc, Mob target, Hits.Hit hit) {
        if (target.getCombatState().getPoisonCount() <= 0) {
            if (NumberUtils.random(8) == 1) {
                target.submitTickable(new PoisonTick(target, 16));
            }
        }
        if (npc.getAttributes().isSet("zamorak_boss_special")) {
            if (target.isPlayer()) {
                Player player = (Player) target;
                double count = player.getSettings().getPrayerPoints() / 2;
                player.getSettings().decreasePrayerPoints(count);
            }
        }
        npc.getAttributes().remove("zamorak_boss_special");
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.MELEE;
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (player.getPrayers().isPrayingMelee()) {
                boolean special = NumberUtils.random(8) == 1;
                if (special) {
                    npc.getAttributes().set("zamorak_boss_special", true);
                    return type;
                }
            }
        }
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
    public Hits.Hit getDamage(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        boolean bossSpecial = npc.getAttributes().isSet("zamorak_boss_special");
        if (type.equals(CombatType.MAGIC)) {
            maxHit = 30;
        } else if (bossSpecial) {
            maxHit = 49;
        }
        final Hits.Hit hit = new Hits.Hit(npc, NumberUtils.random(maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, type, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (type.equals(CombatType.MELEE)) {
                if (player.getPrayers().isPrayingMelee()) {
                    if (!bossSpecial) {
                        hit.setDamage(0);
                    }
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
        npc.getCombatState().setCombatType(CombatType.MAGIC);
    }
}
