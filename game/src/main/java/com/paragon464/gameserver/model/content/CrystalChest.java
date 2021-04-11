package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.util.NumberUtils;

public class CrystalChest {

    private static final int KEY = 989;

    private static final int UNCUT_DRAGONSTONE = 1631;

    private static Item[] LOW_REWARDS = {
        // runes
        new Item(561, 50), new Item(560, 50), new Item(562, 50), new Item(563, 50), new Item(555, 100),
        new Item(557, 100), new Item(554, 100), new Item(556, 100),
        //
    };

    private static Item[] MED_REWARDS = {

        // half keys
        new Item(995, 20000), new Item(986, 1), new Item(988, 1),
        // rune platelegs/skirt
        new Item(1079, 1), new Item(1093, 1),
        // iron ore
        new Item(441, 50),
        // uncuts
        new Item(1618, 10), new Item(1620, 10),
        // rune bar
        new Item(2364, 10),
    };

    public static void execute(final Player player) {
        if (player.getInventory().hasItem(KEY)) {
            player.getInventory().deleteItem(KEY);
            player.getInventory().addItem(UNCUT_DRAGONSTONE);
            int low_chance = NumberUtils.random(3);
            int med_chance = NumberUtils.random(10);
            Item reward = null;
            if (low_chance < 2) {
                reward = LOW_REWARDS[NumberUtils.random(LOW_REWARDS.length - 1)];
            } else if (med_chance <= 4) {
                reward = MED_REWARDS[NumberUtils.random(MED_REWARDS.length - 1)];
            }
            if (reward != null) {
                if (player.getInventory().addItem(reward)) {
                    player.getInventory().refresh();
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(reward, player));
                }
            }
        } else {
            player.getFrames().sendMessage("You need a Crystal key.");
        }
    }
}
