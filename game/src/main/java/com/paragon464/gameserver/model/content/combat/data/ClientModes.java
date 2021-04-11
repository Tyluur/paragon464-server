package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class ClientModes {

    @SuppressWarnings("unused")
    public static int getFixedProjectileIds(Player player, int current) {
        int mode = player.getDetails().getClientMode();
        return current;
    }

    public static int getFixedGfxIds(Player player, int current) {
        int mode = player.getDetails().getClientMode();
        if (mode == 464) {
            if (current == 1843) {
                return 345;
            } else if (current == 1842) {
                return -1;
            }
        }
        return current;
    }

    public static int getFixedAnimations(Player player, int current_anim) {
        int mode = player.getDetails().getClientMode();
        //Debugging.print("Mode: " + mode + ", current_anim: " + current_anim);
        if (current_anim == 2609 || current_anim == 2612 || current_anim == 2610)
            return 9286;
        else if (current_anim == 2606)
            return 9287;
        else if (current_anim == 2607)
            return 9288;
        else if (current_anim == 2621)
            return 9232;
        else if (current_anim == 2622)
            return 9231;
        else if (current_anim == 2620)
            return 9230;
        else if (current_anim == 2625)
            return 9233;
        else if (current_anim == 2626)
            return 9235;
        else if (current_anim == 2627)
            return 9234;
        else if (current_anim == 2628)
            return 9243;
        else if (current_anim == 2629)
            return 9242;
        else if (current_anim == 2630)
            return 9239;
        else if (current_anim == 2637)
            return 9246;
        else if (current_anim == 2635)
            return 9248;
        else if (current_anim == 2638)
            return 9247;
        else if (current_anim == 2644)
            return 9265;
        else if (current_anim == 2645)
            return 9268;
        else if (current_anim == 2646)
            return 9269;
        if (mode == 464) {
            if (current_anim == 10503)
                return 1819;
            if (current_anim == 2304)
                return 836;
            if (current_anim == 10080)
                return 808;
            if (current_anim == 10580)
                return 746;
            if (current_anim == 10579)
                return 748;
            if (current_anim == 4853)
                return 839;
            if (current_anim == 12004)
                return 424;
        } else {
            if (mode > 530) {
                if (current_anim == 5486)
                    return 12632;
                if (current_anim == 1659)
                    return 11974;
                if (current_anim == 10080)
                    return 11973;
                if (current_anim == 5581)
                    return 5568;
            }
        }
        return current_anim;
    }
}
