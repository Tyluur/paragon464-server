package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.content.skills.magic.StaffInterface;

public class AttackStyles {

    public static boolean handleAttackStyles(Player player, int interfaceId, int buttonId) {
        switch (interfaceId) {
            case 90:// Staffs interface
            case 85:// wand interface
                switch (buttonId) {
                    case 5:// selecting spell
                        StaffInterface.openAutoCastInterface(player);
                        break;
                    case 9:// Auto retal
                    case 24:// Auto retal
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;
            case 92: // Unarmed attack interface.
                switch (buttonId) {
                    case 24: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;
            case 87://Guthans spear
                switch (buttonId) {
                    case 26:
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                break;
            case 84: // Spear attack interface.
                switch (buttonId) {
                    case 8: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 24: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 93: // Whip attack interface.
                switch (buttonId) {
                    case 8: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 24: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 89: // Dagger attack interface.
                switch (buttonId) {
                    case 10: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 26: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 82: // Longsword/scimitar attack interface.
                switch (buttonId) {
                    case 10: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 26: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 78: // Claw attack interface.
                switch (buttonId) {
                    case 10: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 26: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 81: // Godsword attack interface.
                switch (buttonId) {
                    case 10: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 26: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 88: // Mace attack interface.
                switch (buttonId) {
                    case 10: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;

                    case 26: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 76: // Granite maul attack interface.
                switch (buttonId) {
                    case 8: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;
                    case 24: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;
            case 473://Chinchompa
            case 79:// crossbow
                switch (buttonId) {
                    case 24:
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    case 8:
                        player.getSettings().toggleSpecBar();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 77: // Bow attack interface.
                switch (buttonId) {
                    case 8: // Special attack.
                        player.getSettings().toggleSpecBar();
                        break;
                    case 24: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 75: // Battleaxe attack interface.
                switch (buttonId) {
                    case 10: // Special attack.
                        player.getSettings().toggleSpecBar();
                        // player.getSettings().dragonBattleaxe();
                        break;
                    case 26: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;
                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;

            case 91: // Thrown weapon
                switch (buttonId) {
                    case 8:
                        player.getSettings().toggleSpecBar();
                        break;
                    case 24: // Auto retaliate.
                        player.getSettings().toggleAutoRetaliate();
                        break;

                    default:
                        AttackInterfaceConfig.configureButton(player, interfaceId, buttonId);
                        break;
                }
                return true;
        }
        return false;
    }
}
