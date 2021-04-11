package com.paragon464.gameserver.io.database;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class ReportAbuse {

    public static void sendReport(Player player, String name, int rule) {
        String previousName = player.getAttributes().get("last_reported_name");
        long lastReport = player.getAttributes().getLong("last_reported");
        if (previousName != null && previousName.equals(name)) {
            //TODO - timer for same player u are reporting
        } else {
            if (rule == 0) {
            }
        }
        player.getInterfaceSettings().closeInterfaces(false);
    }

    public static void open(Player player) {
        //if (!player.getAttributes().isSet("report_abuse")) {
            /*player.getAttributes().set("report_abuse", true);
            player.getInterfaceSettings().openInterface(553);
            player.getFrames().sendInterfaceVisibility(553, 16, true);
            player.getFrames().sendClientScript(136, new Object[] {}, "");*/
        //}
    }

    public static void close(Player player) {
        /*if (player.getAttributes().isSet("report_abuse")) {
            player.getAttributes().remove("report_abuse");
            player.getFrames().sendClientScript(80, new Object[] {}, "");
        }*/
    }

    public enum REPORT_TYPE {
        OFFENSIVE_LANGUAGE
    }
}
