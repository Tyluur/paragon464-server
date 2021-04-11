package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;

public class Spinning extends AbstractSkillAction {

    protected static final int SPINNING_WHEEL = 6;

    protected static final String[] SPIN_FINISH = {"Ball of Wool", "Bow String", "Crossbow String"};

    protected static final Object[][] SPINNING_ITEMS = {
        // finished id, needed id, level, xp, message
        {1759, 1737, 1, 2.5, "Wool"}, {1777, 1779, 10, 15.0, "Flax"}, {9438, 9436, 10, 15.0, "Sinew"}};

    private int index = -1;

    public Spinning(final Player player) {
        this.player = player;
        this.displayInterface();
    }

    public void displayInterface() {
        int k = 2;
        int l = 8;
        String s = "<br><br><br><br>";
        for (int j = 0; j < 3; j++) {
            player.getFrames().itemOnInterface(304, k, 180, (Integer) SPINNING_ITEMS[j][0]);
            player.getFrames().modifyText(s + SPIN_FINISH[j], 304, l);
            l += 4;
            k++;
        }
        player.getFrames().sendChatboxInterface(304);
    }

    public void handleButtons(final int button) {
        int index = (button >= 6 && button <= 8) ? 0
            : (button >= 9 && button <= 12) ? 1 : (button >= 13 && button <= 16) ? 2 : -1;
        if (index == -1)
            return;
        this.index = index;
        player.getInterfaceSettings().closeInterfaces(false);
    }

    @Override
    public boolean canBegin(boolean init) {
        boolean sm = index > -1;
        if (index != -1) {
            if (!player.getInventory().hasItem((Integer) SPINNING_ITEMS[index][1])) {
                if (sm) {
                    player.getFrames().sendMessage("You don't have any " + SPINNING_ITEMS[index][4] + ".");
                }
                return false;
            }
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < (Integer) SPINNING_ITEMS[index][2]) {
                if (sm) {
                    player.getFrames()
                        .sendMessage("You need a Crafting level of " + SPINNING_ITEMS[index][2] + " to spin that.");
                }
                return false;
            }
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        // TODO Auto-generated method stub

    }

    @Override
    public void rewards() {
        if (index != -1) {
            player.playAnimation(896, AnimationPriority.HIGH);
            player.getInventory().deleteItem((Integer) SPINNING_ITEMS[index][1]);
            player.getInventory().addItem((Integer) SPINNING_ITEMS[index][0]);
            player.getSkills().addExperience(SKILL_TYPE(), exp());
        }
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
        return 3;
    }

    @Override
    public double exp() {
        return (double) SPINNING_ITEMS[index][3];
    }
}
