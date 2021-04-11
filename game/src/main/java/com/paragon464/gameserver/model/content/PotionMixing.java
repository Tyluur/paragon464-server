package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class PotionMixing {

    public static final int[][] DOSES = {
        {125, 179, 119, 131, 3014, 137, 3038, 9745, 143, 12146, 149, 185, 155, 3022, 10004, 161, 3030, 167, 2458,
            173, 3046, 193, 6691, 10545}, // 1 dose
        {123, 177, 117, 129, 3012, 135, 3036, 9743, 141, 12144, 147, 183, 153, 3020, 10002, 159, 3028, 165, 2456,
            171, 3044, 191, 6689, 10544}, // 2 dose
        {121, 175, 115, 127, 3010, 133, 3034, 9741, 139, 12142, 145, 181, 151, 3018, 10000, 157, 3026, 163, 2454,
            169, 3042, 189, 6687, 10543}, // 3 dose
        {2428, 2446, 113, 2430, 3008, 2432, 3032, 9739, 2434, 12140, 2436, 2448, 2438, 3016, 9998, 2440, 3024,
            2442, 2452, 2444, 3040, 2450, 6685, 10542} // 4 dose
    };
    protected static final int VIAL = 229;

    public static boolean mixDoses(Player player, int itemUsed, int usedWith) {
        int vial = VIAL;
        if (itemUsed >= 10542 && usedWith <= 10545) {
            vial = 10546;
        }
        int ONE = 0, TWO = 1, THREE = 2, FOUR = 3;
        for (int i = 0; i < DOSES.length; i++) {
            for (int j = 0; j < DOSES[ONE].length; j++) {
                if (itemUsed == DOSES[ONE][j] && usedWith == DOSES[ONE][j]) {
                    player.getInventory().replaceItem(DOSES[ONE][j], vial);
                    player.getInventory().replaceItem(DOSES[ONE][j], DOSES[TWO][j]);
                    return true;
                }
                if (itemUsed == DOSES[TWO][j] && usedWith == DOSES[TWO][j]) {
                    player.getInventory().replaceItem(DOSES[TWO][j], vial);
                    player.getInventory().replaceItem(DOSES[TWO][j], DOSES[FOUR][j]);
                    return true;
                }
                if (itemUsed == DOSES[THREE][j] && usedWith == DOSES[THREE][j]) {
                    player.getInventory().replaceItem(DOSES[THREE][j], DOSES[TWO][j]);
                    player.getInventory().replaceItem(DOSES[THREE][j], DOSES[FOUR][j]);
                    return true;
                }
                if (itemUsed == DOSES[ONE][j] && usedWith == DOSES[TWO][j]) {
                    player.getInventory().replaceItem(DOSES[ONE][j], vial);
                    player.getInventory().replaceItem(DOSES[TWO][j], DOSES[THREE][j]);
                    return true;
                }
                if (itemUsed == DOSES[TWO][j] && usedWith == DOSES[ONE][j]) {
                    player.getInventory().replaceItem(DOSES[TWO][j], vial);
                    player.getInventory().replaceItem(DOSES[ONE][j], DOSES[THREE][j]);
                    return true;
                }
                if (itemUsed == DOSES[ONE][j] && usedWith == DOSES[THREE][j]) {
                    player.getInventory().replaceItem(DOSES[ONE][j], vial);
                    player.getInventory().replaceItem(DOSES[THREE][j], DOSES[FOUR][j]);
                    return true;
                }
                if (itemUsed == DOSES[THREE][j] && usedWith == DOSES[ONE][j]) {
                    player.getInventory().replaceItem(DOSES[THREE][j], vial);
                    player.getInventory().replaceItem(DOSES[ONE][j], DOSES[FOUR][j]);
                    return true;
                }
                if (itemUsed == DOSES[TWO][j] && usedWith == DOSES[THREE][j]) {
                    player.getInventory().replaceItem(DOSES[TWO][j], DOSES[ONE][j]);
                    player.getInventory().replaceItem(DOSES[THREE][j], DOSES[FOUR][j]);
                    return true;
                }
                if (itemUsed == DOSES[THREE][j] && usedWith == DOSES[TWO][j]) {
                    player.getInventory().replaceItem(DOSES[THREE][j], DOSES[ONE][j]);
                    player.getInventory().replaceItem(DOSES[TWO][j], DOSES[FOUR][j]);
                    return true;
                }
            }
        }
        return false;
    }
}
