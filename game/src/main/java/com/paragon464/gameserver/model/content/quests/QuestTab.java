package com.paragon464.gameserver.model.content.quests;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.BeginnerTutorial;
import com.paragon464.gameserver.util.NumberUtils;
import com.paragon464.gameserver.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class QuestTab {

    private static final int QUEST_TAB_ID = 610;
    private static final int GAME_INFORMATION_ID = 274;

    public static void handle(Player player, int id, int button) {
        BeginnerTutorial tut = player.getAttributes().get("beginner_tutorial");
        if (tut != null)
            return;
        if (player.getAttributes().isSet("new_account_verify")) {
            return;
        }
        if (button == 10) {
            boolean isOnQuestTab = player.getAttributes().is("quest_tab_viewing");
            player.getAttributes().set("quest_tab_viewing", !isOnQuestTab);
            if (isOnQuestTab) {
                sendGameInformation(player);
            } else {
                sendQuests(player);
            }
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 67 : 88, isOnQuestTab ? GAME_INFORMATION_ID : QUEST_TAB_ID);
            player.getInterfaceSettings().closeInterfaces(false);
            return;
        }
        switch (id) {
            case 610://Quests
                switch (button) {
                    case 11://Hfd progress
                        QuestManager.progress(player, QuestManager.Quest.Horror_From_The_Deep);
                        break;
                    case 12://md prog
                        QuestManager.progress(player, QuestManager.Quest.Mountain_Daughter);
                        break;
                    case 13://gbr prog
                        QuestManager.progress(player, QuestManager.Quest.Great_Brain_Robbery);
                        break;
                    case 14://rfd prog
                        QuestManager.progress(player, QuestManager.Quest.Recipe_For_Disaster);
                        break;
                    case 15://dt prog
                        QuestManager.progress(player, QuestManager.Quest.Desert_Treasure);
                        break;
                }
                break;
            case 274://Information
                switch (button) {
                    case 11://Players online
                        displayPlayers(player);
                        break;
                    case 12://Staff online
                        displayStaff(player);
                        break;
                }
                break;
        }
    }

    public static void sendGameInformation(Player player) {// TODO - finish
        player.getFrames().sendInterfaceVisibility(GAME_INFORMATION_ID, 17, true);
        player.getFrames().modifyText("Players online: <col=00FF00>" + World.getWorld().getPlayerCount() + "", GAME_INFORMATION_ID, 11);
        player.getFrames().modifyText("Staff online: <col=00FF00>" + World.getWorld().getStaffCount() + "", GAME_INFORMATION_ID, 12);
        int uptime_seconds = (int) ((Utils.currentTimeMillis() - World.getWorld().engine.getCreationTime()) / 1000);
        int uptime_days = NumberUtils.getDays(uptime_seconds);
        uptime_seconds -= uptime_days * 60 * 60 * 24;
        int uptime_hours = NumberUtils.getHours(uptime_seconds);
        uptime_seconds -= uptime_hours * 60 * 60;
        int uptime_mins = NumberUtils.getMinutes(uptime_seconds);
        String uptime_days_string = uptime_days > 1 ? uptime_days + " Days " : uptime_days + " Day ";
        String uptime_hours_string = uptime_hours > 1 ? uptime_hours + " hours " : uptime_hours + " hour ";
        String uptime_mins_string = uptime_mins > 1 ? uptime_mins + " Mins " : uptime_mins + " Min ";
        StringBuilder uptime_builder = new StringBuilder();
        if (uptime_days > 0) {
            uptime_builder.append("<col=00FF00>").append(uptime_days_string).append("</col>");
        }
        if (uptime_hours > 0) {
            uptime_builder.append("<col=00FF00>").append(uptime_hours_string).append("</col>");
        }
        if (uptime_mins > 0 && uptime_days <= 0) {
            uptime_builder.append("<col=00FF00>").append(uptime_mins_string).append("</col>");
        }
        if (uptime_seconds > 0 && (uptime_days <= 0 && uptime_hours <= 0 && uptime_mins < 1)) {
            uptime_builder.append("<col=FF0000>Less than a min");
        }
        player.getFrames().modifyText("Uptime: <col=00FF00>" + uptime_builder.toString() + "", GAME_INFORMATION_ID, 13);
        String membersString = player.getDetails().isGoldMember() ? "<col=00FF00>True" : "<col=FF0000>False";
        player.getFrames().modifyText("Gold Members: " + (membersString), GAME_INFORMATION_ID, 14);
        int credits = player.getAttributes().getInt("credits");
        String creditsString = credits > 0 ? "<col=00FF00>" + credits + "" : "<col=FF0000>" + credits + "";
        player.getFrames().modifyText("Credits: " + (creditsString), GAME_INFORMATION_ID, 15);
        int vote_points = player.getAttributes().getInt("vote_points");
        String votingString = vote_points > 0 ? "<col=00FF00>" + vote_points + "" : "<col=FF0000>" + vote_points + "";
        player.getFrames().modifyText("Vote points: " + (votingString), GAME_INFORMATION_ID, 16);
        int slayer_points = player.getAttributes().getInt("slayer_points");
        String slayerString = slayer_points > 0 ? "<col=00FF00>" + slayer_points + "" : "<col=FF0000>" + slayer_points + "";
        player.getFrames().modifyText("Slayer points: " + (slayerString), GAME_INFORMATION_ID, 17);
        int bonus_xp_seconds = (int) (player.getAttributes().getInt("bonus_xp_ticks") * 0.6);
        int bonus_xp_days = NumberUtils.getDays(bonus_xp_seconds);
        bonus_xp_seconds -= bonus_xp_days * 60 * 60 * 24;
        int bonus_xp_hours = NumberUtils.getHours(bonus_xp_seconds);
        bonus_xp_seconds -= bonus_xp_hours * 60 * 60;
        int bonus_xp_mins = NumberUtils.getMinutes(bonus_xp_seconds);
        bonus_xp_seconds -= bonus_xp_mins * 60;
        int bonus_xp_secs = NumberUtils.getSeconds(bonus_xp_seconds);
        String bonus_xp_days_string = bonus_xp_days > 1 ? bonus_xp_days + " Days " : bonus_xp_days + " Day ";
        String bonus_xp_hours_string = bonus_xp_hours > 1 ? bonus_xp_hours + " hours " : bonus_xp_hours + " hour ";
        String bonus_xp_mins_string = bonus_xp_mins > 1 ? bonus_xp_mins + " Mins " : bonus_xp_mins + " Min ";
        StringBuilder builder = new StringBuilder();
        if (bonus_xp_days > 0) {
            builder.append("<col=00FF00>").append(bonus_xp_days_string).append("</col>");
        }
        if (bonus_xp_hours > 0) {
            builder.append("<col=00FF00>").append(bonus_xp_hours_string).append("</col>");
        }
        if (bonus_xp_mins > 0 && bonus_xp_days <= 0) {
            builder.append("<col=00FF00>").append(bonus_xp_mins_string).append("</col>");
        }
        if (bonus_xp_seconds > 0 && (bonus_xp_days <= 0 && bonus_xp_hours <= 0 && bonus_xp_mins < 1)) {
            builder.append("<col=FF0000>Less than a min");
        }
        if (bonus_xp_days <= 0 && bonus_xp_hours <= 0 && bonus_xp_mins <= 0 && bonus_xp_secs <= 0) {
            builder.append("<col=FF0000>N/A</col>");
        }
        player.getFrames().modifyText("Bonus XP: " + (builder.toString()), GAME_INFORMATION_ID, 18);
    }

    public static void sendQuests(Player player) {//TODO - finish
        player.getFrames().modifyText("<t>Quest Achievements", QUEST_TAB_ID, 9);
        if (player.getAttributes().getInt("hfd_stage") <= 0) {
            player.getFrames().modifyText("<col=FF0000>" + "Horrror From The Deep", QUEST_TAB_ID, 11);
        } else if (player.getAttributes().getInt("hfd_stage") >= 1 && player.getAttributes().getInt("hfd_stage") <= 3) {
            player.getFrames().modifyText("<col=FFFF00>" + "Horrror From The Deep", QUEST_TAB_ID, 11);
        } else if (player.getAttributes().getInt("hfd_stage") == 4) {
            player.getFrames().modifyText("<col=00FF00>" + "Horrror From The Deep", QUEST_TAB_ID, 11);
        }
        if (player.getAttributes().getInt("md_stage") <= 0) {
            player.getFrames().modifyText("<col=FF0000>" + "Mountain Daughter", QUEST_TAB_ID, 12);
        } else if (player.getAttributes().getInt("md_stage") >= 1 && player.getAttributes().getInt("md_stage") <= 2) {
            player.getFrames().modifyText("<col=FFFF00>" + "Mountain Daughter", QUEST_TAB_ID, 12);
        } else if (player.getAttributes().getInt("md_stage") == 3) {
            player.getFrames().modifyText("<col=00FF00>" + "Mountain Daughter", QUEST_TAB_ID, 12);
        }
        if (player.getAttributes().getInt("brain_robbery_stage") <= 0) {
            player.getFrames().modifyText("<col=FF0000>" + "Great Brain Robbery", QUEST_TAB_ID, 13);
        } else if (player.getAttributes().getInt("brain_robbery_stage") >= 1 && player.getAttributes().getInt("brain_robbery_stage") <= 4) {
            player.getFrames().modifyText("<col=FFFF00>" + "Great Brain Robbery", QUEST_TAB_ID, 13);
        } else if (player.getAttributes().getInt("brain_robbery_stage") == 5) {
            player.getFrames().modifyText("<col=00FF00>" + "Great Brain Robbery", QUEST_TAB_ID, 13);
        }
        if (player.getAttributes().getInt("rfd_stage") <= 0) {
            player.getFrames().modifyText("<col=FF0000>" + "Recipe For Disaster", QUEST_TAB_ID, 14);
        } else if (player.getAttributes().getInt("rfd_stage") >= 1 && player.getAttributes().getInt("rfd_stage") <= 11) {
            player.getFrames().modifyText("<col=FFFF00>" + "Recipe For Disaster", QUEST_TAB_ID, 14);
        } else if (player.getAttributes().getInt("rfd_stage") == 12) {
            player.getFrames().modifyText("<col=00FF00>" + "Recipe For Disaster", QUEST_TAB_ID, 14);
        }
        if (player.getAttributes().getInt("dt_stage") <= 0) {
            player.getFrames().modifyText("<col=FF0000>" + "Desert Treasure", QUEST_TAB_ID, 15);
        } else if (player.getAttributes().getInt("dt_stage") >= 1 && player.getAttributes().getInt("dt_stage") <= 7) {
            player.getFrames().modifyText("<col=FFFF00>" + "Desert Treasure", QUEST_TAB_ID, 15);
        } else if (player.getAttributes().getInt("dt_stage") == 8) {
            player.getFrames().modifyText("<col=00FF00>" + "Desert Treasure", QUEST_TAB_ID, 15);
        }
        if (player.getAttributes().getInt("lunar_stage") <= 0) {
            player.getFrames().modifyText("<col=FF0000>" + "Lunar's Diplomacy", QUEST_TAB_ID, 16);
        }
        player.getFrames().sendInterfaceVisibility(QUEST_TAB_ID, 17, false);
    }

    private static void displayPlayers(Player player) {
        List<String> results = new ArrayList<>();
        int count = 0;
        for (Player players : World.getWorld().getPlayers()) {
            if (players == null) continue;
            results.add(players.getDetails().getName());
            count++;
        }
        player.getFrames().modifyText("Total Online: " + World.getWorld().getPlayerCount(), 275, 2);
        int line = 4;
        for (int i = 0; i < count && i < 133; i++) {
            if (results.get(i) != null) {
                player.getFrames().modifyText(results.get(i), 275, line);
                line++;
            }
        }
        for (int extraLines = line; extraLines < 134; extraLines++) {
            player.getFrames().modifyText("", 275, extraLines);
        }
        player.getInterfaceSettings().openInterface(275);
    }

    private static void displayStaff(Player player) {
        List<String> results = new ArrayList<>();
        int count = 0;
        for (Player players : World.getWorld().getPlayers()) {
            if (players == null || !players.getDetails().isStaff()) continue;
            results.add(players.getDetails().getName());
            count++;
        }
        player.getFrames().modifyText("Total Staff Online: " + count, 275, 2);
        int line = 4;
        for (int i = 0; i < count && i < 133; i++) {
            if (results.get(i) != null) {
                player.getFrames().modifyText(results.get(i), 275, line);
                line++;
            }
        }
        for (int extraLines = line; extraLines < 134; extraLines++) {
            player.getFrames().modifyText("", 275, extraLines);
        }
        player.getInterfaceSettings().openInterface(275);
    }

    private static void displayMemberShipBenefits(Player player) {
    }
}
