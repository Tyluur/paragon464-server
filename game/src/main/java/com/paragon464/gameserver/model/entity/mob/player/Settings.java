package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.data.Specials;
import com.paragon464.gameserver.model.content.minigames.NightMareZone;

/**
 * Contains client-side settings.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class Settings {

    public int windowType = 0;
    private Player player;
    private double energy = 100;

    private boolean chat, split, mouse, aid, achievementDiaryTab, autoRetaliate;
    private int magicType;
    private double prayerPoints;

    private boolean specialOn;
    private int specialEnergy = 100;
    private boolean cursesEnabled;

    private boolean newHp, newMarkers, newMenus, newHits, newKeys, newFonts;

    public Settings(Player player) {
        this.player = player;
        this.setDefaultSettings();
    }

    public void setDefaultSettings() {
        prayerPoints = 1;
        magicType = 1;
        achievementDiaryTab = false;
        chat = true;
        split = false;
        mouse = true;
        aid = false;
    }

    public Settings() {
    }

    public void refresh() {
        // player.getFrames().sendInterfaceConfig(261, 51,
        // player.getWalkingQueue().isRunningToggled());
        // player.getFrames().sendInterfaceConfig(261, 52,
        // !player.getWalkingQueue().isRunningToggled());
        player.getFrames().sendVarp(171, !chat ? 1 : 0);
        player.getFrames().sendVarp(287, split ? 1 : 0);
        // player.getFrames().sendInterfaceConfig(261, 55, split);
        // player.getFrames().sendInterfaceConfig(261, 56, !split);
        if (split) {
            player.getFrames().sendBlankClientScript(83, "s");
        }
        player.getFrames().sendVarp(170, !mouse ? 1 : 0);
        player.getFrames().sendVarp(427, aid ? 1 : 0);
        // player.getFrames().sendInterfaceConfig(261, 59, aid);
        // player.getFrames().sendInterfaceConfig(261, 60, !aid);
        player.getFrames().sendVarp(172, !autoRetaliate ? 1 : 0);
        if (player.getCombatState().isSkulled()) {
            player.getPrayers().setPkIcon(0);
        }
        refreshToggles();
    }

    private void refreshToggles() {// TODO - send to client
        boolean old_hp = !isNewHp();
        player.getFrames().sendChildColor(584, 12, old_hp ? 31 : 0, !old_hp ? 31 : 0, 0);
        player.getFrames().modifyText(isNewHp() ? "New" : "Old", 584, 12);
        boolean old_markers = !isNewMarkers();
        player.getFrames().sendChildColor(584, 13, old_markers ? 31 : 0, !old_markers ? 31 : 0, 0);
        player.getFrames().modifyText(isNewMarkers() ? "New" : "Old", 584, 13);
        boolean old_menus = !isNewMenus();
        player.getFrames().sendChildColor(584, 14, old_menus ? 31 : 0, !old_menus ? 31 : 0, 0);
        player.getFrames().modifyText(!old_menus ? "New" : "Old", 584, 14);
        boolean old_hits = !isNewHits();
        player.getFrames().sendChildColor(584, 15, old_hits ? 31 : 0, !old_hits ? 31 : 0, 0);
        player.getFrames().modifyText(!old_hits ? "On" : "Off", 584, 15);
        boolean old_keys = !isNewKeys();
        player.getFrames().sendChildColor(584, 16, old_keys ? 31 : 0, !old_keys ? 31 : 0, 0);
        player.getFrames().modifyText(!old_keys ? "New" : "Old", 584, 16);
        boolean old_fonts = !isNewFonts();
        player.getFrames().sendChildColor(584, 17, old_fonts ? 31 : 0, !old_fonts ? 31 : 0, 0);
        player.getFrames().modifyText(!old_fonts ? "New" : "Old", 584, 17);
        player.getFrames().sendMessage("", isNewHp() ? 26 : 27);
        player.getFrames().sendMessage("", isNewMarkers() ? 28 : 29);
        player.getFrames().sendMessage("", isNewMenus() ? 22 : 23);
        player.getFrames().sendMessage("", isNewHits() ? 24 : 25);
        player.getFrames().sendMessage("", isNewKeys() ? 20 : 21);
        player.getFrames().sendMessage("", isNewFonts() ? 30 : 31);
    }

    public boolean isNewHp() {
        return newHp;
    }

    public void setNewHp(boolean newHp) {
        this.newHp = newHp;
    }

    public boolean isNewMarkers() {
        return newMarkers;
    }

    public void setNewMarkers(boolean newMarkers) {
        this.newMarkers = newMarkers;
    }

    public boolean isNewMenus() {
        return newMenus;
    }

    public void setNewMenus(boolean newMenus) {
        this.newMenus = newMenus;
    }

    public boolean isNewHits() {
        return newHits;
    }

    public void setNewHits(boolean newHits) {
        this.newHits = newHits;
    }

    public boolean isNewKeys() {
        return newKeys;
    }

    public void setNewKeys(boolean newKeys) {
        this.newKeys = newKeys;
    }

    public boolean isNewFonts() {
        return newFonts;
    }

    public void setNewFonts(boolean newFonts) {
        this.newFonts = newFonts;
    }

    public void sendTogglables(boolean sendTab) {
        if (sendTab) {
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 76 : 97, 584);
        }
    }

    public boolean isInResizable() {
        return windowType > 1;
    }

    public void handleTogglables(int button) {
        switch (button) {
            case 20:
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 76 : 97, 261);
                break;
            case 0:// New hp
                this.setNewHp(!isNewHp());
                player.getFrames().sendMessage("", isNewHp() ? 26 : 27);
                refreshToggles();
                break;
            case 1:// new markers
                this.setNewMarkers(!isNewMarkers());
                player.getFrames().sendMessage("", isNewMarkers() ? 28 : 29);
                refreshToggles();
                break;
            case 2:// new menus
                this.setNewMenus(!isNewMenus());
                player.getFrames().sendMessage("", isNewMenus() ? 22 : 23);
                refreshToggles();
                break;
            case 3:// new hits
                this.setNewHits(!isNewHits());
                player.getFrames().sendMessage("", isNewHits() ? 24 : 25);
                refreshToggles();
                break;
            case 4:// new keys
                this.setNewKeys(!isNewKeys());
                player.getFrames().sendMessage("", isNewKeys() ? 20 : 21);
                refreshToggles();
                break;
            case 5:// new fonts
                this.setNewFonts(!isNewFonts());
                player.getFrames().sendMessage("", isNewFonts() ? 30 : 31);
                refreshToggles();
                break;
        }
    }

    public boolean isAchievementDiaryTab() {
        return achievementDiaryTab;
    }

    public void setAchievementDiaryTab(boolean b) {
        this.achievementDiaryTab = b;
    }

    public boolean isMouseTwoButtons() {
        return mouse;
    }

    public void setMouseTwoButtons(boolean mouse) {
        this.mouse = mouse;
    }

    public boolean isChatEffectsEnabled() {
        return chat;
    }

    public void setChatEffectsEnabled(boolean chat) {
        this.chat = chat;
    }

    public boolean isPrivateChatSplit() {
        return split;
    }

    public boolean isAcceptAidEnabled() {
        return aid;
    }

    public void setAcceptAidEnabled(boolean aid) {
        this.aid = aid;
    }

    public void setPrivateChatSplit(boolean split, boolean send) {
        this.split = split;
        if (split && send) {
            player.getFrames().sendClientScript(83, new Object[0], "");
        }
    }

    public void setAutoRetaliate(boolean autoRetaliate) {
        this.autoRetaliate = autoRetaliate;
    }

    public void toggleAutoRetaliate() {
        this.autoRetaliate = !autoRetaliate;
        player.getFrames().sendVarp(172, !autoRetaliate ? 1 : 0);
    }

    public boolean isAutoRetaliating() {
        return autoRetaliate;
    }

    public int getMagicType() {
        return magicType;
    }

    public void setMagicType(int magicType) {
        this.magicType = magicType;
    }

    public void increasePrayerPoints(final double p) {
        final int prayerLevel = player.getSkills().getLevel(SkillType.PRAYER);
        double prayerPoints = getPrayerPoints() + p;

        if (prayerPoints > prayerLevel) {
            prayerPoints = prayerLevel;
        }

        setPrayerPoints(prayerPoints);
        player.getFrames().sendSkillLevel(SkillType.PRAYER);
    }

    public double getPrayerPoints() {
        return prayerPoints;
    }

    public void setPrayerPoints(final double p) {
        this.prayerPoints = p;
    }

    public void decreasePrayerPoints(double modification) {
        int lvlBefore = (int) Math.ceil(prayerPoints);
        if (prayerPoints > 0) {
            prayerPoints = (prayerPoints - modification <= 0 ? 0 : prayerPoints - modification);
        }
        int lvlAfter = (int) Math.ceil(prayerPoints);
        if (lvlBefore - lvlAfter >= 1) {
            player.getSkills().setCurrentLevel(SkillType.PRAYER, lvlAfter);
        }
    }

    public void setSpecialAmount(int specialAmount, boolean refresh) {
        this.specialEnergy = specialAmount;
        if (refresh)
            refreshBar();
    }

    public void refreshBar() {
        player.getFrames().sendVarp(300, specialEnergy * 10);
        player.getFrames().sendVarp(301, specialOn ? 1 : 0);
    }

    public void deductSpecialAmount(double amount) {
        boolean deduct = true;
        NightMareZone nightmare_zone = player.getAttributes().get("nightmare_zone");
        if (nightmare_zone != null) {
            if (nightmare_zone.isInfiniteSpecialEnabled()) {
                deduct = false;
            }
        }
        if (deduct) {
            this.specialEnergy -= amount;
        }
        refreshBar();
    }

    public void increaseSpecialAmount(int amount) {
        this.specialEnergy += amount;
        if (this.specialEnergy > 100) {
            this.specialEnergy = 100;
        }
        refreshBar();
    }

    public void resetSpecial() {
        specialEnergy = 100;
        specialOn = false;
        refreshBar();
    }

    public void toggleSpecBar() {
        int wep = player.getEquipment().getItemInSlot(3);
        int currentPower = player.getSettings().getSpecialAmount();
        double neededPower = Specials.getRequiredAmount(wep);
        if (neededPower > currentPower) {
            return;
        }
        if (wep == 4153) {
            Mob victim = player.getCombatState().getLastTarget();
            if (victim == null) {
                player.getFrames().sendMessage(
                    "Warning: Since the maul's special is an instant attack, it will be wasted when used");
                player.getFrames().sendMessage("on a first strike.");
                return;
            }
            specialOn = true;
            player.getCombatState().setIgnoringCombatCycles(true);
            CombatAction.beginCombat(player, victim);
            CombatAction.process(player);
            specialOn = false;
            player.getCombatState().setIgnoringCombatCycles(false);
            return;
        }
        specialOn = !specialOn;
        refreshBar();
    }

    public int getSpecialAmount() {
        return specialEnergy;
    }

    public void setSpecial(boolean toggle) {
        this.specialOn = toggle;
    }

    public boolean isSpecOn() {
        return specialOn;
    }

    public void toggleRun() {
        toggleRun(!player.getWalkingQueue().isRunningToggled());
    }

    /**
     * Toggles the player's run status.
     *
     * @param running
     */
    public void toggleRun(final boolean running) {
        player.getFrames().sendVarp(SettingsConstants.RUN_TOGGLE_VARP, running ? 1 : 0);
        player.getWalkingQueue().setRunningQueue(running);
        player.getWalkingQueue().setRunningToggled(running);
    }

    public void increaseRunEnergy() {
        final double energyModifier = player.getSkills().getCurrentLevel(SkillType.AGILITY) * SettingsConstants.RUN_ENERGY_RESTORE_PER_LEVEL;
        final double energyToAdd = energyModifier + SettingsConstants.BASE_RUN_ENERGY_RESTORE_RATE;

        increaseRunEnergy(energyToAdd);
    }

    public void increaseRunEnergy(final double amount) {
        if (getEnergy() + amount > SettingsConstants.MAX_RUN_ENERGY) {
            setEnergy(SettingsConstants.MAX_RUN_ENERGY, true);
        } else {
            setEnergy(getEnergy() + amount, true);
        }
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double runningEnergy, boolean send) {
        this.energy = runningEnergy;
        if (send) {
            player.getFrames().sendEnergy();
        }
    }

    public void decreaseRunEnergy() {
        final double energyModifier = Math.floor((player.getWeight() * SettingsConstants.RUN_ENERGY_DRAIN_PER_KG) * 100.0) / 100.0;
        double energyToDecrease = energyModifier + SettingsConstants.BASE_RUN_ENERGY_DRAIN_RATE;

        if (energyToDecrease < SettingsConstants.MIN_RUN_ENERGY_DRAIN_RATE) {
            energyToDecrease = SettingsConstants.MIN_RUN_ENERGY_DRAIN_RATE;
        } else if (energyToDecrease > SettingsConstants.MAX_RUN_ENERGY_DRAIN_RATE) {
            energyToDecrease = SettingsConstants.MAX_RUN_ENERGY_DRAIN_RATE;
        }
        decreaseRunEnergy(energyToDecrease);
    }

    public void decreaseRunEnergy(final double amount) {
        if (getEnergy() - amount < SettingsConstants.MIN_RUN_ENERGY) {
            setEnergy(SettingsConstants.MIN_RUN_ENERGY, true);
            toggleRun(false);
        } else {
            setEnergy(getEnergy() - amount, true);
        }
    }

    public boolean isInLD() {
        return windowType == 0;
    }

    public boolean isInHD() {
        return windowType == 1;
    }

    public int getWindowScreen() {
        if (isInResizable()) {
            return 606;
        }
        return 548;
    }

    public boolean isCursesEnabled() {
        return cursesEnabled;
    }

    public void toggleCurses(boolean bool) {
        this.cursesEnabled = bool;
    }
}
