package com.paragon464.gameserver.model.content.skills.prayer;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;

public class QuickPrayers {

    public static void handle(final Player player, int button) {
        if (button == 1) {
            done(player);
            return;
        }
        PrayerData.PRAYERS_DATA main_data = PrayerData.getQuickPrayer(player, button);
        if (main_data == null) {
            return;
        }
        int prayer_index = button - 4;
        if (player.getSkills().getLevel(SkillType.PRAYER) < main_data.getReq()) {
            player.getFrames().sendMessage("You need a Prayer level of " + main_data.getReq() + " to select " + main_data.getName() + ".");
            return;
        }
        if (prayer_index < 0 || (player.getSettings().isCursesEnabled() ? prayer_index > 19 : prayer_index > 25)) {
            return;
        }
        player.getAttributes().set("quick_prayers_value", player.getAttributes().getInt("quick_prayers_value") ^ (1 << prayer_index));
        int[] prayersToTurnOff = main_data.getPrayersOff();
        if (prayersToTurnOff != null) {
            for (int i : prayersToTurnOff) {
                PrayerData.PRAYERS_DATA data = PrayerData.getPrayers(i);
                if (data == null) continue;
                if (player.getSettings().isCursesEnabled()) {
                    player.getAttributes().set("quick_prayers_value", player.getAttributes().getInt("quick_prayers_value") & ~(1 << data.getIndex() - 26));
                } else {
                    player.getAttributes().set("quick_prayers_value", player.getAttributes().getInt("quick_prayers_value") & ~(1 << data.getIndex()));
                }
            }
        }
        player.getFrames().sendVarp(player.getSettings().isCursesEnabled() ? 601 : 600, player.getAttributes().getInt("quick_prayers_value"));
    }

    public static void done(final Player player) {
        player.getFrames().sendTab(player.getSettings().isInResizable() ? 70 : 91, player.getSettings().isCursesEnabled() ? 597 : 271);
    }

    public static void turnOn(final Player player) {
        boolean quickPraysOn = player.getAttributes().is("quick_prayer_toggled");
        if (player.getSettings().isCursesEnabled()) {
            for (int i = 0; i < 20; i++) {
                PrayerData.PRAYERS_DATA d = PrayerData.getPrayers(i + 26);
                if (d == null) continue;
                if ((player.getAttributes().getInt("quick_prayers_value") & (1 << i)) != 0) {
                    boolean alreadyActive = player.getPrayers().getActivePrayers()[i + 26];
                    if ((!quickPraysOn && alreadyActive) || (quickPraysOn && !alreadyActive)) {
                        continue;
                    }
                    if (!player.getPrayers().togglePrayers(d, true)) {
                    }
                }
            }
        } else {
            for (int i = 0; i < 26; i++) {
                PrayerData.PRAYERS_DATA d = PrayerData.getPrayers(i);
                if (d == null) continue;
                int prayer_index = d.getIndex();
                if ((player.getAttributes().getInt("quick_prayers_value") & (1 << prayer_index)) != 0) {
                    boolean alreadyActive = player.getPrayers().isPrayerActive(d.getName());
                    if ((!quickPraysOn && alreadyActive) || (quickPraysOn && !alreadyActive)) {
                        continue;
                    }
                    player.getPrayers().togglePrayers(d, true);
                }
            }
        }
        boolean quickPraysOff = player.getPrayers().quickPrayersOff();
        player.getAttributes().set("quick_prayer_toggled", !quickPraysOff);
        player.getFrames().sendVarp(602, !quickPraysOff ? 1 : 0);
    }

    public static void open(final Player player) {
        if (player.getSettings().isCursesEnabled()) {
            player.getFrames().sendVarp(601, player.getAttributes().getInt("quick_prayers_value"));
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 70 : 91, 617);
        } else {
            player.getFrames().sendVarp(600, player.getAttributes().getInt("quick_prayers_value"));
            player.getFrames().sendTab(player.getSettings().isInResizable() ? 70 : 91, 616);
        }
        player.getFrames().forceSendTab(5);
    }
}
