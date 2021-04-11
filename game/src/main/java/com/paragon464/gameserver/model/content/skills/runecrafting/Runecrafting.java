package com.paragon464.gameserver.model.content.skills.runecrafting;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class Runecrafting extends AbstractSkillAction {

    private final int PURE_ESSENCE = 7936;

    private final int[][] MULTIPLY_LEVELS = {{22, 33, 44, 55, 66, 77, 88, 99}, // Air
        {14, 28, 42, 56, 70, 84, 98}, // Mind
        {19, 38, 57, 76, 95}, // Water
        {26, 52, 78}, // Earth
        {35, 70}, // Fire
        {46, 92}, // Body
        {59}, // Cosmic
        {74}, // Chaos
        {82}, // Astral
        {91}, // Nature
        {-1}, // Law
        {-1}, // Death
        {-1}, // Blood
    };

    private final int[] CRAFT_LEVEL = {1, // Air
        2, // Mind
        5, // Water
        9, // Earth
        14, // Fire
        20, // Body
        27, // Cosmic
        35, // Chaos
        40, // Astral
        44, // Nature
        54, // Law
        65, // Death
        77, // Blood
    };

    private final double[] CRAFT_XP = {5, // Air
        5.5, // Mind
        6, // Water
        6.5, // Earth
        7, // Fire
        7.5, // Body
        8, // Cosmic
        8.5, // Chaos
        8.7, // Astral
        9, // Nature
        9.5, // Law
        10, // Death
        10.5, // Blood
    };

    private final int[] RUNES = {556, // Air
        558, // Mind
        555, // Water
        557, // Earth
        554, // Fire
        559, // Body
        564, // Cosmic
        562, // Chaos
        9075, // Astral
        561, // Nature
        563, // Law
        560, // Death
        565, // Blood
    };

    private final int[] ALTARS = {2478, // Air
        2479, // Mind
        2480, // Water
        2481, // Earth
        2482, // Fire
        2483, // Body
        2484, // Cosmic
        2487, // Chaos
        17010, // Astral
        2486, // Nature
        2485, // Law
        2488, // Death
        30624, // Blood
        -1, // Soul
    };

    private int index = -1;

    public Runecrafting(final Player player, final GameObject object) {
        this.player = player;
        for (int i = 0; i < ALTARS.length; i++) {
            if (object.getId() == ALTARS[i]) {
                this.index = i;
            }
        }
    }

    @Override
    public boolean canBegin(boolean init) {
        if (index == -1) {
            return false;
        }
        if (!player.getInventory().hasItem(PURE_ESSENCE)) {
            if (init) {
                player.getFrames().sendMessage("You don't have any pure essence.");
            }
            return false;
        }
        if (init) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < CRAFT_LEVEL[index]) {
                player.getFrames().sendMessage("You need a Runecrafting level of " + CRAFT_LEVEL[index] + " to craft "
                    + ItemDefinition.forId(RUNES[index]).getName() + "s.");
                return false;
            }
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        player.playAnimation(791, AnimationPriority.HIGH);
        int essAmount = player.getInventory().getItemAmount(PURE_ESSENCE);
        for (int i = 0; i < essAmount; i++) {
            player.getInventory().deleteItem(PURE_ESSENCE);
        }
        player.getInventory().refresh();
        int multiply = 1;
        for (int j = 0; j < MULTIPLY_LEVELS[index].length; j++) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) >= MULTIPLY_LEVELS[index][j]) {
                multiply++;
            }
        }
        String s = essAmount > 1 || (essAmount == 1 && multiply > 1) ? "s." : ".";
        String s1 = essAmount > 1 || (essAmount == 1 && multiply > 1) ? "" : "a ";
        int total = essAmount * multiply;
        player.getAttributes().addInt("rc_points", (total / multiply));
        player.getInventory().addItem(RUNES[index], total);
        player.getInventory().refresh();
        player.getSkills().addExperience(SKILL_TYPE(), exp() * essAmount);
        player.getFrames()
            .sendMessage("You craft the essence into " + s1 + ItemDefinition.forId(RUNES[index]).getName() + s);
    }

    @Override
    public void rewards() {
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.RUNECRAFTING;
    }

    @Override
    public short speed() {
        return 0;
    }

    @Override
    public double exp() {
        return CRAFT_XP[index];
    }
}
