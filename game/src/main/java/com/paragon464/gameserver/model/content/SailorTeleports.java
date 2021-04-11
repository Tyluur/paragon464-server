package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class SailorTeleports {

    private static final int MAIN_INTERFACE = 583;
    private static final int CITY_AREAS = 608;
    private static final int MONSTER_AREAS = 609;
    private static final int GUILDS = 618;

    public static void open(Player player) {
        player.getInterfaceSettings().openInterface(MAIN_INTERFACE);
    }

    public static void handle(final Player player, final int screen, final int button) {
        boolean closeInterfaces = true;
        boolean back = button == 49;
        if (back) {
            player.getInterfaceSettings().openInterface(MAIN_INTERFACE);
            return;
        }
        switch (screen) {
            case MAIN_INTERFACE:
                closeInterfaces = (button != 33 && button != 34 && button != 40);
                switch (button) {
                    case 33://city teles
                        player.getInterfaceSettings().openInterface(CITY_AREAS);
                        break;
                    case 34://monster teles
                        player.getInterfaceSettings().openInterface(MONSTER_AREAS);
                        break;
                    case 35://duel
                        player.teleport(3366, 3265, 0);
                        break;
                    case 36://pest
                        player.teleport(2659, 2676, 0);
                        break;
                    case 37://barrows
                        player.teleport(3565, 3306, 0);
                        break;
                    case 38://barb course
                        player.teleport(2542, 3568, 0);
                        break;
                    case 39://Nightmare zone
                        player.teleport(2616, 3150, 0);
                        break;
                    case 40://guilds
                        player.getInterfaceSettings().openInterface(GUILDS);
                        break;
                }
                break;
            case GUILDS:
                switch (button) {
                    case 33://crafting
                        player.teleport(2933, 3288, 0);
                        break;
                    case 34://fishing
                        player.teleport(2608, 3401, 0);
                        break;
                    case 35://woodcutting
                        player.teleport(1565, 3487, 0);
                        break;
                    case 36://mining
                        player.teleport(3019, 9726, 0);
                        break;
                    case 37://magic
                        player.teleport(2603, 3088, 0);
                        break;
                }
                break;
            case CITY_AREAS:
                switch (button) {
                    case 33://edge
                        player.teleport(3087, 3491, 0);
                        break;
                    case 34://lumb
                        player.teleport(3215, 3219, 0);
                        break;
                    case 35://varrock
                        player.teleport(3212, 3423, 0);
                        break;
                    case 36://al-kharid
                        player.teleport(3288, 3217, 0);
                        break;
                    case 37://rellekka
                        player.teleport(2706, 3715, 0);
                        break;
                    case 38://misc
                        player.teleport(2515, 3860, 0);
                        break;
                    case 39://burth
                        player.teleport(2885, 3547, 0);
                        break;
                    case 40://tree gnome
                        player.teleport(2461, 3388, 0);
                        break;
                    case 41://canifis
                        player.teleport(3494, 3483, 0);
                        break;
                    case 42://ape atoll
                        player.teleport(2788, 2786, 0);
                        break;
                    case 43://Mos Le' Harmless
                        player.teleport(3678, 2954, 0);
                        break;
                    case 44://Lunar Isle
                        player.teleport(2112, 3915, 0);
                        break;
                }
                break;
            case MONSTER_AREAS:
                switch (button) {
                    case 33://ancient cavern
                        player.teleport(1745, 5325, 0);
                        break;
                    case 34://asgarnian dung
                        player.teleport(3009, 9550, 0);
                        break;
                    case 35://trolls dung
                        player.teleport(2759, 10064, 0);
                        break;
                    case 36://dags cave
                        player.teleport(2442, 10147, 0);
                        break;
                    case 37://slayer tower
                        player.teleport(3429, 3538, 0);
                        break;
                    case 38://ape atoll dungeon
                        player.teleport(2764, 9103, 0);
                        break;
                }
                break;
        }
        if (closeInterfaces && !back) {
            player.getInterfaceSettings().closeInterfaces(false);
        }
    }
}
