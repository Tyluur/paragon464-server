package com.paragon464.gameserver.model.content.skills.crafting;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class GemCutting extends AbstractSkillAction {

    protected static final int CHISEL = 1755;

    private static final Set<CuttingGem> CUTTING_GEMS_SET = Collections.unmodifiableSet(EnumSet.allOf(CuttingGem.class));

    private CuttingGem gem;

    public GemCutting(final Player player, final int uncut, final int amount) {
        this.player = player;
        this.gem = CUTTING_GEMS_SET.stream().filter(amlt -> amlt.getUncut().getId() == uncut).findFirst().orElse(null);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (gem == null) {
            return false;
        }
        if (init) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < gem.getLevel()) {
                player.getFrames()
                    .sendMessage("You need a Crafting level of " + gem.getLevel() + " to cut this gem.");
                return false;
            }
        }
        if (!player.getInventory().hasItem(gem.getUncut().getId())) {
            if (init) {
                player.getFrames().sendMessage("You don't have anymore gems to cut.");
            }
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
    }

    @Override
    public void rewards() {
        player.playAnimation(gem.getAnimation(), AnimationPriority.HIGH);
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        player.getInventory().deleteItem(gem.getUncut());
        player.getInventory().addItem(gem.getCut());
        player.getInventory().refresh();
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
        return gem.getExp();
    }
}
