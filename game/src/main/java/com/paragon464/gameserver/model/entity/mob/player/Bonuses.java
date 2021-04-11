package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.item.ItemDefinition;

public class Bonuses {

    /**
     * The attack stab bonus identifier.
     */
    public static final int ATTACK_STAB = 0;

    /**
     * The attack slash bonus identifier.
     */
    public static final int ATTACK_SLASH = 1;

    /**
     * The attack crush bonus identifier.
     */
    public static final int ATTACK_CRUSH = 2;

    /**
     * The attack magic bonus identifier.
     */
    public static final int ATTACK_MAGIC = 3;

    /**
     * The attack ranged bonus identifier.
     */
    public static final int ATTACK_RANGED = 4;

    /**
     * The defence stab bonus identifier.
     */
    public static final int DEFENCE_STAB = 5;

    /**
     * The defence slash bonus identifier.
     */
    public static final int DEFENCE_SLASH = 6;

    /**
     * The defence crush bonus identifier.
     */
    public static final int DEFENCE_CRUSH = 7;

    /**
     * The defence magic bonus identifier.
     */
    public static final int DEFENCE_MAGIC = 8;

    /**
     * The defence ranged bonus identifier.
     */
    public static final int DEFENCE_RANGED = 9;

    /**
     * The strength bonus identifier.
     */
    public static final int BONUS_STRENGTH = 10;

    /**
     * The prayer bonus identifier.
     */
    public static final int BONUS_PRAYER = 11;

    /**
     * The ranged strength identifier
     */
    public static final int RANGED_STRENGTH = 12;
    public static final int SIZE = 13;
    private Player player;
    private int[] bonuses = new int[SIZE];

    public Bonuses(Player player) {
        this.player = player;
        this.bonuses = new int[SIZE];
    }

    public static final int getCorrespondingBonus(int bonus) {
        switch (bonus) {
            case ATTACK_CRUSH:
                return DEFENCE_CRUSH;
            case ATTACK_MAGIC:
                return DEFENCE_MAGIC;
            case ATTACK_RANGED:
                return DEFENCE_RANGED;
            case ATTACK_SLASH:
                return DEFENCE_SLASH;
            case ATTACK_STAB:
                return DEFENCE_STAB;
            default:
                return DEFENCE_CRUSH;
        }
    }

    public void recalc() {
        for (int index = 0; index < SIZE; index++) {
            this.bonuses[index] = 0;
        }
        for (int index = 0; index < 14; index++) {
            int item = player.getEquipment().getItemInSlot(index);
            if (item != -1) {
                ItemDefinition def = ItemDefinition.forId(item);
                if (def == null)
                    continue;
                if (def.equipmentDefinition == null)
                    continue;
                //attack bonuses
                this.bonuses[0] += def.equipmentDefinition.getOffensiveStab();
                this.bonuses[1] += def.equipmentDefinition.getOffensiveSlash();
                this.bonuses[2] += def.equipmentDefinition.getOffensiveCrush();
                this.bonuses[3] += def.equipmentDefinition.getOffensiveMagic();
                this.bonuses[4] += def.equipmentDefinition.getOffensiveRanged();
                //defence bonuses
                this.bonuses[5] += def.equipmentDefinition.getDefensiveStab();
                this.bonuses[6] += def.equipmentDefinition.getDefensiveSlash();
                this.bonuses[7] += def.equipmentDefinition.getDefensiveCrush();
                this.bonuses[8] += def.equipmentDefinition.getDefensiveMagic();
                this.bonuses[9] += def.equipmentDefinition.getDefensiveRanged();
                //other
                this.bonuses[10] += def.equipmentDefinition.getOffensiveStrength();
                this.bonuses[11] += def.equipmentDefinition.getPrayerBonus();
                this.bonuses[12] += def.equipmentDefinition.getRangedStrength();
            }
        }
    }

    public void refreshStrings() {
        //attack bonuses
        player.getFrames().modifyText(("Stab" + ": " + (this.bonuses[ATTACK_STAB] >= 0 ? "+" : "")
            + this.bonuses[ATTACK_STAB]), 465, 108);
        player.getFrames().modifyText(("Slash" + ": " + (this.bonuses[ATTACK_SLASH] >= 0 ? "+" : "")
            + this.bonuses[ATTACK_SLASH]), 465, 109);
        player.getFrames().modifyText(("Crush" + ": " + (this.bonuses[ATTACK_CRUSH] >= 0 ? "+" : "")
            + this.bonuses[ATTACK_CRUSH]), 465, 110);
        player.getFrames().modifyText(("Magic" + ": " + (this.bonuses[ATTACK_MAGIC] >= 0 ? "+" : "")
            + this.bonuses[ATTACK_MAGIC]), 465, 111);
        player.getFrames().modifyText(("Ranged" + ": " + (this.bonuses[ATTACK_RANGED] >= 0 ? "+" : "")
            + this.bonuses[ATTACK_RANGED]), 465, 112);
        //defence bonuses
        player.getFrames().modifyText(("Stab" + ": " + (this.bonuses[DEFENCE_STAB] >= 0 ? "+" : "")
            + this.bonuses[DEFENCE_STAB]), 465, 113);
        player.getFrames().modifyText(("Slash" + ": " + (this.bonuses[DEFENCE_SLASH] >= 0 ? "+" : "")
            + this.bonuses[DEFENCE_SLASH]), 465, 114);
        player.getFrames().modifyText(("Crush" + ": " + (this.bonuses[DEFENCE_CRUSH] >= 0 ? "+" : "")
            + this.bonuses[DEFENCE_CRUSH]), 465, 115);
        player.getFrames().modifyText(("Magic" + ": " + (this.bonuses[DEFENCE_MAGIC] >= 0 ? "+" : "")
            + this.bonuses[DEFENCE_MAGIC]), 465, 116);
        player.getFrames().modifyText(("Ranged" + ": " + (this.bonuses[DEFENCE_RANGED] >= 0 ? "+" : "")
            + this.bonuses[DEFENCE_RANGED]), 465, 117);
        //other
        player.getFrames().modifyText(("Strength" + ": " + (this.bonuses[BONUS_STRENGTH] >= 0 ? "+" : "")
            + this.bonuses[BONUS_STRENGTH]), 465, 119);
        player.getFrames().modifyText(("Prayer" + ": " + (this.bonuses[BONUS_PRAYER] >= 0 ? "+" : "")
            + this.bonuses[BONUS_PRAYER]), 465, 120);
        player.getFrames().modifyText(("Ranged Str" + ": " + (this.bonuses[RANGED_STRENGTH] >= 0 ? "+" : "")
            + this.bonuses[RANGED_STRENGTH]), 465, 125);
    }

    public int[] getBonuses() {
        return bonuses;
    }

    public int getBonus(int i) {
        return bonuses[i];
    }
}
