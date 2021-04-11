package com.paragon464.gameserver.model.entity.mob.npc;

import java.util.ArrayList;
import java.util.List;

public class NPCBonuses {

    public static List<NPCBonuses> definitions = new ArrayList<>();
    public int id;
    public int defensiveStab, defensiveSlash, defensiveCrush, defensiveRanged, defensiveMagic = 0;
    public int offensiveStab, offensiveSlash, offensiveCrush, offensiveMagic, offensiveRanged, offensiveAttack, offensiveStrength = 0;

    public NPCBonuses(int id) {
        this.id = id;
    }

    public static NPCBonuses forId(int id) {
        for (NPCBonuses defs : definitions) {
            if (defs.id == id) {
                return defs;
            }
        }
        return new NPCBonuses(id);
    }

    public int getDefensiveStab() {
        return defensiveStab;
    }

    public void setDefensiveStab(int defensiveStab) {
        this.defensiveStab = defensiveStab;
    }

    public int getDefensiveSlash() {
        return defensiveSlash;
    }

    public void setDefensiveSlash(int defensiveSlash) {
        this.defensiveSlash = defensiveSlash;
    }

    public int getDefensiveCrush() {
        return defensiveCrush;
    }

    public void setDefensiveCrush(int defensiveCrush) {
        this.defensiveCrush = defensiveCrush;
    }

    public int getDefensiveRanged() {
        return defensiveRanged;
    }

    public void setDefensiveRanged(int defensiveRanged) {
        this.defensiveRanged = defensiveRanged;
    }

    public int getDefensiveMagic() {
        return defensiveMagic;
    }

    public void setDefensiveMagic(int defensiveMagic) {
        this.defensiveMagic = defensiveMagic;
    }

    public int getOffensiveStab() {
        return offensiveStab;
    }

    public void setOffensiveStab(int offensiveStab) {
        this.offensiveStab = offensiveStab;
    }

    public int getOffensiveSlash() {
        return offensiveSlash;
    }

    public void setOffensiveSlash(int offensiveSlash) {
        this.offensiveSlash = offensiveSlash;
    }

    public int getOffensiveCrush() {
        return offensiveCrush;
    }

    public void setOffensiveCrush(int offensiveCrush) {
        this.offensiveCrush = offensiveCrush;
    }

    public int getOffensiveMagic() {
        return offensiveMagic;
    }

    public void setOffensiveMagic(int offensiveMagic) {
        this.offensiveMagic = offensiveMagic;
    }

    public int getOffensiveRanged() {
        return offensiveRanged;
    }

    public void setOffensiveRanged(int offensiveRanged) {
        this.offensiveRanged = offensiveRanged;
    }

    public int getOffensiveAttack() {
        return offensiveAttack;
    }

    public void setOffensiveAttack(int offensiveAttack) {
        this.offensiveAttack = offensiveAttack;
    }

    public int getOffensiveStrength() {
        return offensiveStrength;
    }

    public void setOffensiveStrength(int offensiveStrength) {
        this.offensiveStrength = offensiveStrength;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
