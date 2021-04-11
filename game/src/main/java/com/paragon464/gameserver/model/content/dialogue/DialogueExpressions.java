package com.paragon464.gameserver.model.content.dialogue;

public class DialogueExpressions {

    //500+
    public static int NO_EXPRESSION = 9760, SAD = 9764, SAD_TWO = 9768, NO_EXPRESSION_TWO = 9772, WHY = 9776;
    public static int SCARED = 9780, MILDY_ANGRY = 9784, ANGRY = 9788, VERY_ANGRY = 9792, ANGRY_TWO = 9796;
    public static int MANIC_FACE = 9800, JUST_LISTEN = 9804, PLAIN_TALKING = 9808, LOOK_DOWN = 9812;
    public static int WHAT_THE = 9816, WHAT_THE_TWO = 9820, EYES_WIDE = 9824, CROOKED_HEAD = 9828;
    public static int GLANCE_DOWN = 9832, UNSURE = 9836, LISTEN_LAUGH = 9840, TALK_SWING = 9844, NORMAL = 9847;
    public static int GOOFY_LAUGH = 9851, NORMAL_STILL = 9855, THINKING_STILL = 9859, LOOKING_UP = 9862;

    //400s
    public enum Emotes {
        HAPPY(588), // - Joyful/happy
        CALM1(589), // - Speakingly calmly
        CALM2(590), // - Calm talk
        DEFAULT(591), // - Default speech
        EVIL(592), //  - Evil
        EVILCONTINUED(593), //  - Evil continued
        DELIGHTEDEVIL(594), //  - Delighted evil
        ANNOYED(595), //  - Annoyed
        DISTRESSED(596), //  - Distressed
        DISTRESSEDCONTINUED(597), //  - Distressed continued
        ALMOSTCRYING(598), //  - Almost crying
        BOWSHEADWHILESAD(599), //  - Bows head while sad
        DRUNKTOLEFT(600), //  - Talks and looks sleepy/drunk to left
        DRUNKTORIGHT(601), //  - Talks and looks sleepy/drunk to right
        DISINTERESTED(602), //  - Sleepy or disinterested
        SPEELY(603), //  - Tipping head as if sleepy.
        PLAINEVIL(604), //  - Plain evil (Grits teeth and moves eyebrows)
        LAUGH1(605), //  - Laughing or yawning
        LAUGH2(606), //  - Laughing or yawning for longer
        LAUGH3(607), //  - Laughing or yawning for longer
        LAUGH4(608), //  - Laughing or yawning
        EVILLAUGH(609), //  - Evil laugh then plain evil
        SAD(610), //  - Slightly sad
        MORESAD(611), //  - Quite sad
        ONONEHAND(612), //  - On one hand...
        NEARLYCRYING(613), //  - Close to crying
        ANGER1(614), //  - Angry
        ANGER2(615), //  - Angry
        ANGER3(616), //  - Angry
        ANGER4(617); //  - Angry

        private int id;

        Emotes(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }
}
