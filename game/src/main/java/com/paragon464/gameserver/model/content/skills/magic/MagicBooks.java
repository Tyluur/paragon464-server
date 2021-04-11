package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.skills.Loaders;

public class MagicBooks {

    public static void handleClicking(Player player, int interfaceId, int button) {
        int bookType = player.getSettings().getMagicType();
        switch (interfaceId) {
            case 430:// Lunars
                if (bookType != 3)
                    break;
                switch (button) {
                    case 16:
                        Loaders.Teleports.home_teleport(player);
                        break;
                    case 14:
                        Vengeance.cast(player);
                }
            case 193:// ancients
                if (bookType != 2)
                    break;
                switch (button) {
                    case 24:
                        Loaders.Teleports.home_teleport(player);
                        break;
                    case 16:
                        Loaders.Teleports.teleport(player, "ancients_paddewwa");
                        break;
                    case 17:
                        // Loaders.Teleports.teleport(player, "ancients_senntisten");
                        break;
                    case 18:
                        Loaders.Teleports.teleport(player, "ancients_kharyrll");
                        break;
                    case 19:
                        // Loaders.Teleports.teleport(player, "ancients_lassar");
                        break;
                    case 20:
                        Loaders.Teleports.teleport(player, "ancients_dareeyak");
                        break;
                    case 21:
                        Loaders.Teleports.teleport(player, "ancients_carrallangar");
                        break;
                    case 22:
                        Loaders.Teleports.teleport(player, "ancients_annakarl");
                        break;
                    case 23:
                        Loaders.Teleports.teleport(player, "ancients_ghorrock");
                        break;
                }
                break;
            case 192:// modern
                if (bookType != 1)
                    break;
                switch (button) {
                    case 0:
                        Loaders.Teleports.home_teleport(player);
                        break;
                    case 3:
                        player.getInterfaceSettings().openInterface(432);
                        break;
                    case 58:
                        ChargeSpell.cast(player);
                        break;
                }
                break;
        }
    }
}
