package com.paragon464.gameserver.model.content.imbuements;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;

public class Imbuements {

    private Imbuements() {

    }

    public static boolean imbueItem(final Player player, final Item used, final Item usedWith) {
        var imbuable = Imbuables.imbuableForItems(used, usedWith);
        imbuable.ifPresent(imbue -> {
            player.getInventory().deleteItem(usedWith);
            player.getInventory().replaceItem(used.getId(), imbue.getReplacementId());
            player.getInventory().refresh();
            player.getFrames().sendMessage("The ring begins to glow...");
            player.getFrames().sendMessage("After a quick flash of light, the ring appears to have been replaced.");
        });
        return imbuable.isPresent(); // ghetto
    }
}
