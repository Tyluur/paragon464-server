package com.paragon464.gameserver.model.content.skills.smithing;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class SmeltingAction extends AbstractSkillAction {

    private int barIndex = -1;

    public SmeltingAction(Player player) {
        this.player = player;
        displayInterface();
    }

    private void displayInterface() {
        int gfxChild = 4;
        int textChild = 16;
        String s = "<br><br><br><br>";
        for (int i = 0; i < SmeltBarData.BARS.length; i++) {
            boolean enabled = player.getSkills().getCurrentLevel(SKILL_TYPE()) >= SmeltBarData.SMELT_LEVELS[i];
            String colour = enabled ? "<col=000000>" : "<col=A00000>";
            player.getFrames().itemOnInterface(311, gfxChild, 130, SmithBarData.BARS[i]);
            player.getFrames().modifyText(s + colour + SmithBarData.BAR_NAMES[i], 311, textChild);
            gfxChild++;
            textChild += 4;
        }
        player.getFrames().sendChatboxInterface(311);
    }

    public void handleButtons(final int interId, final int button, final int slot) {
        if (interId != 311) {
            return;
        }
        int counter = 0, barIndex = 0;
        for (int i = 13; i <= 48; i++) {
            if (counter == 4) {
                barIndex++;
                counter = 0;
            }
            if (button == i) {
                this.barIndex = barIndex;
                break;
            }
            counter++;
        }
        player.getInterfaceSettings().closeInterfaces(false);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (barIndex > -1) {
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < SmeltBarData.SMELT_LEVELS[barIndex]) {
                player.getFrames().sendMessage(
                    "You need a Smithing level of " + SmeltBarData.SMELT_LEVELS[barIndex] + " to make that bar.");
                return false;
            }
            int[] ores = SmeltBarData.SMELT_ORES[barIndex];
            for (int i = 0; i < ores.length; i++) {
                int id = ores[i];
                int amount = SmeltBarData.SMELT_ORE_AMT[barIndex][i];
                if (amount > 0) {
                    if (!player.getInventory().hasItemAmount(id, amount)) {
                        player.getFrames().sendMessage(
                            "You don't have enough " + ItemDefinition.forId(id).getName() + " to make that bar.");
                        return false;
                    }
                }
            }
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        if (barIndex > -1) {
            player.playAnimation(3243, Animation.AnimationPriority.HIGH);
        }
    }

    @Override
    public void rewards() {
        if (barIndex > -1) {
            int[] ores = SmeltBarData.SMELT_ORES[barIndex];
            for (int i = 0; i < ores.length; i++) {
                int id = ores[i];
                int amount = SmeltBarData.SMELT_ORE_AMT[barIndex][i];
                if (amount > 0) {
                    for (int j = 0; j < amount; j++) {
                        player.getInventory().deleteItem(id, 1);
                    }
                }
            }
            player.getInventory().addItem(SmeltBarData.BARS[barIndex]);
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
        return 1;
    }

    @Override
    public double exp() {
        return SmeltBarData.SMELT_XP[barIndex];
    }
}
