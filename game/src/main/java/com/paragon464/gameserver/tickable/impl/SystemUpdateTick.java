package com.paragon464.gameserver.tickable.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;

public class SystemUpdateTick extends Tickable {

    public SystemUpdateTick(int ticks) {
        super(ticks, true);
    }

    @Override
    public void execute() {
        int timer = World.getWorld().getEngine().getSystemUpdateTimer();
        World.getWorld().getEngine().deductSystemTimer(1);
        if (timer == 10) {
            for (Player players : World.getWorld().getPlayers()) {
                if (players != null) {
                    players.getFrames().forceLogout();
                }
            }
        } else if (timer <= 0) {
            this.stop();
        }
    }
}
