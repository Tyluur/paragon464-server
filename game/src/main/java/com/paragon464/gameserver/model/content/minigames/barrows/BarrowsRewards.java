package com.paragon464.gameserver.model.content.minigames.barrows;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class BarrowsRewards {

    private static Item[] BARROWS = {
        // verac
        new Item(4753, 1), new Item(4755, 1), new Item(4757, 1), new Item(4759, 1),
        // dharok
        new Item(4716, 1), new Item(4718, 1), new Item(4720, 1), new Item(4722, 1),
        // torags
        new Item(4745, 1), new Item(4747, 1), new Item(4749, 1), new Item(4751, 1),
        // karils
        new Item(4732, 1), new Item(4734, 1), new Item(4736, 1), new Item(4738, 1),
        // guthans
        new Item(4724, 1), new Item(4726, 1), new Item(4728, 1), new Item(4730, 1),
        // ahrims
        new Item(4708, 1), new Item(4710, 1), new Item(4712, 1), new Item(4714, 1),};

    private static Item[] FOODS = {new Item(385, 10), new Item(380, 20), new Item(7947, 45), new Item(392, 8),
        new Item(15273, 5)};

    private static Item[] AMMO = {
        // runes
        new Item(554, 200), new Item(555, 200), new Item(556, 200), new Item(557, 200), new Item(558, 200),
        new Item(559, 200), new Item(560, 200), new Item(561, 200), new Item(562, 200), new Item(563, 200),
        new Item(564, 200), new Item(565, 200), new Item(566, 200),
        // arrows
        new Item(882, 2000), new Item(884, 1500), new Item(886, 1250), new Item(888, 1000), new Item(890, 850),
        new Item(892, 250),
        // bolts
        new Item(9142, 500), new Item(9144, 250), new Item(8882, 800),
        // knives
        new Item(863, 1000), new Item(868, 200),};

    private static Item[] MISC = {
        // gems
        new Item(1624, 20), new Item(1618, 10), new Item(1620, 10), new Item(1622, 20), new Item(1631, 3)};

    public static void dropRewards(final Player player, final NPC npc) {
        List<Item> rewards = new ArrayList<>();
        int barrowsChance = NumberUtils.random(6);
        if ((barrowsChance * 2) == 6) {
            rewards.add(BARROWS[NumberUtils.random(BARROWS.length - 1)]);
        }
        switch (NumberUtils.random(3)) {
            case 0:
                Item randomizedAmmo = AMMO[NumberUtils.random(AMMO.length - 1)];
                rewards.add(new Item(randomizedAmmo.getId(), NumberUtils.random(randomizedAmmo.getAmount())));
                break;
            case 1:
                Item randomizedFoods = FOODS[NumberUtils.random(FOODS.length - 1)];
                rewards.add(new Item(randomizedFoods.getId(), NumberUtils.random(randomizedFoods.getAmount())));
                break;
            case 2:
                Item randomizedMisc = MISC[NumberUtils.random(MISC.length - 1)];
                rewards.add(new Item(randomizedMisc.getId(), NumberUtils.random(randomizedMisc.getAmount())));
                break;
            case 3:
                rewards.add(BARROWS[NumberUtils.random(BARROWS.length - 1)]);
                break;
        }
        for (Item reward : rewards) {
            if (reward == null) continue;
            GroundItemManager.registerGroundItem(new GroundItem(reward, player, npc.getPosition()));
        }
    }
}
