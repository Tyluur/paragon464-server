package com.paragon464.gameserver.model.content.skills.slayer;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCDefinition;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.quests.QuestTab;
import com.paragon464.gameserver.model.content.skills.slayer.TaskData.HighLevel;
import com.paragon464.gameserver.model.content.skills.slayer.TaskData.LowLevel;
import com.paragon464.gameserver.model.content.skills.slayer.TaskData.MediumLevel;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Slayer {

    private final Player player;
    private String task = "none", area = "none";
    private byte amount = 0;
    private TaskDifficulty current = null;

    public Slayer(Player p) {
        this.player = p;
    }

    public void assign(TaskDifficulty diff) {
        switch (diff) {
            case LOW:
                List<LowLevel> low_tasks = Arrays.asList(TaskData.LOW_LEVEL_TASKS);
                Collections.shuffle(low_tasks);
                TaskData.LowLevel low_task = low_tasks.get(NumberUtils.random(low_tasks.size() - 1));
                while (player.getSkills().getLevel(SkillType.SLAYER) < low_task.lvl) {
                    low_task = low_tasks.get(NumberUtils.random(low_tasks.size() - 1));
                }
                task = low_task.name;
                area = low_task.area;
                amount = ((byte) (30 + NumberUtils.random(25)));
                current = TaskDifficulty.LOW;
                player.getFrames().sendMessage("You have been assigned to kill " + amount + " " + task + ".");
                break;
            case MEDIUM:
                List<MediumLevel> medium_tasks = Arrays.asList(TaskData.MEDIUM_TASKS);
                Collections.shuffle(medium_tasks);
                TaskData.MediumLevel medium_task = medium_tasks.get(NumberUtils.random(medium_tasks.size() - 1));
                while (player.getSkills().getLevel(SkillType.SLAYER) < medium_task.lvl) {
                    medium_task = medium_tasks.get(NumberUtils.random(medium_tasks.size() - 1));
                }
                task = medium_task.name;
                area = medium_task.area;
                amount = ((byte) (30 + NumberUtils.random(25)));
                current = TaskDifficulty.MEDIUM;
                player.getFrames().sendMessage("You have been assigned to kill " + amount + " " + task + ".");
                break;
            case HIGH:
                List<HighLevel> high_tasks = Arrays.asList(TaskData.HIGH_LEVEL_TASKS);
                Collections.shuffle(high_tasks);
                TaskData.HighLevel high_task = high_tasks.get(NumberUtils.random(high_tasks.size() - 1));
                while (player.getSkills().getLevel(SkillType.SLAYER) < high_task.lvl) {
                    high_task = high_tasks.get(NumberUtils.random(high_tasks.size() - 1));
                }
                task = high_task.name;
                area = high_task.area;
                amount = ((byte) (20 + NumberUtils.random(25)));
                current = TaskDifficulty.HIGH;
                player.getFrames().sendMessage("You have been assigned to kill " + amount + " " + task + ".");
                break;

            default:
                throw new IllegalArgumentException("Error assigning task to player.");
        }
    }

    public void checkForSlayer(NPC killed) {
        if (task == null) {
            return;
        }
        NPCDefinition def = killed.getDefinition();
        if (isSlayerTask(player, def.getName().toLowerCase())) {
            amount = ((byte) (amount - 1));
            double exp = def.getCombatLevel() * 2;
            addSlayerExperience(exp);
            if (amount == 0) {
                task = "none";
                area = "none";
                addSlayerExperience(def.getCombatLevel() * 35);
                player.getFrames().sendMessage("You have completed your Slayer task; Speak to Vannaka at home for more tasks.");
                int points = def.getCombatLevel();
                if (def.getCombatLevel() > 50) {
                    points /= 2;
                }
                player.getAttributes().addInt("slayer_points", points);
                QuestTab.sendGameInformation(player);
                player.getFrames().sendMessage("You received " + points + " Slayer points!");
            }
        }
    }

    public static boolean isSlayerTask(Player p, String other) {
        return (p.getSlayer().getTask() != null)
            && (other.toLowerCase().contains(p.getSlayer().getTask().toLowerCase()) || other.toLowerCase().equals(p.getSlayer().getTask().toLowerCase()));
    }

    public void addSlayerExperience(double am) {
        player.getSkills().addExperience(SkillType.SLAYER, am);
    }

    public String getTask() {
        if (task == null)
            return "none";
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public byte getAmount() {
        return amount;
    }

    public void setAmount(byte amount) {
        this.amount = amount;
    }

    public TaskDifficulty getCurrent() {
        return current;
    }

    public void setCurrent(TaskDifficulty current) {
        this.current = current;
    }

    public boolean hasTask() {
        return (amount > 0) && (task != null);
    }

    public void reset() {
        task = null;
        amount = 0;
        current = null;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String var) {
        this.area = var;
    }
}
