package com.paragon464.gameserver.model.content.skills.woodcutting;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.content.skills.SkillAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class Woodcutting extends AbstractSkillAction {

    private static final Set<AxeDefinition> AXE_SET = Collections.unmodifiableSet(EnumSet.allOf(AxeDefinition.class));
    private TreeDefinitions tree;
    private AxeDefinition axe;

    public Woodcutting(final Player player, final TreeDefinitions tree) {
        this.player = player;
        this.tree = tree;
        int prevLvl = -1;
        Item weap = player.getEquipment().get(3);
        if (weap != null) {
            AxeDefinition axe = getAxe(weap);
            if (axe != null) {
                if (player.getSkills().getLevel(SKILL_TYPE()) >= axe.level) {
                    if (prevLvl < axe.level) {
                        this.axe = axe;
                        prevLvl = axe.level;
                    }
                }
            }
        }
        for (Item inv : player.getInventory().getItems()) {
            if (inv != null) {
                AxeDefinition axe = getAxe(inv);
                if (axe != null) {
                    if (player.getSkills().getLevel(SKILL_TYPE()) >= axe.level) {
                        if (prevLvl < axe.level) {
                            this.axe = axe;
                            prevLvl = axe.level;
                        }
                    }
                }
            }
        }
    }

    public static AxeDefinition getAxe(final Item item) {
        return AXE_SET.stream().filter(amlt -> amlt.getId() == item.getId()).findFirst().orElse(null);
    }

    public static int getIndex(GameObject object) {
        String name = object.getName().toLowerCase();
        if (name.equals("tree")) {
            return 0;
        } else if (name.equals("oak")) {
            return 1;
        } else if (name.equals("willow")) {
            return 2;
        } else if (name.equals("maple tree")) {
            return 5;
        } else if (name.equals("yew")) {
            return 10;
        } else if (name.equals("magic tree")) {
            return 11;
        }
        return -1;
    }

    @Override
    public boolean canBegin(boolean init) {
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < tree.getLevel()) {
            player.getFrames().sendMessage("You need a Woodcutting level of " + tree.getLevel() + " to cut that tree.");
            return false;
        }
        if (axe == null) {
            player.getFrames().sendMessage("You do not have an axe that you can use.");
            return false;
        }
        if (player.getInventory().findFreeSlot() == -1) {
            player.getFrames().sendMessage("Your inventory is too full to carry any more logs.");
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
        player.getInventory().addItem(tree.logsId);
        SkillAction.handleMoneyCasket(player, SKILL_TYPE());
        //nests
        if (Areas.atWoodcuttingGuild(player.getPosition())) {
            double roll = NumberUtils.getRandomDouble(100);
            double chance = 1.0;
            switch (this.tree) {
                case YEW:
                    chance = 4.0;
                    break;
                case MAGIC:
                    chance = 5.0;
                    break;
                default:
                    break;
            }
            if (roll <= (chance * 1.5)) {
                if (player.getInventory().addItem(5070)) {
                    player.getInventory().refresh();
                    player.getFrames().sendMessage("You've cut down a bird's nest!");
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(new Item(5070, 1), player));
                    player.getFrames().sendMessage("A bird's nest fell from the tree!");
                }
            }
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.WOODCUTTING;
    }

    @Override
    public short speed() {
        int wcTimer = tree.getLogBaseTime() - (player.getSkills().getCurrentLevel(SKILL_TYPE()) - NumberUtils.random(axe.time));
        if (wcTimer < 1 + tree.getLogRandomTime())
            wcTimer = 1 + NumberUtils.random(tree.getLogRandomTime());
        return (short) NumberUtils.random(wcTimer);
    }

    @Override
    public double exp() {
        return tree.xp;
    }

    public enum AxeDefinition {

        BRONZE(1351, 879, 1, 1), IRON(1349, 877, 2, 1), STEEL(1353, 875, 3, 5), BLACK(1361, 873, 4, 10), MITHRIL(1355,
            871, 5, 20), ADAMANT(1357, 869, 7, 30), RUNE(1359, 867, 10, 40), DRAGON(6739, 2846, 13, 60);

        private int id, anim, time, level;

        AxeDefinition(int id, int anim, int time, int level) {
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

    public enum TreeDefinitions {

        NORMAL(1, 25, 1511, 20, 4, 1341, 8, 0), // TODO

        EVERGREEN(1, 25, 1511, 20, 4, 57931, 8, 0),

        DEAD(1, 25, 1511, 20, 4, 12733, 8, 0),

        OAK(15, 37.5, 1521, 30, 4, 1341, 15, 15), // TODO

        WILLOW(30, 67.5, 1519, 60, 4, 1341, 51, 15), // TODO

        MAPLE(45, 100, 1517, 83, 16, 31057, 72, 10),

        YEW(60, 175, 1515, 120, 17, 1341, 94, 10), // TODO

        IVY(68, 332.5, -1, 120, 17, 46319, 58, 10),

        MAGIC(75, 250, 1513, 150, 21, 37824, 121, 10),

        CURSED_MAGIC(82, 250, 1513, 150, 21, 37822, 121, 10),

        REDWOOD(90, 380, 119669, 150, 21, 37822, 121, 10);

        private int level;

        private double xp;

        private int logsId;

        private int logBaseTime;

        private int logRandomTime;

        private int stumpId;

        private int respawnDelay;

        private int randomLifeProbability;

        TreeDefinitions(int level, double xp, int logsId, int logBaseTime, int logRandomTime, int stumpId,
                        int respawnDelay, int randomLifeProbability) {
            this.level = level;
            this.xp = xp;
            this.logsId = logsId;
            this.logBaseTime = logBaseTime;
            this.logRandomTime = logRandomTime;
            this.stumpId = stumpId;
            this.respawnDelay = respawnDelay;
            this.randomLifeProbability = randomLifeProbability;
        }

        public int getLevel() {
            return level;
        }

        public double getXp() {
            return xp;
        }

        public int getLogsId() {
            return logsId;
        }

        public int getLogBaseTime() {
            return logBaseTime;
        }

        public int getLogRandomTime() {
            return logRandomTime;
        }

        public int getStumpId() {
            return stumpId;
        }

        public int getRespawnDelay() {
            return respawnDelay;
        }

        public int getRandomLifeProbability() {
            return randomLifeProbability;
        }
    }
}
