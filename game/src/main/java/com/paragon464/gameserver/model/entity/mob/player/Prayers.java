package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.content.skills.prayer.PrayerData;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags.UpdateFlag;

public class Prayers extends PrayerData {

    public int[] leechBonuses;
    public boolean boostedLeech;
    private Player player;
    private int headIcon = -1;
    private int pkIcon = -1;
    private boolean protectingItem;
    private boolean[] prayers;

    public Prayers(Player player) {
        this.player = player;
        this.prayers = new boolean[46];
        this.leechBonuses = new int[11];
    }

    public boolean togglePrayers(int button) {
        return togglePrayers(getPrayer(player, button), false);
    }

    public boolean togglePrayers(PRAYERS_DATA data, boolean quickPrays) {
        if (data == null) {
            return false;
        }
        if (player.getCombatState().isDead()) {
            boolean activePrayer = getActivePrayers()[data.getIndex()];
            player.getFrames().sendVarp(data.getConfig(), activePrayer ? 1 : 0);
            return false;
        }
        if (player.getSettings().getPrayerPoints() <= 0) {
            togglePrayer(data.getIndex(), false);
            return false;
        }
        if (isHeadIconPrayer(data.getIndex())) {
            if (player.getAttributes().isSet("disableprotectionprayers")) {
                player.getFrames().sendMessage("You've been injured and cannot use protection prayers!");
                togglePrayer(data.getIndex(), false);
                return false;
            }
        }
        DuelBattle duel_battle = player.getVariables().getDuelBattle();
        if (duel_battle != null) {
            if (duel_battle.prayersDisabled()) {
                player.getFrames().sendMessage("Your prayers are disabled in this duel.");
                togglePrayer(data.getIndex(), false);
                return false;
            }
        }
        int requiredPrayer = data.getReq();
        String prayerName = data.getName();
        if (player.getSkills().getLevel(SkillType.PRAYER) < requiredPrayer) {
            togglePrayer(data.getIndex(), false);
            player.getFrames()
                .sendMessage("You need a Prayer level of " + requiredPrayer + " to use " + prayerName + ".");
            return false;
        }
        if (data.getIndex() >= 24 && data.getIndex() <= 25) {
            if (!player.getAttributes().isSet("prayers_unlocked")) {
                togglePrayer(data.getIndex(), false);
                player.getFrames()
                    .sendMessage("You must unlock these Prayers in Nightmare Zone.");
                return false;
            }
        }
        handlePrayerExecuting(data, quickPrays);
        return true;
    }

    public boolean[] getActivePrayers() {
        return prayers;
    }

    public void togglePrayer(int indexOfPrayer, boolean active) {
        boolean protectItem = (indexOfPrayer == 10);
        if (protectItem) {
            setProtectingItem(active);
        }
        PRAYERS_DATA data = PrayerData.getPrayers(indexOfPrayer);
        int config = data.getConfig();
        player.getFrames().sendVarp(config, active ? 1 : 0);
        this.prayers[indexOfPrayer] = active;
        if (!active) {
            switch (data.getIndex()) {
                case 27://sap melee
                    leechBonuses[0] = 0;
                    break;
                case 28://sap range
                    leechBonuses[1] = 0;
                    break;
                case 29://sap mage
                    leechBonuses[2] = 0;
                    break;
                case 35://leech attk
                    leechBonuses[3] = 0;
                    break;
                case 36://leech range
                    leechBonuses[4] = 0;
                    break;
                case 37://leech magic
                    leechBonuses[5] = 0;
                    break;
                case 38://leech def
                    leechBonuses[6] = 0;
                    break;
                case 39://leech str
                    leechBonuses[7] = 0;
                    break;
                case 45://Turmoil
                    leechBonuses[8] = 0;
                    leechBonuses[9] = 0;
                    leechBonuses[10] = 0;
                    break;
            }
        }
    }

    public boolean isHeadIconPrayer(int index) {
        return (index == 16 || index == 17 || index == 18 || index == 21 || index == 22 || index == 23) ||
            (index >= 32 && index <= 34) || (index == 44);
    }

    public void handlePrayerExecuting(PRAYERS_DATA data, boolean quickPrays) {
        boolean turningOn = !getActivePrayers()[data.getIndex()];
        togglePrayer(data.getIndex(), turningOn);
        int[] prayersToTurnOff = data.getPrayersOff();
        int headIcon = -1;
        if (turningOn) {
            switch (data.getIndex()) {
                case 16: // Protect from magic
                    headIcon = 2;
                    break;
                case 17: // Protect from missles
                    headIcon = 1;
                    break;
                case 18: // Protect from melee
                    headIcon = 0;
                    break;
                case 21: // Retribution
                    headIcon = 3;
                    break;
                case 22: // Redemption
                    headIcon = 5;
                    break;
                case 23: // Smite
                    headIcon = 4;
                    break;
                case 26://Curse - protect item
                    player.playAnimation(12567, AnimationPriority.HIGH);
                    player.playGraphic(2213);
                    break;
                case 31://Berserker
                    player.playAnimation(12589, AnimationPriority.HIGH);
                    player.playGraphic(2266);
                    break;
                case 32://Deflect magic
                    headIcon = 13;
                    break;
                case 33://Deflect missles
                    headIcon = 14;
                    break;
                case 34://Deflect melee
                    headIcon = 12;
                    break;
                case 44://Soul split
                    headIcon = 20;
                    break;
                case 45://Turmoil
                    player.playAnimation(12565, AnimationPriority.HIGH);
                    player.playGraphic(2226);
                    break;
            }
        }
        if (isHeadIconPrayer(data.getIndex())) {
            setHeadIcon(headIcon);
        }
        if (turningOn) {
            if (prayersToTurnOff != null) {
                activatePrayer(data.getIndex(), prayersToTurnOff);
            }
        }
    }

    /**
     * Turns off prayers; use indexes.
     *
     * @param prayers
     */
    public void activatePrayer(int index, int[] prayers) {
        for (int i : prayers) {
            togglePrayer(i, false);
            if (!isHeadIconPrayer(index)) {// is wat we are turning on, a head
                // icon prayer?
                if (isHeadIconPrayer(i)) {// if not, and prayers to turn off is,
                    // we set our head icon off.
                    setHeadIcon(-1);
                }
            }
        }
    }

    public void turnPrayersOff(int[] prayers) {
        for (int i : prayers) {
            togglePrayer(i, false);
            if (isHeadIconPrayer(i)) {
                setHeadIcon(-1);
            }
        }
    }

    public boolean isProtectionPrayer(int index) {
        return index >= 16 && index <= 18 || index >= 32 && index <= 34;
    }

    public int getPkIcon() {
        return pkIcon;
    }

    public void setPkIcon(int i) {
        this.pkIcon = i;
        player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
        player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
    }

    public boolean isPrayingMagic() {
        return isPrayerActive("Protect from Magic") || isPrayerActive("Deflect Magic");
    }

    public boolean isPrayerActive(String name) {
        PRAYERS_DATA data = PrayerData.getPrayers(name);
        if (data != null) {
            return getActivePrayers()[data.getIndex()];
        }
        return false;
    }

    public boolean isPrayingRange() {
        return isPrayerActive("Protect from Ranged") || isPrayerActive("Deflect Missiles");
    }

    public boolean isPrayingMelee() {
        return isPrayerActive("Protect from Melee") || isPrayerActive("Deflect Melee");
    }

    public void handleDraining() {
        if (prayersOff()) {
            return;
        }
        if (player.getCombatState().isDead()) {
            return;
        }
        boostedLeech = false;
        final int prayerDrainResistance = 60 + (player.getBonuses().getBonus(11) * 2);
        int totalDrain = 0;
        for (int i = 0; i < prayers.length; i++) {
            if (getActivePrayers()[i]) {
                totalDrain += PrayerData.getPrayers(i).getDrain();
            }
        }
        player.getAttributes().addInt("drain_counter", totalDrain);
        if (player.getAttributes().getInt("drain_counter") > prayerDrainResistance) {
            player.getAttributes().subtractInt("drain_counter", prayerDrainResistance);
            player.getSettings().decreasePrayerPoints(1);
            if (player.getSettings().getPrayerPoints() <= 0) {
                if (!player.getCombatState().isDead()) {
                    player.getFrames().sendMessage("You have run out of Prayer points, please recharge your prayer at an altar.");
                    deactivateAllPrayers();
                }
            }
        }
    }

    public boolean prayersOff() {
        for (int i = 0; i < getActivePrayers().length; i++) {
            if (getActivePrayers()[i]) {
                return false;
            }
        }
        return true;
    }

    public void deactivateAllPrayers() {
        setHeadIcon(-1);
        for (int i = 0; i < this.prayers.length; i++) {
            togglePrayer(i, false);
        }
        leechBonuses = new int[11];
        player.getAttributes().set("quick_prayer_toggled", false);
        player.getFrames().sendVarp(602, 0);
    }

    public boolean quickPrayersOff() {
        for (int i = 0; i < getActivePrayers().length; i++) {
            if (getActivePrayers()[i]) {
                int prayer_index = -1;
                if (player.getSettings().isCursesEnabled()) {
                    prayer_index = i - 26;
                    if ((player.getAttributes().getInt("quick_prayers_value") & (1 << prayer_index)) != 0) {
                        if (player.getPrayers().prayers[prayer_index + 26]) {
                            return false;
                        }
                    }
                } else {
                    prayer_index = i;
                    if ((player.getAttributes().getInt("quick_prayers_value") & (1 << prayer_index)) != 0) {
                        if (player.getPrayers().prayers[prayer_index]) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isProtectingItem() {
        return protectingItem;
    }

    public void setProtectingItem(boolean protectingItem) {
        this.protectingItem = protectingItem;
    }

    public void increaseLeechBonus(int bonus) {
        leechBonuses[bonus]++;
    }

    public void increaseTurmoilBonus(Mob other) {
        if (other.isNPC()) {
            return;
        }
        Player p2 = (Player) other;
        leechBonuses[8] = (int) ((100 * Math.floor(0.15 * Skills
            .getLevelForExperience(p2.getSkills().getExperience(SkillType.ATTACK)))) / Skills.getLevelForExperience(
            p2.getSkills().getExperience(SkillType.ATTACK)));
        leechBonuses[9] = (int) ((100 * Math.floor(0.15 * Skills
            .getLevelForExperience(p2.getSkills().getExperience(SkillType.DEFENCE))) / Skills
            .getLevelForExperience(p2.getSkills().getExperience(SkillType.DEFENCE))));
        leechBonuses[10] = (int) ((100 * Math.floor(0.1 * Skills
            .getLevelForExperience(p2.getSkills().getExperience(SkillType.STRENGTH)))) / Skills
            .getLevelForExperience(p2.getSkills().getExperience(SkillType.STRENGTH)));
    }

    public boolean reachedMax(int bonus) {
        return bonus != 8 && bonus != 9 && bonus != 10 && leechBonuses[bonus] >= 20;
    }
}
