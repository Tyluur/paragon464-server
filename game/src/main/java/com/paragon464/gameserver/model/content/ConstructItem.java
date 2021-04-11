package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.skills.smithing.ItemMaking;
import com.paragon464.gameserver.model.item.Item;

public class ConstructItem {

    public static void check(Player player, int itemOne, int itemTwo) {
        if (itemOne == 233 && itemTwo == 243) {//dragon dust making
            player.getInventory().deleteItem(itemTwo);
            player.getInventory().addItem(new Item(241, 1));
            player.getInventory().refresh();
        } else if (itemOne == 987 && itemTwo == 985) {// crystal keys
            player.getInventory().deleteItem(itemOne);
            player.getInventory().deleteItem(itemTwo);
            player.getInventory().addItem(new Item(989, 1));
            player.getInventory().refresh();
        } else if (itemOne == 2366 && itemTwo == 2368) {// dragon sq
            player.getInventory().deleteItem(2366);
            player.getInventory().deleteItem(2368);
            player.getInventory().addItem(new Item(1187, 1));
            player.getInventory().refresh();
        } else if (itemOne == 11702 && itemTwo == 11690) {// ags
            player.getInventory().deleteItem(11702);
            player.getInventory().deleteItem(11690);
            player.getInventory().addItem(new Item(11694, 1));
            player.getInventory().refresh();
        } else if (itemOne == 11704 && itemTwo == 11690) {// bgs
            player.getInventory().deleteItem(11704);
            player.getInventory().deleteItem(11690);
            player.getInventory().addItem(new Item(11696, 1));
            player.getInventory().refresh();
        } else if (itemOne == 11706 && itemTwo == 11690) {// sgs
            player.getInventory().deleteItem(11706);
            player.getInventory().deleteItem(11690);
            player.getInventory().addItem(new Item(11698, 1));
            player.getInventory().refresh();
        } else if (itemOne == 11708 && itemTwo == 11690) {// zgs
            player.getInventory().deleteItem(11708);
            player.getInventory().deleteItem(11690);
            player.getInventory().addItem(new Item(11700, 1));
            player.getInventory().refresh();
        } else if (itemOne == 11710 && itemTwo == 11712 || itemOne == 11712 && itemTwo == 11714
            || itemOne == 11686 && itemTwo == 11714 || itemOne == 11710 && itemTwo == 11692 ||
            itemOne == 11712 && itemTwo == 11688) {// shards
            ItemMaking.makeGSBlade(player, itemOne, itemTwo);
        }
    }
}
