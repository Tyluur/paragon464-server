package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.tickable.Tickable;

public class ChargeSpell {

    public static void cast(final Player player) {
        if (player.getCombatState().isDead() || player.getAttributes().isSet("stopActions")) {
            return;
        }
        if (player.getSkills().getCurrentLevel(SkillType.MAGIC) < 80) {
            player.getFrames().sendMessage("You need a Magic level of 80 to cast this spell.");
            return;
        }
        Item[] runes = {new Item(554, 3), new Item(565, 3), new Item(556, 3)};
        if (!RuneReplacers.hasEnoughRunes(player, runes, true)) {
            return;
        }
        if (player.getCombatState().isCharged()) {
            player.getFrames().sendMessage("Your charge spell is already in effect.");
            return;
        }
        RuneReplacers.deleteRunes(player, runes);
        player.playAnimation(811, AnimationPriority.HIGH);
        player.playGraphic(301, 0, 100);
        player.getCombatState().setCharged(true);
        player.submitTickable(new Tickable(600) {
            @Override
            public void execute() {
                this.stop();
                player.getFrames().sendMessage("Your charge spell effect has wore off..");
                player.getAttributes().remove("charged_spell");
            }
        });
    }
}
