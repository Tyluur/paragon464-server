package com.paragon464.gameserver.model.entity.mob.npc;

import com.paragon464.gameserver.model.entity.mob.CombatType;

import java.util.ArrayList;
import java.util.List;

public class NPCDefinition {

    public static List<NPCDefinition> definitions = new ArrayList<>();
    public int id;
    public String name;
    public String examine = null;
    public int combatLevel;
    public int size = 1, respawn = 0;
    private CombatType combatType = CombatType.MELEE;

    public static NPCDefinition forId(int id) {
        for (NPCDefinition defs : definitions) {
            if (defs.id == id) {
                return defs;
            }
        }
        return null;
    }

    public static NPCDefinition produceDefinition(int id) {
        NPCDefinition def = new NPCDefinition();
        def.id = id;
        def.name = "NPC #" + def.id;
        def.examine = "It's an NPC.";
        return def;
    }

    public String getName() {
        return name;
    }

    public String getExamine() {
        return examine;
    }

    public int getRespawn() {
        return respawn;
    }

    public int getSize() {
        return size;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public CombatType getCombatType() {
        if (combatType == null) {
            combatType = CombatType.MELEE;
        }
        return combatType;
    }

    public void setCombatType(CombatType combatType) {
        this.combatType = combatType;
    }

    @Override
    public String toString() {
        return "" + name + ", id: " + id + ", size: " + size;
    }
}
