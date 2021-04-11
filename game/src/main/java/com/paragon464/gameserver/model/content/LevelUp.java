package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;

import java.util.HashMap;
import java.util.Map;

public class LevelUp {

    private static Map<SkillType, Type> levelupTypes = new HashMap<>();

    static {
        for (final Type types : Type.values()) {
            levelupTypes.put(types.skillType, types);
        }
    }

    public static void send(Player player, SkillType skillType) {
        Type type = Type.getType(skillType);
        if (type == null) {
            return;
        }
        player.playGraphic(199, 2);
        int chatbox = type.getInterface();
        player.getFrames().modifyText(
            "<col=00008B>" + "Congratulations, you have just advanced a " + skillType.getDisplayName() + " level!",
            chatbox, 0);
        player.getFrames().modifyText("You have now reached level " + player.getSkills().getLevel(skillType) + ".",
            chatbox, 1);
        player.getFrames().sendChatboxInterface(chatbox);
    }

    public enum Type {
        AGILITY_LEVEL_UP(SkillType.AGILITY, 157),
        ATTACK_LEVEL_UP(SkillType.ATTACK, 158),
        COOKING_LEVEL_UP(SkillType.COOKING, 159),
        CRAFTING_LEVEL_UP(SkillType.CRAFTING, 160),
        DEFENCE_LEVEL_UP(SkillType.DEFENCE, 161),
        FARMING_LEVEL_UP(SkillType.FARMING, 162),
        FIREMAKING_LEVEL_UP(SkillType.FIREMAKING, 163),
        FISHING_LEVEL_UP(SkillType.FISHING, 164),
        FLETCHING_LEVEL_UP(SkillType.FLETCHING, 165),
        HERBLORE_LEVEL_UP(SkillType.HERBLORE, 166),
        HITPOINT_LEVEL_UP(SkillType.HITPOINTS, 167),
        MAGIC_LEVEL_UP(SkillType.MAGIC, 168),
        MINING_LEVEL_UP(SkillType.MINING, 169),
        PRAYER_LEVEL_UP(SkillType.PRAYER, 170),
        RANGING_LEVEL_UP(SkillType.RANGED, 171),
        RUNECRAFTING_LEVEL_UP(SkillType.RUNECRAFTING, 172),
        SLAYER_LEVEL_UP(SkillType.SLAYER, 173),
        SMITHING_LEVEL_UP(SkillType.SMITHING, 174),
        STRENGTH_LEVEL_UP(SkillType.STRENGTH, 175),
        THIEVING_LEVEL_UP(SkillType.THIEVING, 176),
        WOODCUTTING_LEVEL_UP(SkillType.WOODCUTTING, 177),
        ;

        private SkillType skillType;
        private int interfaceId;

        Type(SkillType skillType, int interfaceId) {
            this.skillType = skillType;
            this.interfaceId = interfaceId;
        }

        public static Type getType(SkillType skillType) {
            return levelupTypes.get(skillType);
        }

        public SkillType getSkill() {
            return skillType;
        }

        public int getInterface() {
            return interfaceId;
        }
    }
}
