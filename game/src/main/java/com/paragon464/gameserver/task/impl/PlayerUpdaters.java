package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;

public class PlayerUpdaters implements Runnable {

    private Player player;

    public PlayerUpdaters(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            PlayerUpdateTask pu = new PlayerUpdateTask(player);
            pu.execute();
            NPCUpdateTask nu = new NPCUpdateTask(player);
            nu.execute();
        } catch (Exception e) {
            World.getWorld().handleError(e, player);
        }
    }
}
