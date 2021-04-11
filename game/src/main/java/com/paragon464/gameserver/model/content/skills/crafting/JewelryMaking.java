package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;

public class JewelryMaking extends AbstractSkillAction {

    protected static final int NULL_RING = 1647;
    protected static final int GOLD_BAR = 2357;
    protected static final Object[][] GEMS = {
        // uncut, cut, level, xp, name, cut emote
        {1625, 1609, 1, 15, "Opal", 886}, {1627, 1611, 13, 20.0, "Jade", 886},
        {1629, 1613, 16, 25.0, "Red topaz", 887}, {1623, 1607, 20, 50.0, "Sapphire", 888},
        {1621, 1605, 27, 67.5, "Emerald", 889}, {1619, 1603, 34, 85.0, "Ruby", 887},
        {1617, 1601, 43, 107.5, "Diamond", 886}, {1631, 1615, 55, 137.5, "Dragonstone", 885},
        {6571, 6573, 67, 167.5, "Onyx", 2717},};
    protected static final int[] NULL_JEWELRY = {1647, 1666, 1685, 11067};
    protected static final int[][] JEWELRY_INTERFACE_VARS = {
        // mould id, id to remove the text, index the images start at
        {1592, 63, 66}, {1597, 110, 113}, {1595, 157, 160}, {11065, 207, 210},};
    protected static final Object[][] RINGS = {{1635, 5, 15.0, "ring"}, {1637, 30, 40.0, "ring"},
        {1639, 27, 55.0, "ring"}, {1641, 34, 70.0, "ring"}, {1643, 43, 85.0, "ring"},
        {1645, 55, 100.0, "ring"}, {6575, 67, 115.0, "ring"},};
    protected static final Object[][] NECKLACES = {{1654, 6, 20.0, "necklace"}, {1656, 22, 55.0, "necklace"},
        {1658, 29, 60.0, "necklace"}, {1660, 40, 75.0, "necklace"}, {1662, 56, 90.0, "necklace"},
        {1664, 72, 105.0, "necklace"}, {6577, 82, 120.0, "necklace"},};
    protected static final Object[][] BRACELETS = {{11069, 7, 25.0, "bracelet"}, {11072, 23, 60.0, "bracelet"},
        {11076, 30, 65.0, "bracelet"}, {11085, 42, 80.0, "bracelet"}, {11092, 58, 95.0, "bracelet"},
        {11115, 74, 110.0, "bracelet"}, {11130, 84, 125.0, "bracelet"},};
    protected static final Object[][] AMULETS = {
        // finished id, level, xp, message
        {1673, 8, 30.0, "amulet"}, {1675, 24, 65.0, "amulet"}, {1677, 31, 70.0, "amulet"},
        {1679, 50, 85.0, "amulet"}, {1681, 70, 100.0, "amulet"}, {1683, 80, 150.0, "amulet"},
        {6579, 90, 165.0, "amulet"},};
    private int reward, delete;
    private double exp;

    public JewelryMaking(final Player player, final int reward, final int delete, final double exp) {
        this.player = player;
        this.reward = reward;
        this.delete = delete;
        this.exp = exp;
    }

    public static void displayInterface(final Player player) {
        for (int i = 0; i < JEWELRY_INTERFACE_VARS.length; i++) {
            if (player.getInventory().hasItem(JEWELRY_INTERFACE_VARS[i][0])) {
                player.getFrames().modifyText("", 446, JEWELRY_INTERFACE_VARS[i][1]);
                sendJewels(player, i);
            }
        }
        player.getInterfaceSettings().openInterface(446);
    }

    private static void sendJewels(Player p, int index) {
        Object[][] items = getItemArray(p, index);
        if (items == null) {
            return;
        }
        int[] sizes = {100, 85, 65, 85};
        int SIZE = sizes[index];
        int interfaceSlot = JEWELRY_INTERFACE_VARS[index][2];
        p.getFrames().itemOnInterface(446, interfaceSlot, SIZE, (Integer) items[0][0]);
        interfaceSlot += 6;
        if (index == 3) {
            interfaceSlot += 1;
        }
        for (int i = 1; i < items.length; i++) {
            for (int j = 0; j < items[i].length; j++) {
                if (p.getInventory().hasItem((Integer) GEMS[i + 2][1])
                    && p.getSkills().getCurrentLevel(SkillType.CRAFTING) >= (Integer) items[i][1]) {
                    p.getFrames().itemOnInterface(446, interfaceSlot, SIZE, (Integer) items[i][0]);
                } else {
                    p.getFrames().itemOnInterface(446, interfaceSlot, SIZE, NULL_JEWELRY[index]);
                }
            }
            interfaceSlot += 6;
        }
    }

    private static Object[][] getItemArray(Player p, int index) {
        switch (index) {
            case 0:
                return RINGS;
            case 1:
                return NECKLACES;
            case 2:
                return AMULETS;
            case 3:
                return BRACELETS;
        }
        return null;
    }

    public static void handleButtons(final Player player, int button) {
        int index = getIndex(button, false);
        if (index == -1)
            return;
        int itemType = getIndex(button, true);
        Object[][] items = getItemArray(player, itemType);
        double exp = (Double) items[index][2];
        int item = (Integer) items[index][0];
        String type = (String) items[index][3];
        int reqLvl = (Integer) items[index][1];
        String gemType = (String) GEMS[index + 2][4];
        String s = index == 3 ? "an" : "a";
        int mouldReq = JEWELRY_INTERFACE_VARS[itemType][0];
        if (player.getSkills().getCurrentLevel(SkillType.CRAFTING) < reqLvl) {
            player.getFrames().sendMessage(
                "You need a Crafting level of " + reqLvl + " to make " + s + " " + gemType + " " + type + ".");
            return;
        }
        if (!player.getInventory().hasItem(mouldReq)) {
            player.getFrames().sendMessage("You need " + s + " " + type + " mould to craft that.");
            return;
        }
        if (!player.getInventory().hasItem(GOLD_BAR)) {
            player.getFrames().sendMessage("You need a Gold bar to craft jewels.");
            return;
        }
        if (index > 0) {
            int cutGem = (Integer) GEMS[index + 2][1];
            if (!player.getInventory().hasItem(cutGem)) {
                player.getFrames().sendMessage("You need a cut " + GEMS[index + 2][4] + " gem.");
                return;
            }
            player.getVariables().skillActionExecuting(new JewelryMaking(player, item, (Integer) GEMS[index + 2][1], exp));
        } else {
            player.getVariables().skillActionExecuting(new JewelryMaking(player, item, GOLD_BAR, exp));
        }
        player.getInterfaceSettings().closeInterfaces(false);
    }

    private static int getIndex(int buttonId, boolean getItemType) {
        int[][] BUTTONS = {{71, 77, 83, 89, 95, 101, 107}, {118, 124, 130, 136, 142, 148, 154},
            {165, 171, 177, 183, 189, 195, 201}, {215, 221, 227, 233, 239, 245, 251},};
        for (int i = 0; i < BUTTONS.length; i++) {
            for (int j = 0; j < BUTTONS[i].length; j++) {
                boolean five = (BUTTONS[i][j] - 1) == buttonId;
                boolean ten = (BUTTONS[i][j] - 2) == buttonId;
                boolean x = (BUTTONS[i][j] - 3) == buttonId;
                boolean otherOption = (five || ten || x);
                if (buttonId == BUTTONS[i][j] || otherOption) {
                    return getItemType ? i : j;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean canBegin(boolean init) {
        if (delete != GOLD_BAR) {
            if (!player.getInventory().hasItem(delete)) {
                return false;
            }
        }
        return player.getInventory().hasItem(GOLD_BAR) && super.canBegin(init);
    }

    @Override
    public void handler() {
        // TODO Auto-generated method stub

    }

    @Override
    public void rewards() {
        player.playAnimation(3243, Animation.AnimationPriority.HIGH);
        if (delete != GOLD_BAR) {
            player.getInventory().deleteItem(delete);
        }
        player.getInventory().deleteItem(GOLD_BAR);
        player.getInventory().addItem(reward);
        player.getInventory().refresh();
        player.getSkills().addExperience(SKILL_TYPE(), exp);
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.CRAFTING;
    }

    @Override
    public short speed() {
        return 1;
    }
}
