package com.paragon464.gameserver.model.content.skills.smithing;

/**
 * @author Reece <valiw@hotmail.com>
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class SmeltBarData {

    public static final int[] BARS = {2349, // Bronze
        9467, // Blurite
        2351, // Iron
        2355, // Silver
        2353, // Steel
        2357, // Gold
        2359, // Mithril
        2361, // Adamant
        2363, // Rune
    };
    protected static final int[] SMELT_LEVELS = {1, // Bronze
        8, // Blurite
        15, // Iron
        20, // Silver
        30, // Steel
        40, // Gold
        50, // Mithril
        70, // Adamant
        85, // Rune
    };
    protected static final double[] SMELT_XP = {6.2, // Bronze
        8, // Blurite
        12.5, // Iron
        13.7, // Silver
        17.5, // Steel
        22.5, // Gold
        30, // Mithril
        37.5, // Adamant
        50, // Rune
    };
    protected static final int[][] SMELT_ORE_AMT = {{1, 1}, // 1 Tin 1 Copper
        {1, 0}, // 1 Blurite
        {1, 0}, // 1 Iron
        {1, 0}, // 1 Silver
        {1, 2}, // 1 Iron 2 Coal
        {1, 0}, // 1 Gold
        {1, 4}, // 1 Mithril 4 Coal
        {1, 6}, // 1 Adamant 6 Coal
        {1, 8}, // 1 Runite 8 Coal
    };
    private static final int COAL = 453;
    protected static final int[][] SMELT_ORES = {{436, 438}, // Tin, Copper
        {668, 0}, // Blurite
        {440, 0}, // Iron
        {442, 0}, // Silver
        {440, COAL}, // Iron, Coal
        {444, 0}, // Gold
        {447, COAL}, // Mithril, Coal
        {449, COAL}, // Adamant, Coal
        {451, COAL}, // Rune, Coal
    };
}
