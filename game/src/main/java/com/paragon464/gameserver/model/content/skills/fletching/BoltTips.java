package com.paragon464.gameserver.model.content.skills.fletching;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;

public class BoltTips extends AbstractSkillAction {

    private static final Object[][] GEMS = {
        // cut, bolt, level, xp, name, cut emote
        {1609, 45, 11, 1.5, "Opal", 886}, {1611, 9187, 26, 2.0, "Jade", 886},
        {411, 46, 41, 3.2, "Pearl", 886}, {1613, 9188, 48, 3.9, "Red topaz", 887},
        {1607, 9189, 56, 4.0, "Sapphire", 888}, {1605, 9190, 58, 5.5, "Emerald", 889},
        {1603, 9191, 63, 6.0, "Ruby", 887}, {1601, 9192, 65, 7.0, "Diamond", 886},
        {1615, 9193, 71, 8.2, "Dragonstone", 885}, {6573, 9194, 73, 9.4, "Onyx", 2717},};
    private int gemIndex = -1;

    public BoltTips(final Player player, final Item gem) {
        this.player = player;
        for (int i = 0; i < GEMS.length; i++) {
            if (gem.getId() == (Integer) GEMS[i][0]) {
                this.gemIndex = i;
            }
        }
    }

    @Override
    public boolean canBegin(boolean init) {
        if (gemIndex == -1) {
            return false;
        }
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < (Integer) GEMS[gemIndex][2]) {
            player.getFrames().sendMessage("You need a Fletching level of " + GEMS[gemIndex][2] + " to cut that gem.");
            return false;
        }
        if (!player.getInventory().hasItem(1755)) {
            player.getFrames().sendMessage("You need a chisel to cut gems into bolt tips.");
            return false;
        }
        // player.getFrames().sendMessage("You have no " + (String)
        // GEMS[gemIndex][4] + " to cut.");
        return player.getInventory().hasItem((Integer) GEMS[gemIndex][0]) && super.canBegin(init);
    }

    @Override
    public void handler() {
        player.playAnimation((Integer) GEMS[gemIndex][5], Animation.AnimationPriority.HIGH);
    }

    @Override
    public void rewards() {
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        player.getInventory().deleteItem((Integer) GEMS[gemIndex][0]);
        player.getInventory().addItem(new Item((Integer) GEMS[gemIndex][1], 12));
        player.getInventory().refresh();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.FLETCHING;
    }

    @Override
    public short speed() {
        return 1;
    }

    @Override
    public double exp() {
        return (double) GEMS[gemIndex][3];
    }
}
