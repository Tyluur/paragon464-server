package com.paragon464.gameserver.model.area;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.minigames.fightcaves.CavesBattleSession;
import com.paragon464.gameserver.model.content.minigames.pestcontrol.PestWaiting;
import com.paragon464.gameserver.model.region.Position;

public class Areas {

    public static boolean isInMultiZone(Mob mob, Position loc) {
        return (isInWildernessMultiArea(loc) || camelotMulti(loc) || ardougneMulti(loc) || faladorMulti(loc)
            || draynorMulti(loc) || barbarianVillageMulti(loc) || alKharidMulti(loc) || isInMultiAreaInFfa(loc)
            || atDagKingsLair(loc) || atGodwars(loc) || atKQLair(loc) || atKBDLair(loc) || atCorpLair(loc) ||
            inArea(loc, 2690, 9090, 2815, 9150));
    }

    public static boolean isInWildernessMultiArea(final Position loc) {
        int x = loc.getX();
        int y = loc.getY();
        return (x >= 3136 && x <= 3327 && y >= 3520 && y <= 3607) || (x >= 3190 && x <= 3327 && y >= 3648 && y <= 3839)
            || (x >= 3200 && x <= 3390 && y >= 3840 && y <= 3967)
            || (x >= 2992 && x <= 3007 && y >= 3912 && y <= 3967)
            || (x >= 2946 && x <= 2959 && y >= 3816 && y <= 3831)
            || (x >= 3008 && x <= 3199 && y >= 3856 && y <= 3903)
            || (x >= 3008 && x <= 3071 && y >= 3600 && y <= 3711)
            || (x >= 3072 && x <= 3327 && y >= 3608 && y <= 3647);
    }

    public static boolean camelotMulti(final Position position) {
        return inArea(position, 2651, 3409, 2687, 3447) || inArea(position, 2647, 3486, 2664, 3513)
            || inArea(position, 2820, 3455, 2891, 3525);
    }

    public static boolean ardougneMulti(final Position position) {
        return inArea(position, 2513, 3215, 2543, 3251) || inArea(position, 2659, 3220, 2684, 3254);
    }

    public static boolean faladorMulti(final Position position) {
        return inArea(position, 2946, 3391, 3008, 3456) || inArea(position, 2943, 3305, 3008, 3391)
            || inArea(position, 3008, 3303, 3024, 3327);
    }

    public static boolean draynorMulti(final Position position) {
        return inArea(position, 3105, 3232, 3135, 3261);
    }

    public static boolean barbarianVillageMulti(final Position position) {
        return inArea(position, 3073, 3390, 3138, 3453);
    }

    public static boolean alKharidMulti(final Position position) {
        return inArea(position, 3272, 3150, 3310, 3182);
    }

    /**
     * Determines if the position is within the multi line at ffa.
     *
     * @param loc
     * @return
     */
    public static boolean isInMultiAreaInFfa(final Position loc) {
        return inArea(loc, 2756, 5512, 2798, 5627);
    }

    public static boolean atDagKingsLair(final Position position) {
        return inArea(position, 2898, 4434, 2932, 4464);
    }

    public static boolean atGodwars(final Position position) {
        return inArea(position, 2820, 5245, 2964, 5380);
    }

    public static boolean atKQLair(final Position loc) {
        return isInArea(loc, 3465, 9483, 3506, 9519);
    }

    public static boolean atKBDLair(final Position loc) {
        return inArea(loc, 2250, 4677, 2292, 4716);
    }

    public static boolean atCorpLair(final Position loc) {
        return loc.getX() >= 2882 && loc.getX() <= 2998 && loc.getY() >= 4355 && loc.getY() <= 4414;
    }

    public static boolean inArea(final Position position, int lowestX, int lowestY, int highestX, int highestY) {
        return position.getX() >= lowestX && position.getY() >= lowestY &&
            position.getX() <= highestX && position.getY() <= highestY;
    }

    public static boolean isInArea(final Position position, int bottomLeftX, int bottomLeftY, int topRightX, int topRightY) {
        return (position.getX() >= bottomLeftX && position.getX() <= topRightX &&
            position.getY() >= bottomLeftY && position.getY() <= topRightY);
    }

    public static boolean atWoodcuttingGuild(final Position loc) {
        return inArea(loc, 1562, 3471, 1659, 3518);
    }

    public static boolean inAttackableArea(final Position l) {
        return inDuelArenas(l) || inWilderness(l);
    }

    /**
     * Determines if the position is in the duel arenas.
     *
     * @param l
     * @return
     */
    public static boolean inDuelArenas(final Position l) {
        return inArea(l, 3331, 3205, 3390, 3259);
    }

    /**
     * Determines if we are in the wilderness.
     *
     * @param l
     * @return
     */
    public static boolean inWilderness(final Position l) {
        int x = l.getX();
        int y = l.getY();
        return (x > 3009 && x < 3060 && y > 10303 && y < 10356) || (x > 2941 && x < 3392 && y > 3520 && y < 3966)
            || (x >= 3063 && x <= 3071 && y >= 10252 && y <= 10263);
    }

    public static boolean inChallengeArea(final Position l) {
        return atDuelArena(l);
    }

    /**
     * Determines if we are at duel arena challenge area.
     *
     * @param l
     * @return
     */
    public static boolean atDuelArena(final Position l) {
        return inArea(l, 3318, 3247, 3327, 3247) || inArea(l, 3324, 3247, 3328, 3264) || inArea(l, 3327, 3262, 3342, 3270)
            || inArea(l, 3342, 3262, 3387, 3288) || inArea(l, 3387, 3262, 3394, 3271)
            || inArea(l, 3312, 3224, 3325, 3247) || inArea(l, 3326, 3200, 3398, 3267);
    }

    /**
     * Determines if the position is at free for all clan wars
     *
     * @param loc
     * @return in ffa clan wars
     */
    public static boolean isInFreeForAllClanWars(final Position loc) {
        return inArea(loc, 2752, 5505, 2878, 5630);
    }

    /**
     * Determines if the mob is within an attackable area.
     *
     * @param mob
     * @return
     */
    public static boolean isInAttackableArea(final Mob mob) {
        final Position loc = mob.getPosition();
        return isInAttackableAreaInFfa(loc) || inWilderness(mob.getPosition()) || inDuelArenas(loc);
    }

    /**
     * Determines if the position is within an attackable area at ffa clan wars.
     *
     * @param loc
     * @return in ffa clan wars
     */
    public static boolean isInAttackableAreaInFfa(final Position loc) {
        return inArea(loc, 2752, 5512, 2878, 5630);
    }

    public static boolean atBarrows(final Position position) {
        return (inArea(position, 3545, 3265, 3586, 3312) || inArea(position, 3567, 9701, 3580, 9711)
            || inArea(position, 3548, 9709, 3561, 9721) || inArea(position, 3549, 9691, 3562, 9706)
            || inArea(position, 3532, 9698, 3546, 9710) || inArea(position, 3544, 9677, 3559, 9689)
            || inArea(position, 3563, 9680, 3577, 9694));
    }

    public static boolean inGrandExchangeArea(final Position position) {
        return inArea(position, 3161, 3473, 3168, 3506) || inArea(position, 3148, 3484, 3181, 3495)
            || inArea(position, 3148, 3473, 3181, 3506);
    }

    public static boolean inEdgevilleBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        if (absX == 3090) {
            if (absY >= 3494 && absY <= 3496) {
                return true;
            }
        }
        return (absX > 3090 && absX < 3099 && absY > 3487 && absY < 3500);
    }

    public static boolean inWestVarrockBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 3181 && absX < 3195 && absY > 3431 && absY < 3447);
    }

    public static boolean inEastVarrockBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 3249 && absX < 3258 && absY > 3418 && absY < 3425);
    }

    public static boolean inWestFaladorBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 2941 && absX < 2948 && absY > 3367 && absY < 3374
            || absX >= 2948 && absX <= 2949 && absY >= 3368 && absY <= 3369);
    }

    public static boolean inEastFaladorBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 3008 && absX < 3019 && absY > 3354 && absY < 3359);
    }

    public static boolean inCamelotBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 2720 && absX < 2731 && absY > 3489 && absY < 3494
            || absX > 2723 && absX < 2728 && absY > 3486 && absY < 3490);
    }

    public static boolean inCatherbyBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 2805 && absX < 2813 && absY > 3437 && absY < 3442);
    }

    public static boolean inNorthArdougneBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 2611 && absX < 2622 && absY > 3329 && absY < 3336);
    }

    public static boolean inSouthArdougneBank(final Position position) {
        int absX = position.getX();
        int absY = position.getY();
        return (absX > 2648 && absX < 2657 && absY > 3279 && absY < 3288);
    }

    public static boolean grandExchangeRoof(final Position position) {
        return inArea(position, 3161, 3473, 3168, 3506) || inArea(position, 3148, 3484, 3181, 3495)
            || inArea(position, 3148, 3473, 3181, 3506);
    }

    public static boolean atArmadylChamber(final Position position) {
        return inArea(position, 2823, 5295, 2843, 5309);
    }

    public static boolean atBandosChamber(final Position position) {
        return inArea(position, 2864, 5351, 2876, 5369);
    }

    public static boolean atSaradominChamber(final Position loc) {
        return inArea(loc, 2888, 5257, 2908, 5277);
    }

    public static boolean atZamorakChamber(final Position loc) {
        return inArea(loc, 2917, 5317, 2937, 5332);
    }

    public static boolean atWarriorsGuild(final Position position, boolean cyclops) {
        if (cyclops) {
            return (inArea(position, 2847, 3532, 2876, 3542) || inArea(position, 2837, 3543, 2876, 3556))
                && position.getZ() == 2 && position.getX() != 2846;
        }
        return inArea(position, 2837, 3534, 2876, 3556);
    }

    public static void handle_login(final Player player) {
        Position loc = player.getPosition();
        if (Areas.inPestBoat(loc)) {
            PestWaiting.enter(player);
        } else if (player.getAttributes().getInt("caves_wave") > 0) {
            player.getAttributes().set("caves_session", new CavesBattleSession(player));
        }
    }

    public static boolean inPestBoat(final Position loc) {
        return inArea(loc, 2660, 2638, 2663, 2644);
    }
}
