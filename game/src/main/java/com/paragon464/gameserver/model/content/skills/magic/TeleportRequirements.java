package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.minigames.fightcaves.CavesBattleSession;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

public class TeleportRequirements {

    public static boolean prevent(final Player player, final Position pos, final Item item) {
        int wildernessReq = 20;
        if (item != null) {
        	if (!player.getControllerManager().processItemTeleport(pos)) {
        		return true;
        	}
            if (item.getId() >= 1704 && item.getId() <= 1712) {//Glory
                wildernessReq = 30;
            }
        } else if (item == null) {//regular teleporting
        	if (!player.getControllerManager().processMagicTeleport(pos)) {
        		return true;
        	}
        }
        if (player.getCombatState().isTeleblocked()) {
            return true;
        }
        if (player.getWildLevel() > wildernessReq) {
            player.getFrames().sendMessage("You can't teleport above " + wildernessReq + " Wilderness.");
            return true;
        }
        if (MinigameHandler.minigameArea(player)) {
            player.getFrames().sendMessage("You can't teleport out of here.");
            return true;
        }
        CavesBattleSession caves_session = player.getAttributes().get("caves_session");
        if (caves_session != null) {
            player.getFrames().sendMessage("Use the exit! You can't teleport from here.");
            return true;
        }
        if (Areas.atWarriorsGuild(player.getPosition(), true)) {
            player.getFrames().sendMessage("Use the exit! You can't teleport from here.");
            return true;
        }
        if (Areas.inPestBoat(player.getPosition())) {
            return true;
        }
        if (player.getAttributes().isSet("stopActions")) {
            return true;
        }
        return player.getCombatState().isDead();
    }
}
