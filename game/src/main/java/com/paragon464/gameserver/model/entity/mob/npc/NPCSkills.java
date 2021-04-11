package com.paragon464.gameserver.model.entity.mob.npc;

import java.util.ArrayList;
import java.util.List;

public class NPCSkills {

    public static final List<NPCSkills> definitions = new ArrayList<>();
    public int id;
    public int attack, strength, defence, ranged, magic = 1;
    public int hitpoints = 0, maxHitpoints = 1;

    public static void load(int id, NPCSkills skills) {
        NPCSkills loaded = forId(id);
        if (loaded == null) {
            return;
        }
        skills.id = id;
        skills.attack = loaded.attack;
        skills.defence = loaded.defence;
        skills.strength = loaded.strength;
        skills.hitpoints = loaded.hitpoints;
        skills.maxHitpoints = loaded.hitpoints;
        skills.ranged = loaded.ranged;
        skills.magic = loaded.magic;
    }

    public static NPCSkills forId(int id) {
        for (NPCSkills defs : definitions) {
            if (defs.id == id) {
                return defs;
            }
        }
        return null;
    }

    public int getMaxHitpoints() {
        return maxHitpoints;
    }

    public int getLevel(int id) {
        if (id == 0) {
            return attack;
        } else if (id == 1) {
            return defence;
        } else if (id == 2) {
            return strength;
        } else if (id == 3) {
            return hitpoints;
        } else if (id == 4) {
            return ranged;
        } else if (id == 6) {
            return magic;
        }
        return 0;
    }

    public void increase(int id, int amount) {
        if (id == 0) {
            attack += amount;
        } else if (id == 1) {
            defence += amount;
        } else if (id == 2) {
            strength += amount;
        } else if (id == 3) {
            hitpoints += amount;
        } else if (id == 4) {
            ranged += amount;
        } else if (id == 6) {
            magic += amount;
        }
    }

    public void deduct(int id, int amount) {
        if (id == 0) {
            attack -= amount;
            if (attack < 0) {
                attack = 0;
            }
        } else if (id == 1) {
            defence -= amount;
            if (defence < 0) {
                defence = 0;
            }
        } else if (id == 2) {
            strength -= amount;
            if (strength < 0) {
                strength = 0;
            }
        } else if (id == 3) {
            hitpoints -= amount;
            if (hitpoints < 0) {
                hitpoints = 0;
            }
        } else if (id == 4) {
            ranged -= amount;
            if (ranged < 0) {
                ranged = 0;
            }
        } else if (id == 6) {
            magic -= amount;
            if (magic < 0) {
                magic = 0;
            }
        }
    }

    public void setLevel(int id, int lvl) {
        if (id == 0) {
            attack = lvl;
        } else if (id == 1) {
            defence = lvl;
        } else if (id == 2) {
            strength = lvl;
        } else if (id == 3) {
            hitpoints = lvl;
        } else if (id == 4) {
            ranged = lvl;
        } else if (id == 6) {
            magic = lvl;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
