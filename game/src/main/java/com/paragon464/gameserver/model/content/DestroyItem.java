package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;

public class DestroyItem {

    public static void open(final Player player, final Item item) {
        player.getAttributes().set("destroyable_item", item);
        player.getFrames().modifyText(94, 3, "Are you sure you want to destroy this item?");
        player.getFrames().modifyText(94, 11, "This item is valuable, you will not");
        player.getFrames().modifyText(94, 12, "get it back once lost.");
        player.getFrames().modifyText(94, 13, "" + item.getDefinition().getName());
        player.getFrames().sendItem(94, 0, 2, 0, item);
        player.getFrames().sendInterfaceVisibility(94, 8, false);
        player.getFrames().sendInterfaceVisibility(94, 9, true);
        player.getInterfaceSettings().setChatboxOverlay(94);
    }

    public static void handle(final Player player, int button) {
        final Item destroy = player.getAttributes().get("destroyable_item");
        if (destroy != null) {
            if (button == 4) {//yes
                if (player.getInventory().hasItem(destroy.getId())) {
                    player.getInventory().deleteItem(destroy.getId());
                    player.getInventory().refresh();
                    player.getAttributes().remove("destroyable_item");
                    player.getInterfaceSettings().restoreChatbox();
                }
            } else if (button == 5) {//no
                player.getAttributes().remove("destroyable_item");
                player.getInterfaceSettings().restoreChatbox();
            }
        }
    }
}
