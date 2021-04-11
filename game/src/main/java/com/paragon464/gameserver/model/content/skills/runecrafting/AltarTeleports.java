package com.paragon464.gameserver.model.content.skills.runecrafting;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;

public class AltarTeleports {

    private static final Position[] ALTAR_PLACEMENTS = {new Position(2843, 4828, 0), // Air
        new Position(3493, 4832, 0), // Water
        new Position(2657, 4829, 0), // Earth
        new Position(2575, 4848, 0), // Fire
        new Position(2793, 4829, 0), // Mind
        new Position(2142, 4853, 0), // Cosmic
        new Position(2464, 4819, 0), // Law
        new Position(2400, 4835, 0), // Nature
        new Position(2280, 4837, 0), // Chaos
        new Position(2207, 4832, 0), // Death
        new Position(2465, 4889, 1), // Blood
        new Position(2161, 3869, 0), // Astral
    };

    public static void open(final Player player) {
        player.getInterfaceSettings().openInterface(598);
    }

    public static void handleButtons(final Player player, final int button) {
        int index = button - 17;
        if (index < 0 || index > ALTAR_PLACEMENTS.length)
            return;
        Position altar = ALTAR_PLACEMENTS[index];
        player.teleport(altar);
        player.getInterfaceSettings().closeInterfaces(false);
    }
}
