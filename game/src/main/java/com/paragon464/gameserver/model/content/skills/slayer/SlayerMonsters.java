package com.paragon464.gameserver.model.content.skills.slayer;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCCombatDefinition;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;

public class SlayerMonsters {

    public static boolean canAttackMob(Player player, NPC mob) {
        int req = getRequiredLevel(mob.getId());
        if ((req != 0) && (player.getSkills().getCurrentLevel(SkillType.SLAYER) < req)) {
            player.getFrames().sendMessage("You need a Slayer level of " + req + " to attack this monster.");
            return false;
        }
        return true;
    }

    public static byte getRequiredLevel(int id) {
        NPCCombatDefinition def = NPCCombatDefinition.forId(id);
        if (def != null)
            return (byte) def.slayerLvl;
        return 1;
    }

    public static byte getLevelForName(String check) {
        byte lvl = 1;
        switch (check) {
            case "crawling hand":
                lvl = (byte) 5;
                break;
            case "cave bug":
                lvl = (byte) 7;
                break;
            case "cave crawler":
                lvl = (byte) 10;
                break;
            case "banshee":
                lvl = (byte) 15;
                break;
            case "cave slime":
                lvl = (byte) 17;
                break;
            case "rockslug":
                lvl = (byte) 20;
                break;
            case "desert lizard":
                lvl = (byte) 22;
                break;
            case "cockatrice":
                lvl = (byte) 25;
                break;
            case "pyrefiend":
                lvl = (byte) 30;
                break;
            case "mogre":
                lvl = (byte) 32;
                break;
            case "infernal mage":
                lvl = (byte) 45;
                break;
            case "bloodveld":
                lvl = (byte) 50;
                break;
            case "jelly":
                lvl = (byte) 62;
                break;
            case "cave horror":
                lvl = (byte) 58;
                break;
            case "aberrant spectre":
                lvl = (byte) 60;
                break;
            case "dust devil":
                lvl = (byte) 65;
                break;
            case "spiritual ranger":
                lvl = (byte) 63;
                break;
            case "spiritual warrior":
                lvl = (byte) 68;
                break;
            case "kurask":
                lvl = (byte) 70;
                break;
            case "gargoyle":
                lvl = (byte) 75;
                break;
            case "aquanite":
                lvl = (byte) 78;
                break;
            case "nechryael":
                lvl = (byte) 80;
                break;
            case "spiritual mage":
                lvl = (byte) 83;
                break;
            case "abyssal demon":
                lvl = (byte) 85;
                break;
            case "dark beast":
                lvl = (byte) 90;
                break;
        }
        return lvl;
    }
}
