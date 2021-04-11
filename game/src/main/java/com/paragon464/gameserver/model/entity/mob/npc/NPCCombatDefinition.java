package com.paragon464.gameserver.model.entity.mob.npc;

import java.util.ArrayList;
import java.util.List;

public class NPCCombatDefinition {

    public static List<NPCCombatDefinition> definitions = new ArrayList<>();
    public int id;
    public int attackAnim = -1;
    public int defendAnim = -1;
    public int deathAnim = -1;
    public int speed = 1;
    public int maxHit = 0;
    public int poisonMaxHit = 1;
    public int dienTime = 1;
    public boolean aggressive = false, retreats = false, poisonImmuned = false;
    public double slayerXP = 0;
    public int slayerLvl = 0;

    public static NPCCombatDefinition forId(int id) {
        for (NPCCombatDefinition defs : definitions) {
            if (defs.id == id) {
                return defs;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public int getAttackSpeed() {
        return speed;
    }

    public int getMaxHit() {
        return maxHit;
    }

    public int getPoisonMaxHit() {
        return poisonMaxHit;
    }

    public boolean isAggressive() {
        return aggressive;
    }

    public boolean shouldRetreat() {
        return retreats;
    }

    public boolean isPoisonImmuned() {
        return poisonImmuned;
    }

    public int getAttackAnim() {
        return attackAnim;
    }

    public void setAttackAnim(int attackAnim) {
        this.attackAnim = attackAnim;
    }

    public int getDefendAnim() {
        return defendAnim;
    }

    public void setDefendAnim(int defendAnim) {
        this.defendAnim = defendAnim;
    }

    public int getDeathAnim() {
        return deathAnim;
    }

    public void setDeathAnim(int deathAnim) {
        this.deathAnim = deathAnim;
    }
}
