package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;

public class Ladders {

    public static void executeLadder(final Player player, boolean down, final int x, final int y, final int z) {
        player.getAttributes().set("stopActions", true);
        if (!down) {// up
            player.playAnimation(828, Animation.AnimationPriority.HIGH);
        } else {// down
            player.playAnimation(827, Animation.AnimationPriority.HIGH);
        }
        player.submitTickable(new Tickable(2) {
            @Override
            public void execute() {
                this.stop();
                player.teleport(x, y, z);
                player.getAttributes().remove("stopActions");
            }
        });
    }
}
