package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;

public class IKOD {

    public static void display(Player p) {
        if (!p.getCombatState().outOfCombat()) {
            p.getFrames().sendMessage("You can't open this while in combat.");
            return;
        }
        if (p.getAttributes().isSet("new_account_verify")) {
            return;
        }
        int count = 3;
        if (p.getPrayers().isProtectingItem()) {
            count++;
        }
        if (p.getCombatState().isSkulled()) {
            count -= 3;
        }
        Container[] itemsKeptOnDeath = p.getItemsKeptOnDeath();
        Object[] keptItems = new Object[]{-1, -1, "null", 0, 0,
            (itemsKeptOnDeath[0].getSize() >= 4 && itemsKeptOnDeath[0].get(3) != null)
                ? itemsKeptOnDeath[0].get(3).getId() : -1,
            (itemsKeptOnDeath[0].getSize() >= 3 && itemsKeptOnDeath[0].get(2) != null)
                ? itemsKeptOnDeath[0].get(2).getId() : -1,
            (itemsKeptOnDeath[0].getSize() >= 2 && itemsKeptOnDeath[0].get(1) != null)
                ? itemsKeptOnDeath[0].get(1).getId() : -1,
            (itemsKeptOnDeath[0].getSize() >= 1 && itemsKeptOnDeath[0].get(0) != null)
                ? itemsKeptOnDeath[0].get(0).getId() : -1,
            count, 0};
        p.getWalkingQueue().reset();
        p.getFrames().clearMapFlag();
        p.resetActionAttributes();
        p.getInterfaceSettings().openInterface(102);
        p.getFrames().sendClickMask(0, 42, 102, 18, 210);
        p.getFrames().sendClickMask(0, 42, 102, 21, 210);
        p.getFrames().sendClickMask(0, 4, 102, 3, 211);
        p.getFrames().sendClientScript(118, keptItems, "iiooooiisii");
    }
}
