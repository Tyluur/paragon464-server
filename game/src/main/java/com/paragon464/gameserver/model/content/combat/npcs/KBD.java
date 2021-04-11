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

public class KBD extends NPCAttackLayout {

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
        int anim = NumberUtils.random(1) == 0 ? 80 : 91;
        int projectileId = -1;
        final Hit hit = getDamage(npc, mainTarget);
        if (combatType.equals(CombatType.MAGIC)) {
            anim = 81;
            projectileId = NumberUtils.random(393, 396);
            Projectiles magicProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                mainTarget, projectileId, 40, 70, 50, 43, 35);
            npc.executeProjectile(magicProj);
        } else if (combatType.equals(CombatType.FIRE_BREATH)) {
            npc.playGraphic(1, 0, 100);
            anim = 81;
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
        handleInitEffects(npc, mainTarget, hit);
        attackEffects(npc, mainTarget, hit, projectileId);
        World.getWorld().submit(new Tickable(0) {
            @Override
            public void execute() {
                this.stop();
                mainTarget.inflictDamage(hit, false);
                handleEndEffects(npc, mainTarget, hit);
            }
        });
    }

    @Override
    public void handleEndEffects(NPC npc, Mob target, Hit hit) {
        // TODO Auto-generated method stub

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
                combatType = CombatType.FIRE_BREATH;
                break;
        }
        if (combatType.equals(CombatType.MELEE)) {
            if (!npc.getPosition().isWithinRadius(target, 1)) {
                combatType = CombatType.MAGIC;
            }
        }
        npc.getCombatState().setCombatType(combatType);
        return npc.getCombatState().getCombatType();
    }

    @Override
    public boolean isWithinRadius(NPC npc, Mob target) {
        int dist = npc.getCombatState().getCombatType().equals(CombatType.MAGIC) ? 8 : 1;
        return npc.getCoverage().correctCombatPosition(npc, target, target.getCoverage(), dist,
            npc.getCombatState().getCombatType());
    }

    @Override
    public Hit getDamage(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        int maxHit = combatType.equals(CombatType.MELEE) ? 25 : 65;
        final Hit hit = new Hit(npc, (int) (Math.random() * maxHit));
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
            } else if (combatType.equals(CombatType.FIRE_BREATH)) {
                double percentage = 0.0;
                boolean praying = player.getPrayers().isPrayingMagic();
                if (player.getEquipment().hasItem(11283) || player.getEquipment().hasItem(1540)) {
                    percentage = 0.8;
                    player.getFrames().sendMessage("You shield absorbs most of the dragon fire!");
                } else if (praying) {
                    percentage = 0.6;
                    player.getFrames().sendMessage("Your prayers resist some of the dragon fire.");
                }
                if (percentage > 0.0) {
                    hit.deductDamage((int) (hit.getDamage() * percentage));
                }
            } else {
                boolean praying = player.getPrayers().isPrayingMagic();
                if (praying || player.getEquipment().hasItem(11283) || player.getEquipment().hasItem(1540)) {
                    maxHit = 10;
                    hit.setDamage((int) (Math.random() * maxHit));
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

    private void attackEffects(NPC npc, final Mob target, Hit hit, int projectile) {
        if (projectile != -1) {
            switch (projectile) {
                case 394:// Poison
                    if (hit.getDamage() > 0) {
                        if (target.getCombatState().getPoisonCount() <= 0) {
                            if (NumberUtils.random(10) < 7) {
                                target.submitTickable(new PoisonTick(target, 8));
                            }
                        }
                    }
                    break;
                case 395:// Freeze
                    if (target.getCombatState().isFreezable()) {
                        if (hit.getDamage() > 0) {
                            if (NumberUtils.random(10) < 7) {
                                int freezeTimer = 24;
                                if (target.isPlayer()) {
                                    Player player = (Player) target;
                                    if (player.getPrayers().isPrayingMagic()) {
                                        freezeTimer /= 2;
                                    }
                                    player.getFrames().sendMessage("You've been frozen!");
                                }
                                target.getCombatState().end(1);
                                target.getCombatState().setFreezable(false);
                                target.getCombatState().setFrozen(true);
                                target.submitTickable(new Tickable(freezeTimer) {
                                    @Override
                                    public void execute() {
                                        this.stop();
                                        target.getCombatState().setFrozen(false);
                                        target.submitTickable(new Tickable(6) {
                                            @Override
                                            public void execute() {
                                                this.stop();
                                                target.getCombatState().setFreezable(true);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                    break;
                case 396:// pray deducting
                    if (NumberUtils.random(10) < 3) {
                        if (hit.getDamage() > 0) {
                            if (target.isPlayer()) {
                                Player player = (Player) target;
                                player.getSettings().decreasePrayerPoints(NumberUtils.random(3));
                            }
                        }
                    }
                    break;
            }
        }
    }
}
