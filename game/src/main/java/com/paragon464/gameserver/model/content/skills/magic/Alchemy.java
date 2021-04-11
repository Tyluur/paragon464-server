package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.magic.onitems.HighAlchemy;
import com.paragon464.gameserver.model.item.Item;

public abstract class Alchemy {

    public void execute(Player player, Item item) {
        player.getFrames().forceSendTab(6);
        if (System.currentTimeMillis() - player.getVariables().getLastAction() < 1800) {
            return;
        }
        if (player.getSkills().getCurrentLevel(SkillType.MAGIC) < requiredLevel()) {
            player.getFrames().sendMessage("You need a Magic level of " + requiredLevel() + " to use this spell.");
            return;
        }
        if (item.getDefinition().getName().equalsIgnoreCase("coins")) {
            player.getFrames().sendMessage("You can't alch coins!");
            return;
        }
        if (player.getInventory().hasEnoughRoomFor(995)) {
            if (!RuneReplacers.hasEnoughRunes(player, runes(), true)) {
                return;
            }
            int price = item.getDefinition().getLowAlchPrice();
            if (this instanceof HighAlchemy) {
                price = item.getDefinition().getHighAlch();
            }
            if (price <= 0) {
                price = 1;
            }
            player.getVariables().setLastAction(System.currentTimeMillis());
            player.getSkills().addExperience(SkillType.MAGIC, exp());
            player.getInventory().addItem(new Item(995, price));
            player.getInventory().deleteItem(item.getId(), 1);
            RuneReplacers.deleteRunes(player, runes());
            player.playAnimation(startAnim(), AnimationPriority.HIGH);
            player.playGraphic(startGraphic());
            player.getInventory().refresh();
        }
    }

    /**
     * req lvl
     */
    public abstract int requiredLevel();

    /**
     * Required runes
     */
    public abstract Item[] runes();

    /**
     * exp this spell gives
     */
    public abstract double exp();

    /**
     * animation
     */
    public abstract int startAnim();

    /**
     * graphic
     */
    public abstract int startGraphic();
}
