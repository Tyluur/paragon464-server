package com.paragon464.gameserver.model.content.quests;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.util.TextUtils;

public class QuestManager {

    private static final int QUEST_COMPLETED_INTERFACE_ID = 277;
    private static final int QUEST_PROGRESS_LOG_INTERFACE_ID = 275;

    public static void progress(final Player player, Quest quest) {
        for (int i = 0; i < 134; i++) {
            player.getFrames().modifyText("", QUEST_PROGRESS_LOG_INTERFACE_ID, i);
        }
        if (quest == Quest.Recipe_For_Disaster) {
            int stage = player.getAttributes().getInt("rfd_stage");
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "I can start this quest by speaking to the Cook.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 5);
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "He is located in Lumbridge.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 6);
            if (stage >= 1) {
                player.getFrames().modifyText(
                    (stage > 1 ? "<str>" : "") + "I should probably go talk to this Gypsy lady.",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 7);
            }
            if (stage >= 4) {
                player.getFrames().modifyText(
                    (stage > 4 ? "<str>" : "") + "Gypsy gave me a magic scroll. Maybe I should find",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 8);
                player.getFrames().modifyText(
                    (stage > 4 ? "<str>" : "") + "this dining room she was talking about.",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 9);
            }
            if (stage >= 12) {
                player.getFrames().modifyText("Quest completed!", QUEST_PROGRESS_LOG_INTERFACE_ID, 11);
            }
        } else if (quest == Quest.Mountain_Daughter) {
            int stage = player.getAttributes().getInt("md_stage");
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "I can start this quest by speaking to Hamal the Chieftain.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 5);
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "He is located at the Mountain camp, east of Rellekka.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 6);
            if (stage >= 1) {
                player.getFrames().modifyText(
                    (stage > 2 ? "<str>" : "") + "A monster has been terrorizing this mountain camp for some time.",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 7);
                player.getFrames().modifyText(
                    (stage > 2 ? "<str>" : "") + "I was told it's located nearby in a cave.",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 8);
            }
            if (stage >= 3) {
                player.getFrames().modifyText("Quest completed!", QUEST_PROGRESS_LOG_INTERFACE_ID, 10);
            }
        } else if (quest == Quest.Great_Brain_Robbery) {
            int stage = player.getAttributes().getInt("brain_robbery_stage");
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "I can start this quest by speaking to Brother Tranquility.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 5);
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "He is located somewhere at Mos Le' Harmless.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 6);
            if (stage >= 1) {
                player.getFrames().modifyText(
                    (stage > 2 ? "<str>" : "") + "I must find a way to Harmony Island.",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 7);
            }
            if (stage >= 3) {
                player.getFrames().modifyText(
                    (stage > 3 ? "<str>" : "") + "I should head back to Brother Tranquility.",
                    QUEST_PROGRESS_LOG_INTERFACE_ID, 8);
            }
            if (stage >= 5) {
                player.getFrames().modifyText("Quest completed!", QUEST_PROGRESS_LOG_INTERFACE_ID, 10);
            }
        } else if (quest == Quest.Desert_Treasure) {
            int stage = player.getAttributes().getInt("dt_stage");
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "I can start this quest by speaking to the Archaeologist in Al-Kharid.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 5);
            if (stage >= 1) {
                player.getFrames().modifyText(((stage > 1) ? "<str>" : "") + "Archaeologist told me I should head to Canifis.", QUEST_PROGRESS_LOG_INTERFACE_ID, 6);
            }
            if (stage >= 2) {
                player.getFrames().modifyText((stage >= 3 ? "<str>" : "") + "Archaeologist told me I should head to Burthorpe.", QUEST_PROGRESS_LOG_INTERFACE_ID, 7);
            }
            if (stage >= 5) {
                player.getFrames().modifyText((stage > 5 ? "<str>" : "") + "Archaeologist told me I should head to Al-Kharid.", QUEST_PROGRESS_LOG_INTERFACE_ID, 8);
            }
            if (stage >= 6) {
                player.getFrames().modifyText((stage > 6 ? "<str>" : "") + "Archaeologist told me I should head to Baxtorian Falls.", QUEST_PROGRESS_LOG_INTERFACE_ID, 9);
            }
            if (stage >= 7) {
                player.getFrames().modifyText("I've given Archaeologist all the diamonds he needed.", QUEST_PROGRESS_LOG_INTERFACE_ID, 10);
            }
            if (stage >= 8) {
                player.getFrames().modifyText("Quest completed!", QUEST_PROGRESS_LOG_INTERFACE_ID, 12);
            }
        } else if (quest == Quest.Horror_From_The_Deep) {
            int stage = player.getAttributes().getInt("hfd_stage");
            player.getFrames().modifyText(
                (stage > 0 ? "<str>" : "") + "I can start this quest by speaking to the Cook of Lumbridge.",
                QUEST_PROGRESS_LOG_INTERFACE_ID, 5);
            if (stage > 1) {
                player.getFrames().modifyText(((stage > 1) ? "<str>" : "") + "I should go find Gypsy somewhere in Varrock.", QUEST_PROGRESS_LOG_INTERFACE_ID, 6);
            }
            if (stage >= 2) {
                player.getFrames().modifyText((stage >= 3 ? "<str>" : "") + "Gypsy told me a man in an Apron ran off to a dining room.", QUEST_PROGRESS_LOG_INTERFACE_ID, 7);
            }
            if (stage >= 3) {
                player.getFrames().modifyText((stage >= 12 ? "<str>" : "") + "Gypsy gave me this map scroll to remove the hex.", QUEST_PROGRESS_LOG_INTERFACE_ID, 8);
                player.getFrames().modifyText((stage >= 12 ? "<str>" : "") + "I should go find this dining room she was talking about.", QUEST_PROGRESS_LOG_INTERFACE_ID, 9);
            }
            if (stage == 12) {
                player.getFrames().modifyText("Quest completed!", QUEST_PROGRESS_LOG_INTERFACE_ID, 11);
            }
        }
        player.getFrames().modifyText("" + TextUtils.formatName(quest.name()), QUEST_PROGRESS_LOG_INTERFACE_ID, 2);
        player.getInterfaceSettings().openInterface(QUEST_PROGRESS_LOG_INTERFACE_ID);
    }

    public static void completed(final Player player, Quest quest) {
        if (quest == Quest.Desert_Treasure) {
            player.getFrames().itemOnInterface(QUEST_COMPLETED_INTERFACE_ID, 3, 400, 4675);
        } else if (quest == Quest.Recipe_For_Disaster) {
            player.getFrames().itemOnInterface(QUEST_COMPLETED_INTERFACE_ID, 3, 400, 7462);
        } else if (quest == Quest.Mountain_Daughter) {
            player.getFrames().itemOnInterface(QUEST_COMPLETED_INTERFACE_ID, 3, 200, 4502);
        } else if (quest == Quest.Great_Brain_Robbery) {
            player.getFrames().itemOnInterface(QUEST_COMPLETED_INTERFACE_ID, 3, 250, 10887);
        } else if (quest == Quest.Horror_From_The_Deep) {
            player.getFrames().itemOnInterface(QUEST_COMPLETED_INTERFACE_ID, 3, 200, 3842);
        }
        int qpAwarded = quest.getQP();
        player.getAttributes().addInt("quest_points", qpAwarded);
        player.getFrames().modifyText("" + player.getAttributes().getInt("quest_points"), QUEST_COMPLETED_INTERFACE_ID, 5);
        for (int i = 0; i < 7; i++) {
            int compId = 8 + i;
            if (i >= quest.getRewards().length) {
                player.getFrames().modifyText("", QUEST_COMPLETED_INTERFACE_ID, compId);
            } else {
                String line = quest.getRewards()[i];
                player.getFrames().modifyText(line, QUEST_COMPLETED_INTERFACE_ID, compId);
            }
        }
        player.getFrames().sendMessage("Congratulations! You've completed the " + TextUtils.formatName(quest.name()) + " Quest.");
        player.getFrames().modifyText("You have completed the " + TextUtils.formatName(quest.name()) + " Quest!", QUEST_COMPLETED_INTERFACE_ID, 2);
        player.getInterfaceSettings().openInterface(QUEST_COMPLETED_INTERFACE_ID);
    }

    public enum Quest {
        Desert_Treasure(3, new String[]{"3 Quest Points", "20,000 Magic XP", "Ancient Magicks"}),
        Recipe_For_Disaster(5, new String[]{"5 Quest Points", "Access to Culinaromancer's chest."}),
        Mountain_Daughter(2, new String[]{"2 Quest Points", "A bear's head."}),
        Great_Brain_Robbery(2, new String[]{"2 Quest Points", "A broken Barrelchest anchor."}),
        Horror_From_The_Deep(2, new String[]{"2 Quest Points", "A casket."});

        private int qpAwarded;
        private String[] rewards;

        Quest(int qp, String[] rewards) {
            this.qpAwarded = qp;
            this.rewards = rewards;
        }

        public int getQP() {
            return qpAwarded;
        }

        public String[] getRewards() {
            return rewards;
        }
    }
}
