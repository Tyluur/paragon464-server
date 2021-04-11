package com.paragon464.gameserver.model.content.skills.herblore;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;

/**
 * Handles the creating of a finished potion
 *
 * @author Reece <valiw@hotmail.com>
 * @since Tuesday, November 24th. 2015.
 */
public class CreateFinishedPotionAction extends AbstractSkillAction {

    /**
     * The animation played when a player is making a potion
     */
    public static final Animation ANIMATION = Animation.create(363, Animation.AnimationPriority.HIGH);

    private FinishedPotionData data = null;

    public CreateFinishedPotionAction(Player player, FinishedPotionData data) {
        this.player = player;
        this.data = data;
    }

    @Override
    public boolean canBegin(boolean init) {
        if (data == null) {
            return false;
        }
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < data.getLevelReq()) {
            player.getFrames()
                .sendMessage("You need a Herblore level of " + data.getLevelReq() + " to make this potion.");
            return false;
        }
        if (!player.getInventory().hasItem(data.getItemNeeded())
            || !player.getInventory().hasItem(data.getUnfinishedPotion())) {
            player.getFrames().sendMessage("You have ran out of ingredients to continue.");
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        player.playAnimation(CreateUnfinishedPotionAction.ANIMATION);
    }

    @Override
    public void rewards() {
        player.getInventory().deleteItem(data.getItemNeeded(), 1);
        player.getInventory().deleteItem(data.getUnfinishedPotion(), 1);
        player.getInventory().addItem(data.getFinishedPotion(), 1);
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        player.getInventory().refresh();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.HERBLORE;
    }

    @Override
    public short speed() {
        return 2;
    }

    @Override
    public double exp() {
        return data.getExpGained();
    }
}
