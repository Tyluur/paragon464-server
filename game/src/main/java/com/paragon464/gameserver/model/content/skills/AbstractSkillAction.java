package com.paragon464.gameserver.model.content.skills;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.runecrafting.Runecrafting;
import com.paragon464.gameserver.tickable.Tickable;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class AbstractSkillAction implements SkillAction {

    protected Player player;

    protected Tickable processor;

    @Override
    public String[] messages() {
        return null;
    }

    @Override
    public Animation animation() {
        return null;
    }

    @Override
    public boolean canBegin(boolean init) {
        return player.getVariables().getSkill() != null;
    }

    @Override
    public void init() {
        if (!canBegin(true)) {
            player.resetActionAttributes();
            return;
        }
        processor = new Tickable(0, true) {
            int counter = speed();

            @Override
            public void execute() {
                if (!canBegin(false)) {
                    player.resetActionAttributes();
                    this.stop();
                    return;
                }
                handler();
                if (counter-- > 0) {
                    return;
                } else if (counter <= 0) {
                    counter = speed();
                }
                rewards();
            }
        };
        player.submitTickable(processor);
    }

    @Override
    public Tickable processor() {
        return processor;
    }

    @Override
    public void handler() {
        // TODO Auto-generated method stub
    }

    @Override
    public void rewards() {
    }

    @Override
    public void end() {
        if (!(this instanceof Runecrafting)) {
            player.playAnimation(-1, Animation.AnimationPriority.HIGH);
        }
        if (processor != null) {
            processor.stop();
        }
    }

    @Override
    public SkillType SKILL_TYPE() {
        return null;
    }

    @Override
    public short speed() {
        return 0;
    }

    @Override
    public double exp() {
        return 0;
    }
}
