package com.paragon464.gameserver.model.content.skills.mining;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.content.skills.SkillAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class Mining extends AbstractSkillAction {

    private static final Set<AxeDefinitions> AXE_DEFINITIONS_SET = Collections.unmodifiableSet(EnumSet.allOf(AxeDefinitions.class));
    private RockDefinitions rock;
    private AxeDefinitions axe;

    public Mining(final Player player, final RockDefinitions rock) {
        this.player = player;
        this.rock = rock;
        AxeDefinitions def = null;
        int index = -1;
        for (int i = 0; i < Inventory.SIZE; i++) {
            Item item = player.getInventory().get(i);
            if (item == null) continue;
            def = getAxeType(item);
            if (def == null) continue;
            if (def.ordinal() > index) {
                if (player.getSkills().getLevel(SKILL_TYPE()) >= def.level) {
                    index = def.ordinal();
                    this.axe = def;
                }
            }
        }
        def = getAxeType(player.getEquipment().get(3));
        if (def != null) {
            if (def.ordinal() > index) {
                if (player.getSkills().getLevel(SKILL_TYPE()) >= def.level) {
                    index = def.ordinal();
                    this.axe = def;
                }
            }
        }
    }

    public static AxeDefinitions getAxeType(final Item item) {
        return AXE_DEFINITIONS_SET.stream().filter(amlt -> amlt.getId() == item.getId()).findFirst().orElse(null);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < rock.level) {
            player.getFrames().sendMessage("You need a Mining level of " + rock.level + " to mine that.");
            return false;
        }
        if (axe == null) {
            player.getFrames().sendMessage("You do not have a pickaxe that you can use.");
            return false;
        }
        if (player.getInventory().findFreeSlot() == -1) {
            player.getFrames().sendMessage("Your inventory is full!");
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        player.playAnimation(axe.anim, Animation.AnimationPriority.HIGH);
    }

    @Override
    public void rewards() {
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        player.getInventory().addItem(rock.oreId);
        SkillAction.handleMoneyCasket(player, SKILL_TYPE());
        double roll = NumberUtils.getRandomDouble(100);
        double chance = 10.0;
        switch (this.rock) {
            case Adamant_Ore:
                chance = 15.0;
                break;
            case Runite_Ore:
                chance = 20.0;
                break;
            default:
                break;
        }
        if (roll <= (chance * 1.5)) {
            if (player.getInventory().addItem(112012, NumberUtils.random(5, 5))) {
                player.getInventory().refresh();
            }
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.MINING;
    }

    @Override
    public short speed() {
        int mineTimer = NumberUtils.random((rock.oreRandomTime - axe.time));
        if (mineTimer < 1) {
            mineTimer = NumberUtils.random(axe.time);
        }
        return (short) mineTimer;
    }

    @Override
    public double exp() {
        return rock.xp;
    }

    public enum AxeDefinitions {

        BRONZE(1265, 625, 1, 1),

        IRON(1267, 626, 2, 1),

        STEEL(1269, 627, 3, 5),

        MITHRIL(1273, 629, 5, 20),

        ADAMANT(1271, 628, 7, 30),

        RUNE(1275, 624, 10, 40),

        RAGON(15259, 12189, 13, 60);

        private int id, anim, time, level;

        AxeDefinitions(int id, int anim, int time, int level) {
            this.id = id;
            this.anim = anim;
            this.time = time;
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public int getAnim() {
            return anim;
        }

        public int getTimer() {
            return time;
        }

        public int getLevel() {
            return level;
        }
    }

    public enum RockDefinitions {

        PURE_ESS(1, 20.0, 7936, 15, 1, -1, 5, 0), Clay_Ore(1, 5, 434, 10, 1, 11552, 5, 0), Copper_Ore(1, 17.5, 436, 10,
            1, 11552, 5, 0), Tin_Ore(1, 17.5, 438, 15, 1, 11552, 5, 0), Iron_Ore(15, 35, 440, 15, 1, 11552, 10,
            0), Sandstone_Ore(35, 30, 6971, 30, 1, 11552, 10, 0), Silver_Ore(20, 40, 442, 25, 1, 11552, 20,
            0), Coal_Ore(30, 50, 453, 50, 10, 11552, 30, 0), Granite_Ore(45, 50, 6979, 50, 10,
            11552, 20, 0), Gold_Ore(40, 60, 444, 80, 20, 11554, 40, 0), Mithril_Ore(55, 80,
            447, 100, 20, 11552, 60,
            0), Adamant_Ore(70, 95, 449, 130, 25, 11552, 180, 0), Runite_Ore(85,
            125, 451, 150, 30, 11552, 360,
            0), LRC_Coal_Ore(77, 50, 453, 50, 10, -1, -1, -1), LRC_Gold_Ore(
            80, 60, 444, 40, 10, -1, -1,
            -1), CRASHED_STAR(10, 150, 13727, 2, 10, -1, -1, -1);

        private int level;

        private double xp;

        private int oreId;

        private int oreBaseTime;

        private int oreRandomTime;

        private int emptySpot;

        private int respawnDelay;

        private int randomLifeProbability;

        RockDefinitions(int level, double xp, int oreId, int oreBaseTime, int oreRandomTime, int emptySpot,
                        int respawnDelay, int randomLifeProbability) {
            this.level = level;
            this.xp = xp;
            this.oreId = oreId;
            this.oreBaseTime = oreBaseTime;
            this.oreRandomTime = oreRandomTime;
            this.emptySpot = emptySpot;
            this.respawnDelay = respawnDelay;
            this.randomLifeProbability = randomLifeProbability;
        }

        public int getLevel() {
            return level;
        }

        public double getXp() {
            return xp;
        }

        public int getOreId() {
            return oreId;
        }

        public int getOreBaseTime() {
            return oreBaseTime;
        }

        public int getOreRandomTime() {
            return oreRandomTime;
        }

        public int getEmptyId() {
            return emptySpot;
        }

        public int getRespawnDelay() {
            return respawnDelay;
        }

        public int getRandomLifeProbability() {
            return randomLifeProbability;
        }
    }
}
