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
import com.paragon464.gameserver.model.content.minigames.fightcaves.CavesBattleSession;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class TzTokJad extends NPCAttackLayout {

    @SuppressWarnings("incomplete-switch")
    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        if (npc.getCombatState().getAttackTimer() > 0) {
            return;
        }
        if (!canAttack(npc, mainTarget)) {
            return;
        }
        CombatType combatType = getCombatType(npc, mainTarget);
        npc.getCombatState().setCombatType(combatType);
        npc.setInteractingMob(mainTarget);
        if (!isWithinRadius(npc, mainTarget)) {
            return;
        } else {
            npc.getWalkingQueue().reset();
        }
        CavesBattleSession battle = npc.getAttributes().get("caves_session");
        if (battle != null) {
            if (!battle.healersSpawned && npc.getHp() <= (npc.getMaxHp() / 2)) {
                battle.healersSpawned = true;
                for (int i = 0; i < 4; i++) {
                    NPC healer = new NPC(2746);
                    Position placement = battle.npcLoc();
                    healer.getAttributes().set("force_multi", true);
                    healer.getAttributes().set("caves_session", battle);
                    healer.setPosition(placement);
                    healer.setLastKnownRegion(placement);
                    battle.healers.add(healer);
                    World.getWorld().addNPC(healer);
                    healer.setInteractingMob(npc);
                    healer.getFollowing().setFollowing(npc, false);
                }
            }
        }
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        handleInitEffects(npc, mainTarget, null);
        switch (combatType) {
            case RANGED:
                npc.playAnimation(9276, Animation.AnimationPriority.HIGH);
                npc.playGraphic(1625);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        mainTarget.playGraphic(451);
                        World.getWorld().submit(new Tickable(1) {
                            @Override
                            public void execute() {
                                this.stop();
                                final Hits.Hit hit = getDamage(npc, mainTarget);
                                mainTarget.inflictDamage(hit, false);
                                handleEndEffects(npc, mainTarget, hit);
                                mainTarget.playGraphic(157, 0, 100);
                            }
                        });
                    }
                });
                break;
            case MAGIC:
                npc.playAnimation(9278, Animation.AnimationPriority.HIGH);
                npc.playGraphic(1626);
                Projectiles mageProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getPosition(), mainTarget,
                    1627, 45, 90, 50, 40, 34);
                npc.executeProjectile(mageProj);
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        final Hits.Hit hit = getDamage(npc, mainTarget);
                        if (hit.getDamage() > 0) {
                            mainTarget.inflictDamage(hit, false);
                            handleEndEffects(npc, mainTarget, hit);
                            mainTarget.playGraphic(157, 0, 100);
                        } else {
                            mainTarget.playGraphic(85, 0, 100);
                        }
                    }
                });
                break;
            case MELEE:
                npc.playAnimation(npc.getCombatDefinition().getAttackAnim(), Animation.AnimationPriority.HIGH);
                final Hits.Hit hit = getDamage(npc, mainTarget);
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
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        CombatType type = CombatType.MELEE;
        if (!npc.getPosition().isWithinRadius(target, 1)) {
            switch (NumberUtils.random(1)) {
                case 0:
                    type = CombatType.MAGIC;
                    break;
                case 1:
                    type = CombatType.RANGED;
                    break;
            }
        } else {
            switch (NumberUtils.random(2)) {
                case 0:
                    type = CombatType.MAGIC;
                    break;
                case 1:
                    type = CombatType.RANGED;
                    break;
                case 2:
                    type = CombatType.MELEE;
                    break;
            }
        }
        return type;
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int distance = 1;
        if (type.equals(CombatType.RANGED)) {
            distance = 7;
        } else if (type.equals(CombatType.MAGIC)) {
            distance = 8;
        }
        return !TileControl.locationOccupied(npc, target) && npc.getPosition().isWithinRadius(target, distance);
    }

    @Override
    public Hits.Hit getDamage(NPC npc, Mob target) {
        CombatType type = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        if (!type.equals(CombatType.MELEE)) {
            maxHit = 97;
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
                    hit.setDamage(0);
                }
            } else if (type.equals(CombatType.MAGIC)) {
                if (player.getPrayers().isPrayingMagic()) {
                    hit.setDamage(0);
                }
            } else if (type.equals(CombatType.RANGED)) {
                if (player.getPrayers().isPrayingRange()) {
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
        npc.getCombatState().setCombatType(CombatType.RANGED);
    }
}
