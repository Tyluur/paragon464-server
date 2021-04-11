package com.paragon464.gameserver.model.content.skills.fletching;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class AmmoMaking extends AbstractSkillAction {

    protected static final int FEATHER = 314;
    protected static final int HEADLESS_ARROW = 53;
    protected static final double HEADLESS_ARROW_XP = 1.0;
    protected static final int HEADLESS_ARROW_LVL = 1;
    protected static final int[] ARROW = {882, 884, 886, 888, 890, 892, 11212};
    protected static final int[] ARROWHEAD = {39, 40, 41, 42, 43, 44, 11237};
    protected static final int[] ARROW_LVL = {1, 15, 30, 45, 60, 75, 90};
    protected static final double[] ARROW_XP = {1.3, 2.5, 5.0, 7.5, 10, 12.5, 15};
    protected static final int[] FEATHERLESS_BOLT = {9375, 9376, 9377, 9378, 9379, 9380, 9381, 9382};
    protected static final int[] FEATHERED_BOLT = {877, 9139, 9140, 9141, 9142, 9143, 9144, 9145};
    protected static final int[] FEATHERLESS_BOLT_LVL = {9, 24, 39, 46, 54, 61, 69, 43};
    protected static final double[] FEATHERLESS_BOLT_XP = {0.5, 1, 1.5, 3.5, 5, 7, 10, 2.5};
    protected static final int[] BOLT_TIPS = {45, 9187, 46, 9188, 9189, 9190, 9191, 9192, 9193, 9194};
    protected static final int[] BOLT = {879, 9335, 880, 9336, 9337, 9338, 9339, 9340, 9341, 9342};
    protected static final int[] HEADLESS_BOLT = {877, 9139, 9140, 9141, 9142, 9142, 9143, 9143, 9144, 9144};
    protected static final int[] HEADLESS_BOLT_LVL = {11, 26, 41, 48, 56, 58, 63, 65, 71, 73};
    protected static final double[] HEADLESS_BOLT_XP = {1.6, 2.4, 3.2, 3.9, 4.7, 5.5, 6.3, 7, 8.2, 9.4};
    private AmmoType ammo;
    private int ammoAmount;
    private int currentIndex;

    public AmmoMaking(final Player player, final Item usedItem, final Item usedWith) {
        this.player = player;
        if (usedItem.getId() == FEATHER) {
            if (usedWith.getDefinition().getName().endsWith("(unf)")) {
                for (int i = 0; i < FEATHERLESS_BOLT.length; i++) {
                    if (usedWith.getId() == FEATHERLESS_BOLT[i]) {
                        this.currentIndex = i;
                        this.ammoAmount = 12;
                        this.ammo = new AmmoType(usedItem.getId(), usedWith.getId(), FEATHERLESS_BOLT_LVL[i],
                            FEATHERLESS_BOLT_XP[i], FEATHERED_BOLT[i]);
                        break;
                    }
                }
            }
        } else if (usedItem.getDefinition().getName().endsWith("bolt tips")) {
            if (usedWith.getDefinition().getName().endsWith("bolts")) {
                for (int i = 0; i < BOLT_TIPS.length; i++) {
                    if (usedItem.getId() == BOLT_TIPS[i]) {
                        this.currentIndex = i;
                        this.ammoAmount = 12;
                        this.ammo = new AmmoType(usedItem.getId(), usedWith.getId(), HEADLESS_BOLT_LVL[i],
                            HEADLESS_BOLT_XP[i], BOLT[i]);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean canBegin(boolean init) {
        if (ammo == null || currentIndex == -1) {
            return false;
        }
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < ammo.getReqLvl()) {
            player.getFrames().sendMessage("You need a Fletching level of " + ammo.getReqLvl() + " to fletch that.");
            return false;
        }
        if (!player.getInventory().hasItemAmount(ammo.used, ammoAmount)) {
            player.getFrames().sendMessage(
                "You need atleast " + ammoAmount + " " + ItemDefinition.forId(ammo.used).getName() + ".");
            return false;
        }
        if (!player.getInventory().hasItemAmount(ammo.usedOn, ammoAmount)) {
            player.getFrames().sendMessage(
                "You need atleast " + ammoAmount + " " + ItemDefinition.forId(ammo.usedOn).getName() + ".");
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        // TODO Auto-generated method stub
    }

    @Override
    public void rewards() {
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        player.getInventory().deleteItem(ammo.used, ammoAmount);
        player.getInventory().deleteItem(ammo.usedOn, ammoAmount);
        player.getInventory().addItem(ammo.result, ammoAmount);
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
        return 0;
    }

    @Override
    public double exp() {
        return ammo.exp;
    }

    public class AmmoType {
        private int used, usedOn, reqLvl;
        private double exp;
        private int result;

        public AmmoType(int used, int usedOn, int reqLvl, double exp, int result) {
            this.used = used;
            this.usedOn = usedOn;
            this.reqLvl = reqLvl;
            this.exp = exp;
            this.result = result;
        }

        public int getUsed() {
            return used;
        }

        public void setUsed(int used) {
            this.used = used;
        }

        public int getUsedOn() {
            return usedOn;
        }

        public void setUsedOn(int usedOn) {
            this.usedOn = usedOn;
        }

        public int getReqLvl() {
            return reqLvl;
        }

        public void setReqLvl(int reqLvl) {
            this.reqLvl = reqLvl;
        }

        public double getExp() {
            return exp;
        }

        public void setExp(double exp) {
            this.exp = exp;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }
}
