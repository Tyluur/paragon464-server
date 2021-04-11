package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.io.database.table.log.PacketTable;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.net.Packet;

/**
 * Handles walking packets.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 * @author Luke132
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class Walk implements PacketHandler {

    @Override
    public void handle(Player player, Packet packet) {
        if (!canExecute(player, packet)) {
            return;
        }
        resetWalkingActions(player, packet);
        boolean noclip = false;
        int size = packet.getLength();
        if (packet.getOpcode() == 143) {
            size -= 14;
        }
        final int steps = (size - 5) / 2;
        if (steps < 0)
            return;
        final int[][] path = new int[steps][2];
        final boolean runSteps = packet.getByteS() == 1;
        int endY = packet.getLEShort();
        int endX = packet.getShortA();
        for (int i = 0; i < steps; i++) {
            path[i][0] = packet.getByteS();
            path[i][1] = packet.getByteS();
        }
        player.getWalkingQueue().setRunningQueue(runSteps);
        if (steps > 0) {
            endX += path[steps - 1][0];
            endY += path[steps - 1][1];
        }
        if (endX < 0 || endY < 0) {
            return;
        }
        if (player.getAppearance().isNpc()) {
            player.executeEntityPath(endX, endY);
        } else {
            if (!noclip) {
                player.executeEntityPath(endX, endY);
            } else {
                player.getWalkingQueue().addStep(endX, endY);
                player.getWalkingQueue().finish();
            }
        }
        Position playerLoc = player.getPosition();
        PacketTable.save(player, "Walked:  START[x: " + playerLoc.getX() + ", y: " + playerLoc.getY() + ", z: " + playerLoc.getZ() + "], END[x: " + endX + ", y: " + endY + ", z: " + playerLoc.getZ() + "]");
        player.getInterfaceSettings().closeInterfaces(false);
    }

    private void resetWalkingActions(Player player, Packet packet) {
        player.resetActionAttributes();
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        if (player.getAttributes().isSet("stopActions")) {
            player.getFrames().clearMapFlag();
            return false;
        }
        if (player.getCombatState().isDead()) {
            return false;
        }
        if (player.getCombatState().isFrozen()) {
            player.getFrames().sendMessage("A magical force stops you from moving.");
            return false;
        }
        return true;
    }
}
