package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.io.database.table.log.PacketTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.RequestManager;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.MagicAction;
import com.paragon464.gameserver.model.content.combat.data.MagicData;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.net.Packet;

public class PlayerInteract implements PacketHandler {

    private static final int ATTACK = 84;
    private static final int FOLLOW = 180;
    private static final int TRADE = 183;
    private static final int MAGIC_ON_PLAYER = 123;

    @Override
    public void handle(Player player, Packet packet) {
        if (!canExecute(player, packet)) {
            return;
        }
        player.getCombatState().end(1);
        player.resetActionAttributes();
        switch (packet.getOpcode()) {
            default:
                handleActions(player, packet);
                break;
        }
    }

    private void handleActions(final Player player, Packet packet) {
        int index = -1;
        String interactType = "playerInteract[";
        switch (packet.getOpcode()) {
            case ATTACK:
                index = packet.getShort();
                interactType += "click-attack]";
                break;
            case FOLLOW:
                index = packet.getShort() & 0xFFFF;
                interactType += "follow]";
                break;
            case TRADE:
                index = packet.getLEShortA() & 0xFFFF;
                interactType += "trade]";
                break;
            case MAGIC_ON_PLAYER:
                index = packet.getLEShortA();
                interactType += "magic-attack]";
                break;
        }
        if (index < 0 || index >= Config.PLAYER_LIMIT) {
            player.getWalkingQueue().reset();
            return;
        }
        final Player other = World.getWorld().getPlayer(index);
        if (other == null) {
            return;
        }

        if (player.getInterfaceSettings().getCurrentInterface() != -1) {
            player.getInterfaceSettings().closeInterfaces(false);
        }
        int otherX = other.getPosition().getX();
        int otherY = other.getPosition().getY();
        int otherZ = other.getPosition().getZ();
        int x = player.getPosition().getX(), y = player.getPosition().getY(), z = player.getPosition().getZ();
        player.setInteractingMob(other);
        player.getInterfaceSettings().closeInterfaces(false);
        if (packet.getOpcode() == ATTACK) {
        	if (!player.getControllerManager().processPlayerInteract(other, 1)) {
        		return;
        	}
            if (player.getAttributes().isSet("duelArea")) {
                if (!Areas.inDuelArenas(player.getPosition())) {
                    PathState state = player.executeVariablePath(other, 9, 0, 0, otherX, otherY);
                    if (!state.isRouteFound() || state.isRouteFound() && !state.hasReached()) {
                        player.getFrames().sendMessage("I can't reach that!");
                        player.getWalkingQueue().reset();
                        return;
                    }
                    if (player.getCombatState().isFrozen()) {
                        player.getWalkingQueue().reset();
                    }
                    if (!player.getPosition().isVisibleFrom(other.getPosition())) {
                        player.getWalkingQueue().reset();
                        return;
                    }
                    player.getRequestManager().sendRequest(other, RequestManager.RequestType.DUEL);
                } else {
                    CombatAction.beginCombat(player, other);
                }
            } else {
                CombatAction.beginCombat(player, other);
            }
            PacketTable.save(player, interactType + ", pos[x: " + x + ", y: " + y + ", z: " + z + "] other: [user:[" + other.getDetails().getName() + "], x: " + otherX + ", y: " + otherY + ", z: " + otherZ + "]");
        } else if (packet.getOpcode() == MAGIC_ON_PLAYER) {
        	if (!player.getControllerManager().processPlayerInteract(other, 1)) {
        		return;
        	}
            int junk = packet.getLEShort();
            int interfaceHash = packet.getLEInt();
            int spellBook = interfaceHash >> 16;
            int spell = interfaceHash & 0xffff;
            MagicAction.setNextSpell(player, spell, true);
            CombatAction.beginCombat(player, other);
            String spellInfo = "Spell[serverIndex: " + MagicData.getSpellIndex(player, spell) + ", clientIndex: " + spell + ", interfaceId: " + (interfaceHash >> 16) + "]";
            PacketTable.save(player, interactType + ", {user: " + other.getDetails().getName() + ", pos: " + other.getPosition() + "}, playerPos: " + player.getPosition() + ", " + spellInfo + ".");
        } else if (packet.getOpcode() == FOLLOW) {
        	if (!player.getControllerManager().processPlayerInteract(other, 2)) {
        		return;
        	}
            player.getFollowing().setFollowing(other, false);
            PacketTable.save(player, interactType + ", from[x: " + x + ", y: " + y + ", z: " + z + "] to [x: " + otherX + ", y: " + otherY + ", z: " + otherZ + "]");
        } else {
            PathState state = player.executeVariablePath(other, 9, 0, 0, otherX, otherY);
            if (!state.isRouteFound() || state.isRouteFound() && !state.hasReached()) {
                player.getFrames().sendMessage("I can't reach that!");
                player.getWalkingQueue().reset();
                return;
            }
            if (player.getCombatState().isFrozen()) {
                player.getWalkingQueue().reset();
            }
            if (!player.getPosition().isVisibleFrom(other.getPosition())) {
                player.getWalkingQueue().reset();
                return;
            }
            if (packet.getOpcode() == TRADE) {
            	if (!player.getControllerManager().processPlayerInteract(other, 3)) {
            		return;
            	}
                player.getRequestManager().sendRequest(other, RequestManager.RequestType.TRADE);
                PacketTable.save(player, interactType + ", pos[x: " + x + ", y: " + y + ", z: " + z + "] otherPos: [x: " + otherX + ", y: " + otherY + ", z: " + otherZ + "]");
            } else {
                PacketTable.save(player, interactType + ".");
            }
            player.setInteractingMob(other);
        }
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return !player.getAttributes().isSet("stopActions");
    }
}
