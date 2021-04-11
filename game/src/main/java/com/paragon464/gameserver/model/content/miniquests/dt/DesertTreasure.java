package com.paragon464.gameserver.model.content.miniquests.dt;

import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;

public class DesertTreasure {

    public static int DAMIS_INDEX = 0;
    public static int DESSOUS_INDEX = 1;
    public static int FAREED_INDEX = 2;
    public static int KAMIL_INDEX = 3;

    public static void enterKamil(final Player player) {
    	if (player.getControllerManager().isMinigameOrMiniquest()) {
    		return;
    	}
    	player.getControllerManager().startController(new BattleController(new NPC(1913)));
    }

    public static void enterDessous(final Player player) {
    	if (player.getControllerManager().isMinigameOrMiniquest()) {
    		return;
    	}
    	player.getControllerManager().startController(new BattleController(new NPC(1914)));
    }

    public static void enterFareed(final Player player) {
    	if (player.getControllerManager().isMinigameOrMiniquest()) {
    		return;
    	}
    	player.getControllerManager().startController(new BattleController(new NPC(1977)));
    }

    public static void enterDamis(final Player player) {
    	if (player.getControllerManager().isMinigameOrMiniquest()) {
    		return;
    	}
    	player.getControllerManager().startController(new DamisBattle(new NPC(1974)));
    }
}
