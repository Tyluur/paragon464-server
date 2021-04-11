package com.paragon464.gameserver.model.entity.mob.player;

import java.util.HashMap;
import java.util.Map;

public class AttackVars {

    private static Map<String, CombatSkill> combatSkills = new HashMap<>();
    private static Map<String, CombatStyle> combatStyles = new HashMap<>();

    static {
        for (CombatSkill skills : CombatSkill.values()) {
            combatSkills.put(skills.name(), skills);
        }
        for (CombatStyle skills : CombatStyle.values()) {
            combatStyles.put(skills.name(), skills);
        }
    }

    private CombatSkill skill;
    private CombatStyle style;
    private int slot;

    public AttackVars() {
        setDefault();
    }

    public void setDefault() {
        this.skill = CombatSkill.ACCURATE;
        this.style = CombatStyle.CRUSH;
        this.slot = 0;
    }

    public CombatSkill getSkill() {
        return skill;
    }

    public void setSkill(CombatSkill skill) {
        this.skill = skill;
    }

    public void setSkill(String skill) {
        this.skill = combatSkills.get(skill);
    }

    public CombatStyle getStyle() {
        return style;
    }

    public void setStyle(CombatStyle style) {
        this.style = style;
    }

    public void setStyle(String style) {
        this.style = combatStyles.get(style);
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isStyleRanged() {
        return style.equals(CombatStyle.RANGE_ACCURATE) || style.equals(CombatStyle.RANGE_RAPID)
            || style.equals(CombatStyle.RANGE_DEFENSIVE);
    }

    public boolean isStyleMelee() {
        return style.equals(CombatStyle.STAB) || style.equals(CombatStyle.SLASH) || style.equals(CombatStyle.CRUSH);
    }

    public boolean isStyleMage() {
        return style.equals(CombatStyle.MAGIC) || style.equals(CombatStyle.MAGIC_DEFENSIVE);
    }

    public enum CombatSkill {
        ACCURATE, DEFENSIVE, AGGRESSIVE, CONTROLLED, RANGE
    }

    public enum CombatStyle {
        STAB, SLASH, CRUSH, MAGIC, MAGIC_DEFENSIVE, RANGE_ACCURATE, RANGE_RAPID, RANGE_DEFENSIVE
    }
}
