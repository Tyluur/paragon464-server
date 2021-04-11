package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;

/**
 * An interface which describes a class that handles packets.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public interface PacketHandler {

    /**
     * Handles a single packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    void handle(Player player, Packet packet);

    /**
     * Can this packet continue
     *
     * @param player
     * @return
     */
    boolean canExecute(Player player, Packet packet);
}
