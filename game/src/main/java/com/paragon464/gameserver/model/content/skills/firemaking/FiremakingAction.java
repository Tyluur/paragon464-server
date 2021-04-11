package com.paragon464.gameserver.model.content.skills.firemaking;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.util.NumberUtils;

/**
 * Handles the starting of a fire
 *
 * @author Reece <valiw@hotmail.com>
 * @since Tuesday, November 17th. 2015.
 */
public class FiremakingAction extends AbstractSkillAction {

    /**
     * The animation played when a player is attempting to create a fire
     */
    public static final Animation ANIMATION = Animation.create(733, Animation.AnimationPriority.HIGH);

    /**
     * The log type from the {@link FireData} enumeration
     */
    private final FireData log;

    /**
     * The amount of ticks the firemaking animation takes to finish
     */
    private int ticks;

    /**
     * Constructs a {@link FiremakingAction} skill action.
     *
     * @param player  The player making the fire.
     * @param logData The logs being lit.
     */
    public FiremakingAction(Player player, FireData log, int ticks) {
        this.player = player;
        this.log = log;
        this.ticks = ticks;
    }

    @Override
    public boolean canBegin(boolean init) {
        return Fire.canStart(player, log) && super.canBegin(init);
    }

    @Override
    public void handler() {
        ticks--;
    }

    @Override
    public void rewards() {
        if (success()) {
            player.getInventory().deleteItem(log.getLogId(), 1);
            player.getInventory().refresh();
            Fire.start(player, log);
            if (TileControl.canMove(player, Directions.NormalDirection.EAST, player.getSize(), false)) {
                player.executeEntityPath(player.getPosition().getX() + 1,
                    player.getPosition().getY());
            } else if (TileControl.canMove(player, Directions.NormalDirection.WEST, player.getSize(), false)) {
                player.executeEntityPath(player.getPosition().getX() - 1,
                    player.getPosition().getY());
            }
            player.getSkills().addExperience(SKILL_TYPE(), exp());
            end();
        }
    }

    /**
     * The calculation of a succesful firemaking attempt
     *
     * @return
     */
    private boolean success() {
        //TODO:
        int level = player.getSkills().getCurrentLevel(SkillType.FIREMAKING);
        double multiplier = NumberUtils.random(level) / 10;
        int chance = NumberUtils.random(level) * 2 / 5;
        double failChance = NumberUtils.random(15) + (multiplier);
        return chance >= failChance;
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.FIREMAKING;
    }

    @Override
    public short speed() {
        return 1;
    }

    @Override
    public double exp() {
        return log.getExperience();
    }
}
