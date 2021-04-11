package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;

import java.util.HashMap;

public class Emotes {

    static int[][] SKILL_CAPE_DATA = {{9747, 4959, 823}, {9753, 4961, 824}, {9750, 4981, 828},
        {9768, 4971, 833}, {9756, 4973, 832}, {9759, 4979, 829}, {9762, 4939, 813}, {9801, 4955, 821},
        {9807, 4957, 822}, {9783, 4937, 812}, {9798, 4951, 819}, {9804, 4975, 831}, {9780, 4949, 818},
        {9765, 4943, 815}, {9792, 4941, 814}, {9774, 4969, 835}, {9771, 4977, 830}, {9777, 4965, 826},
        {9786, 4967, 1656}, {9810, 4963, 825}, {9765, 4947, 817}, {9948, 5158, 907}, {9789, 4953, 820},
        {12169, 8525, 1515}, {9813, 4945, 816}};

    public static void perform(Player player, int buttonId) {
        EmoteData emotes = EmoteData.load(buttonId);
        if (emotes == null) {
            return;
        }
        if (emotes.getAnimation() == 0) {
            performSpecial(player, buttonId);
            return;
        }
        player.playAnimation(Animation.create(emotes.getAnimation(), AnimationPriority.HIGH));
        if (emotes.getGraphic() > 0) {
            player.playGraphic(emotes.getGraphic());
        }
        emotes = null;
    }

    public static void performSpecial(final Player player, int buttonId) {
        switch (buttonId) {
            case 37:
                int cape = player.getEquipment().getItemInSlot(1);
                boolean performed = false;
                for (int i = 0; i < SKILL_CAPE_DATA.length; i++) {
                    if (cape == SKILL_CAPE_DATA[i][0] || cape == SKILL_CAPE_DATA[i][0] + 1) {
                        player.playAnimation(SKILL_CAPE_DATA[i][1], AnimationPriority.HIGH);
                        player.playGraphic(SKILL_CAPE_DATA[i][2]);
                        performed = true;
                    }
                }
                if (!performed) {
                    player.getFrames().sendMessage("You need to be wearing a Skillcape in order to perform that emote.");
                    performed = false;
                }
                break;
        }
    }

    public enum EmoteData {

        YES(1, 855),

        NO(2, 856),

        BOW(3, 858),

        ANGRY(4, 864),

        THINK(5, 857),

        WAVE(6, 863),

        SHRUG(7, 2113),

        CHEER(8, 862),

        BECKON(9, 859),

        LAUGH(10, 861),

        JUMP_FOR_JOY(11, 2109),

        YAWN(12, 2111),

        DANCE(13, 866),

        JIG(14, 2106),

        SPIN(15, 2107),

        HEADBANG(16, 2108),

        CRY(17, 860),

        BLOW_KISS(18, 0x558, 574),

        PANIC(19, 2105),

        RASPBERRY(20, 2110),

        CLAP(21, 865),

        SALUTE(22, 2112),

        GOBLIN_BOW(23, 0x84F),

        GOBLIN_SALUTE(24, 0x850),

        GLASS_BOX(25, 1131),

        CLIMB_ROPE(26, 1130),

        LEAN(27, 1129),

        GLASS_WALL(28, 1128),

        IDEA(32, 4275, 712),

        STOMP(30, 1745),

        FLAP(31, 4280),

        SLAP_HEAD(29, 4276),

        ZOMBIE_WALK(33, 3544),

        ZOMBIE_DANCE(34, 3543),

        ZOMBIE_HAND(33, 7272, 1244),

        SCARED(35, 2836),

        BUNNY_HOP(36, 6111),

        SKILL_CAPE(37, 0);

        public static HashMap<Integer, EmoteData> emotes = new HashMap<>();

        static {
            for (EmoteData emote : EmoteData.values())
                emotes.put(emote.getButton(), emote);
        }

        private int button;
        private int animation;
        private int graphic;

        EmoteData(int button, int animation) {
            this.button = button;
            this.animation = animation;
        }

        EmoteData(int button, int animation, int graphic) {
            this.button = button;
            this.animation = animation;
            this.graphic = graphic;
        }

        public static EmoteData load(int buttonId) {
            return emotes.get(buttonId);
        }

        public int getButton() {
            return button;
        }

        public int getAnimation() {
            return animation;
        }

        public int getGraphic() {
            return graphic;
        }
    }
}
