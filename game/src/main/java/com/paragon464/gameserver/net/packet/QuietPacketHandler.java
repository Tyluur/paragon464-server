package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;

/**
 * A packet handler which takes no action i.e. it ignores the packet.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class QuietPacketHandler implements PacketHandler {

    @Override
    public void handle(Player player, Packet packet) {
        // System.out.println("Unhandled Packet : [opcode=" + packet.getOpcode()
        // + " length=" + packet.getLength() + " payload=" + packet.getPayload()
        // + "]");
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
