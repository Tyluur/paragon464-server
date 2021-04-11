package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.io.database.table.log.CommandTable;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.packets.Commands;
import com.paragon464.gameserver.model.entity.mob.player.packets.DevCommands;
import com.paragon464.gameserver.net.Packet;

/**
 * @author Luke132
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class CommandHandler implements PacketHandler {

    @Override
    public void handle(final Player player, Packet packet) {
        String command = packet.getRS2String().toLowerCase();
        int rights = player.getDetails().getRights();
        if (rights >= 0) {// Global commands
            Commands.handleGlobalCommands(player, command);
        }
        if (rights >= 1) {// Moderator commands
            Commands.handleModeratorCommands(player, command);
        }
        if (rights >= 2) {// Administrator commands
            Commands.handleAdministratorCommands(player, command);
            if (Config.DEBUG_MODE || player.getDetails().getName().toLowerCase().matches("(nando|omar|chris)")) {
                DevCommands.execute(player, command);
            }
        }
        CommandTable.save(player, command);
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
