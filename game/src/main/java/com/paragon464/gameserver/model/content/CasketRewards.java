package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CasketRewards {

    private static final int CASKET = 2714;

    private static Item[] rare_rewards = {new Item(10330), new Item(10332), new Item(10334), new Item(10336),
        new Item(10338), new Item(10340), new Item(10342), new Item(10344), new Item(10346), new Item(10348),
        new Item(10350), new Item(10352), new Item(2581), new Item(2577)};

    private static Item[] medium_rewards = {new Item(120246), new Item(10452), new Item(10454), new Item(10456), new Item(10446),
        new Item(10448), new Item(10450), new Item(2599), new Item(2601), new Item(2603), new Item(2605),
        new Item(2607), new Item(2609), new Item(2611), new Item(2613), new Item(3474), new Item(3475),
        new Item(7319), new Item(7321), new Item(7323), new Item(7325), new Item(7327), new Item(10400),
        new Item(10402), new Item(10404), new Item(10406), new Item(10408), new Item(10410), new Item(10412),
        new Item(10414), new Item(10416), new Item(10418), new Item(10420), new Item(10422), new Item(10424),
        new Item(10426), new Item(10428), new Item(10430), new Item(10432), new Item(10434), new Item(10436),
        new Item(10438), new Item(7370), new Item(7372), new Item(7374), new Item(7376), new Item(7378),
        new Item(7380), new Item(7382), new Item(7384), new Item(2577), new Item(2579), new Item(13107),
        new Item(13109), new Item(13111), new Item(13113), new Item(13115), new Item(7112), new Item(7124),
        new Item(7130), new Item(7136), new Item(13370), new Item(13372), new Item(13374), new Item(986, 6),
        new Item(988, 6), new Item(13354), new Item(15509), new Item(15503), new Item(15505), new Item(15507)};

    private static Item[] low_rewards = {new Item(2595), new Item(2591), new Item(2593), new Item(3473),
        new Item(2597), new Item(7390), new Item(7392), new Item(7394), new Item(7396), new Item(7386),
        new Item(7388), new Item(2583), new Item(2585), new Item(2587), new Item(2589), new Item(7362),
        new Item(7364), new Item(7366), new Item(7368), new Item(10392), new Item(10396), new Item(10392),
        new Item(10394), new Item(10398), new Item(2633), new Item(2635), new Item(2637), new Item(2631),
        new Item(10458), new Item(10460), new Item(10462), new Item(10464), new Item(10466), new Item(10468),
        new Item(10452), new Item(10454), new Item(10456), new Item(986, 3), new Item(988, 3)};

    public static void open_casket(Player player) {
        if (player.getInventory().deleteItem(CASKET)) {
            int medium_chance = NumberUtils.random(12);
            int rare_chance = NumberUtils.random(25);
            List<Item> items = Arrays.asList(low_rewards);
            Collections.shuffle(items);
            int randomized_index = NumberUtils.random(items.size() - 1);
            Item reward = items.get(randomized_index);
            if (medium_chance == 1) {
                items = Arrays.asList(medium_rewards);
                Collections.shuffle(items);
                randomized_index = NumberUtils.random(items.size() - 1);
                reward = items.get(randomized_index);
            } else if (rare_chance == 1) {
                items = Arrays.asList(rare_rewards);
                Collections.shuffle(items);
                randomized_index = NumberUtils.random(items.size() - 1);
                reward = items.get(randomized_index);
            }
            player.getFrames().sendClueScroll(new Item[]{reward});
            player.getInventory().addItem(reward);
            player.getInventory().refresh();
        }
    }
}
