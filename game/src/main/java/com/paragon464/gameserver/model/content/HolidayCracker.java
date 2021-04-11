package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.util.NumberUtils;

public class HolidayCracker {

    private static final int HOLIDAY_CRACKER = 962;

    private static Item[][] ITEMS = {
        //LOW
        {
            /*BOBBLE HAT & SCARF*/new Item(6856, 1), new Item(6857, 1),
            /*JESTER HAT & SCARF*/new Item(6858, 1), new Item(6859, 1),
            /*TRI-JESTER HAT & SCARF*/new Item(6860, 1), new Item(6861, 1),
            /*WOOLLY HAT & SCARF*/new Item(6862, 1), new Item(6863, 1),
            /*GNOME GOGGLES & SCARF*/new Item(9472, 1), new Item(9946, 1), new Item(9470, 1),
        },
        //MED
        {/*CHICKEN SUITE*/new Item(4566, 1), new Item(11021, 1), new Item(11020, 1), new Item(11022, 1), new Item(11019, 1),
            /*cosmetic casket*/ new Item(2714, 1),
        },
        //HIGH
        {/*SANTA ITEMS*/new Item(1050, 1), new Item(14595, 1), new Item(14603, 1), new Item(14602, 1), new Item(14605, 1),
            /*H'MASKS*/new Item(1053, 1), new Item(1055, 1), new Item(1057, 1),
            /*PARTYHATS*/new Item(1038, 1), new Item(1040, 1), new Item(1042, 1), new Item(1044, 1), new Item(1046, 1), new Item(1048, 1),
            /*BUNNY EARS*/new Item(1037, 1),
            /*Scythe*/new Item(1419, 1),
            /*EASTER Egg*/new Item(1961, 1),
            /*EASTER RING*/new Item(7927, 1),
        },
    };

    public static void pull(final Player player) {
        int roll = NumberUtils.random(5);
        boolean rare = roll == 0;
        Item item = null;
        if (player.getInventory().deleteItem(HOLIDAY_CRACKER)) {
            if (rare) {
                item = ITEMS[2][NumberUtils.random(ITEMS[2].length - 1)];
            } else {
                if (roll >= 4) {
                    item = ITEMS[1][NumberUtils.random(ITEMS[1].length - 1)];
                } else {
                    item = ITEMS[0][NumberUtils.random(ITEMS[0].length - 1)];
                }
            }
            player.getInventory().addItem(item.getId());
            player.getInventory().refresh();
        }
    }
}
