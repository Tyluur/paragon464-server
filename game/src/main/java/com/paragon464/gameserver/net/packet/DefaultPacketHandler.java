package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports information about unhandled packets.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public final class DefaultPacketHandler implements PacketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPacketHandler.class);

    @Override
    public void handle(Player player, Packet packet) {
        LOGGER.debug("Unhandled Packet : [opcode={} length={} payload={}]", packet.getOpcode(), packet.getLength(), packet.getPayload());
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
