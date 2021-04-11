package com.paragon464.gameserver.model.content.skills.fletching;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;

public class BowMaking extends AbstractSkillAction {

    protected static final int BOWSTRING = 1777;
    protected static final int ARROW_AMOUNT = 15;
    private LOG_TYPES logType;
    private int bowIndex = -1;
    private boolean stringingBow = false;

    public BowMaking(final Player player, final Item log) {
        this.player = player;
        if (log.getDefinition().getName().endsWith("(u)")) {
            stringingBow = true;
            if (log.getDefinition().getName().contains("short")) {
                bowIndex = 0;
            } else if (log.getDefinition().getName().contains("long")) {
                bowIndex = 1;
            }
            for (LOG_TYPES types : LOG_TYPES.values()) {
                for (int unstrungbows : types.unstrungbows) {
                    if (log.getId() == unstrungbows) {
                        this.logType = types;
                    }
                }
            }
        } else {
            for (LOG_TYPES types : LOG_TYPES.values()) {
                if (log.getId() == types.logId) {
                    this.logType = types;
                    display();
                    break;
                }
            }
        }
    }

    public void display() {
        String s = "<br><br><br><br>";
        boolean stringing = false;
        if (!stringing) {
            if (logType.ordinal() == 0) {
                player.getFrames().itemOnInterface(305, 2, 175, 52);
                player.getFrames().itemOnInterface(305, 3, 175, 50);
                player.getFrames().itemOnInterface(305, 4, 175, 48);
                player.getFrames().itemOnInterface(305, 5, 175, 9440);
                player.getFrames().modifyText(s + ARROW_AMOUNT + " Arrow Shafts", 305, 9);
                player.getFrames().modifyText(s + "Short Bow", 305, 13);
                player.getFrames().modifyText(s + "Long Bow", 305, 17);
                player.getFrames().modifyText(s + "Crossbow Stock", 305, 21);
                player.getFrames().sendChatboxInterface(305);
            } else {
                player.getFrames().sendChatboxInterface(303);
                player.getFrames().itemOnInterface(303, 2, 175, logType.unstrungbows[0]);
                player.getFrames().itemOnInterface(303, 3, 175, logType.unstrungbows[1]);
                player.getFrames().modifyText(s + "Short Bow", 303, 7);
                player.getFrames().modifyText(s + "Long Bow", 303, 11);
            }
        } else {
            /*
             * int[] bows = bowType == 0 ? STRUNG_SHORTBOW : STRUNG_LONGBOW;
             * player.getFrames().sendChatboxInterface(309);
             * player.getFrames().itemOnInterface(309, 2, 150, bows[logIndex]);
             * player.getFrames().modifyText(s +
             * ItemDefinition.forId(bows[logIndex]).getName(), 309, 6);
             */
        }
    }

    public void handleButtons(final int button) {
        // Debugging.print("logcut button: " + button);
        if (logType.ordinal() == 0) {
            if (button >= 14 && button <= 17) {
                bowIndex = 1;
            } else if (button >= 10 && button <= 13) {
                bowIndex = 0;
            } else if (button >= 6 && button <= 9) {
                bowIndex = 2;// arrow shafts
            }
        } else {
            if (button >= 8 && button <= 11) {
                // unstrung longbows
                bowIndex = 1;
            } else if (button >= 4 && button <= 7) {
                bowIndex = 0;
                // unstrung shortbows
            }
        }
        player.getInterfaceSettings().closeInterfaces(false);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (logType == null) {
            return false;
        }
        if (!stringingBow) {
            if (!player.getInventory().hasItem(946)) {// knife
                player.getFrames().sendMessage("You need a knife to cut this log.");
                return false;
            }
            if (!player.getInventory().hasItem(logType.logId)) {
                // player.getFrames().sendMessage("You don't have any
                // "+ItemDefinition.forId(logType.logId).getName()+" to cut.");
                return false;
            }
        } else if (stringingBow) {
            if (!player.getInventory().hasItem(BOWSTRING)) {
                if (init) {
                    player.getFrames().sendMessage("You don't have any Bowstrings.");
                }
                return false;
            }
            if (!player.getInventory().hasItem(logType.unstrungbows[bowIndex])) {
                player.getFrames().sendMessage("You don't have any unstrung bows to string.");
                return false;
            }
        }
        if (bowIndex > -1) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < logType.reqLvl[bowIndex]) {
                player.getFrames()
                    .sendMessage("You need a Fletching level of " + logType.reqLvl[bowIndex] + " to cut that log.");
                return false;
            }
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        if (!stringingBow && bowIndex > -1) {
            player.playAnimation(1248, AnimationPriority.HIGH);
        } else if (stringingBow) {
            player.playAnimation(logType.anims[bowIndex], AnimationPriority.HIGH);
        }
    }

    @Override
    public void rewards() {
        if (!stringingBow && bowIndex < 0) {
            return;
        }
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        if (!stringingBow) {
            player.getInventory().deleteItem(logType.logId);
            player.getInventory().addItem(new Item(logType.unstrungbows[bowIndex], bowIndex == 2 ? 15 : 1));
        } else if (stringingBow) {
            player.getInventory().deleteItem(BOWSTRING);
            player.getInventory().deleteItem(logType.unstrungbows[bowIndex]);
            player.getInventory().addItem(new Item(logType.strungbows[bowIndex], 1));
        }
        player.getInventory().refresh();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.FLETCHING;
    }

    @Override
    public short speed() {
        return 1;
    }

    @Override
    public double exp() {
        return logType.exp[bowIndex];
    }

    public enum LOG_TYPES {// arrays; idx 0 for shortbows 1 for longbows
        REGULAR(1511,
            new int[]{50, 48, 52},
            new int[]{841, 839},
            new int[]{5, 10, 1},
            new double[]{5, 10, 0.33},
            new int[]{6678, 6684}),
        OAK(1521,
            new int[]{54, 56},
            new int[]{843, 845},
            new int[]{20, 25},
            new double[]{16.5, 25},
            new int[]{6679, 6685}),
        WILLOW(1519,
            new int[]{60, 58},
            new int[]{849, 847},
            new int[]{35, 40},
            new double[]{33, 41.5},
            new int[]{6680, 6686}),
        MAPLE(1517,
            new int[]{64, 62},
            new int[]{853, 851},
            new int[]{50, 55},
            new double[]{50, 58},
            new int[]{6681, 6687}),
        YEW(1515,
            new int[]{68, 66},
            new int[]{857, 855},
            new int[]{65, 70},
            new double[]{67.5, 75},
            new int[]{6682, 6688}),
        MAGIC(1513,
            new int[]{72, 70},
            new int[]{861, 859},
            new int[]{80, 85},
            new double[]{83, 91.5},
            new int[]{6683, 6689}),
        // other
        // ARROW_SHAFTS(1511,
        // new int[] {52}, new
        // int[] {}, new int[]
        // {1}, new double[]
        // {0.33}, new int[]
        // {1248})
        ;

        private int logId;
        private int[] unstrungbows, strungbows;
        private int[] reqLvl;
        private double[] exp;
        private int[] anims;

        LOG_TYPES(int log, int[] unstrungbows, int[] strungbows, int[] reqLvl, double[] exp, int[] anims) {
            this.logId = log;
            this.unstrungbows = unstrungbows;
            this.strungbows = strungbows;
            this.reqLvl = reqLvl;
            this.exp = exp;
            this.anims = anims;
        }
    }
}
