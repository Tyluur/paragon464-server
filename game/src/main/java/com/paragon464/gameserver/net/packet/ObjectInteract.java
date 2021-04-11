package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.skills.agility.AgilityHandler;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.pathfinders.ObjectPathFinder;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.net.Packet;

/**
 * Object clicking packets.
 *
 * @author Luke132
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Joker
 */
public class ObjectInteract implements PacketHandler {

    private static final int FIRST_CLICK = 44;
    private static final int SECOND_CLICK = 119;
    private static final int THIRD_CLICK = 120;
    private static final int FOURTH_CLICK = 247;
    private static final int EXAMINE_OBJECT = 176;

    @Override
    public void handle(Player player, Packet packet) {
        if (!canExecute(player, packet)) {
            return;
        }
        switch (packet.getOpcode()) {
            case EXAMINE_OBJECT:
                handleExamineObject(player, packet);
                break;
            default:
                handleActions(player, packet);
                break;
        }
    }

    @SuppressWarnings("unused")
    private void handleExamineObject(Player player, Packet packet) {
        int objectId = packet.getShort() & 0xffff;
    }

    private void handleActions(final Player player, Packet packet) {
        player.resetActionAttributes();
        int objectId = -1;
        int objectX = -1;
        int objectY = -1;
        int z = player.getPosition().getZ();
        int playerX = player.getPosition().getX();
        int playerY = player.getPosition().getY();
        int finalX = -1;
        int finalY = -1;
        int type = -1;
        String interactType = "objectInteract[";
        switch (packet.getOpcode()) {
            case FIRST_CLICK:
                objectId = packet.getInt();
                objectX = packet.getShort();
                objectY = packet.getLEShort();
                type = 1;
                interactType += "click-1]";
                break;
            case SECOND_CLICK:
                objectX = packet.getShort() & 0xFFFF;
                objectId = packet.getInt();
                objectY = packet.getLEShortA() & 0xFFFF;
                type = 2;
                interactType += "click-2]";
                break;
            case THIRD_CLICK:
                objectY = packet.getShort();
                objectId = packet.getInt();
                objectX = packet.getShortA();
                type = 3;
                interactType += "click-3]";
                break;
        }
        if (objectId == -1) {
            return;// by default don't continue
        }
        Position tile = new Position(objectX, objectY, z);
        final int regionId = tile.getRegionId();
        if (World.getRegion(player, regionId) == null) {
            return;
        }
        GameObject mapObject = World.getObjectWithId(tile, objectId);
        if (mapObject == null || mapObject.getId() != objectId) {
            return;
        }
        if (!player.getPosition().isWithinRadius(mapObject.getPosition(), 16)) {
            return;
        }
        interactType += ": OBJECT[" + mapObject.logString() + "], PLAYER[" + player.logString() + "]";
        player.face(tile);
        PathState state = ObjectPathFinder.executePath(player, mapObject);
        boolean courseCheck = AgilityHandler.isUsingCourse(player, mapObject);
        if (!courseCheck) {
            if (!state.isRouteFound() && mapObject.getId() != 12309 || (state.isRouteFound() && !state.hasReached())) {
                player.getWalkingQueue().reset();
                player.getFrames().sendMessage("I can't reach that!");
                player.resetActionAttributes();
                player.getFrames().clearMapFlag();
                return;
            }
        }
        if (player.getCombatState().isFrozen()) {
            player.getWalkingQueue().reset();
            if (state.getDest() != null) {
                if (playerX != state.getDest().getX() || playerY != state.getDest().getY()) {
                    player.getFrames().sendMessage("I can't reach that!");
                    player.resetActionAttributes();
                    return;
                }
            } else if (state.getPoints() != null) {
                if (state.getPoints().getFirst() != null) {
                    if ((playerX != state.getPoints().getFirst().getX() || playerY != state.getPoints().getFirst().getY())) {
                        player.getFrames().sendMessage("I can't reach that!");
                        player.resetActionAttributes();
                        return;
                    }
                }
            }
        }
        player.getInterfaceSettings().closeInterfaces(false);
        player.getAttributes().set("packet_interaction_type", type);
        player.getAttributes().set("object_interact", true);
        player.getAttributes().set("packet_object", mapObject);
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        if (player.getAttributes().isSet("stopActions")) {
            return false;
        }
        return !player.getCombatState().isDead();
    }
}
