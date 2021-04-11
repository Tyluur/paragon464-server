package com.paragon464.gameserver.tickable.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.tickable.Tickable;

public class MinuteTick extends Tickable {

    /**
     * The delay in milliseconds between healing.
     */
    public static final int DELAY = 100;

    /**
     * Sets the server to run event to run every 60 seconds.
     */
    public MinuteTick() {
        super(DELAY);
    }

    @Override
    public void execute() {
        for (final Player player : World.getWorld().getPlayers()) {
            if (player == null || player.getCombatState().isDead()) {
                continue;
            }

            player.heal(1);
            player.getSkills().getSkillSet().forEach(skillType -> {
                final int realLevel = player.getSkills().getLevel(skillType);
                int level = player.getSkills().getCurrentLevel(skillType);

                if (level != realLevel && skillType != SkillType.PRAYER) {
                    if (level > realLevel) {
                        level--;
                    } else if (level < realLevel) {
                        level++;
                    }

                    player.getSkills().setCurrentLevel(skillType, level);
                }
            });
        }
    }
}
