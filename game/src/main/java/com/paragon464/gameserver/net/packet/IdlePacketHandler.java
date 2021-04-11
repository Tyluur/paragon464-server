package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;

public class IdlePacketHandler implements PacketHandler {

    /**
     * Handles the idle packet.
     * In this case, it logs the player out if debug mode is not enabled and the player is not in combat.
     *
     * @param player The player who is sending this packet.
     * @param packet The packet being sent to the server.
     */
    @Override
    public void handle(final Player player, final Packet packet) {
        if (!canExecute(player, packet) || Config.DEBUG_MODE) {
            return;
        }

        player.getFrames().forceLogout();
    }

    /**
     * Determines whether or not we should continue handling the packet.
     *
     * @param player The player who is sending this packet.
     * @param packet The packet being sent to the server.
     * @return {@code true} if the player is not in a combat situation. Otherwise, {@code false}.
     */
    @Override
    public boolean canExecute(final Player player, final Packet packet) {
        return player.getCombatState().outOfCombat() && !player.getAttributes().is("stopActions");
    }
}
