package com.paragon464.gameserver.model.content.skills;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public interface SkillAction {

    static void handleMoneyCasket(Player player, SkillType skill) {
        double roll = NumberUtils.getRandomDouble(100);
        int levelForSkill = player.getSkills().getLevel(skill);
        double chance = 4.0;
        if (levelForSkill >= 70) {
            chance = 7.0;
        } else if (levelForSkill >= 90) {
            chance = 10.0;
        }
        if (roll <= (chance * 1.5)) {
            if (player.getInventory().addItem(2717)) {
                player.getInventory().refresh();
            } else {
                GroundItemManager.registerGroundItem(new GroundItem(new Item(2717, 1), player));
            }
        }
    }

    String[] messages();

    Animation animation();

    boolean canBegin(boolean init);

    void init();

    Tickable processor();

    void handler();

    void rewards();

    void end();

    SkillType SKILL_TYPE();

    short speed();

    double exp();
}
