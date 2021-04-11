package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.Item;

public abstract class EnchantingSpells {

    /**
     * Executes enchanting spells.
     *
     * @param player
     */
    public void execute(Player player, Item item) {
        player.getFrames().forceSendTab(6);
        if (player.getSkills().getCurrentLevel(SkillType.MAGIC) < getRequiredLevel()) {
            player.getFrames().sendMessage("You need a Magic level of " + getRequiredLevel() + " to use this spell.");
            return;
        }
        if (!RuneReplacers.hasEnoughRunes(player, getRunes(), true)) {
            return;
        }
        int index = -1;
        for (int i = 0; i < items().length; i++) {
            if (item.getId() == items()[i][0]) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            player.getFrames().sendMessage(sendReqMessage());
            return;
        }
        if (!player.getInventory().replaceItem(items()[index][0], items()[index][1])) {
            return;
        }
        player.getInventory().refresh();
        RuneReplacers.deleteRunes(player, getRunes());
        player.playAnimation(startAnim(), Animation.AnimationPriority.HIGH);
        player.playGraphic(startGraphic(), 0, 100);
    }

    public int getRequiredLevel() {
        return requiredLevel();
    }

    public Item[] getRunes() {
        return runes();
    }

    /**
     * Items this spell can use on
     *
     * @return
     */
    public abstract int[][] items();

    /**
     * Sends this if we don't use the right items for this spell.
     *
     * @return
     */
    public abstract String sendReqMessage();

    /**
     * animation
     */
    public abstract int startAnim();

    /**
     * graphic
     */
    public abstract int startGraphic();

    /**
     * req lvl
     */
    public abstract int requiredLevel();

    /**
     * Required runes
     */
    public abstract Item[] runes();

    public String getName() {
        return name();
    }

    /**
     * Spell name
     */
    public abstract String name();

    public double getExp() {
        return exp();
    }

    /**
     * exp this spell gives
     */
    public abstract double exp();
}
