package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class CorporealBeast extends NPCAttackLayout {

    public NPC npc;
    private DarkEnergyCore core;
    private int attackStyle = 0;

    public CorporealBeast(NPC n) {
        this.npc = n;
    }

    public void removeCore() {
        World.getWorld().unregister(core.npc);
        core = null;
    }

    public void tick() {
        int maxHp = npc.getMaxHp();
        if (maxHp > npc.getHp() && World.getSurroundingPlayers(npc.getPosition()).isEmpty()) {
            npc.setHp(maxHp);
        }
    }

    @Override
    public void executeAttacks(NPC npc, Mob mainTarget) {
        boolean stomp = false;
        for (Player p : World.getSurroundingPlayers(npc.getPosition())) {
            if (TileControl.locationOccupied(p, npc)) {
                stomp = true;
            }
        }
        CombatType combatType = getCombatType(npc, mainTarget);
        boolean withinDist = isWithinRadius(npc, mainTarget);
        if (!withinDist && !stomp)
            return;
        if (!canAttack(npc, mainTarget)) {
            npc.getCombatState().end(1);
            return;
        }
        int combatSpeed = npc.getCombatState().getAttackTimer();
        if (combatSpeed > 0)
            return;
        /*if (NumberUtils.random(40) == 0) {
            if (core == null) {
                core = new DarkEnergyCore(this);
            }
        }*/
        npc.getCombatState().setAttackTimer(npc.getCombatDefinition().getAttackSpeed());
        int anim = npc.getCombatDefinition().getAttackAnim();
        handleInitEffects(npc, mainTarget, null);
        if (stomp) {
            combatType = CombatType.MELEE;
            npc.playAnimation(10496, AnimationPriority.HIGH);
            npc.playGraphic(1834);
            World.getWorld().submit(new Tickable(0) {
                @Override
                public void execute() {
                    this.stop();
                    final Hits.Hit hit = getDamage(npc, mainTarget);
                    mainTarget.inflictDamage(hit, false);
                    handleEndEffects(npc, mainTarget, hit);
                }
            });
            return;
        }
        switch (combatType) {
            case MELEE:
                if (attackStyle == 0) {
                    anim = 10058;
                }
                World.getWorld().submit(new Tickable(0) {
                    @Override
                    public void execute() {
                        this.stop();
                        final Hits.Hit hit = getDamage(npc, mainTarget);
                        mainTarget.inflictDamage(hit, false);
                        handleEndEffects(npc, mainTarget, hit);
                    }
                });
                break;
            case MAGIC:
                anim = 10053;
                Projectiles proj = null;
                switch (attackStyle) {
                    case 2:
                        proj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                            mainTarget, 1825, 41, 70, 16, 95, 16);
                        World.getWorld().submit(new Tickable(2) {
                            @Override
                            public void execute() {
                                this.stop();
                                final Hits.Hit hit = getDamage(npc, mainTarget);
                                mainTarget.inflictDamage(hit, false);
                                handleEndEffects(npc, mainTarget, hit);
                            }
                        });
                        break;
                    case 3:
                        proj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                            mainTarget, 1823, 41, 70, 16, 95, 16);
                        if (mainTarget.isPlayer()) {
                            final Player p = (Player) mainTarget;
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    final Hits.Hit hit = getDamage(npc, mainTarget);
                                    mainTarget.inflictDamage(hit, false);
                                    handleEndEffects(npc, mainTarget, hit);
                                }
                            });
                            World.getWorld().submit(new Tickable(0) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    int skill = NumberUtils.random(1);
                                    SkillType skillType = null;
                                    if (skill == 0) {
                                        skillType = SkillType.MAGIC;
                                    } else if (skill == 1) {
                                        skillType = SkillType.PRAYER;
                                    }
                                    if (skillType.equals(SkillType.PRAYER))
                                        p.getSkills().decrementCurrentLevel(skillType, 10 + NumberUtils.random(4));
                                    else {
                                        p.getSkills().decrementCurrentLevel(skillType, 1 + NumberUtils.random(4));
                                    }
                                    p.getFrames().sendMessage(
                                        "Your " + skillType.getDisplayName() + " has been slighly drained!");
                                }
                            });
                        }
                        break;
                    case 4:
                        proj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                            mainTarget, 1824, 41, 70, 16, 95, 16);
                        final Position tile = mainTarget.getCentreLocation();
                        World.getWorld().submit(new Tickable(1) {
                            @Override
                            public void execute() {
                                this.stop();
                                final Hits.Hit hit = getDamage(npc, mainTarget);
                                mainTarget.inflictDamage(hit, false);
                                handleEndEffects(npc, mainTarget, hit);
                            }
                        });
                        World.getWorld().submit(new Tickable(0) {
                            @Override
                            public void execute() {
                                this.stop();
                                for (int i = 0; i < 6; i++) {
                                    final Position newTile = new Position(tile.getX(), 3, 0);
                                    Directions.NormalDirection dir = Directions.directionForr(npc.getPosition(), newTile);
                                    if (!TileControl.canMove(npc, dir, npc.getSize(), false))
                                        continue;
                                    final Projectiles proj = Projectiles.create(tile, newTile,
                                        null, 1824, 41, 95, 50, 95, 16);
                                    World.sendProjectile(npc.getCentreLocation(), proj);
                                    for (Player p : World.getSurroundingPlayers(npc.getPosition())) {
                                        if (p.getPosition().getDistanceFrom(newTile) > 0)
                                            continue;
                                        World.getWorld().submit(new Tickable(1) {
                                            @Override
                                            public void execute() {
                                                this.stop();
                                                final Hits.Hit hit = getDamage(npc, p);
                                                mainTarget.inflictDamage(hit, false);
                                                handleEndEffects(npc, p, hit);
                                            }
                                        });
                                    }
                                    World.getWorld().submit(new Tickable(2) {
                                        @Override
                                        public void execute() {
                                            this.stop();
                                            World.sendStillGraphic(newTile, Graphic.create(1806));
                                        }
                                    });
                                }
                            }
                        });
                        break;
                }
                if (proj != null) {
                    npc.executeProjectile(proj);
                }
                break;
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        int ran = NumberUtils.random(4);
        CombatType type = null;
        if (ran == 0 || ran == 1) {//Melee
            type = CombatType.MELEE;
            if (!npc.getPosition().isWithinRadius(target, 1)) {
                type = CombatType.MAGIC;
                ran = NumberUtils.random(2, 4);
            }
        } else {
            type = CombatType.MAGIC;
        }
        attackStyle = ran;
        npc.getCombatState().setCombatType(type);
        return npc.getCombatState().getCombatType();
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
    public Hit getDamage(NPC npc, Mob target) {
        CombatType combatType = npc.getCombatState().getCombatType();
        int maxHit = npc.getCombatDefinition().getMaxHit();
        if (combatType.equals(CombatType.MAGIC)) {
            maxHit = 65;
        }
        final Hit hit = new Hit(npc, (int) (Math.random() * maxHit));
        boolean accurate = Formulas.isAccurate(npc, target, combatType, false);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (target.isPlayer()) {
            Player player = (Player) target;
            if (combatType.equals(CombatType.MAGIC)) {
                if (player.getPrayers().isPrayingMagic()) {
                    hit.setDamage((int) (hit.getDamage() * 0.66));
                }
            } else if (combatType.equals(CombatType.MELEE)) {
                if (player.getPrayers().isPrayingMelee()) {
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
