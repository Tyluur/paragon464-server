package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;

/**
 * Packets relating to client operation.
 *
 * @author Luke132
 */
public class ClientAction implements PacketHandler {

    private static final int IDLE = 91; // d
    private static final int MOVE_CAMERA = 21; // d
    private static final int PING = 93; // d
    private static final int FOCUS = 22; // d
    private static final int CLICK_MOUSE = 99; // d
    private static final int WINDOW_TYPE = 243; // d
    private static final int SOUND_SETTINGS = 98; // d
    private static final int MAP_REGION = 174;

    @Override
    public void handle(Player player, Packet packet) {
        switch (packet.getOpcode()) {
            case MAP_REGION:
                //handleChangingMapRegion(player, packet);
                break;
            case CLICK_MOUSE:
                handleMouseClicking(player, packet);
                break;
            case IDLE:
                //handleIdleLogout(player, packet);
                break;
            case MOVE_CAMERA:
            case PING:
            case FOCUS:
            case SOUND_SETTINGS:
                break;

            case WINDOW_TYPE:
                windowModeInformation(player, packet);
                break;
        }
    }

    private void handleMouseClicking(Player player, Packet packet) {
        packet.getLong();
        packet.get();
    }

    private void windowModeInformation(Player player, Packet packet) {
        int windowType = packet.getByte() & 0xff;
        int windowWidth = packet.getShort();
        int windowHeight = packet.getShort();
        int antiAlias = packet.getByte() & 0xff;
        boolean changed = (player.getSettings().windowType != windowType);
        player.getSettings().windowType = windowType;
        if (changed) {
            player.getFrames().sendGameScreen();
        }
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }

    private void handleIdleLogout(Player player, Packet packet) {
        if (player.getDetails().isAdmin()) {
            return;
        }
        player.getFrames().sendLogout();
    }

    private void handleChangingMapRegion(Player player, Packet packet) {
    }
}
