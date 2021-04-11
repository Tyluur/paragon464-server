package com.paragon464.gameserver.model.content.skills.prayer;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;

public class PrayerAltars {

    public static boolean isPrayerAltar(final Player player, final GameObject object) {
        if (object.getDefinition().hasAction("Pray-at")) {
            if (object.getDefinition().name.equals("Altar") && object.getId() != 6552) {
                restorePrayer(player);
                return true;
            } else if (object.getDefinition().name.equals("Gorilla Statue")) {
                restorePrayer(player);
                return true;
            }
        }
        return false;
    }

    private static void restorePrayer(Player player) {
        if (player.getSettings().getPrayerPoints() < player.getSkills().getLevel(SkillType.PRAYER)) {
            player.playAnimation(645, AnimationPriority.HIGH);
            player.getSkills().setCurrentLevel(SkillType.PRAYER, player.getSkills().getLevel(SkillType.PRAYER));
            player.getFrames().sendMessage("You fully recharge your prayer points.");
        } else {
            player.getFrames().sendMessage("You already have full prayer points.");
        }
    }
}
