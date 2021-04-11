package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import lombok.val;

public class ExpCounter {

    public static void toggleCounter(final Player player) {
        val isOpen = player.getAttributes().is("show_exp_counter");

        if (!isOpen) {
            player.getFrames().sendVarp(559, player.getVariables().getTotalExp());
        }

        player.getAttributes().set("show_exp_counter", !isOpen);
        player.getFrames().sendVarp(555, isOpen ? 1 : 0);
    }

    public static void resetTotalExp(final Player player) {
        player.getVariables().setTotalExp(0);
        player.getFrames().sendVarp(559, 0);
    }

    public static void showGainedExp(final Player player, int skill, double exp) {
        player.getVariables().increaseTotalExp((int) exp);

        if (player.getAttributes().is("show_exp_counter")) {
            player.getFrames().sendVarp(556, skill); // TODO: Replace skill icon with EXP icon
            player.getFrames().sendVarp(553, (int) exp);
            player.getFrames().sendVarp(559, player.getVariables().getTotalExp());
        }
    }
}
