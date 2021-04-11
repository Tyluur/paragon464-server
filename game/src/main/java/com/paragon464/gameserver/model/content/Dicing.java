package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public final class Dicing {

    /**
     * The message that appears in the chat and over the player's head when dice are rolled.
     */
    private static final String DICING_MESSAGE = "%s rolled %d on the percentile dice.";

    /**
     * A default constructor to prevent instantiation.
     */
    private Dicing() {
    }

    /**
     * Rolls a random 0-100 number, and then prints the result to the surrounding players
     * and in the overhead chat of the specified player.
     *
     * @param player The player who has rolled their percentile dice.
     */
    public static void rollDice(final Player player) {
        if (!player.getAttributes().isSet("duelArea")) {
            player.getFrames().sendMessage("You're not allowed to dice outside of the Duel Arena.");
            return;
        }

        final int roll = NumberUtils.random(100);

        player.getCombatState().end(1);
        player.resetActionAttributes();
        player.getAttributes().set("stopActions", true);
        player.playGraphic(2075);
        player.playAnimation(11900, 15, AnimationPriority.HIGH);
        player.playForcedChat(String.format(DICING_MESSAGE, "I", roll));

        World.getWorld().submit(new Tickable(2) {
            @Override
            public void execute() {
                this.stop();
                player.getAttributes().remove("stopActions");

                for (Player p : World.getSurroundingPlayers(player.getPosition())) {
                    if (!player.getPosition().isWithinRadius(p, 5)) {
                        continue;
                    }

                    p.getFrames().sendMessage(String.format(DICING_MESSAGE, "[<col=432891>DICE-BAG</col>] "
                        + player.getDetails().getName(), roll));
                }
            }
        });
    }
}
