package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class HalloweenReaper {

    /**
     * An array containing all the possible grim reaper messages.
     */
    private static final String[] REAPER_MESSAGES = {"There is no escape, %s...", "Muahahahaha!", "You belong to me!",
        "Beware mortals, %s travels with death!", "Your time here is over, %s!", "Now is the time you die, %s!",
        "I claim %s as my own!", "%s is mine!", "I have come for you, %s!"};

    /**
     * The full name of the current calendar month in American English.
     */
    private static final String CURRENT_MONTH = new SimpleDateFormat("MMMM", Locale.US)
        .format(Calendar.getInstance().getTime());

    /**
     * A default constructor to prevent instantiation.
     */
    private HalloweenReaper() {
    }

    /**
     * Spawns the grim reaper NPC for the specified player.
     *
     * @param player The player who has (more than likely) died.
     */
    public static void spawn(final Player player) {
        if (!CURRENT_MONTH.equals("October")) {
            return;
        }

        final NPC reaper = new NPC(2862);
        final Position pLoc = player.getPosition();
        final Position placement = new Position(pLoc.getX() + 1, pLoc.getY() + 1, pLoc.getZ());

        reaper.setPosition(placement);
        reaper.setLastKnownRegion(placement);
        World.getWorld().addNPC(reaper);

        reaper.setInteractingMob(player);
        reaper.playAnimation(382, AnimationPriority.HIGH);
        reaper.playForcedChat(String.format(REAPER_MESSAGES[NumberUtils.random(REAPER_MESSAGES.length - 1)],
            player.getDetails().getName()));

        reaper.submitTickable(new Tickable(10, false) {
            @Override
            public void execute() {
                this.stop();
                World.getWorld().unregister(reaper);
            }
        });
    }
}
