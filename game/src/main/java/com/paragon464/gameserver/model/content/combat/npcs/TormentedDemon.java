package com.paragon464.gameserver.model.content.combat.npcs;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class TormentedDemon extends NPCAttackLayout {

    public boolean[] demonPrayer = new boolean[3];
    public int shieldTimer;
    public int[] cachedDamage = new int[3];
    private NPC npc;
    private int fixedAmount;
    private int fixedCombatType;
    private int prayerTimer;

    public TormentedDemon(NPC n) {
        this.npc = n;
        switchPrayers(0);
    }

    public void switchPrayers(int type) {
        npc.setTransformationId(8349 + type);
        demonPrayer[type] = true;
        resetPrayerTimer();
    }

    private void resetPrayerTimer() {
        prayerTimer = 16;
    }

    public void tick() {
        if (npc.getCombatState().isDead())
            return;
        if (NumberUtils.random(40) <= 2)
            sendRandomProjectile(npc);
        if (shieldTimer > 0)
            shieldTimer--;
        if (prayerTimer > 0)
            prayerTimer--;
        if (fixedAmount >= 5)
            fixedAmount = 0;
        if (prayerTimer == 0) {
            for (int i = 0; i < cachedDamage.length; i++) {
                if (cachedDamage[i] >= 31) {
                    demonPrayer = new boolean[3];
                    switchPrayers(i);
                    cachedDamage = new int[3];
                }
            }
        }
        for (int i = 0; i < cachedDamage.length; i++) {
            if (cachedDamage[i] >= 31) {
                demonPrayer = new boolean[3];
                switchPrayers(i);
                cachedDamage = new int[3];
            }
        }
    }

    private void sendRandomProjectile(NPC n) {
        Position center = new Position(n.getPosition().getX() + NumberUtils.random(7), n.getPosition().getY() + NumberUtils.random(7), n.getPosition().getZ());
        World.sendProjectile(n.getCentreLocation(), Projectiles.create(n.getCentreLocation(), center,
            null, 1887, 55, 85, 50, 95, 16));
        World.getWorld().submit(new Tickable(1) {
            @Override
            public void execute() {
                this.stop();
                for (Player targets : World.getSurroundingPlayers(n.getPosition())) {
                    if (targets.getCombatState().isDead() || !targets.getPosition().isWithinRadius(center, 3))
                        continue;
                    targets.getFrames().sendMessage("The demon's magical attack splashes on you.");
                    Hit hit = new Hit(n, 28);
                    handleInitEffects(n, targets, hit);
                    targets.inflictDamage(hit, false);
                }
            }
        });
    }

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
        handleInitEffects(npc, mainTarget, null);
        switch (combatType) {
            case MELEE:
                npc.playGraphic(1886);
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
            case RANGED:
                anim = 10919;
                npc.playGraphic(1888);
                Projectiles rangedProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                    mainTarget, 1887, 65, 90, 50, 95, 16);
                npc.executeProjectile(rangedProj);
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
            case MAGIC:
                anim = 10918;
                Projectiles magicProj = Projectiles.create(npc.getCentreLocation(), mainTarget.getCentreLocation(),
                    mainTarget, 1884, 30, 80, 50, 95, 16);
                npc.executeProjectile(magicProj);
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        final Hits.Hit hit = getDamage(npc, mainTarget);
                        if (hit.getDamage() > 0) {
                            mainTarget.inflictDamage(hit, false);
                            handleEndEffects(npc, mainTarget, hit);
                            mainTarget.playGraphic(1883, 0, 100);
                        } else {
                            mainTarget.playGraphic(85, 0, 100);
                        }
                    }
                });
                break;
        }
        npc.playAnimation(anim, AnimationPriority.HIGH);
        setFixedAmount(getFixedAmount() + 1);
    }

    @Override
    public CombatType getCombatType(NPC npc, Mob target) {
        int attackStyle = getFixedAmount() == 0 ? NumberUtils.random(2)
            : getFixedCombatType();
        if (getFixedAmount() == 0)
            setFixedCombatType(attackStyle);
        CombatType type = null;
        if (attackStyle == 0) {//Melee
            type = CombatType.MELEE;
            if (!npc.getPosition().isWithinRadius(target, 1)) {
                switch (NumberUtils.random(1)) {
                    case 0:
                        type = CombatType.MAGIC;
                        break;
                    case 1:
                        type = CombatType.RANGED;
                        break;
                }
            }
        } else if (attackStyle == 1) {
            type = CombatType.MAGIC;
        } else if (attackStyle == 2) {
            type = CombatType.RANGED;
        }
        npc.getCombatState().setCombatType(type);
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
        int maxHit = 18;
        switch (combatType) {
            case RANGED:
            case MAGIC:
                maxHit = 27;
                break;
        }
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

    public int getFixedAmount() {
        return fixedAmount;
    }

    public int getFixedCombatType() {
        return fixedCombatType;
    }

    public void setFixedCombatType(int fixedCombatType) {
        this.fixedCombatType = fixedCombatType;
    }

    public void setFixedAmount(int fixedAmount) {
        this.fixedAmount = fixedAmount;
    }
}
