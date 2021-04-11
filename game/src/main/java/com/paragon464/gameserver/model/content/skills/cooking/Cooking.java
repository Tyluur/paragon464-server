package com.paragon464.gameserver.model.content.skills.cooking;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Cooking extends AbstractSkillAction {

    private static final Set<RawFish> RAW_FISH_SET = Collections.unmodifiableSet(EnumSet.allOf(RawFish.class));
    /**
     * The {@link GameObject} being used to cook the fish
     */
    private final GameObject object;

    private final RawFish rawFish;

    private byte animCounter = 0;

    public Cooking(final Player player, final RawFish fishType, final GameObject object) {
        this.player = player;
        this.rawFish = fishType;
        this.object = object;
        animCounter = (byte) (object.getName().toLowerCase().equals("fire") ? 3 : 1);
    }

    public static RawFish getFishType(final Item item) {
        return RAW_FISH_SET.stream().filter(amlt -> amlt.getRaw() == item.getId()).findFirst().orElse(null);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (rawFish == null) {
            return false;
        }
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < rawFish.reqLevel) {
            player.getFrames().sendMessage("You need a Cooking level of " + rawFish.reqLevel + " to cook that.");
            return false;
        }
        //player.getFrames().sendMessage("You have ran out of " + ItemDefinition.forId(rawFish.raw).getName() + ".");
        return player.getInventory().hasItem(rawFish.raw) && super.canBegin(init);
    }

    @Override
    public void handler() {
        int reset = object.getName().toLowerCase().equals("fire") ? 3 : 1;
        if (animCounter == reset) {
            player.playAnimation(object.getName().toLowerCase().equals("fire") ? 897 : 883, AnimationPriority.HIGH);
        }
        if (animCounter > 0) {
            animCounter--;
            return;
        }
        animCounter = (byte) reset;
    }

    @Override
    public void rewards() {
        player.getInventory().deleteItem(rawFish.raw);
        player.getInventory().addItem(burned() ? rawFish.burnt : rawFish.cooked);

        if (!burned())
            player.getSkills().addExperience(SKILL_TYPE(), exp());
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.COOKING;
    }

    @Override
    public short speed() {
        return 1;
    }

    @Override
    public double exp() {
        return rawFish.xp;
    }

    private boolean burned() {
        int foodLevel = rawFish.reqLevel;
        int cookLevel = player.getSkills().getCurrentLevel(SKILL_TYPE());

        return cookLevel < rawFish.perfectLevel && NumberUtils.random(cookLevel) <= NumberUtils.random((int) (foodLevel / 1.5));
    }

    public enum RawFish {

        SHRIMP(317, 315, 7954, 33, 1, 30),
        ANCHOVIES(321, 319, 323, 34, 5, 45),
        TROUT(335, 333, 343, 50, 20, 70),
        SALMON(331, 329, 343, 58, 30, 90),
        PIKE(349, 351, 343, 64, 35, 100),
        LOBSTER(377, 379, 381, 74, 40, 120),
        SWORDFISH(371, 373, 375, 86, 50, 140),
        MONKFISH(7944, 7946, 7948, 91, 62, 150),
        SHARK(383, 385, 387, 94, 80, 210),
        MANTA_RAY(389, 391, 393, 99, 91, 169),
        ROCKTAIL(15270, 15272, 15274, 99, 93, 225);

        private final int raw;
        private final int cooked;
        private final int burnt;
        private final int perfectLevel;
        private final int reqLevel;
        private final double xp;

        RawFish(int raw, int cooked, int burnt, int perfectLevel, int reqLevel, double xp) {
            this.raw = raw;
            this.cooked = cooked;
            this.burnt = burnt;
            this.perfectLevel = perfectLevel;
            this.reqLevel = reqLevel;
            this.xp = xp;
        }

        public int getRaw() {
            return raw;
        }

        public int getCooked() {
            return cooked;
        }

        public int getBurnt() {
            return burnt;
        }

        public int getLevel() {
            return reqLevel;
        }

        public double getXp() {
            return xp;
        }

        public int getPerfectLevel() {
            return perfectLevel;
        }
    }
}
