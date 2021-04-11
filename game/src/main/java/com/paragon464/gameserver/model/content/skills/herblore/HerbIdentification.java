package com.paragon464.gameserver.model.content.skills.herblore;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.Item;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * The Identification action of cleaning a grimy herb
 *
 * @author Reece <valiw@hotmail.com>
 * @since Thursday, October 29th. 2015.
 */
public class HerbIdentification {

    /**
     * The set of all elements from the herb data enumeration
     */
    private static final Set<HerbData> HERBS = Collections.unmodifiableSet(EnumSet.allOf(HerbData.class));

    /**
     * Manages the action of cleaning a herb
     *
     * @param player the player cleaning the herb
     * @param item   the herb being cleaned
     */
    public static boolean executing(Player player, Item item) {
        // Optional containg the herb data
        Optional<HerbData> data = HERBS.stream().filter(herb -> herb.getHerb().getId() == item.getId()).findFirst();
        if (data == null || !data.isPresent()) {
            return false;
        }
        // if data is present within the optional we try to complete the
        // indentification
        data.ifPresent(herb -> {
            if (herb.getRequired() > player.getSkills().getCurrentLevel(SkillType.HERBLORE)) {
                player.getFrames()
                    .sendMessage("You need a Herblore level of " + herb.getRequired() + " to clean this herb.");
                return;
            }
            player.getInventory().deleteItem(item);
            player.getInventory().addItem(herb.getClean());
            player.getInventory().refresh();
            player.getSkills().addExperience(SkillType.HERBLORE,
                herb.getExperience());
        });
        return true;
    }
}
