package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class AmuletStringing extends AbstractSkillAction {

    protected static final int BALL_OF_WOOL = 1759;

    protected static final int STRINGING_XP = 4;

    private static final Set<StrungAmulet> STRUNG_AMULET_SET = Collections.unmodifiableSet(EnumSet.allOf(StrungAmulet.class));

    private StrungAmulet amulet;

    public AmuletStringing(final Player player, final int item) {
        this.player = player;
        this.amulet = STRUNG_AMULET_SET.stream().filter(amlt -> amlt.getUnfinished().getId() == item).findFirst().orElse(null);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (amulet == null)
            return false;
        if (init) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < amulet.getLevel()) {
                player.getFrames().sendMessage(
                    "You need a Crafting level of " + amulet.getLevel() + " to string this amulet.");
                return false;
            }
        }
        if (!player.getInventory().hasItem(BALL_OF_WOOL)) {
            return false;
        }
        return player.getInventory().hasItem(amulet.getUnfinished().getId()) && super.canBegin(init);
    }

    @Override
    public void handler() {
        // TODO Auto-generated method stub

    }

    @Override
    public void rewards() {
        player.getInventory().deleteItem(BALL_OF_WOOL);
        player.getInventory().deleteItem(amulet.getUnfinished().getId());
        player.getInventory().addItem(amulet.getFinished().getId());
        player.getInventory().refresh();
        player.getSkills().addExperience(SKILL_TYPE(), exp());
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

    @Override
    public double exp() {
        return STRINGING_XP;
    }
}
