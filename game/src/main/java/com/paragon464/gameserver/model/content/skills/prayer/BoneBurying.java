package com.paragon464.gameserver.model.content.skills.prayer;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.Item;

public class BoneBurying {

    public static boolean executing(final Player player, final Item item) {
        if (item.getDefinition().getName().toLowerCase().endsWith("bones")) {
            PrayerData.BONES bones = PrayerData.getBones(item);
            if (bones != null) {
                bury(player, bones);
            }
            return true;
        }
        return false;
    }

    private static void bury(final Player player, final PrayerData.BONES bones) {
        if (System.currentTimeMillis() - player.getAttributes().getLong("buried_last") < 1800)
            return;
        if (!player.getInventory().deleteItem(bones.getId()))
            return;
        player.getAttributes().set("buried_last", System.currentTimeMillis());
        player.getCombatState().end(1);
        player.playAnimation(827, AnimationPriority.HIGH);
        player.getInventory().refresh();
        player.getSkills().addExperience(SkillType.PRAYER,
            bones.getExp());
    }
}
