package com.paragon464.gameserver.model.content.skills.smithing;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class SmithingAction extends AbstractSkillAction {

    private int barIndex = -1, armourType = -1;

    private Object[][] data;

    public SmithingAction(final Player player, final Item item) {
        this.player = player;
        for (int i = 0; i < SmithBarData.BARS.length; i++) {
            if (item.getId() == SmithBarData.BARS[i]) {
                this.barIndex = i;
            }
        }
        displayInterface();
    }

    private void displayInterface() {
        this.data = getDataForIndex();
        if (data == null) {
            end();
            return;
        }
        int child = 146;
        int count = 0;
        for (int j = 0; j < this.data.length; j++) {
            boolean canSmith = player.getSkills().getCurrentLevel(SKILL_TYPE()) >= (Integer) this.data[j][1];
            String barColour = player.getInventory().hasItemAmount(SmithBarData.BARS[barIndex],
                (Integer) this.data[j][3]) ? "<col=00FF00>" : "";
            String amount = barColour + this.data[j][3];
            String name = (String) this.data[j][5];
            String s = (Integer) this.data[j][3] > 1 ? "s" : "";
            int nameId = (int) this.data[j][6];
            int barsId = (int) this.data[j][7];
            if (name != null) {
                player.getFrames().sendItem(312, child, -1, count, (Item) this.data[j][0]);
                player.getFrames().modifyText(canSmith ? "<col=FFFFFF>" + name : name, 312, nameId);
                if (!barColour.equals("")) {
                    player.getFrames().modifyText(barColour + amount + " Bar" + s, 312, barsId);
                }
            } else {
                player.getFrames().modifyText("", 312, nameId);
                player.getFrames().modifyText("", 312, barsId);
            }
            if (count == 4) {
                count = 0;
                child++;
            } else {
                count++;
            }
        }
        player.getInterfaceSettings().openInterface(312);
    }

    private Object[][] getDataForIndex() {
        switch (barIndex) {
            case 0:// bronze
                return SmithBarData.BRONZE;
            case 2:// iron
                return SmithBarData.IRON;
            case 4:// steel
                return SmithBarData.STEEL;
            case 6:// mith
                return SmithBarData.MITHRIL;
            case 7:// adamant
                return SmithBarData.ADAMANT;
            case 8:// rune
                return SmithBarData.RUNE;
        }
        return null;
    }

    public void handleButtons(final int interId, final int button, final int slot) {
        boolean firstRow = button == 146;
        boolean secondRow = button == 147;
        boolean thirdRow = button == 148;
        boolean fourthRow = button == 149;
        boolean fifthRow = button == 150;
        boolean lastRow = button == 151;
        if (firstRow) {
            armourType = slot;
        } else if (secondRow) {
            armourType = 5 + slot;
        } else if (thirdRow) {
            armourType = 10 + slot;
        } else if (fourthRow) {
            armourType = 15 + slot;
        } else if (fifthRow) {
            armourType = 20 + slot;
        } else if (lastRow) {
            armourType = 25 + slot;
        }
        player.getInterfaceSettings().closeInterfaces(false);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (armourType > -1) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < (int) data[armourType][1]) {
                player.getFrames().sendMessage(
                    "You need a Smithing level of atleast " + data[armourType][1] + " to smith this.");
                return false;
            }
            if (!player.getInventory().hasItem(SmithBarData.HAMMER)) {
                player.getFrames().sendMessage("You don't have a hammer.");
                return false;
            }
            if (!player.getInventory().hasItemAmount(SmithBarData.BARS[barIndex], (int) data[armourType][3])) {
                String s = (int) data[armourType][3] > 1 ? "s" : "";
                player.getFrames().sendMessage("You need atleast " + data[armourType][3] + " "
                    + ItemDefinition.forId(SmithBarData.BARS[barIndex]).getName() + "" + s + " to make that item.");
                return false;
            }
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        if (armourType > -1) {
            player.playAnimation(898, Animation.AnimationPriority.HIGH);
        }
    }

    @Override
    public void rewards() {
        if (armourType > -1) {
            for (int i = 0; i < (int) data[armourType][3]; i++) {
                player.getInventory().deleteItem(SmithBarData.BARS[barIndex]);
            }
            player.getInventory().addItem((Item) data[armourType][0]);
            player.getInventory().refresh();
            player.getSkills().addExperience(SKILL_TYPE(), exp());
        }
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.SMITHING;
    }

    @Override
    public short speed() {
        return 2;
    }

    @Override
    public double exp() {
        return (double) data[armourType][4];
    }
}
