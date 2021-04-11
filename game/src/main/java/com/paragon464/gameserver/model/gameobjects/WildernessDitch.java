package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class WildernessDitch {

    public static void jump(final Player player, final GameObject ditch) {
        player.getAttributes().set("stopActions", true);
        final Position playerLoc = player.getPosition();
        player.playAnimation(6132, Animation.AnimationPriority.HIGH);
        Position before_ditch = new Position(ditch.getPosition().getX(), ditch.getPosition().getY() - 1, 0);
        Position after_ditch = new Position(ditch.getPosition().getX(), ditch.getPosition().getY() + 2, 0);
        final Position toTile = playerLoc.equals(before_ditch) ? after_ditch : before_ditch;
        final int dir = playerLoc.getY() >= 3523 ? ForceMovement.SOUTH : ForceMovement.NORTH;
        player.getVariables().setNextForceMovement(new ForceMovement(playerLoc, 33, toTile, 60, dir));
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.FORCE_MOVEMENT);
        player.submitTickable(new Tickable(1, true) {
            int count = 4;

            @Override
            public void execute() {
                if (count > 0) {
                    count--;
                }
                if (count == 2) {
                    player.teleport(toTile);
                    player.getAttributes().remove("stopActions");
                    this.stop();
                }
            }
        });
    }
}
