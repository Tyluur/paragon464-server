package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.PacketBuilder;

public class PlayerOutgoingPacketTask implements Runnable {

    private Player player;

    public PlayerOutgoingPacketTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            if (player.getSession() == null) {
                return;
            }

            player.getSession().write(player.packets.toPacket());
            player.packets = new PacketBuilder(-1);
        } catch (Exception e) {
            World.getWorld().handleError(e, player);
        }
    }
}
