package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class CombatState {

    private final List<Hit> hits = new LinkedList<>();
    private DamageMap damageMap = new DamageMap();
    private CombatAction actions;
    private Mob mob;

    public CombatState(Mob mob) {
        this.mob = mob;
        this.defaultValues(true);
    }

    public void defaultValues(boolean loggedIn) {
        this.setVenged(false);
        this.setFreezable(true);
        this.setDead(false);
        this.setFrozen(false);
        this.setTarget(null);
        if (loggedIn) {
            mob.getAttributes().set("last_attacked", 0);
            mob.getAttributes().set("last_hit", 0);
            mob.getAttributes().set("autocast_spell", -1);
            mob.getAttributes().set("manual_cast", -1);
        } else if (!loggedIn) {
            this.clearSkull();
            this.setVenged(false);
            this.setLastVenged(0);
        }
        this.setPoisonCount(0);
        this.setAttackTimer(0);
        this.setPotionTimer(0);
        this.setFoodTimer(0);
        this.setIgnoringCombatCycles(false);
        this.setLastTarget(null);
        this.setLastAttacker(null);
        this.damageMap.reset();
        this.actions = MeleeAction.getAction();
    }

    public void clearSkull() {
        mob.getAttributes().remove("skull_timer");
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            player.getPrayers().setPkIcon(-1);
        }
    }

    public void setLastVenged(int var) {
        mob.getAttributes().set("last_venged", var);
    }

    public void setIgnoringCombatCycles(boolean var) {
        mob.getAttributes().set("ignore_cycles", var);
    }

    public void setLastAttacker(Mob var) {
        mob.getAttributes().set("last_attacker", var);
    }

    public void end(int type) {
        if (type == 1) {
            mob.getFollowing().setFollowing(null, false);
            mob.setInteractingMob(null);
            mob.getWalkingQueue().reset();
        } else if (type == 2) {
            mob.getFollowing().setFollowing(null, false);
            mob.getWalkingQueue().reset();
        }
        // mob.getWalkingQueue().reset();
        if (mob.getAttributes().isSet("manual_cast")) {
            mob.getAttributes().remove("manual_cast");
        }
        setTarget(null);
    }

    public void tick() {
        if (getFoodTimer() > 0) {
            setFoodTimer(getFoodTimer() - 1);
        }
        if (getPotionTimer() > 0) {
            setPotionTimer(getPotionTimer() - 1);
        }
        if (getAttackTimer() > 0) {
            deductAttackTimer(1);
        }
        if (getTbTimer() > 0) {
            deductTbTime(1);
        }
        if (previouslyHitInSecs() > 0) {
            mob.getAttributes().subtractInt("last_hit", 1);
        }
        if (previouslyAttackedInSecs() > 0) {
            mob.getAttributes().subtractInt("last_attacked", 1);
        }
        deductSkullTimer(1);
    }

    public int getFoodTimer() {
        return mob.getAttributes().getInt("food_timer");
    }

    public int getPotionTimer() {
        return mob.getAttributes().getInt("potion_timer");
    }

    public int getAttackTimer() {
        return mob.getAttributes().getInt("attack_timer");
    }

    public void setAttackTimer(int var) {
        mob.getAttributes().set("attack_timer", var);
    }

    public void deductAttackTimer(int var) {
        mob.getAttributes().subtractInt("attack_timer", var);
    }

    public int getTbTimer() {
        return mob.getAttributes().getInt("teleblock_time");
    }

    public void deductTbTime(int var) {
        mob.getAttributes().subtractInt("teleblock_time", var);
    }

    public int previouslyHitInSecs() {
        return mob.getAttributes().getInt("last_hit");
    }

    public int previouslyAttackedInSecs() {
        return mob.getAttributes().getInt("last_attacked");
    }

    public void deductSkullTimer(int var) {
        mob.getAttributes().subtractInt("skull_timer", var);
        if (!isSkulled()) {
            clearSkull();
        }
    }

    public boolean isSkulled() {
        return mob.getAttributes().getInt("skull_timer") > 0;
    }

    public void setPotionTimer(int var) {
        mob.getAttributes().set("potion_timer", var);
    }

    public void setFoodTimer(int var) {
        mob.getAttributes().set("food_timer", var);
    }

    public void setTbTime(int var) {
        mob.getAttributes().set("teleblock_time", var);
    }

    public boolean isTeleblocked() {
        return getTbTimer() > 0;
    }

    public boolean isDead() {
        return mob.getAttributes().is("dead");
    }

    public void setDead(boolean dead) {
        mob.getAttributes().set("dead", dead);
    }

    public boolean isFrozen() {
        return mob.getAttributes().is("frozen");
    }

    public void setFrozen(boolean frozen) {
        mob.getAttributes().set("frozen", frozen);
    }

    public boolean isFreezable() {
        if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            if (npc.getId() == 8133) {//corp
                return false;
            }
        }
        return mob.getAttributes().is("freezable");
    }

    public void setFreezable(boolean var) {
        mob.getAttributes().set("freezable", var);
    }

    public Mob getTarget() {
        return mob.getAttributes().get("target");
    }

    public void setTarget(Mob target) {
        if (target != null) {
            target.getCombatState().setLastAttacker(mob);
        }
        mob.getAttributes().set("target", target);
        if (target != null) {
            setLastTarget(target);
        }
    }

    public Mob getLastTarget() {
        return mob.getAttributes().get("last_target");
    }

    public void setLastTarget(Mob target) {
        mob.getAttributes().set("last_target", target);
    }

    public Mob getLastAttackedBy() {
        return mob.getAttributes().get("last_attacker");
    }

    public boolean isIgnoringCycles() {
        return mob.getAttributes().is("ignore_cycles");
    }

    public CombatAction getCurrentAction() {
        return actions;
    }

    public void setCurrentAction(CombatAction actions) {
        this.actions = actions;
    }

    public void resetAttackTimer() {
        if (mob.isNPC()) {
            mob.getAttributes().set("attack_timer", ((NPC) mob).getCombatDefinition().getAttackSpeed() / 1000);
        }
    }

    public void increaseAttackTimer(int var) {
        mob.getAttributes().addInt("attack_timer", var);
    }

    public List<Hit> getHitQueue() {
        return hits;
    }

    public boolean outOfCombat() {
        int lastHit = previouslyHitInSecs();
        int lastAttacked = previouslyAttackedInSecs();
        return lastAttacked == 0 && lastHit == 0;
    }

    public void setOutOfCombat() {
        mob.getAttributes().set("last_hit", 0);
        mob.getAttributes().set("last_attacked", 0);
    }

    public void refreshLastHit() {
        mob.getAttributes().set("last_hit", 16);
    }

    public void refreshLastAttacked() {
        mob.getAttributes().set("last_attacked", 16);
    }

    public long lastVenged() {
        return mob.getAttributes().getLong("last_venged");
    }

    public void refreshVenged() {
        mob.getAttributes().set("last_venged", System.currentTimeMillis());
    }

    public boolean isVenged() {
        return mob.getAttributes().is("is_venged");
    }

    public void setVenged(boolean var) {
        mob.getAttributes().set("is_venged", var);
    }

    public boolean isCharged() {
        return mob.getAttributes().isSet("charged_spell");
    }

    public void setCharged(boolean var) {
        mob.getAttributes().set("charged_spell", var);
    }

    public int getRecoilCount() {
        return mob.getAttributes().getInt("recoil_count");
    }

    public void setRecoilCount(int var) {
        mob.getAttributes().set("recoil_count", var);
    }

    public void deductRecoilCount(int var) {
        mob.getAttributes().subtractInt("recoil_count", var);
    }

    public int getPoisonCount() {
        return mob.getAttributes().getInt("poison_count");
    }

    public void setPoisonCount(int var) {
        mob.getAttributes().set("poison_count", var);
    }

    public void deductPoisonCount(int var) {
        mob.getAttributes().subtractInt("poison_count", var);
    }

    public CombatType getCombatType() {
        CombatType type = mob.getAttributes().get("combatType");
        if (type == null) {
            type = CombatType.MELEE;
        }
        return type;
    }

    public void setCombatType(final CombatType type) {
        mob.getAttributes().set("combatType", type);
    }

    public DamageMap getDamageMap() {
        return damageMap;
    }

    public int getManualSpell() {
        if (!mob.getAttributes().isSet("manual_cast")) {
            return -1;
        }
        return mob.getAttributes().getInt("manual_cast");
    }

    public int getAutocastSpell() {
        if (!mob.getAttributes().isSet("autocast_spell")) {
            return -1;
        }
        return mob.getAttributes().getInt("autocast_spell");
    }

    public void activateSkull() {
        mob.getAttributes().set("skull_timer", 2000);
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            player.getPrayers().setPkIcon(0);
        }
    }
}
