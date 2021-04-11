package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;

public class LeatherMaking extends AbstractSkillAction {

    public static final int[] UNTANNED_HIDE = {1753, 1751, 1749, 1747, 1739};
    public static final int[] TANNED_HIDE = {1745, 2505, 2507, 2509, 1741};
    protected static final int NEEDLE = 1733;
    protected static final int THREAD = 1734;
    protected static final int COWHIDE = 1739;
    protected static final Object[][] NORMAL_LEATHER = {
        // finished item, level, xp, name
        {1129, 14, 25.0, "Leather body"}, {1059, 1, 13.8, "Leather gloves"}, {1061, 7, 16.3, "Leather boots"},
        {1063, 11, 22.0, "Leather vambraces"}, {1095, 18, 27.0, "Leather chaps"}, {1169, 38, 37.0, "Coif"},
        {1167, 9, 18.5, "Leather cowl"},};
    protected static final Object[][] LEATHER_ITEMS = {
        // finished item, level, xp, # of hides, name
        {1135, 63, 186.0, 3, "Green body"}, {2499, 71, 210.0, 3, "Blue body"},
        {2501, 77, 234.0, 3, "Red body"}, {2503, 84, 258.0, 3, "Black body"},

        {1065, 57, 62.0, 1, "Green vambraces"}, {2487, 66, 70.0, 1, "Blue vambraces"},
        {2489, 73, 78.0, 1, "Red vambraces"}, {2491, 79, 86.0, 1, "Black vampbraces"},

        {1099, 60, 124.0, 2, "Green chaps"}, {2493, 68, 140.0, 2, "Blue chaps"},
        {2495, 75, 156.0, 2, "Red chaps"}, {2497, 82, 172.0, 2, "Black chaps"},};
    private int leatherType = -1;
    private int index = -1;

    public LeatherMaking(final Player player, Item used, Item usedOn) {
        this.player = player;
        for (int j = 0; j < TANNED_HIDE.length; j++) {
            if (used.getId() == TANNED_HIDE[j] && usedOn.getId() == NEEDLE) {
                leatherType = j;
            }
        }
        if (leatherType == -1)
            return;
        this.displayInterface();
    }

    private void displayInterface() {
        if (leatherType == 4) {
            player.getInterfaceSettings().openInterface(154);
            return;
        }
        int i = leatherType;
        int k = 2;
        int l = 8;
        String s = "<br><br><br><br>";
        for (int j = 0; j < 3; j++) {
            player.getFrames().itemOnInterface(304, k, 180, (Integer) LEATHER_ITEMS[i][0]);
            player.getFrames().modifyText(s + LEATHER_ITEMS[i][4], 304, l);
            l += 4;
            i += 4;
            k++;
        }
        player.getFrames().sendChatboxInterface(304);
    }

    public void handleButtons(final int interId, final int button) {
        if (interId == 304) {// chatbox leather making
            boolean body = (button >= 5 && button <= 8);
            boolean vambs = (button >= 9 && button <= 12);
            boolean chaps = (button >= 13 && button <= 16);
            index = body ? leatherType : chaps ? 8 + leatherType : vambs ? 4 + leatherType : -1;
        } else if (interId == 154) {// normal leather making
            boolean body = (button >= 124 && button <= 125 || button >= 114 && button <= 115);
            boolean gloves = ((button >= 126 && button <= 127) || button == 108 || button == 116);
            boolean boots = ((button >= 128 && button <= 129) || button == 109 || button == 117);
            boolean vambs = ((button >= 130 && button <= 131) || button == 110 || button == 118);
            boolean chaps = ((button >= 132 && button <= 133) || button == 111 || button == 119);
            boolean coif = ((button >= 134 && button <= 135) || button == 112 || button == 120);
            boolean cowl = ((button >= 136 && button <= 137) || button == 113 || button == 121);
            index = body ? 0 : gloves ? 1 : boots ? 2 : vambs ? 3 : chaps ? 4 : coif ? 5 : cowl ? 6 : -1;
        }
        player.getInterfaceSettings().closeInterfaces(false);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (leatherType == -1)
            return false;
        if (index > -1) {
            if (leatherType == 4) {// normal leather making
                if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < (int) NORMAL_LEATHER[index][1]) {
                    player.getFrames().sendMessage(
                        "You need a Crafting level of " + NORMAL_LEATHER[index][1] + " to make this.");
                    return false;
                }
                if (!player.getInventory().hasItem(TANNED_HIDE[4])) {
                    player.getFrames().sendMessage("You need some leather.");
                    return false;
                }
                if (!player.getInventory().hasItem(NEEDLE)) {
                    player.getFrames().sendMessage("You need a Needle to craft leather.");
                    return false;
                }
                if (!player.getInventory().hasItem(THREAD)) {
                    player.getFrames().sendMessage("You need a Needle to craft leather.");
                    return false;
                }
            } else {// dhide making
                if (player.getSkills().getCurrentLevel(SkillType.CRAFTING) < (int) LEATHER_ITEMS[index][1]) {
                    player.getFrames().sendMessage(
                        "You need a Crafting level of " + LEATHER_ITEMS[index][1] + " to make this.");
                    return false;
                }
                if (!player.getInventory().hasItem(NEEDLE)) {
                    player.getFrames().sendMessage("You need a Needle to craft leather.");
                    return false;
                }
                if (!player.getInventory().hasItemAmount(THREAD, (Integer) LEATHER_ITEMS[index][3])) {
                    player.getFrames().sendMessage("You need " + LEATHER_ITEMS[index][3] + " threads to craft this.");
                    return false;
                }
                if (!player.getInventory().hasItemAmount(TANNED_HIDE[leatherType], (int) LEATHER_ITEMS[index][3])) {
                    String name = (String) LEATHER_ITEMS[index][4];
                    player.getFrames().sendMessage("You need atleast " + LEATHER_ITEMS[index][3]
                        + " dragonhides to craft " + name + ".");
                    return false;
                }
            }
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        if (index > -1) {
            player.playAnimation(1249, AnimationPriority.HIGH);
        }
    }

    @Override
    public void rewards() {
        if (index > -1) {
            if (leatherType == 4) {
                player.getInventory().deleteItem(THREAD);
                player.getInventory().deleteItem(TANNED_HIDE[4]);
                player.getInventory().addItem((int) NORMAL_LEATHER[index][0]);
                player.getInventory().refresh();
            } else {
                for (int i = 0; i < (int) LEATHER_ITEMS[index][3]; i++) {
                    player.getInventory().deleteItem(TANNED_HIDE[leatherType]);
                }
                player.getInventory().deleteItem(THREAD, (int) LEATHER_ITEMS[index][3]);
                player.getInventory().addItem((int) LEATHER_ITEMS[index][0]);
                player.getInventory().refresh();
            }
            player.getSkills().addExperience(SKILL_TYPE(), exp());
        }
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.CRAFTING;
    }

    @Override
    public short speed() {
        return 2;
    }

    @Override
    public double exp() {
        return leatherType == 4 ? (double) NORMAL_LEATHER[index][2]
            : (double) LEATHER_ITEMS[index][2];
    }
}
