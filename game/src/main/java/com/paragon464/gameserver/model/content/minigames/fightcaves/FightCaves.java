package com.paragon464.gameserver.model.content.minigames.fightcaves;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class FightCaves {

    public static final int TZ_KIH = 2734;
    public static final int TZ_KEK = 2736;
    public static final int TOX_XIL = 2739;
    public static final int YT_MEJKOT = 2741;
    public static final int KET_ZEK = 2743;
    public static final int TZ_TOK_JAD = 2745;

    public static int[][] WAVES = {{-1}, // wave 0, ignore..
        {TZ_KIH}, {TZ_KIH, TZ_KIH}, {TZ_KEK}, {TZ_KEK, TZ_KIH}, {TZ_KEK, TZ_KIH, TZ_KIH},
        {TZ_KEK, TZ_KEK}, {TOX_XIL}, {TOX_XIL, TZ_KIH}, {TOX_XIL, TZ_KIH, TZ_KIH}, {TOX_XIL, TZ_KEK},
        {TOX_XIL, TZ_KEK, TZ_KIH}, {TOX_XIL, TZ_KEK, TZ_KIH, TZ_KIH}, {TOX_XIL, TZ_KEK, TZ_KEK},
        {TOX_XIL, TOX_XIL}, {YT_MEJKOT}, {YT_MEJKOT, TZ_KIH}, {YT_MEJKOT, TZ_KIH, TZ_KIH},
        {YT_MEJKOT, TZ_KEK}, {YT_MEJKOT, TZ_KEK, TZ_KIH}, {YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH},
        {YT_MEJKOT, TZ_KEK, TZ_KEK}, {YT_MEJKOT, TOX_XIL}, {YT_MEJKOT, TOX_XIL, TZ_KIH},
        {YT_MEJKOT, TOX_XIL, TZ_KIH, TZ_KIH}, {YT_MEJKOT, TOX_XIL, TZ_KEK},
        {YT_MEJKOT, TOX_XIL, TZ_KEK, TZ_KIH}, {YT_MEJKOT, TOX_XIL, TZ_KEK, TZ_KIH, TZ_KIH},
        {YT_MEJKOT, TOX_XIL, TZ_KEK, TZ_KEK}, {YT_MEJKOT, TOX_XIL, TOX_XIL}, {YT_MEJKOT, YT_MEJKOT},
        {KET_ZEK}, {KET_ZEK, TZ_KIH}, {KET_ZEK, TZ_KIH, TZ_KIH}, {KET_ZEK, TZ_KEK},
        {KET_ZEK, TZ_KEK, TZ_KIH}, {KET_ZEK, TZ_KEK, TZ_KIH, TZ_KIH}, {KET_ZEK, TZ_KEK, TZ_KEK},
        {KET_ZEK, TOX_XIL}, {KET_ZEK, TOX_XIL, TZ_KIH}, {KET_ZEK, TOX_XIL, TZ_KIH, TZ_KIH},
        {KET_ZEK, TOX_XIL, TZ_KEK}, {KET_ZEK, TOX_XIL, TZ_KEK, TZ_KIH},
        {KET_ZEK, TOX_XIL, TZ_KEK, TZ_KIH, TZ_KIH}, {KET_ZEK, TOX_XIL, TZ_KEK, TZ_KEK},
        {KET_ZEK, TOX_XIL, TOX_XIL}, {KET_ZEK, YT_MEJKOT}, {KET_ZEK, YT_MEJKOT, TZ_KIH},
        {KET_ZEK, YT_MEJKOT, TZ_KIH, TZ_KIH}, {KET_ZEK, YT_MEJKOT, TZ_KEK},
        {KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH}, {KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH},
        {KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KEK}, {KET_ZEK, YT_MEJKOT, TOX_XIL},
        {KET_ZEK, YT_MEJKOT, TOX_XIL, TZ_KIH}, {KET_ZEK, YT_MEJKOT, TOX_XIL, TZ_KIH, TZ_KIH},
        {KET_ZEK, YT_MEJKOT, TOX_XIL, TZ_KEK}, {KET_ZEK, YT_MEJKOT, TOX_XIL, TZ_KEK, TZ_KIH},
        {KET_ZEK, YT_MEJKOT, TOX_XIL, TZ_KEK, TZ_KIH, TZ_KIH}, {KET_ZEK, YT_MEJKOT, TOX_XIL, TZ_KEK, TZ_KEK},
        {KET_ZEK, YT_MEJKOT, TOX_XIL, TOX_XIL}, {KET_ZEK, YT_MEJKOT, YT_MEJKOT}, {KET_ZEK, KET_ZEK},
        {TZ_TOK_JAD}};

    public static int[] getNPCS(Player player, int wave) {
        int[] mobs = new int[WAVES[wave].length];
        int mobsAdded = 0;
        for (int i = 0; i < mobs.length; i++) {
            mobs[mobsAdded++] = WAVES[wave][i];
        }
        return mobs;
    }
}
